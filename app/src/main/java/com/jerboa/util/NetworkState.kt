package com.jerboa.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Stable
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@Stable
interface NetworkState {
    val isOnline: LiveData<Boolean>
}

class NetworkStateImpl(
    private val context: Context,
) : NetworkState {
    private val _isOnline = MutableLiveData(call())

    override val isOnline: LiveData<Boolean> = _isOnline

    private fun call(): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>() ?: return false
        return connectivityManager.isCurrentlyConnected()
    }

    init {

        val connectivityManager = context.getSystemService<ConnectivityManager>()

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {

                private val networks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    networks += network
                    this@NetworkStateImpl._isOnline.postValue(true)
                }

                override fun onLost(network: Network) {
                    networks -= network
                    this@NetworkStateImpl._isOnline.postValue(networks.isNotEmpty())
                }
            },
        )
    }

    private fun ConnectivityManager.isCurrentlyConnected() =
        activeNetwork
            ?.let(::getNetworkCapabilities)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
}
