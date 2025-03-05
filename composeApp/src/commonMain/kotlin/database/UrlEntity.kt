package database

import androidx.room.Entity
import androidx.room.PrimaryKey
import util.getCurrentTimeMillis

@Entity
data class UrlEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val timestamp: Long = getCurrentTimeMillis()
)
