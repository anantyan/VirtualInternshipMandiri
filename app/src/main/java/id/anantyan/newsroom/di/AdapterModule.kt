package id.anantyan.newsroom.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import id.anantyan.newsroom.presentation.home.HomeAllNewsAdapter
import id.anantyan.newsroom.presentation.home.HomeSearchAdapter
import id.anantyan.newsroom.presentation.home.HomeHeadlineAdapter
import id.anantyan.newsroom.presentation.load_more.LoadMoreAdapter

@Module
@InstallIn(ActivityComponent::class)
object AdapterModule {
    @Provides
    fun provideHeadline(): HomeHeadlineAdapter {
        return HomeHeadlineAdapter()
    }

    @Provides
    fun provideSearch(): HomeSearchAdapter {
        return HomeSearchAdapter()
    }

    @Provides
    fun provideAllNews(): HomeAllNewsAdapter {
        return HomeAllNewsAdapter()
    }

    @Provides
    fun provideLoadMoer(): LoadMoreAdapter {
        return LoadMoreAdapter()
    }
}