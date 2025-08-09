package com.example.holidayplanner.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp

data class Holiday(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val location: String = "",
    val notes: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val createdAt: Timestamp? = null
)
