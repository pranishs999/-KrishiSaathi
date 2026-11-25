package com.krishisaathi.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies package and entry points for com.krishisaathi.app.
 */
class AppConfigTest {

    @Test
    fun entryPointsResideInRootPackage() {
        assertEquals("com.krishisaathi.app", KrishiSaathiApp::class.java.name.substringBeforeLast('.'))
        assertEquals("com.krishisaathi.app", MainActivity::class.java.name.substringBeforeLast('.'))
    }

    @Test
    fun applicationClassNameIsCorrect() {
        assertTrue(KrishiSaathiApp::class.java.simpleName == "KrishiSaathiApp")
    }
}
