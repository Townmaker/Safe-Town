package com.example.hyundai_app

import android.car.Car
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

internal fun Context.isAutomotive(): Boolean =
    packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)

internal suspend fun observeBattery(context: Context, onBattery: (Int?) -> Unit) {
    val vehicle = Vehicle(context.applicationContext)
    try {
        vehicle.initialize()
        val battery = vehicle.getEvBattery()
        while (true) {
            val level = withTimeoutOrNull(3000) {
                suspendCancellableCoroutine { continuation ->
                    battery.checkEvBatteryStateOfChargeCapabilities({ config ->
                        if (config.accessType == AccessType.READ || config.accessType == AccessType.READ_WRITE) {
                            battery.getEvBatteryStateOfCharge(
                                { value -> if (continuation.isActive) continuation.resume(value) },
                                { error ->
                                    Log.w("PleosVehicle", "Battery reading unavailable", error)
                                    if (continuation.isActive) continuation.resume(null)
                                },
                            )
                        } else if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }, { error ->
                        Log.w("PleosVehicle", "Battery capability unavailable", error)
                        if (continuation.isActive) continuation.resume(null)
                    })
                }
            }
            onBattery(level?.takeIf { it in 0..100 })
            delay(4000)
        }
    } catch (cancel: CancellationException) {
        throw cancel
    } catch (error: Exception) {
        Log.w("PleosVehicle", "Vehicle service unavailable", error)
        onBattery(null)
    } finally {
        runCatching { vehicle.release() }
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

