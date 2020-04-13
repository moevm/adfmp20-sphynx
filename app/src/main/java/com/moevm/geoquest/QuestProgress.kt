package com.moevm.geoquest

import android.location.Location
import android.location.LocationManager
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE
import android.util.Log
import com.moevm.geoquest.models.AttractionModel
import com.moevm.geoquest.models.AttractionStatus
import kotlin.math.abs


//TODO: Save quest progress with change fragment
class QuestProgress() : Parcelable {
    private var questSelected: Boolean = false
    private var previousDistance: Float? = Float.POSITIVE_INFINITY
    private var questAttractionStartCount: Int = 0
    private lateinit var questAttractions : MutableList<AttractionModel>
    private var completedTimer: Int = 0

    private var lastFounded: AttractionModel? = null

    constructor(parcel: Parcel) : this() {
        questSelected = parcel.readByte() != 0.toByte()
        previousDistance = parcel.readValue(Float::class.java.classLoader) as? Float
        completedTimer = parcel.readInt()
        parcel.readTypedList(questAttractions, AttractionModel.CREATOR)
    }

    fun getQuestAttractionStartCount() : Int {
        return this.questAttractionStartCount
    }

    fun getLastFounded(): AttractionModel? {
        return lastFounded
    }

    fun questGiveUp() {
        questSelected = false
        previousDistance = null
        completedTimer = 0
        questAttractionStartCount = 0
    }

    fun setupQuest(qa: MutableList<AttractionModel>, qi:Int, uId: String) {
        this.questSelected = true
        this.questAttractions = qa
        this.questAttractionStartCount = qa.size
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
                Log.d("quest_action", "{${questAttractions.size}}: (${min.triggerZone}): $distance >? $previousDistance; min obj: $min")
                var toReturn = AttractionStatus.Nothing
                if(distance <= min.triggerZone){
                    toReturn = AttractionStatus.Success
                    Log.d("quest_action", "completedCount: $completedTimer")
                    if(completedTimer < 3)
                        completedTimer++
                    else{
                        lastFounded = min
                        questAttractions.remove(min)
                        Log.d("quest_action", " quest coml?: $questAttractionStartCount, ${questAttractions.size}")
                        if(questAttractions.size == 0)
                            toReturn = AttractionStatus.QuestCompleted
                        completedTimer = 0
                    }
                }
                else {
                    if (previousDistance != null) {
                        val diff = distance - previousDistance!!
                        toReturn = if (abs(diff) > 0.5 && diff > 0) {
                            Log.d("quest_action", "restart timer")
                            completedTimer = 0
                            AttractionStatus.Colder
                        }
                        else
                            AttractionStatus.Warmer
                    }
                }
                previousDistance = distance
                return toReturn
            }
            else {
            Log.d("quest_action", "Quest completed")
            }
        }
        return AttractionStatus.Nothing
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        Log.d("parcelable", "QuestProgress: call writeToParcel")
        parcel.writeByte(if (questSelected) 1 else 0)
        parcel.writeValue(previousDistance)
        parcel.writeInt(completedTimer)
        parcel.writeTypedList(questAttractions)// хз что это за флаг
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestProgress> {
        override fun createFromParcel(parcel: Parcel): QuestProgress {
            Log.d("parcelable", "QuestProgress: call createFromParcel")
            return QuestProgress(parcel)
        }

        override fun newArray(size: Int): Array<QuestProgress?> {
            Log.d("parcelable", "QuestProgress: call newArray")
            return arrayOfNulls(size)
        }
    }
}