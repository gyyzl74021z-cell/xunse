package com.xunse.app.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "history_records")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val createdAt: Long,
    val taskType: String,
    val resultSummary: String,
)

@Dao
internal interface HistoryDao {
    @Query("SELECT * FROM history_records ORDER BY createdAt DESC, id DESC")
    fun observeRecords(): Flow<List<HistoryRecord>>

    @Insert
    suspend fun insertRecord(record: HistoryRecord)

    @Query("DELETE FROM history_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)
}

@Database(entities = [HistoryRecord::class], version = 1, exportSchema = false)
internal abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var instance: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                HistoryDatabase::class.java,
                "xunse_history.db",
            ).build().also { instance = it }
        }
    }
}

class HistoryRepository(context: Context) {
    private val dao = HistoryDatabase.getInstance(context).historyDao()

    val records: Flow<List<HistoryRecord>> = dao.observeRecords()

    suspend fun addRecord(taskType: String, resultSummary: String) {
        dao.insertRecord(
            HistoryRecord(
                createdAt = System.currentTimeMillis(),
                taskType = taskType,
                resultSummary = resultSummary,
            ),
        )
    }

    suspend fun deleteRecord(id: Long) {
        dao.deleteRecord(id)
    }
}
