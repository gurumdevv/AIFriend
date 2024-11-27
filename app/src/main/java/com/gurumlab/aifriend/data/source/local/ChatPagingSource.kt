package com.gurumlab.aifriend.data.source.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gurumlab.aifriend.data.model.ChatMessage
import javax.inject.Inject

class ChatPagingSource @Inject constructor(
    private val dao: ChatDao
) : PagingSource<Int, ChatMessage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessage> {
        return try {
            val pageNumber = params.key ?: INIT_PAGE_INDEX

            val chats = dao.getPartialChatMessages(pageNumber, params.loadSize)
            val prevKey = if (pageNumber == INIT_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (chats.isEmpty()) null else pageNumber + 1

            LoadResult.Page(
                data = chats,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ChatMessage>): Int? {
        return null
    }

    private companion object {
        const val INIT_PAGE_INDEX = 0
    }
}