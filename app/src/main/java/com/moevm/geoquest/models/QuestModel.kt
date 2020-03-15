package com.moevm.geoquest.models

enum class QuestStatus {
    completed,
    inProgress,
    dropped,
    nothing
}

data class QuestModel(
    val id: Int,
    val name: String,
    val status: QuestStatus,
    val location: String,
    val imageUrl: String
)

