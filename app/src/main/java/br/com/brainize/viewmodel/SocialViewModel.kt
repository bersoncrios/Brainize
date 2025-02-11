package br.com.brainize.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brainize.model.FriendListItem
import br.com.brainize.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SocialViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    suspend fun getUserDocument(userId: String) =
        firestore.collection(USERS_COLLECTION).document(userId).get().await()

    private val _friendData = MutableStateFlow(User())
    val friendData: StateFlow<User> = _friendData

    fun searchUserAndAddFriend(query: String): Flow<List<FriendListItem>> = flow {
        if (getCurrentUser() == null) {
            emit(emptyList())
            return@flow
        }

        val usersRef = firestore.collection(USERS_COLLECTION)

        val usernameQuery = usersRef
            .whereEqualTo("username", query)
            .whereNotEqualTo(com.google.firebase.firestore.FieldPath.documentId(), getCurrentUser()?.uid)

        val completeNameQuery = usersRef
            .whereEqualTo("completeName", query)
            .whereNotEqualTo(com.google.firebase.firestore.FieldPath.documentId(), getCurrentUser()?.uid)

        val usernameSnapshot = usernameQuery.get().await()
        val completeNameSnapshot = completeNameQuery.get().await()

        val combinedResults = mutableListOf<FriendListItem>()
        combinedResults.addAll(usernameSnapshot.toUserListItemList())
        combinedResults.addAll(completeNameSnapshot.toUserListItemList())

        val uniqueResults = combinedResults.distinctBy { it.id }

        emit(uniqueResults)
    }

    private fun QuerySnapshot.toUserListItemList(): List<FriendListItem> =
        documents.mapNotNull { document ->
            val completeName = document.getString("completeName")?.lowercase()
            val username = document.getString("username")?.lowercase()
            if (completeName != null && username != null) {
                FriendListItem(document.id, completeName, username, "")
            } else {
                null
            }
        }
    suspend fun addUserToFriendsList(currentUser: FirebaseUser, friendId: String) {
        val userRef = firestore.collection(USERS_COLLECTION).document(currentUser.uid)

        val userSnapshot = userRef.get().await()
        val currentFriends = userSnapshot.get("friends") as? List<String> ?: emptyList()

        if (currentFriends.contains(friendId)) {
            return
        }

        val updatedFriends = currentFriends + friendId

        userRef.update("friends", updatedFriends).await()
    }

    fun getFriendsList(currentUser: FirebaseUser): Flow<List<FriendListItem>> = flow {
        val userRef = firestore.collection(USERS_COLLECTION).document(currentUser.uid)
        val userSnapshot = userRef.get().await()
        val friendIds = userSnapshot.get("friends") as? List<String> ?: emptyList()

        val friendsList = mutableListOf<FriendListItem>()
        for (friendId in friendIds) {
            val friendSnapshot = firestore.collection(USERS_COLLECTION).document(friendId).get().await()
            val completeName = friendSnapshot.getString("completeName")
            val username = friendSnapshot.getString("username")
            val email = friendSnapshot.getString("email")

            if (completeName != null && username != null && email != null) {
                friendsList.add(FriendListItem(friendId, completeName, username, email))
            }
        }

        emit(friendsList)
    }



    fun getFriendById(friendId: String) {
        viewModelScope.launch {
            try {
                val friendSnapshot = firestore
                    .collection(USERS_COLLECTION)
                    .document(friendId)
                    .get()
                    .await()
                if (friendSnapshot.exists()) {
                    val friend = friendSnapshot.toObject(User::class.java)
                    if (friend != null) {
                        val friendListItem = User(
                            uid =  friend.uid,
                            completeName = friend.completeName,
                            username = friend.username,
                            email = friend.email
                        )
                        _friendData.value = friendListItem
                    } else {
                        _friendData.value = User()
                    }
                } else {
                    _friendData.value = User()
                }
            } catch (e: Exception) {
                _friendData.value = User()
            }
        }
    }
    companion object {
        private const val USERS_COLLECTION = "users"
    }
}