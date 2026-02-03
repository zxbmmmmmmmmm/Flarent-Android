package com.bettafish.flarent.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.TagsRepository
import com.bettafish.flarent.models.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TagsViewModel (
    private val repository: TagsRepository
): ViewModel(){
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags

    suspend fun refresh() {
        try{
            _tags.value = repository.fetchTags()
        }
        catch(e: Exception){

        }
    }
}
