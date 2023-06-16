package com.jerboa.ui.components.common

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.html.MarkwonHtmlParserImpl
import org.commonmark.internal.util.Parsing
import org.commonmark.node.CustomBlock
import org.commonmark.parser.Parser
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState
import java.util.regex.Pattern

val spoilerPattern: Pattern =
    Pattern.compile("::: spoiler.*?:::")

const val SPOILER_START_BLOCK: String = "::: spoiler"
const val SPOILER_END_BLOCK: String = ":::"

private fun consumeSequence(skip: String, s: CharSequence, start: Int, end: Int): Int? {
    if (start + skip.length > s.length) {
        return null
    }

    for (i in 0 until skip.length) {
        if (s[start + i] != skip[i]) {
            return null
        }
    }
    return start + skip.length
}

class SpoilerBlockParserFactory : AbstractBlockParserFactory() {
    override fun tryStart(
        state: ParserState,
        matchedBlockParser: MatchedBlockParser?,
    ): BlockStart? {
        if (state.indent >= Parsing.CODE_BLOCK_INDENT) {
            return BlockStart.none()
        }

        val endStartBlock =
            consumeSequence(
                SPOILER_START_BLOCK,
                state.line,
                state.nextNonSpaceIndex,
                state.line.length,
            )
                ?: return BlockStart.none()
        val beginVisibleText = Parsing.skipSpaceTab(state.line, endStartBlock, state.line.length)
        if (beginVisibleText == endStartBlock) {
            return BlockStart.none()
        }
        val visibleText = state.line.substring(beginVisibleText).trim()
        if (visibleText.isEmpty()) {
            return BlockStart.none()
        }

        return BlockStart.of(SpoilerBlockParser(visibleText)).atIndex(state.line.length)
    }
}

class SpoilerBlock(val visibleText: String) : CustomBlock() {
    var spoilerContent: String = ""
}

class SpoilerBlockParser(visibleText: String) : AbstractBlockParser() {
    val builder = StringBuilder()
    val spoilerBlock = SpoilerBlock(visibleText)

    override fun getBlock() = spoilerBlock

    override fun tryContinue(parserState: ParserState): BlockContinue {
        if (parserState.indent >= Parsing.CODE_BLOCK_INDENT) {
            return BlockContinue.atIndex(parserState.index)
        }

        val blockEndIndex = parserState.line.indexOf(SPOILER_END_BLOCK) + SPOILER_END_BLOCK.length
        val endIndex =
            Parsing.skipSpaceTab(parserState.line, blockEndIndex, parserState.line.length)
        if (endIndex != parserState.line.length) {
            return BlockContinue.atIndex(parserState.index)
        }

        return BlockContinue.finished()
    }

    override fun addLine(line: CharSequence?) {
        builder.append(line)
        builder.appendLine()
    }

    override fun closeBlock() {
        spoilerBlock.spoilerContent = builder.toString()
    }
}

class SpoilerPlugin : AbstractMarkwonPlugin() {
    var htmlParser = MarkwonHtmlParserImpl.create()

    override fun configureParser(builder: Parser.Builder) {
        builder.customBlockParserFactory(SpoilerBlockParserFactory())
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(
            SpoilerBlock::class.java,
            MarkwonVisitor.NodeVisitor<SpoilerBlock> { visitor, block ->
                val stringBuilder = StringBuilder().append("<details><summary>")
                    .append(block.visibleText)
                    .append("</summary>")
                    .appendLine()
                    .append("<p>")
                    .append(block.spoilerContent)
                    .append("</p>")
                    .appendLine()
                    .append("</details>")
                    .appendLine()

                htmlParser.processFragment(visitor.builder(), stringBuilder.toString())
            },
        )
    }
}
