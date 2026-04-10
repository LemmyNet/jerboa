package com.jerboa.feat

import android.net.ConnectivityManager
import androidx.annotation.StringRes
import com.jerboa.R
import com.jerboa.isConnectionMetered
import com.jerboa.isDataSaverEnabled

enum class LowBandwidthMode(
    @param:StringRes val resId: Int,
) {
    Auto(R.string.low_bandwidth_mode_auto),
    Always(R.string.low_bandwidth_mode_always),
    Never(R.string.low_bandwidth_mode_never),
    ;

    fun isActive(connectivityManager: ConnectivityManager?): Boolean =
        when (this) {
            Auto -> connectivityManager.isConnectionMetered() || connectivityManager.isDataSaverEnabled()
            Always -> true
            Never -> false
        }
}
