package id.anantyan.newsroom.domain

import androidx.paging.PagingData
import id.anantyan.newsroom.common.UIState
import id.anantyan.newsroom.data.remote.model.ArticlesItem
import kotlinx.coroutines.flow.Flow

class NewsUseCase(
    private val newsRepository: NewsRepository
) {
    fun executeGetHeadline(): Flow<UIState<List<ArticlesItem>>> = newsRepository.getHeadline()
    fun executeGetAllNews(): Flow<UIState<List<ArticlesItem>>> = newsRepository.getAllNews()
    fun executeGetLoadMore(): Flow<PagingData<ArticlesItem>> = newsRepository.getLoadMoreNews()
    fun executeGetSearchNews(query: String?): Flow<PagingData<ArticlesItem>> = newsRepository.getSearchNews(query)
}