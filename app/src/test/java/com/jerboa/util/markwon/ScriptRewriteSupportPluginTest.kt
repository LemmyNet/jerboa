package com.jerboa.util.markwon

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class ScriptRewriteSupportPluginTest {
    @Test
    @Parameters(
        method = "successCases",
    )
    fun `should rewrite lemmy script to markwon script`(
        input: String,
        expected: String,
    ) {
        val result = ScriptRewriteSupportPlugin.rewriteLemmyScriptToMarkwonScript(input)
        Assert.assertEquals(expected, result)
    }

    fun successCases() =
        listOf(
            listOf("^2^", "<sup>2</sup>"),
            listOf("~2~", "<sub>2</sub>"),
            listOf("~2~ ~2~", "<sub>2</sub> <sub>2</sub>"),
            listOf("^2^ ^2^", "<sup>2</sup> <sup>2</sup>"),
            listOf("^^", "^^"),
            listOf("^\n^", "^\n^"),
            // Due to a parse limitation, the following case isn't fully supported
            // The negative lookbehind matches with consumed tokens :/
            listOf("~2~~2~", "<sub>2</sub>~2~"),
            listOf("~2~\n~2~", "<sub>2</sub>\n<sub>2</sub>"),
            listOf("~2~\n~2~", "<sub>2</sub>\n<sub>2</sub>"),
            listOf("~ blah blah", "~ blah blah"),
            listOf("", ""),
            // Strikethrough syntax
            listOf("~~text~~", "~~text~~"),
            // Intended to fail, else it will increase the complexity of the regex by a huge margin
            listOf("~~text~", "~~text~"),
            listOf("~text~~", "<sub>text</sub>~"),
            listOf(
                "Tesla model X (range ~ 260kms) first, now a model Y LR (range ~ 480kms)",
                "Tesla model X (range ~ 260kms) first, now a model Y LR (range ~ 480kms)",
            ),
            listOf("~ 5 ~ 6 ~", "~ 5 ~ 6 ~"),
            listOf("^ ^", "^ ^"),
            listOf("^", "^"),
            listOf("~", "~"),
            listOf("~~", "~~"),
            listOf("~~~", "~~~"),
            listOf("^ 99 ^", "^ 99 ^"),
            listOf("^ 99^", "^ 99^"),
            listOf("^99 ^", "^99 ^"),
            listOf("~ 99 ~", "~ 99 ~"),
            listOf("~ 99~", "~ 99~"),
            listOf("~99 ~", "~99 ~"),
        )
}
