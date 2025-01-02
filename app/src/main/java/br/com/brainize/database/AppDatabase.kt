package br.com.brainize.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.brainize.dao.CarStatusDao
import br.com.brainize.dao.HouseStatusDao
import br.com.brainize.model.CarStatus
import br.com.brainize.model.HouseStatus

@Database(entities = [CarStatus::class, HouseStatus::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carStatusDao(): CarStatusDao
    abstract fun houseStatusDao(): HouseStatusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
