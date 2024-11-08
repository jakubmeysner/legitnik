package com.jakubmeysner.legitnik.data.parking

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {
    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        //It shouldn't be hardcoded but for now it's fine
        //explanation https://medium.com/androiddevelopers/create-an-application-coroutinescope-using-hilt-dd444e721528
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
