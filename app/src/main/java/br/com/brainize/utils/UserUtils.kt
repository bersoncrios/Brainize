package br.com.brainize.utils

import android.util.Log
import br.com.brainize.utils.UserUtils.Companion.SCORE_PARAM
import br.com.brainize.utils.UserUtils.Companion.USER_COLLECTIONS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


suspend fun incrementUserScore(
    value: Int,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore
) {
    val user = auth.currentUser
    if (user != null) {
        try {
            val userDocRef = firestore
                .collection(USER_COLLECTIONS)
                .document(user.uid)
            val userDoc = userDocRef.get().await()
            if (userDoc.exists()) {
                val currentScore = userDoc.getLong(SCORE_PARAM) ?: 0
                val newScore = currentScore + value

                userDocRef.update(SCORE_PARAM, newScore).await()
            } else {
                Log.w("UserUtils", "User document not found")
            }
        } catch (e: Exception) {
            Log.e("UserUtils", "Error incrementing user score", e)
        }
    }
}
class UserUtils {
    companion object {
        const val USER_COLLECTIONS = "users"
        const val SCORE_PARAM = "score"
        const val DONT_HAVE_SCORE = 0
        const val INCREMENT_POINT_NEW_ELEMENT_SIMPLE = 1
        const val INCREMENT_POINT_NEW_ELEMENT_SPECIAL = 2
        const val INCREMENT_POINT_DONE_SCHEDULE = 3
    }
}