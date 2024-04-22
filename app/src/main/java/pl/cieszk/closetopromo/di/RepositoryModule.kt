package pl.cieszk.closetopromo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cieszk.closetopromo.data.repository.FirestoreRepository
import pl.cieszk.closetopromo.data.service.IFirestoreService

@Module
@InstallIn(SingletonComponent::class) // SingletonComponent is often used for services that are singleton
object RepositoryModule {

    @Provides
    fun provideFirestoreRepository(firestoreService: IFirestoreService): FirestoreRepository {
        return FirestoreRepository(firestoreService)
    }
}