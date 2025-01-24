package br.com.brainize.model

import java.util.Date
import java.util.UUID

data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val time: String = "",
    val date: Date = Date(),
    val name: String = "",
    val priority: String = "",
    val tag: String = "",
    val done: Boolean = false
)