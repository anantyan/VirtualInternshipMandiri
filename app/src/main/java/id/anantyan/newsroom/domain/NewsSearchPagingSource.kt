package id.anantyan.newsroom.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anantyan.newsroom.data.remote.api.NewsApi
import id.anantyan.newsroom.data.remote.model.ArticlesItem
import retrofit2.HttpException
import java.io.IOException

class NewsSearchPagingSource(
    private val newsApi: NewsApi,
    private val query: String?
) : PagingSource<Int, ArticlesItem>() {
    override fun getRefreshKey(state: PagingState<Int, ArticlesItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        val position = params.key ?: 1
        return try {
            val response = newsApi.getSearch(q = query, pageSize = 10, page = position)
            val list = response.body()?.articles ?: emptyList()
            LoadResult.Page(
                data = list,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (list.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}