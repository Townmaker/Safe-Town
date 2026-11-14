package com.example.hyundai_app

import ai.pleos.playground.navi.constants.NaviErrorCode
import ai.pleos.playground.navi.constants.RouteOption
import ai.pleos.playground.navi.data.DestinationInfo
import ai.pleos.playground.navi.data.RouteInfo
import ai.pleos.playground.navi.data.RouteStartInfo
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

internal class RouteRequestException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

internal class PleosNavigationService(private val context: Context) : NavigationService {
    override val isDemo = false
    override val suggestedPlaces = listOf("42dot")

    private val testDestination = RouteInfo(
        latitude = 37.48544722,
        longitude = 127.03636666,
        poiName = "42dot",
        poiId = "36khp37hh8gqcm5nqhe5",
        poiSubId = "0",
        address = "서울시 강남구 남부순환로 2621",
        routeOption = RouteOption.RECOMMENDED,
    )

    override suspend fun requestRoute(place: String): RoutePreview {
        if (!place.equals(testDestination.poiName, ignoreCase = true) &&
            !place.equals(testDestination.address, ignoreCase = true)
        ) {
            val message = if (place == "집") "앱에 저장된 집 주소가 없어요."
                else "현재는 42dot 목적지를 Pleos 내비로 보낼 수 있어요."
            throw RouteRequestException(message)
        }

        val helper = NaviHelper(context)
        val connection = CompletableDeferred<Unit>()
        val result = CompletableDeferred<RoutePreview>()
        val listener = object : NaviHelperEventListener {
            override fun onRouteStarted(routeStartInfo: RouteStartInfo) {
                Log.d("PleosNavigation", "Route search started")
            }

            override fun onRoutingCompleted() = confirmDestination()

            override fun onDestinationChanged() = confirmDestination()

            @Deprecated("Used by older Pleos navigation services")
            override fun onRouteEnded() = confirmDestination()

            override fun onDestinationInfo(destinationInfo: DestinationInfo) {
                val matches = destinationInfo.poiId == testDestination.poiId ||
                    (abs(destinationInfo.latitude - testDestination.latitude) < .00025 &&
                        abs(destinationInfo.longitude - testDestination.longitude) < .00025)
                if (matches) {
                    if (result.complete(RoutePreview(testDestination.poiName, "—", "—"))) {
                        Log.d("PleosNavigation", "Route destination confirmed")
                    }
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
        var listenerAdded = false
        try {
            helper.initialize(
                onConnected = { connection.complete(Unit) },
                onFailed = { connection.completeExceptionally(it) },
            )
            withTimeoutOrNull(5_000) { connection.await() }
                ?: throw RouteRequestException("Pleos 내비에 연결할 수 없어요.")
            helper.addListener(listener)
            listenerAdded = true
            helper.requestRoute(testDestination)
            return withTimeoutOrNull(12_000) { result.await() }
                ?: RoutePreview(testDestination.poiName, "—", "—", confirmed = false)
        } finally {
            if (listenerAdded) runCatching { helper.removeListener(listener) }
            runCatching { helper.release() }
        }
    }
}
