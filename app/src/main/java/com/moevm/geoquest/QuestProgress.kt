package com.moevm.geoquest

import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.moevm.geoquest.models.AttractionModel
import com.moevm.geoquest.models.AttractionStatus
import kotlin.math.abs

class QuestProgress() {
    private var questSelected: Boolean = false
    private var previousDistance: Float? = Float.POSITIVE_INFINITY
    private lateinit var questAttractions : MutableList<AttractionModel>

    fun getQuestAttractions() : MutableList<AttractionModel> {
        return this.questAttractions
    }

    fun questGiveUp() {
        questSelected = false
        previousDistance = null
    }

    fun setQuestAttractions(qa: MutableList<AttractionModel>) {
        questSelected = true
        this.questAttractions = qa
        Log.d("location", "attractions object: ${this.questAttractions}")
    }

    fun checkDistanceToObject(location: Location): AttractionStatus {
        if(questSelected) {
            val min = questAttractions.minBy {
                val attractionLocation = Location(LocationManager.GPS_PROVIDER)
                attractionLocation.latitude = it.coordinates.latitude
                attractionLocation.longitude = it.coordinates.longitude
                location.distanceTo(attractionLocation)
            }
            if(min != null) {
                val attractionLocation = Location(LocationManager.GPS_PROVIDER)
                attractionLocation.latitude = min.coordinates.latitude
                attractionLocation.longitude = min.coordinates.longitude
                val distance = location.distanceTo(attractionLocation)
                Log.d("location", "(${min.triggerZone}): $distance >? $previousDistance; min obj: $min")
                var toReturn = AttractionStatus.Nothing
                if(distance <= min.triggerZone){
                    toReturn = AttractionStatus.Success
                }
                else {
                    if (previousDistance != null) {
                        val diff = distance - previousDistance!!
                        toReturn = if (abs(diff) > 0.1 && diff > 0)
                            AttractionStatus.Colder
                        else
                            AttractionStatus.Warmer
                    }
                }
                previousDistance = distance
                return toReturn
            }
        }
        return AttractionStatus.Nothing
    }
}