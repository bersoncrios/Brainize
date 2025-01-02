package br.com.brainize.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_status_table")
data class CarStatus(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val windowClosed: Boolean,
    val doorClosed: Boolean
)