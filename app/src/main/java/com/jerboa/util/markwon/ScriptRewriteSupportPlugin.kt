package com.jerboa.util.markwon

import io.noties.markwon.AbstractMarkwonPlugin

class ScriptRewriteSupportPlugin : AbstractMarkwonPlugin() {
    override fun processMarkdown(markdown: String): String =
        super.processMarkdown(
            if (markdown.contains("^") || markdown.contains("~")) {
                rewriteLemmyScriptToMarkwonScript(markdown)
            } else { // Fast path: if there are no markdown characters, we don't need to do anything
                markdown
            },
        )

    companion object {
        /*
         * Superscript has the definition:
         * Any text between a '^' that is not interrupted by a linebreak where the starting
         * or ending text can't be a whitespace character.
         */
        val SUPERSCRIPT_RGX = Regex("""\^(?!\s)([^\n^]+)(?<!\s)\^""")

        /*
         * Subscript has the definition:
         * Any text between a tilde that is not interrupted by a linebreak where the starting
         * or ending text can't be a whitespace character. And where the starting the tilde is not prefixed
         * by another tilde. (To prevent matching with strikethrough)
         */
        val SUBSCRIPT_RGX = Regex("""(?<!~)~(?!\s)([^\n~]+)(?<!\s)~""")

        fun rewriteLemmyScriptToMarkwonScript(text: String): String =
            text
                .replace(SUPERSCRIPT_RGX, "<sup>$1</sup>")
                .replace(SUBSCRIPT_RGX, "<sub>$1</sub>")
    }
}
