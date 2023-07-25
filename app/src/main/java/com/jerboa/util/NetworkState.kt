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
    fun unregisterNetworkCallback()
}

class NetworkStateImpl(
    private val context: Context,
) : NetworkState {
    private val _isOnline = MutableLiveData<Boolean>()
    override val isOnline: LiveData<Boolean> = _isOnline

    private val callback = object : ConnectivityManager.NetworkCallback() {

        private val networks = mutableSetOf<Network>()

        override fun onAvailable(network: Network) {
            networks += network
            _isOnline.postValue(true)
        }

        override fun onLost(network: Network) {
            networks -= network
            _isOnline.postValue(networks.isNotEmpty())
        }
    }

    init {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if (connectivityManager == null) {
            _isOnline.postValue(false)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
            _isOnline.postValue(connectivityManager.isCurrentlyConnected())
        }
    }

    private fun ConnectivityManager.isCurrentlyConnected() =
        activeNetwork
            ?.let(::getNetworkCapabilities)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false

    override fun unregisterNetworkCallback() {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        connectivityManager?.unregisterNetworkCallback(callback)
    }
}
