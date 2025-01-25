package br.com.brainize.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SocialViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private fun getCurrentUser() = auth.currentUser
    fun hasLoggedUser(): Boolean = getCurrentUser() != null

    fun searchUserAndAddFriend(query: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUser() ?: return@launch
            val usersRef = firestore.collection(USERS_COLLECTION)

            val querySnapshot = usersRef
                .whereEqualTo("username", query)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                val nameSnapshot = usersRef
                    .whereEqualTo("name", query)
                    .get()
                    .await()

                if (nameSnapshot.isEmpty) {
                    // Nenhum usuário encontrado
                    // Trateo caso de usuário não encontrado (ex: exibir mensagem de erro)
                    return@launch
                } else {
                    // Usuário encontrado por nome
                    addUserToFriendsList(currentUser, nameSnapshot.documents[0].id)
                }
            } else {
                // Usuário encontrado por username
                addUserToFriendsList(currentUser, querySnapshot.documents[0].id)
            }
        }
    }

    private suspend fun addUserToFriendsList(currentUser: FirebaseUser, friendId: String) {
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