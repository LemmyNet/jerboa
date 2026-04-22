package com.jerboa

import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.DEFAULT_LOW_BANDWIDTH_MODE
import com.jerboa.feat.LowBandwidthMode
import org.junit.Assert.assertEquals
import org.junit.Test

class LowBandwidthModeTest {
    @Test
    fun `default low bandwidth mode constant is auto`() {
        assertEquals(LowBandwidthMode.Auto.ordinal, DEFAULT_LOW_BANDWIDTH_MODE)
    }

    @Test
    fun `default app settings uses auto low bandwidth mode`() {
        assertEquals(LowBandwidthMode.Auto.ordinal, APP_SETTINGS_DEFAULT.lowBandwidthMode)
    }
}
