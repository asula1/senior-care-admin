package com.seniorcare.watch.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * 앱 전반적인 의존성 주입 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Timber 초기화
     */
    @Provides
    @Singleton
    fun provideTimberInitializer(@ApplicationContext context: Context): Timber.Tree {
        val tree = Timber.DebugTree()
        Timber.plant(tree)
        return tree
    }
}