package com.bettafish.flarent.ui.theme

import android.graphics.Typeface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.MarkdownTypography

internal val MarkdownCodeFontFamily = FontFamily(Typeface.MONOSPACE)

val markdownTypography: MarkdownTypography
    @Composable
    get() = markdownTypography(
        h1 = MarkdownTextStyle.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
        ),
        h2 = MarkdownTextStyle.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),
        h3 = MarkdownTextStyle.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
        ),
        h4 = MarkdownTextStyle.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        h5 = MarkdownTextStyle.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        ),
        h6 = MarkdownTextStyle.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        ),
        text = MarkdownTextStyle,
        paragraph = MarkdownTextStyle,
        ordered = MarkdownTextStyle,
        bullet = MarkdownTextStyle,
        list = MarkdownTextStyle,
        quote = MarkdownTextStyle.copy(
            fontStyle = FontStyle.Normal,
        ),
        code = MarkdownCodeStyle,
        inlineCode = MarkdownCodeStyle,
        table = MarkdownTextStyle,
        textLink = TextLinkStyles(
            style = MarkdownTextStyle.copy(
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.None,
                color = MaterialTheme.colorScheme.primary,

            ).toSpanStyle()
        ),

    )

private val MarkdownTextStyle: TextStyle
    @Composable
    get() = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    )

private val MarkdownCodeStyle: TextStyle
    @Composable
    get() = MarkdownTextStyle.copy(
        fontFamily = MarkdownCodeFontFamily,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    )
