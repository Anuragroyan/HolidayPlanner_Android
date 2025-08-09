package com.example.holidayplanner.data

import com.example.holidayplanner.model.Holiday
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HolidayRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col = firestore.collection("holidays")

    suspend fun createHoliday(holiday: Holiday): Result<String> {
        return try {
            val docRef = col.document()
            val toSave = holiday.copy(id = docRef.id, createdAt = Timestamp.now())
            docRef.set(toSave).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateHoliday(holiday: Holiday): Result<Unit> {
        return try {
            if (holiday.id.isEmpty()) return Result.failure(IllegalArgumentException("Missing id"))
            col.document(holiday.id).set(holiday).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHoliday(id: String): Result<Unit> {
        return try {
            col.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun streamHolidaysWithQuery(query: String): Flow<List<Holiday>> = callbackFlow {
        val listener = col.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val all = snap?.documents?.mapNotNull { it.toObject(Holiday::class.java) } ?: emptyList()
                val filtered = if (query.isBlank()) all else all.filter { it.title.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true) }
                trySend(filtered)
            }
        awaitClose { listener.remove() }
    }

}