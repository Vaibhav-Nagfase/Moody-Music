package com.example.moodymusic.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moodymusic.model.MoodLog
import com.example.moodymusic.model.MoodType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class MoodRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserEmail(): String? = auth.currentUser?.email

    @RequiresApi(Build.VERSION_CODES.O)

    suspend fun saveMood(mood: MoodType) {
        val email = getUserEmail() ?: return

        val today = LocalDate.now().toString()  // yyyy-MM-dd

        val moodLog = MoodLog(
            date = today,
            mood = mood.name
        )

        val collection = firestore.collection("users")
            .document(email)
            .collection("moodLogs")

        // 🔍 Check if today's mood already exists
        val snapshot = collection
            .whereEqualTo("date", today)
            .get()
            .await()

        if (snapshot.isEmpty) {
            // ➕ No mood for today ➔ Add a new one
            collection.add(moodLog).await()
        } else {
            // ✏️ Mood already exists ➔ Update it
            val documentId = snapshot.documents.first().id
            collection.document(documentId)
                .set(moodLog)
                .await()
        }
    }

    suspend fun fetchAllMoodLogs(): List<MoodLog> {
        val email = getUserEmail() ?: return emptyList()

        val snapshot = firestore.collection("users")
            .document(email)
            .collection("moodLogs")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val date = doc.getString("date")
            val mood = doc.getString("mood")
            if (date != null && mood != null) MoodLog(date, mood) else null
        }
    }
}
