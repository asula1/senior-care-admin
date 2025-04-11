package com.seniorcare.watch.di

import com.seniorcare.watch.data.repository.EmergencyRepositoryImpl
import com.seniorcare.watch.domain.repository.EmergencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 레포지토리 의존성 주입 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * EmergencyRepository 의존성 주입
     */
    @Binds
    @Singleton
    abstract fun bindEmergencyRepository(
        emergencyRepositoryImpl: EmergencyRepositoryImpl
    ): EmergencyRepository
}