package database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), DB_NAME)

    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
