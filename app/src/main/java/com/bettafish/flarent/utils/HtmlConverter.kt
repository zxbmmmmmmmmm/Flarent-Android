package com.bettafish.flarent.utils

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter

object HtmlConverter {
    private val converter = FlexmarkHtmlConverter.builder().build()
    fun convert(html: String): String = converter.convert(html);
}