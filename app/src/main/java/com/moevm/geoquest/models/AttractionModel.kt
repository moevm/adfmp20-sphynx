package com.moevm.geoquest.models

import com.google.android.gms.maps.model.LatLng

enum class AttractionStatus {
    Warmer,
    Colder,
    Success,
    Nothing,
    QuestCompleted
}

data class AttractionModel(
    val name: String,
    val coordinates: LatLng,
    val triggerZone: Float,
    var completed: Boolean = false
)
