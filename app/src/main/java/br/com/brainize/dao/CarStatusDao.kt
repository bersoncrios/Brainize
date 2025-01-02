package br.com.brainize.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brainize.model.CarStatus

@Dao
interface CarStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carStatus: CarStatus)

    @Query("SELECT * FROM car_status_table LIMIT 1")
    suspend fun getStatus(): CarStatus?

    suspend fun insertOrUpdate(carStatus: CarStatus) {
        val existingStatus = getStatus()
        if (existingStatus != null) {
            updateStatus(carStatus)
        } else {
            insert(carStatus)
        }
    }

    @Update
    suspend fun updateStatus(carStatus: CarStatus)
}