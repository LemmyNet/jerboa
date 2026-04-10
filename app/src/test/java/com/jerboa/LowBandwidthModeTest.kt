package com.jerboa

import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.DEFAULT_LOW_BANDWIDTH_MODE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class LowBandwidthModeTest {
    @Test
    fun `default low bandwidth mode constant is disabled`() {
        assertEquals(0, DEFAULT_LOW_BANDWIDTH_MODE)
    }

    @Test
    fun `default app settings has low bandwidth mode disabled`() {
        assertFalse(APP_SETTINGS_DEFAULT.lowBandwidthMode)
    }
}
