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

    @Test
    fun testQuestProject(){
        progress.setupQuest(MutableList<AttractionModel>(1) { _ -> AttractionModel(nm= "Point", coord= LatLng(50.0, 60.0), trig=0.5f) })
        assertEquals(progress.getQuestAttractionStartCount(), 1)
        assertEquals(progress.getLastFounded(), null)
        assertEquals(progress.getTravelledDistance(), 0.0, 0.001)
        // bad assert, cauz' <Method setLatitude in android.location.Location not mocked> in QuestProgress.kt:62 - it using Android API and etc.
        // also as setLongitude
        //assertEquals(progress.checkDistanceToObject(Location(LocationManager.GPS_PROVIDER)),  AttractionStatus.Warmer)
    }

}

