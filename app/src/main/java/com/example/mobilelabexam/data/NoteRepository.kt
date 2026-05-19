package com.example.mobilelabexam.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val quoteApiService: QuoteApiService
) {
    val unlockedNotes: Flow<List<NoteEntity>> = noteDao.getUnlockedNotes()
    val lockedNotes: Flow<List<NoteEntity>> = noteDao.getLockedNotes()

    fun getNoteById(id: Int): Flow<NoteEntity?> {
        return noteDao.getNoteById(id)
    }

    suspend fun insert(note: NoteEntity) {
        noteDao.insertNote(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    private val fallbackQuotes = listOf(
        QuoteResponse("Your time is limited, so don't waste it living someone else's life.", "Steve Jobs"),
        QuoteResponse("The only way to do great work is to love what you do.", "Steve Jobs"),
        QuoteResponse("If you look at what you have in life, you'll always have more.", "Oprah Winfrey"),
        QuoteResponse("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
        QuoteResponse("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        QuoteResponse("Believe you can and you're halfway there.", "Theodore Roosevelt"),
        QuoteResponse("Your vision will become clear only when you look into your heart.", "Carl Jung")
    )

    suspend fun getRandomQuote(): Result<QuoteResponse> {
        return try {
            val response = quoteApiService.getRandomQuote()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(fallbackQuotes.random())
            }
        } catch (e: Exception) {
            Result.success(fallbackQuotes.random())
        }
    }
}
