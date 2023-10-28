package id.anantyan.newsroom.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.anantyan.newsroom.data.remote.api.NewsApi
import id.anantyan.newsroom.domain.NewsRepository
import id.anantyan.newsroom.domain.NewsRepositoryImpl
import id.anantyan.newsroom.domain.NewsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Singleton
    @Provides
    fun provideNewsRepository(newsApi: NewsApi): NewsRepository {
        return NewsRepositoryImpl(newsApi)
    }

    @Singleton
    @Provides
    fun provideNewsUseCase(newsRepository: NewsRepository): NewsUseCase {
        return NewsUseCase(newsRepository)
    }
}