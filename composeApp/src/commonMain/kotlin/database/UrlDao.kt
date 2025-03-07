package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UrlDao {
    @Query("SELECT * FROM UrlEntity ORDER BY timestamp DESC")
    suspend fun getAllUrls(): List<UrlEntity>


    @Query("SELECT * FROM UrlEntity WHERE id = :id")
    suspend fun getUrl(id: Int): UrlEntity?

    @Insert
    suspend fun insertUrl(url: UrlEntity)
}
