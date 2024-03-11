package com.jerboa.util.markwon

import io.noties.markwon.AbstractMarkwonPlugin

class ScriptRewriteSupportPlugin : AbstractMarkwonPlugin() {
    override fun processMarkdown(markdown: String): String {
        return super.processMarkdown(
            if (markdown.contains("^") || markdown.contains("~")) {
                rewriteLemmyScriptToMarkwonScript(markdown)
            } else { // Fast path: if there are no markdown characters, we don't need to do anything
                markdown
            },
        )
    }

    companion object {
        val SUPERSCRIPT_RGX = Regex("""\^([^\n^]+)\^""")
        val SUBSCRIPT_RGX = Regex("""(?<!~)~([^\n~]+)~""")

        fun rewriteLemmyScriptToMarkwonScript(text: String): String {
            return text
                .replace(SUPERSCRIPT_RGX, "<sup>$1</sup>")
                .replace(SUBSCRIPT_RGX, "<sub>$1</sub>")
        }
    }
}
