package com.bettafish.flarent.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.FileRepository
import com.bettafish.flarent.models.File
import kotlinx.coroutines.launch

class FileViewModel (val repository: FileRepository) : ViewModel() {
    suspend fun upload(uri: Uri):List<File>{
        return repository.upload(uri)
    }
}