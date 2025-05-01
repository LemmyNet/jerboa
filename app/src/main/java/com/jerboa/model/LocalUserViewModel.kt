package com.jerboa.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import kotlinx.coroutines.launch

class LocalUserViewModel : ViewModel() {
    fun markDonationNotificationShown(onComplete: () -> Unit) {
        if (API.getInstanceOrNull()?.FF?.markDonationDialogShown() == true) {
            viewModelScope.launch {
                val result = runCatching {
                    API.getInstance().markDonationDialogShown()
                }

                result
                    .onSuccess { response ->
                        response
                            .onSuccess {
                                onComplete()
                            }.onFailure {
                                onComplete() // Still dismiss dialog even if API failed
                                Log.d("markDonationNotificationShown", "mark donation shown request failed: $response")
                            }
                    }.onFailure { throwable ->
                        // Network or other failure
                        onComplete()
                        Log.d("markDonationNotificationShown", "mark donation shown request failed", throwable)
                    }
            }
        } else {
            onComplete()
        }
    }
}
