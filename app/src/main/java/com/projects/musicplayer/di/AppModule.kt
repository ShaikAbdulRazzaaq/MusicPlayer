package com.projects.musicplayer.di

import android.content.Context
import coil.ImageLoader
import com.projects.musicplayer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context) =
        ImageLoader(context).defaults.apply {
            placeholder.apply {
                R.drawable.ic_launcher_background
            }
            error.apply {
                R.drawable.ic_launcher_foreground
            }
            diskCachePolicy.readEnabled
        }
}