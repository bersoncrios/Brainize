package br.com.brainize.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brainize.model.HouseStatus

@Dao
interface HouseStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(houseStatus: HouseStatus)

    @Query("SELECT * FROM house_status_table LIMIT 1")
    suspend fun getStatus(): HouseStatus?

    suspend fun insertOrUpdate(houseStatus: HouseStatus) {
        val existingStatus = getStatus()
        if (existingStatus != null) {
            updateStatus(houseStatus)
        } else {
            insert(houseStatus)
        }
    }

    @Update
    suspend fun updateStatus(houseStatus: HouseStatus)
}