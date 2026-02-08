package com.bettafish.flarent.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bettafish.flarent.data.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReplyViewModel(val discussionId: String, initContent: String?, repository: PostsRepository): ViewModel() {
    companion object{
        private val drafts: MutableMap<String, String> = mutableMapOf()
    }
    private val _content : MutableStateFlow<String>
    val content: StateFlow<String>

    fun onContentChange(newName: String) {
        _content.value = newName
    }
    init{
        val sb = StringBuilder()
        val draft = drafts[discussionId]
        if(draft != null){
            sb.append(draft)
            sb.append("\n")
        }
        if(initContent != null){
            sb.append(initContent)
        }
        _content = MutableStateFlow(sb.toString());
        content = _content.asStateFlow()
    }

    override fun onCleared() {
        super.onCleared()
        if(!content.value.isEmpty()){
            drafts[discussionId] = content.value
        }
    }
}