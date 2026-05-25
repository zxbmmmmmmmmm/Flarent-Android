package com.bettafish.flarent.ui.pages.reply

import androidx.lifecycle.ViewModel
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReplyViewModel(val discussionId: String?, val postId: String?, initContent: String?, val repository: PostsRepository): ViewModel() {
    companion object{
        private val drafts: MutableMap<String, String> = mutableMapOf()
    }
    private val _content : MutableStateFlow<String>
    val content: StateFlow<String>
    val isEdit: Boolean = postId != null
    fun onContentChange(newName: String) {
        _content.value = newName
    }
    init{
        val sb = StringBuilder()
        if(!isEdit){
            val draft = drafts[discussionId]
            if(draft != null){
                sb.append(draft)
                sb.append("\n")
            }
        }
        if(initContent != null){
            sb.append(initContent)
        }
        _content = MutableStateFlow(sb.toString())
        content = _content.asStateFlow()
    }

    override fun onCleared() {
        super.onCleared()
        if(!isEdit && !content.value.isEmpty() && discussionId != null){
            drafts[discussionId] = content.value
        }
    }
    suspend fun send(): Post?{
        try{
            if (isEdit) {
                return repository.editPost(postId!!, content.value)
            } else {
                return repository.sendPost(discussionId!!, content.value)
            }
        }
        catch (e:Exception){
            return null
        }
    }
}