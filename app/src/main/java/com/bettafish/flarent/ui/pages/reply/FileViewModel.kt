package com.bettafish.flarent.ui.pages.reply

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bettafish.flarent.data.FileRepository
import com.bettafish.flarent.models.File

class FileViewModel (val repository: FileRepository) : ViewModel() {
    suspend fun upload(uri: Uri):List<File>{
        return repository.upload(uri)
    }
}