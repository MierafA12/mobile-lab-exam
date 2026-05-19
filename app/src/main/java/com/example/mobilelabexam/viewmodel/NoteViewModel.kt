package com.example.mobilelabexam.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilelabexam.data.NoteDatabase
import com.example.mobilelabexam.data.NoteEntity
import com.example.mobilelabexam.data.NoteRepository
import com.example.mobilelabexam.data.QuoteApiService
import com.example.mobilelabexam.data.QuoteResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val unlockedNotes: StateFlow<List<NoteEntity>>
    val lockedNotes: StateFlow<List<NoteEntity>>

    private val _quoteState = MutableStateFlow<Result<QuoteResponse>?>(null)
    val quoteState: StateFlow<Result<QuoteResponse>?> = _quoteState.asStateFlow()

    init {
        val database = NoteDatabase.getDatabase(application)
        val noteDao = database.noteDao()
        val quoteApiService = QuoteApiService.create()
        repository = NoteRepository(noteDao, quoteApiService)

        unlockedNotes = repository.unlockedNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        lockedNotes = repository.lockedNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        fetchRandomQuote()
    }

    fun fetchRandomQuote() {
        viewModelScope.launch {
            _quoteState.value = null // Set loading
            val result = repository.getRandomQuote()
            _quoteState.value = result
        }
    }

    fun getNoteById(id: Int) = repository.getNoteById(id)

    fun saveNote(id: Int = 0, title: String, content: String, isLocked: Boolean) {
        viewModelScope.launch {
            val note = NoteEntity(
                id = id,
                title = title,
                content = content,
                isLocked = isLocked,
                timestamp = System.currentTimeMillis()
            )
            if (id == 0) {
                repository.insert(note)
            } else {
                repository.update(note)
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}
