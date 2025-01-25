package br.com.brainize

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.brainize.navigation.AppNavigation
import br.com.brainize.ui.theme.BrainizeTheme
import br.com.brainize.viewmodel.CarViewModel
import br.com.brainize.viewmodel.CollectionViewModel
import br.com.brainize.viewmodel.ConfigurationsViewModel
import br.com.brainize.viewmodel.HouseViewModel
import br.com.brainize.viewmodel.LoginViewModel
import br.com.brainize.viewmodel.NotesViewModel
import br.com.brainize.viewmodel.ProfileViewModel
import br.com.brainize.viewmodel.RemoteConfigViewModel
import br.com.brainize.viewmodel.ScheduleViewModel
import br.com.brainize.viewmodel.SocialViewModel
import br.com.brainize.viewmodel.factories.CarViewModelFactory
import br.com.brainize.viewmodel.factories.CollectionViewModelFactory
import br.com.brainize.viewmodel.factories.ConfigurationViewModelFactory
import br.com.brainize.viewmodel.factories.HouseViewModelFactory
import br.com.brainize.viewmodel.factories.LoginViewModelFactory
import br.com.brainize.viewmodel.factories.NotesViewModelFactory
import br.com.brainize.viewmodel.factories.ProfileViewModelFactory
import br.com.brainize.viewmodel.factories.RemoteConfigViewModelFactory
import br.com.brainize.viewmodel.factories.ScheduleViewModelFactory
import br.com.brainize.viewmodel.factories.SocialViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val carViewModel: CarViewModel by viewModels {
            CarViewModelFactory()
        }
        val houseViewModel: HouseViewModel by viewModels {
            HouseViewModelFactory()
        }
        val loginViewModel: LoginViewModel by viewModels {
            LoginViewModelFactory()
        }
        val notesViewModel: NotesViewModel by viewModels {
            NotesViewModelFactory()
        }
        val scheduleViewModel: ScheduleViewModel by viewModels {
            ScheduleViewModelFactory()
        }
        val configurationViewModel: ConfigurationsViewModel by viewModels {
            ConfigurationViewModelFactory()
        }
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory()
        }
        val collectionViewModel: CollectionViewModel by viewModels {
            CollectionViewModelFactory()
        }
        val remoteConfigViewModel: RemoteConfigViewModel by viewModels {
            RemoteConfigViewModelFactory()
        }
        val socialViewModel: SocialViewModel by viewModels {
            SocialViewModelFactory()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Lidar com o erro
                return@addOnCompleteListener
            }

              val token = task.result

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Usuário logado, salvar o token no Firestore
                val userId = currentUser.uid
                val db = FirebaseFirestore.getInstance()
                val tokensCollection = db.collection("tokens")
                val tokenDocument = tokensCollection.document(userId)
                val tokenData = hashMapOf("token" to token)
                tokenDocument.set(tokenData)
                    .addOnSuccessListener {
                        Log.i("Push Notification", "Token salvo com sucesso")
                    }
                    .addOnFailureListener { e ->
                        // Lidar com o erro de autenticação (opcional)
                        if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) { Log.w("Push Notification", "Usuário não autenticado, token não salvo.")
                        } else {
                            Log.e("Push Notification", "Falha ao salvar token: $e")
                        }
                    }
            } else {
                // Usuário não logado, não salvar o token
                Log.w("Push Notification", "Usuário não logado, token não salvo.")
                // Exibir uma mensagem ou tomar outra ação apropriada
            }
        }

        setContent {

            BrainizeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrainizeApp(
                        carViewModel = carViewModel,
                        houseViewModel = houseViewModel,
                        loginViewModel = loginViewModel,
                        notesViewModel = notesViewModel,
                        scheduleViewModel = scheduleViewModel,
                        configurationViewModel = configurationViewModel,
                        profileViewModel = profileViewModel,
                        collectionViewModel = collectionViewModel,
                        remoteConfigViewModel = remoteConfigViewModel,
                        socialViewModel = socialViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun BrainizeApp(
    carViewModel: CarViewModel,
    houseViewModel: HouseViewModel,
    loginViewModel: LoginViewModel,
    notesViewModel: NotesViewModel,
    scheduleViewModel: ScheduleViewModel,
    configurationViewModel: ConfigurationsViewModel,
    profileViewModel: ProfileViewModel,
    collectionViewModel: CollectionViewModel,
    remoteConfigViewModel: RemoteConfigViewModel,
    socialViewModel: SocialViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppNavigation(
                carViewModel = carViewModel,
                houseViewModel = houseViewModel,
                loginViewmodel = loginViewModel,
                notesViewModel = notesViewModel,
                scheduleViewModel = scheduleViewModel,
                configurationViewModel = configurationViewModel,
                profileViewModel = profileViewModel,
                collectionViewModel = collectionViewModel,
                remoteConfigViewModel = remoteConfigViewModel,
                socialViewModel = socialViewModel
            )
        }
    }
}