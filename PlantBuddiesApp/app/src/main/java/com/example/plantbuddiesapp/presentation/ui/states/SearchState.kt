package com.example.plantbuddiesapp.presentation.ui.states

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    object Success : SearchState()
    data class Empty(val message: String) : SearchState()
    data class Error(val message: String) : SearchState()
}