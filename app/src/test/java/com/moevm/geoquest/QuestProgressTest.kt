package com.moevm.geoquest

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.moevm.geoquest.models.AttractionModel
import com.moevm.geoquest.models.AttractionStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class QuestProgressTest {

    lateinit var progress: QuestProgress

    @Before
    fun init() {
        progress = QuestProgress()
    }
    
    @Test
    fun emptyQuestProject(){
        progress = QuestProgress()
        assertEquals(progress.getQuestAttractionStartCount(), 0)
        assertEquals(progress.getLastFounded(), null)
        assertEquals(progress.getTravelledDistance(), 0.0, 0.001)
        assertEquals(progress.checkDistanceToObject(Location(LocationManager.GPS_PROVIDER)),  AttractionStatus.Nothing)
    }

}

