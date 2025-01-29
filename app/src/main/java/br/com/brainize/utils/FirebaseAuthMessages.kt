package br.com.brainize.utils

import android.content.Context
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import br.com.brainize.R
import br.com.brainize.states.LoginState

@Composable
fun LoginErrorDisplay(loginState: LoginState, context: Context) {
    when (loginState) {
        is LoginState.Loading -> CircularProgressIndicator()
        is LoginState.Error -> {
            val errorMessage = translateFirebaseError(loginState.message, context)
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }
        else -> {}
    }
}

fun translateFirebaseError(firebaseMessage: String, context: Context): String {
    return when (firebaseMessage) {
        context.getString(R.string.given_string_is_empty_or_null) ->
            context.getString(R.string.por_favor_preencha_todos_os_campos)
        context.getString(R.string.the_supplied_auth_credential_is_incorrect_malformed_or_has_expired) ->
            context.getString(R.string.email_ou_senha_incorretos_verifique_suas_credenciais)
        context.getString(R.string.a_network_error_such_as_timeout_interrupted_connection_or_unreachable_host_has_occurred) ->
            context.getString(R.string.erro_de_rede_verifique_sua_conex_o_com_a_internet)
        context.getString(R.string.there_is_no_user_record_corresponding_to_this_identifier_the_user_may_have_been_deleted) ->
            context.getString(R.string.usu_rio_n_o_encontrado_verifique_suas_credenciais)
        context.getString(R.string.the_email_address_is_already_in_use_by_another_account) ->
            context.getString(R.string.este_email_j_est_cadastrado)
        context.getString(R.string.password_should_be_at_least_6_characters) ->
            context.getString(R.string.a_senha_deve_ter_pelo_menos_6_caracteres)
        "O username não pode conter espaços" -> "O username não pode conter espaços"
        else -> context.getString(R.string.ocorreu_um_erro_inesperado_tente_novamente)
    }}