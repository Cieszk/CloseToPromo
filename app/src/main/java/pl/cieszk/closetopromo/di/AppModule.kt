package pl.cieszk.closetopromo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cieszk.closetopromo.data.repository.FirestoreRepository
import pl.cieszk.closetopromo.data.service.FirestoreServiceImpl
import pl.cieszk.closetopromo.data.service.IFirestoreService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestoreService(): IFirestoreService = FirestoreServiceImpl()

    @Provides
    @Singleton
    fun provideFirestoreRepository(firestoreService: IFirestoreService): FirestoreRepository {
        return FirestoreRepository(firestoreService)
    }
}