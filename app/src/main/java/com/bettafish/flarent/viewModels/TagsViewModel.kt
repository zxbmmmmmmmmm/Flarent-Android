package com.bettafish.flarent.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.TagsRepository
import com.bettafish.flarent.models.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

public class TagsViewModel (
    private val repository: TagsRepository
): ViewModel(){
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags

    fun load(page: Int = 1) {
        viewModelScope.launch {
            try{
                val result = repository.fetchTags()
                val current = _tags.value
                _tags.value = current + result
            }
            catch(e: Exception){

            }
        }
    }
}
