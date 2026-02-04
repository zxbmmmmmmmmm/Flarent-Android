package com.bettafish.flarent.ui.widgets

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bettafish.flarent.utils.CoilImagesPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.image.ImagesPlugin

@Composable
fun Markdown(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val context = LocalContext.current

    // 记得使用 remember，避免每次重组都重新创建 Markwon 实例
    val markwon = remember {
        Markwon.builder(context)
            // 这里可以根据需要添加插件
            .usePlugin(CorePlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(CoilImagesPlugin.create(context))
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                // 如果需要支持链接点击，需要加上这行
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            // 设置颜色（如果外部有传入）
            if (color != Color.Unspecified) {
                textView.setTextColor(color.toArgb())
            }
            // 渲染 Markdown
            markwon.setMarkdown(textView, markdown)
        }
    )
}