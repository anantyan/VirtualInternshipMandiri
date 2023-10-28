package id.anantyan.newsroom.data.remote.api

import id.anantyan.newsroom.common.Constant.API_KEY
import id.anantyan.newsroom.data.remote.model.NewsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("everything")
    suspend fun getSearch(
        @Query("q") q: String? = null,
        @Query("sortBy") sortBy: String? = "popularity",
        @Query("pageSize") pageSize: Int? = 10,
        @Query("page") page: Int? = 1,
        @Query("apiKey") apiKey: String? = API_KEY
    ): Response<NewsModel>

    @GET("top-headlines")
    suspend fun getHeadline(
        @Query("country") country: String? = "us",
        @Query("pageSize") pageSize: Int? = 10,
        @Query("page") page: Int? = 1,
        @Query("apiKey") apiKey: String? = API_KEY
    ): Response<NewsModel>
}