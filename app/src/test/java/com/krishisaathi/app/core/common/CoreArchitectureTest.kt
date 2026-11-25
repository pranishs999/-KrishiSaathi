package com.krishisaathi.app.core.common

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreArchitectureTest {

    @Test
    fun defaultDispatcherProviderMapsStandardDispatchers() {
        val provider: DispatcherProvider = DefaultDispatcherProvider()
        assertSame(Dispatchers.Main, provider.main)
        assertSame(Dispatchers.IO, provider.io)
        assertSame(Dispatchers.Default, provider.default)
    }

    @Test
    fun appResultModelsLoadingSuccessAndErrorStates() {
        val loading: AppResult<String> = AppResult.Loading
        val success: AppResult<String> = AppResult.Success("Paddy")
        val error: AppResult<String> = AppResult.Error("Offline")

        assertTrue(loading is AppResult.Loading)
        assertEquals("Paddy", (success as AppResult.Success).data)
        assertEquals("Offline", (error as AppResult.Error).message)
    }
}
