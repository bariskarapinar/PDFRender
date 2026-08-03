package com.myapp.pdfrender.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.pdfrender.data.PdfRepository
import com.myapp.pdfrender.data.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PdfUiState {
    object Idle : PdfUiState()
    object Loading : PdfUiState()
    data class Success(val pageCount: Int, val fileName: String) : PdfUiState()
    data class Error(val message: String) : PdfUiState()
}

class PdfViewModel(private val repository: PdfRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PdfUiState>(PdfUiState.Idle)
    val uiState: StateFlow<PdfUiState> = _uiState.asStateFlow()

    private val _pages = MutableStateFlow<Map<Int, Bitmap>>(emptyMap())
    val pages: StateFlow<Map<Int, Bitmap>> = _pages.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // Reduce query length requirement to 1 to make it more reactive
        if (query.isNotEmpty()) {
            search(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            val results = repository.searchText(query)
            _searchResults.value = results
        }
    }

    fun loadPdf(uri: Uri, fileName: String) {
        viewModelScope.launch {
            _uiState.value = PdfUiState.Loading
            try {
                val count = repository.openDocument(uri)
                _uiState.value = PdfUiState.Success(count, fileName)
                // Pre-render first few pages
                for (i in 0 until minOf(count, 5)) {
                    renderPage(i)
                }
            } catch (e: Exception) {
                _uiState.value = PdfUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun renderPage(pageIndex: Int) {
        if (_pages.value.containsKey(pageIndex)) return
        viewModelScope.launch {
            repository.renderPage(pageIndex)?.let { bitmap ->
                _pages.value = _pages.value + (pageIndex to bitmap)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.closeDocument()
    }
}
