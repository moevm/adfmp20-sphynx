package com.moevm.geoquest

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.moevm.geoquest.models.AttractionModel
import com.moevm.geoquest.models.AttractionStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class QuestProgressUnitTest {

    lateinit var progress: QuestProgress

    @Before
    fun init() {
        progress = QuestProgress()
    }
    
    @Test
    fun testEmptyQuestProgress(){
        progress = QuestProgress()
        assertEquals(progress.getQuestAttractionStartCount(), 0)
        assertEquals(progress.getLastFounded(), null)
        assertEquals(progress.getTravelledDistance(), 0.0, 0.001)
        assertEquals(progress.checkDistanceToObject(Location(LocationManager.GPS_PROVIDER)),  AttractionStatus.Nothing)
    }

    @Test
    fun test_setupQuest(){
        progress.setupQuest(mutableListOf<AttractionModel>(AttractionModel(nm= "Point", coord= LatLng(50.0, 60.0), trig=0.5f)))
        assertEquals(progress.getQuestAttractionStartCount(), 1)
        assertEquals(progress.getLastFounded(), null)
        assertEquals(progress.getTravelledDistance(), 0.0, 0.001)
    }

    @Test
    fun test_getQuestAttractionStartCount(){
        progress.setupQuest(mutableListOf<AttractionModel>(AttractionModel(nm= "Point", coord= LatLng(50.0, 60.0), trig=0.5f)))
        assertEquals(progress.getQuestAttractionStartCount(), 1)
        progress.setupQuest(MutableList<AttractionModel>(5) { i -> AttractionModel(nm= "Point", coord= LatLng(50.0, 60.0+i/10), trig=0.5f) })
        assertEquals(progress.getQuestAttractionStartCount(), 5)
        progress.setupQuest(mutableListOf<AttractionModel>())
        assertEquals(progress.getQuestAttractionStartCount(), 0)
    }

}

