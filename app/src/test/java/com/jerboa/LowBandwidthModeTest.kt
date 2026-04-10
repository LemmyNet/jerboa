package com.jerboa

import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.DEFAULT_LOW_BANDWIDTH_MODE
import com.jerboa.ui.components.common.stripInlineImages
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

    @Test
    fun `stripInlineImages rewrites markdown image with alt text to link`() {
        assertEquals(
            "[cat](https://example.com/cat.png)",
            stripInlineImages("![cat](https://example.com/cat.png)"),
        )
    }

    @Test
    fun `stripInlineImages uses fallback label when alt text is empty`() {
        assertEquals(
            "[image](https://example.com/a.jpg)",
            stripInlineImages("![](https://example.com/a.jpg)"),
        )
    }

    @Test
    fun `stripInlineImages handles image with title attribute`() {
        assertEquals(
            "[thing](https://example.com/a.png)",
            stripInlineImages("""![thing](https://example.com/a.png "a title")"""),
        )
    }

    @Test
    fun `stripInlineImages rewrites html img tag to link`() {
        assertEquals(
            "[image](https://example.com/a.png)",
            stripInlineImages("""<img src="https://example.com/a.png" alt="x" />"""),
        )
    }

    @Test
    fun `stripInlineImages leaves non-image markdown intact`() {
        val src = "hello [world](https://example.com) and `code`"
        assertEquals(src, stripInlineImages(src))
    }

    @Test
    fun `stripInlineImages handles multiple images in one comment`() {
        assertEquals(
            "before [a](u1) middle [b](u2) end",
            stripInlineImages("before ![a](u1) middle ![b](u2) end"),
        )
    }
}
