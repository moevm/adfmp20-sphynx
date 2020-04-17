package com.moevm.geoquest

import com.moevm.geoquest.RecordsActivity
import org.junit.Assert.assertEquals
import org.junit.Test

class RecordActivityUnitTest {

    @Test
    fun test_timeToHoursMinutes() {
        assertEquals(timeToHoursMinutes(0), "0 мин.")
        assertEquals(timeToHoursMinutes(50), "50 мин.")
        assertEquals(timeToHoursMinutes(555), "9.3 ч.")
        assertEquals(timeToHoursMinutes(600), "10.0 ч.")
        assertEquals(timeToHoursMinutes(-500), "-500 мин.")     // for fun
        assertEquals(timeToHoursMinutes(-50), "-50 мин.")     // for fun
    }

}