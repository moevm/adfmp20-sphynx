package com.moevm.geoquest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordActivityUnitTest {

    @Test
    fun test_timeToHoursMinutes() {
        assertEquals(timeToHoursMinutes(0), "0 мин.")
        assertEquals(timeToHoursMinutes(50), "50 мин.")
        var str = timeToHoursMinutes(555)
        assertTrue(str == "9.3 ч." || str == "9,3 ч.")
        str = timeToHoursMinutes(600)
        assertTrue(str == "10.0 ч." || str == "10,0 ч.")
        assertEquals(timeToHoursMinutes(-500), "-500 мин.")     // for fun
        assertEquals(timeToHoursMinutes(-50), "-50 мин.")     // for fun
    }

}