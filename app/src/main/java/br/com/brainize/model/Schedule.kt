package br.com.brainize.model

import java.util.UUID

data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val time: String = "",
    val date: String = "",
    val name: String = "",
    val priority: String = "",
    val tag: String = ""
)