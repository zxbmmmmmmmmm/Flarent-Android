package com.bettafish.flarent.ui.widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscussionItemViewModel(
    val id: String,
    initDiscussion: Discussion? = null,
    private val repository: DiscussionsRepository
) : ViewModel() {
    private val _discussion = MutableStateFlow(initDiscussion)
    val discussion: StateFlow<Discussion?> = _discussion

    init {
        if (initDiscussion == null) {
            load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _discussion.value = repository.fetchDiscussion(
                    DiscussionRequest(
                        id = id,
                        near = null,
                        limit = null
                    )
                )
            } catch (_: Exception) {
            }
        }
    }
}

