package com.example.movieappmad24.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieappmad24.data.MovieRepository
import com.example.movieappmad24.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

// Inherit from ViewModel class
class MoviesViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _movies = MutableStateFlow(listOf<Movie>())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()


    init {
        viewModelScope.launch {
            repository.getAllMovies().distinctUntilChanged()
                .collect{ listOfMovies ->
                    _movies.value = listOfMovies
                }
        }
    }
    fun toggleFavoriteMovie(movieId: String) {
        viewModelScope.launch {
            val newMovies = _movies.value.map { movie ->
                if (movie.id == movieId) movie.copy(isFavorite = !movie.isFavorite) else movie
            }
            _movies.value = newMovies
            // Update the database if your architecture requires it
            newMovies.filter { it.id == movieId }.forEach { repository.updateMovie(it) }
        }
    }


}