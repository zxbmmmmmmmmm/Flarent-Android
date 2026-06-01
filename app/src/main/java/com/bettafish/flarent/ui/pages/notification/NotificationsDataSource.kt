package com.bettafish.flarent.ui.pages.notification

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.NotificationsRepository
import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.NotificationRequest
import com.bettafish.flarent.utils.HtmlConverter

class NotificationsDataSource(
    private val repository:  NotificationsRepository,
    private val pageSize: Int,
) : PagingSource<Int, Notification>(){
    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        return try {
            val offset = params.key ?: 0
            val items = repository.fetchNotifications(NotificationRequest(offset = offset, limit = pageSize))
            items.forEach { item ->
                (item.subject as? Post)?.let{
                    if(it.contentType == "comment" && it.content != null)
                    {
                        it.text = HtmlConverter.convertToPlainText(it.content.toString())
                    }
                    it.discussion
                }

            }
            val nextKey = if (items.size < pageSize) null else offset + pageSize
            val prevKey = if (offset == 0) null else maxOf(0, offset - pageSize)
            LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}