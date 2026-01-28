package com.bettafish.flarent.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.models.Discussion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscussionsViewModel(
    private val repository: DiscussionsRepository
) : ViewModel() {

    private val _discussions = MutableStateFlow<List<Discussion>>(emptyList())
    val discussions: StateFlow<List<Discussion>> = _discussions

    fun load(page: Int = 1) {
        viewModelScope.launch {
            try{
                val result = repository.fetchDiscussions(page)
                val current = _discussions.value
                _discussions.value = current + result
            }
            catch(e: Exception){

            }
        }
    }
}
