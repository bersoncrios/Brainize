package br.com.brainize.model

import java.util.UUID

data class User (
    val uid: String = UUID.randomUUID().toString(),
    val email: String = "",
    val completeName: String = "",
    val username: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false,
    val userIsPremium: Boolean = false,
    val score: Int = 0,
    val gender: String = "",
    val birthday: String = "",
    val friends: List<String> = emptyList(),
)
