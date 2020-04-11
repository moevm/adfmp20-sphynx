package com.moevm.geoquest.models

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

