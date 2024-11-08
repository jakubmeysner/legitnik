package com.jakubmeysner.legitnik.data.parking

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesDispatchersModule {
    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineDispatcher(): CoroutineDispatcher {
        // Run this code when providing an instance of CoroutineScope
        //It shouldn't be hardcoded but for now it's fine
        //explanation https://medium.com/androiddevelopers/create-an-application-coroutinescope-using-hilt-dd444e721528
        return Dispatchers.Default
    }
}
