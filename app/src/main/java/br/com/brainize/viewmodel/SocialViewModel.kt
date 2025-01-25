package br.com.brainize.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SocialViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    data class UserListItem(val id: String, val completeName: String, val username: String)

    fun searchUserAndAddFriend(query: String): Flow<List<UserListItem>> = flow {
        val usersRef = firestore.collection(USERS_COLLECTION)

        val querySnapshot = usersRef
            .whereEqualTo("username", query)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            val nameSnapshot = usersRef
                .whereEqualTo("completeName", query)
                .get()
                .await()

            if (!nameSnapshot.isEmpty) {
                emit(nameSnapshot.toUserListItemList())
            }
        } else {
            emit(querySnapshot.toUserListItemList())
        }
    }

    private fun QuerySnapshot.toUserListItemList(): List<UserListItem> =
        documents.mapNotNull { document ->
            val completeName = document.getString("completeName")?.lowercase()
            val username = document.getString("username")?.lowercase()
            if (completeName != null && username != null) {
                UserListItem(document.id, completeName, username)
            } else {
                null
            }
        }
    suspend fun addUserToFriendsList(currentUser: FirebaseUser, friendId: String) {
        val userRef = firestore.collection(USERS_COLLECTION).document(currentUser.uid)

        // Obtém a lista de amigos atual
        val userSnapshot = userRef.get().await()
        val currentFriends = userSnapshot.get("friends") as? List<String> ?: emptyList()

        // Adiciona o novo amigo à lista
        val updatedFriends = currentFriends + friendId

        // Atualiza a lista de amigos no Firestore
        userRef.update("friends", updatedFriends).await()
    }

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}