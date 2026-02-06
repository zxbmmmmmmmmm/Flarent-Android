package com.bettafish.flarent.utils

import com.vladsch.flexmark.html2md.converter.CustomHtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.HtmlMarkdownWriter
import com.vladsch.flexmark.html2md.converter.HtmlNodeConverterContext
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler
import org.jsoup.nodes.Element


object HtmlConverter {
    private val converter = FlexmarkHtmlConverter.builder().htmlNodeRendererFactory {
        PostMentionNodeRenderer()
    }.build()
    fun convert(html: String): String = converter.convert(html);
}

class PostMentionNodeRenderer : HtmlNodeRenderer {

    override fun getHtmlNodeRendererHandlers(): Set<HtmlNodeRendererHandler<*>?> {
        return setOf(
            HtmlNodeRendererHandler("a", Element::class.java, this::processA)
        )
    }

    private fun processA(element: Element, context: HtmlNodeConverterContext, out: HtmlMarkdownWriter) {
        val href = element.attr("href")
        val text = context.processTextNodes(element).trim()

        if (element.hasClass("PostMention")) {
            val dataId = element.attr("data-id")
            val separator = if (href.contains("?")) "&" else "?"
            val newHref = if (dataId.isNotEmpty()) "$href${separator}post=$dataId" else href

            out.append("[").append(text).append("](").append(newHref).append(")")
        } else {
            out.append("[").append(text).append("](").append(href).append(")")
        }

        context.excludeAttributes("href", "data-id")
    }
}