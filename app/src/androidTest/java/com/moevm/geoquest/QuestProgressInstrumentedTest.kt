package com.moevm.geoquest

import android.location.Location
import android.location.LocationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.moevm.geoquest.models.AttractionModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class QuestProgressInstrumentedTest {

    lateinit var progress: QuestProgress

    @Before
    fun init() {
        progress = QuestProgress()
    }


    @Test
    fun test_getTravelledDistance(){
        progress.setupQuest(MutableList<AttractionModel>(1) { _ -> AttractionModel(nm= "Point", coord= LatLng(50.0, 60.0), trig=0.5f) })
        var location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 55.0
        location.longitude = 65.0
        progress.checkDistanceToObject(location)
        assertEquals(progress.getTravelledDistance(), 0.0, 0.01)

        location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 50.0
        location.longitude = 65.0
        progress.checkDistanceToObject(location)
        assertEquals(progress.getTravelledDistance(), 556383.25, 0.01)

        location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = 50.0
        location.longitude = 60.0
        progress.checkDistanceToObject(location)
        assertEquals(progress.getTravelledDistance(), 914795.25, 0.01)
    }

}