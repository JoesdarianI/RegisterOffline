package joes.app.registeroffline.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import joes.app.registeroffline.data.model.Member
import joes.app.registeroffline.data.model.User

@Database(entities = [User::class, Member::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun memberDao(): MemberDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "user_database"
                )
                .fallbackToDestructiveMigration()
                .build().also { Instance = it }
            }
        }
    }
}
