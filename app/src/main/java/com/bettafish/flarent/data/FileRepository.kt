package com.bettafish.flarent.data

import android.net.Uri
import com.bettafish.flarent.models.File

interface FileRepository{
    suspend fun upload(uri: Uri) : List<File>
}