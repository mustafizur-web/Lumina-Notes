package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "lumina_notes_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate notes on database creation
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                populateInitialNotes(database.noteDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialNotes(dao: NoteDao) {
            val notes = listOf(
                NoteEntity(
                    title = "UI/UX",
                    content = "Explore the glassmorphism approach using 20px blur and soft pastel gradients for card backgrounds. Check that colors match standard Material Design 3 specifications.",
                    category = "Work",
                    colorHex = "#EADDFF",
                    isPinned = true
                ),
                NoteEntity(
                    title = "Team Sync",
                    content = "Q4 roadmap review and budget allocations for new hires. Bring mock drafts and slides.",
                    category = "Personal",
                    colorHex = "#D3E2FF",
                    isPinned = false
                ),
                NoteEntity(
                    title = "Moodboard",
                    content = "Focusing on organic shapes and tactile textures for the 2024 campaign. Pair natural matcha tea aesthetics with premium minimal sketchbooks.",
                    category = "Work",
                    colorHex = "#E2F1E3",
                    isPinned = true
                ),
                NoteEntity(
                    title = "Notepad Feature",
                    content = "A new interactive scratchpad is now available on your Profile page. Quickly jot down ideas, reminders, or scratchpad notes without creating a formal note card. We streamlined the interface by replacing the theme setting with this dedicated utility.",
                    category = "Personal",
                    colorHex = "#FFFDE7",
                    isPinned = false
                )
            )
            notes.forEach { dao.insertNote(it) }
        }
    }
}
