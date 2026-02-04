package com.bettafish.flarent.utils


import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.widget.TextView
import coil3.Image
import coil3.ImageLoader
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.target.Target
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableLoader
import io.noties.markwon.image.AsyncDrawableScheduler
import io.noties.markwon.image.DrawableUtils
import io.noties.markwon.image.ImageSpanFactory
import org.commonmark.node.Image as MarkwonImage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

// 1. 定义一个全局单例缓存，用来存放 url -> (width, height)
// 使用 object 单例以保证在 RecyclerView/LazyColumn 滚动回收时不丢失数据
object MarkwonImageCache {
    // 使用 ConcurrentHashMap 保证线程安全，存储结构为 URL -> Rect(宽, 高)
    private val cache = ConcurrentHashMap<String, Rect>()

    fun get(url: String): Rect? = cache[url]

    fun put(url: String, width: Int, height: Int) {
        // 只保存合法的尺寸
        if (width > 0 && height > 0) {
            cache[url] = Rect(0, 0, width, height)
        }
    }
}

class CoilImagesPlugin private constructor(
    private val context: Context,
    private val imageLoader: ImageLoader
) : AbstractMarkwonPlugin() {

    companion object {
        fun create(context: Context): CoilImagesPlugin {
            return CoilImagesPlugin(context, context.imageLoader)
        }

        fun create(context: Context, imageLoader: ImageLoader): CoilImagesPlugin {
            return CoilImagesPlugin(context, imageLoader)
        }
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(MarkwonImage::class.java, ImageSpanFactory())
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.asyncDrawableLoader(CoilAsyncDrawableLoader(context, imageLoader))
    }

    override fun beforeSetText(textView: TextView, markdown: Spanned) {
        AsyncDrawableScheduler.unschedule(textView)
    }

    override fun afterSetText(textView: TextView) {
        AsyncDrawableScheduler.schedule(textView)
    }

    private class CoilAsyncDrawableLoader(
        private val context: Context,
        private val imageLoader: ImageLoader
    ) : AsyncDrawableLoader() {

        private val cache = mutableMapOf<AsyncDrawable, Disposable>()

        override fun load(drawable: AsyncDrawable) {
            val loaded = AtomicBoolean(false)
            val target = AsyncDrawableTarget(drawable, loaded)

            val request = ImageRequest.Builder(context)
                .data(drawable.destination)
                .target(target)
                .build()

            val disposable = imageLoader.enqueue(request)

            if (!loaded.get()) {
                loaded.set(true)
                cache[drawable] = disposable
            }
        }

        override fun cancel(drawable: AsyncDrawable) {
            cache.remove(drawable)?.dispose()
        }

        override fun placeholder(drawable: AsyncDrawable): Drawable? {
            // 2. 核心修改：优先从缓存读取尺寸
            val cachedBounds = MarkwonImageCache.get(drawable.destination)

            if (cachedBounds != null) {
                // 如果缓存命中，创建一个透明 Drawable 并设置缓存的宽高
                // 这样占位符的高度就等于最终图片的高度，避免了替换时的跳动
                return ColorDrawable(Color.TRANSPARENT).apply {
                    setBounds(0, 0, - cachedBounds.bottom,  cachedBounds.right)
                }
            }

            // 缓存未命中（第一次加载），使用默认高度
            val density = context.resources.displayMetrics.density
            val defaultHeight = (200 * density).toInt()
            val defaultWidth = context.resources.displayMetrics.widthPixels

            return ColorDrawable(Color.TRANSPARENT).apply {
                setBounds(0, 0, defaultWidth, defaultHeight)
            }
        }

        private inner class AsyncDrawableTarget(
            private val drawable: AsyncDrawable,
            private val loaded: AtomicBoolean
        ) : Target {

            override fun onStart(placeholder: Image?) {
                handleResult(placeholder)
            }

            override fun onSuccess(result: Image) {
                if (cache.remove(drawable) != null || !loaded.get()) {
                    loaded.set(true)
                    handleResult(result, isSuccess = true) // 标记为成功加载
                }
            }

            override fun onError(error: Image?) {
                if (cache.remove(drawable) != null) {
                    handleResult(error)
                }
            }

            private fun handleResult(coilImage: Image?, isSuccess: Boolean = false) {
                if (coilImage != null && drawable.isAttached) {
                    val outDrawable = coilImage.asDrawable(context.resources)

                    DrawableUtils.applyIntrinsicBoundsIfEmpty(outDrawable)

                    // 3. 核心修改：如果是成功加载，将计算好的尺寸写入缓存
                    if (isSuccess) {
                        val bounds = outDrawable.bounds
                        MarkwonImageCache.put(
                            drawable.destination,
                            bounds.width(),
                            bounds.height()
                        )
                    }

                    drawable.result = outDrawable
                }
            }
        }
    }
}