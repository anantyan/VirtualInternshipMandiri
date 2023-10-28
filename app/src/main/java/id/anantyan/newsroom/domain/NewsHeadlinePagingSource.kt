package id.anantyan.newsroom.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anantyan.newsroom.data.remote.api.NewsApi
import id.anantyan.newsroom.data.remote.model.ArticlesItem
import retrofit2.HttpException
import java.io.IOException

class NewsHeadlinePagingSource(
    private val newsApi: NewsApi
) : PagingSource<Int, ArticlesItem>() {
    override fun getRefreshKey(state: PagingState<Int, ArticlesItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        val position = params.key ?: 3
        return try {
            val response = newsApi.getHeadline(pageSize = 10, page = position)
            val list = response.body()?.articles ?: emptyList()
            LoadResult.Page(
                data = list,
                prevKey = if (position == 3) null else position - 1,
                nextKey = if (list.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            Log.d("DEBUGG", e.toString())
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("DEBUGG-1", e.toString())
            LoadResult.Error(e)
        }
    }
}