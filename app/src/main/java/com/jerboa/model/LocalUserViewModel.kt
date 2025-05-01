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
                val result = API.getInstance().markDonationDialogShown()

                result
                    .onSuccess {
                        onComplete()
                    }.onFailure { throwable ->
                        // Network or other failure
                        onComplete()
                        Log.e("markDonationNotificationShown", "mark donation shown request failed", throwable)
                    }
            }
        } else {
            onComplete()
        }
    }
}
