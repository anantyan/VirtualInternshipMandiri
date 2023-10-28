package id.anantyan.newsroom.domain

import androidx.paging.PagingData
import id.anantyan.newsroom.common.UIState
import id.anantyan.newsroom.data.remote.model.ArticlesItem
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getHeadline(): Flow<UIState<List<ArticlesItem>>>
    fun getAllNews(): Flow<UIState<List<ArticlesItem>>>
    fun getLoadMoreNews(): Flow<PagingData<ArticlesItem>>
    fun getSearchNews(query: String?): Flow<PagingData<ArticlesItem>>
}