package com.moevm.geoquest.models

import com.google.android.gms.maps.model.LatLng

enum class QuestStatus {
    Completed,
    InProgress,
    Nothing
}

data class QuestModel(
    val id: Int,
    val name: String,
    val location: String,
    val imageUrl: String
)

data class AttractionModel(
    val point: LatLng,
    val triggerZone: Float
)
