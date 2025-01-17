package br.com.brainize.model

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val type: String = "Lembrete",
    val dueDate: String? = null,
    val dueTime: String? = null,
    val sequentialId: Int = 0,
    val tag: String = ""
)
