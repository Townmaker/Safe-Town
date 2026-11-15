package com.example.hyundai_app

import android.car.Car
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.area.DoorValue
import ai.pleos.playground.vehicle.constant.area.SeatValue
import ai.pleos.playground.vehicle.constant.control.OilLevelState
import ai.pleos.playground.vehicle.constant.telltale.TelltaleState
import ai.pleos.playground.vehicle.constant.telltale.TelltaleType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

internal fun Context.isAutomotive(): Boolean =
    packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)

internal data class VehicleInspection(
    val battery: Int? = null,
    val tireWarning: Boolean? = null,
    val frontLeftBuckle: Boolean? = null,
    val doorsLocked: Boolean? = null,
    val brakeFluidNormal: Boolean? = null,
    val washerWarning: Boolean? = null,
    val engineOilWarning: Boolean? = null,
    val transmissionOilNormal: Boolean? = null,
    val odometerKm: Float? = null,
)

private val passengerDoors = setOf(DoorValue.ROW_1_LEFT, DoorValue.ROW_1_RIGHT,
    DoorValue.ROW_2_LEFT, DoorValue.ROW_2_RIGHT)
private val frontLeft = SeatArea(SeatValue.ROW_1_LEFT)

internal suspend fun observeVehicleInspection(context: Context, onReading: (VehicleInspection) -> Unit) {
    val vehicle = Vehicle(context.applicationContext)
    try {
        vehicle.initialize()
        while (true) {
            onReading(vehicle.readInspection())
            delay(4_000)
        }
    } catch (cancel: CancellationException) {
        throw cancel
    } catch (error: Exception) {
        Log.w("PleosVehicle", "Vehicle service unavailable", error)
        onReading(VehicleInspection())
    } finally {
        runCatching { vehicle.release() }
    }
}

private suspend fun Vehicle.readInspection(): VehicleInspection = coroutineScope {
    val battery = getEvBattery()
    val seat = getSeat()
    val door = getDoor()
    val safety = getSafety()
    val odometer = getOdometer()

    val mileage = async {
        readProperty(odometer::checkCurrentDistanceDrivenCapabilities, odometer::getCurrentDistanceDriven)
            ?.takeIf { it.isFinite() && it >= 0 }
    }
    val charge = async {
        readProperty(battery::checkEvBatteryStateOfChargeCapabilities, battery::getEvBatteryStateOfCharge)
            ?.takeIf { it in 0..100 }
    }
    val telltales = async {
        // The getter is public. The capability check and direct tire pressure require a private permission.
        awaitVehicle(safety::getTelltalesInfo)?.telltales
    }
    val buckle = async {
        val area = supportedValues(seat::checkSeatBeltBuckledCapabilities)?.firstOrNull { it == frontLeft }
        area?.let { awaitVehicle { success, failure -> seat.getCurrentSeatBeltBuckledState(it, success, failure) } }
    }
    val doors = async {
        val supported = supportedValues(door::checkDoorLockCapabilities).orEmpty()
            .filter { it.values.any(passengerDoors::contains) }
        val readings = supported.map { area ->
            async { area to awaitVehicle<Boolean?> { success, failure -> door.getDoorLockState(area, success, failure) } }
        }.awaitAll()
        when {
            readings.any { it.second == false } -> false
            readings.filter { it.second == true }.flatMap { it.first.values.asList() }.containsAll(passengerDoors) -> true
            else -> null
        }
    }
    val brake = async {
        readOilState(safety::checkBrakeFluidLevelStateCapabilities, safety::getBrakeFluidLevelState)
    }
    val washer = async {
        readProperty(safety::checkLowWasherFluidWarningStateCapabilities, safety::hasLowWasherFluidWarning)
    }
    val transmission = async {
        readOilState(safety::checkTransmissionOilLevelStateCapabilities, safety::getTransmissionOilLevelState)
    }

    VehicleInspection(
        battery = charge.await(),
        tireWarning = telltales.await()?.firstOrNull { it.type == TelltaleType.TPMS }?.state?.warningActive(),
        frontLeftBuckle = buckle.await(),
        doorsLocked = doors.await(),
        brakeFluidNormal = brake.await(),
        washerWarning = washer.await(),
        engineOilWarning = telltales.await()?.firstOrNull { it.type == TelltaleType.ENGINE_OIL_PRESSURE }
            ?.state?.warningActive(),
        transmissionOilNormal = transmission.await(),
        odometerKm = mileage.await(),
    )
}

private fun TelltaleState.warningActive(): Boolean = this != TelltaleState.OFF

private suspend fun readOilState(
    check: ((CapabilityConfig, List<OilLevelState>) -> Unit, (Exception) -> Unit) -> Unit,
    read: ((OilLevelState?) -> Unit, (Exception) -> Unit) -> Unit,
): Boolean? = when (supportedValues(check)?.let { awaitVehicle(read) }) {
    OilLevelState.NORMAL -> true
    OilLevelState.LOW, OilLevelState.HIGH, OilLevelState.CRITICALLY_LOW -> false
    else -> null
}

private val CapabilityConfig.canRead: Boolean
    get() = accessType == AccessType.READ || accessType == AccessType.READ_WRITE

private suspend fun <T> supportedValues(
    check: ((CapabilityConfig, List<T>) -> Unit, (Exception) -> Unit) -> Unit,
): List<T>? = awaitVehicle { success, failure ->
    check({ config, values -> success(values.takeIf { config.canRead }) }, failure)
}

private suspend fun <T> readProperty(
    check: ((CapabilityConfig) -> Unit, (Exception) -> Unit) -> Unit,
    read: ((T) -> Unit, (Exception) -> Unit) -> Unit,
): T? {
    val config = awaitVehicle(check)
    return if (config?.canRead == true) awaitVehicle(read) else null
}

private suspend fun <T> awaitVehicle(call: ((T) -> Unit, (Exception) -> Unit) -> Unit): T? {
    val result = CompletableDeferred<T>()
    return try {
        call({ result.complete(it) }, { result.completeExceptionally(it) })
        withTimeoutOrNull(2_000) { result.await() }
    } catch (cancel: CancellationException) {
        throw cancel
    } catch (error: Exception) {
        if (error.message != "ERROR_NOT_SUPPORTED_PROPERTY") {
            Log.d("PleosVehicle", "Vehicle reading unavailable: ${error.javaClass.simpleName}")
        }
        null
    } finally {
        result.cancel()
    }
}

internal class DrivingRestrictions(context: Context, private val onChanged: (Boolean) -> Unit) :
    AutoCloseable {
    private var manager: CarUxRestrictionsManager? = null
    private var closed = false
    private val car: Car? = Car.createCar(context, null, Car.CAR_WAIT_TIMEOUT_DO_NOT_WAIT) { service, ready ->
        if (!closed) {
            runCatching { manager?.unregisterListener() }
            manager = null
            if (ready) {
                try {
                    val next = service.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as? CarUxRestrictionsManager
                    if (next != null) {
                        next.registerListener { restrictions ->
                            if (!closed && manager === next) onChanged(restrictions.isRequiresDistractionOptimization)
                        }
                        manager = next
                    }
                    onChanged(next?.currentCarUxRestrictions?.isRequiresDistractionOptimization ?: true)
                } catch (error: RuntimeException) {
                    Log.w("PleosVehicle", "Could not observe car UX restrictions", error)
                    onChanged(true)
                }
            } else {
                onChanged(true)
            }
        }
    }

    override fun close() {
        closed = true
        runCatching { manager?.unregisterListener() }
        runCatching { car?.disconnect() }
    }
}

