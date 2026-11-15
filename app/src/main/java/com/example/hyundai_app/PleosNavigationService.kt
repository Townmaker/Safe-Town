package com.example.hyundai_app

import ai.pleos.playground.navi.constants.NaviErrorCode
import ai.pleos.playground.navi.constants.RouteOption
import ai.pleos.playground.navi.data.CurrentLocationInfo
import ai.pleos.playground.navi.data.DestinationInfo
import ai.pleos.playground.navi.data.RecentDestinationInfo
import ai.pleos.playground.navi.data.RouteInfo
import ai.pleos.playground.navi.data.TBTInfo
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

internal class RouteRequestException(message: String) : Exception(message)

internal class PleosNavigationService(private val context: Context) : NavigationService {
    override val isDemo = false
    override val suggestedPlaces = listOf(NavigationPlace(
        name = "42dot", address = "서울시 강남구 남부순환로 2621",
        latitude = 37.48544722, longitude = 127.03636666,
        poiId = "36khp37hh8gqcm5nqhe5", poiSubId = "0",
    ))
    private val connectionLock = Mutex()

    override suspend fun destinationSearch(): DestinationSearch = readFromNavigation(DestinationSearch()) { helper ->
        val origin = CompletableDeferred<String?>()
        val recent = CompletableDeferred<List<NavigationPlace>>()
        val listener = object : NaviHelperEventListener {
            override fun onCurrentLocationInfo(currentLocationInfo: CurrentLocationInfo) {
                origin.complete(currentLocationInfo.address.takeIf { it.isNotBlank() })
            }

            override fun onRecentDestinationInfo(recentDestinationInfoList: List<RecentDestinationInfo>) {
                recent.complete(recentDestinationInfoList.mapNotNull { place ->
                    if (place.name.isBlank() || !validCoordinates(place.latitude, place.longitude)) null
                    else NavigationPlace(place.name, place.address.orEmpty(), place.latitude, place.longitude,
                        place.displayLatitude, place.displayLongitude, place.poiId, place.poiSubId, place.poiProvider)
                }.distinct().take(10))
            }
        }
        helper.withListener(listener) {
            helper.getCurrentLocationInfo()
            helper.getRecentDestinationInfo()
            val address = withTimeoutOrNull(2_000) { origin.await() }
            DestinationSearch(address, withTimeoutOrNull(2_000) { recent.await() })
        }
    }

    override suspend fun nextInstruction(): String? = readFromNavigation(null) { helper ->
        val result = CompletableDeferred<String?>()
        val listener = object : NaviHelperEventListener {
            override fun onTBTInfo(tbtInfoList: List<TBTInfo>) {
                result.complete(tbtInfoList.firstNotNullOfOrNull { it.description.takeIf(String::isNotBlank) })
            }

            override fun onError(errorCode: NaviErrorCode) {
                result.complete(null)
            }
        }
        helper.withListener(listener) {
            helper.getTBTInfo()
            withTimeoutOrNull(2_000) { result.await() }
        }
    }

    override suspend fun requestRoute(place: NavigationPlace): RoutePreview {
        val latitude = place.latitude
        val longitude = place.longitude
        if (latitude == null || longitude == null || !validCoordinates(latitude, longitude)) {
            throw RouteRequestException("최근 목적지나 연결 테스트 목적지를 선택해 주세요.")
        }
        val route = RouteInfo(latitude = latitude, longitude = longitude,
            displayLatitude = place.displayLatitude, displayLongitude = place.displayLongitude,
            poiName = place.name, poiId = place.poiId.orEmpty(), poiSubId = place.poiSubId.orEmpty(),
            address = place.address, routeOption = RouteOption.SHORT_TIME, poiProvider = place.poiProvider.orEmpty())

        return withNavigation { helper ->
            val result = CompletableDeferred<RoutePreview>()
            val listener = object : NaviHelperEventListener {
                override fun onRoutingCompleted() = confirmDestination()

                override fun onDestinationChanged() = confirmDestination()

                @Deprecated("Used by older Pleos navigation services")
                override fun onRouteEnded() = confirmDestination()

                override fun onDestinationInfo(destinationInfo: DestinationInfo) {
                    val matches = (!place.poiId.isNullOrBlank() && destinationInfo.poiId == place.poiId) ||
                        (abs(destinationInfo.latitude - latitude) < .00025 &&
                            abs(destinationInfo.longitude - longitude) < .00025)
                    if (matches && result.complete(RoutePreview(place.name))) {
                        Log.d("PleosNavigation", "Route destination confirmed")
                    }
                }

                override fun onError(errorCode: NaviErrorCode) {
                    Log.w("PleosNavigation", "Route error: $errorCode")
                    result.completeExceptionally(RouteRequestException("Pleos 내비가 경로를 설정하지 못했어요."))
                }

                private fun confirmDestination() {
                    if (!result.isCompleted) {
                        runCatching { helper.getDestinationInfo() }.onFailure { result.completeExceptionally(it) }
                    }
                }
            }
            helper.withListener(listener) {
                helper.requestRoute(route)
                withTimeoutOrNull(12_000) { result.await() } ?: RoutePreview(place.name, confirmed = false)
            }
        }
    }

    private suspend fun <T> readFromNavigation(fallback: T, action: suspend (NaviHelper) -> T): T =
        try {
            withNavigation(action)
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.d("PleosNavigation", "Navigation information unavailable: ${error.javaClass.simpleName}")
            fallback
        }

    private suspend fun <T> withNavigation(action: suspend (NaviHelper) -> T): T = connectionLock.withLock {
        val helper = NaviHelper(context)
        val connection = CompletableDeferred<Unit>()
        try {
            helper.initialize(
                onConnected = { connection.complete(Unit) },
                onFailed = { connection.completeExceptionally(it) },
            )
            withTimeoutOrNull(5_000) { connection.await() }
                ?: throw RouteRequestException("Pleos 내비에 연결할 수 없어요.")
            action(helper)
        } finally {
            connection.cancel()
            runCatching { helper.release() }
        }
    }
}

private suspend fun <T> NaviHelper.withListener(listener: NaviHelperEventListener, action: suspend () -> T): T {
    addListener(listener)
    return try {
        action()
    } finally {
        runCatching { removeListener(listener) }
    }
}

private fun validCoordinates(latitude: Double, longitude: Double): Boolean =
    latitude in -90.0..90.0 && longitude in -180.0..180.0 && (latitude != 0.0 || longitude != 0.0)

