package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UrlDao {
    @Query("SELECT * FROM UrlEntity ORDER BY timestamp DESC")
    suspend fun getAllUrls(): List<UrlEntity>

    @Insert
    suspend fun insertUrl(url: UrlEntity)
}
