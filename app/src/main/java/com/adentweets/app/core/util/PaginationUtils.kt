package com.adentweets.app.core.util

object PaginationUtils {
    fun getNextPageKey(currentItems: Int, pageSize: Int = Constants.FEED_PAGE_SIZE): Int? {
        return if (currentItems % pageSize == 0 && currentItems > 0) {
            currentItems / pageSize + 1
        } else null
    }

    fun getStartIndex(page: Int, pageSize: Int = Constants.FEED_PAGE_SIZE): Int {
        return (page - 1) * pageSize
    }
}