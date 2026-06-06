package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bettafish.flarent.ui.theme.markdownTypography
import com.bettafish.flarent.utils.ClickableCoil3ImageTransformer
import com.mikepenz.markdown.compose.components.MarkdownComponents
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.MarkdownState
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.rememberMarkdownState
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes

@Composable
fun AppMarkdown(
    content: String,
    modifier: Modifier = Modifier,
    retainState: Boolean = false,
    imageClick: (String) -> Unit = {},
) {
    AppMarkdown(
        markdownState = rememberMarkdownState(content, retainState = retainState),
        modifier = modifier,
        imageClick = imageClick,
    )
}

@Composable
fun AppMarkdown(
    markdownState: MarkdownState,
    modifier: Modifier = Modifier,
    imageClick: (String) -> Unit = {},
) {
    AppMarkdownContent(
        modifier = modifier,
    ) { markdownModifier, markdownComponents ->
        Markdown(
            markdownState = markdownState,
            imageTransformer = ClickableCoil3ImageTransformer(imageClick),
            components = markdownComponents,
            typography = markdownTypography,
            modifier = markdownModifier,
        )
    }
}

@Composable
fun AppMarkdown(
    state: State,
    modifier: Modifier = Modifier,
    imageClick: (String) -> Unit = {},
) {
    AppMarkdownContent(
        modifier = modifier,
    ) { markdownModifier, markdownComponents ->
        Markdown(
            state = state,
            imageTransformer = ClickableCoil3ImageTransformer(imageClick),
            components = markdownComponents,
            typography = markdownTypography,
            modifier = markdownModifier,
        )
    }
}

@Composable
private fun AppMarkdownContent(
    modifier: Modifier,
    content: @Composable (Modifier, MarkdownComponents) -> Unit,
) {
    val markdownComponents = rememberAppMarkdownComponents()

    SelectionContainer {
        content(modifier, markdownComponents)
    }
}

@Composable
private fun rememberAppMarkdownComponents(): MarkdownComponents {
    val isDarkTheme = isSystemInDarkTheme()

    return remember(isDarkTheme) {
        val highlightsBuilder =
            Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isDarkTheme))

        markdownComponents(
            codeBlock = {
                MarkdownHighlightedCodeBlock(
                    content = it.content,
                    node = it.node,
                    style = it.typography.code,
                    highlightsBuilder = highlightsBuilder,
                    showHeader = true,
                )
            },
            codeFence = {
                MarkdownHighlightedCodeFence(
                    content = it.content,
                    node = it.node,
                    style = it.typography.code,
                    highlightsBuilder = highlightsBuilder,
                    showHeader = true,
                )
            },
        )
    }
}
