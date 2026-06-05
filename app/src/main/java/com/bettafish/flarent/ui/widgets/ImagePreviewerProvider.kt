package com.bettafish.flarent.ui.widgets

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.bettafish.flarent.R
import com.bettafish.flarent.utils.ImageHelper
import com.jvziyaoyao.scale.image.previewer.ImagePreviewer
import com.jvziyaoyao.scale.zoomable.pager.PagerGestureScope
import com.jvziyaoyao.scale.zoomable.previewer.rememberPreviewerState
import kotlinx.coroutines.launch



val LocalImagePreviewer = compositionLocalOf<(List<String>, Int) -> Unit> {
    error("LocalImagePreviewer not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalImagePreviewerProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val savedToDownloadsMessage = stringResource(R.string.saved_to_downloads)
    val storagePermissionRequiredMessage = stringResource(R.string.storage_permission_required)

    val imageHelper = remember { ImageHelper(context) }

    var images by remember { mutableStateOf(emptyList<String>()) }
    val previewerState = rememberPreviewerState(pageCount = { images.size })

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var currentMenuUrl by remember { mutableStateOf<String?>(null) }

    // 用于 Android 9 及以下处理权限请求后的回调
    var pendingSaveUrl by remember { mutableStateOf<String?>(null) }

    // 权限启动器 (Android 9-)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingSaveUrl != null) {
            scope.launch {
                val result = imageHelper.saveImageToDownloads(pendingSaveUrl!!)
                showResultToast(context, result, savedToDownloadsMessage)
            }
        } else if (!isGranted) {
            Toast.makeText(context, storagePermissionRequiredMessage, Toast.LENGTH_SHORT).show()
        }
        pendingSaveUrl = null
    }

    val showPreview: (List<String>, Int) -> Unit = { urls, index ->
        images = urls
        scope.launch { previewerState.open(index) }
    }

    CompositionLocalProvider(LocalImagePreviewer provides showPreview) {
        content()

        ImagePreviewer(
            state = previewerState,
            detectGesture = PagerGestureScope(
                onTap = { scope.launch { previewerState.close() } },
                onLongPress = {
                    val currentUrl = images.getOrElse(previewerState.currentPage) { "" }
                    if (currentUrl.isNotEmpty()) {
                        currentMenuUrl = currentUrl
                        showSheet = true
                    }
                }
            ),
            imageLoader = { page ->
                val url = images.getOrElse(page) { "" }
                val painter = rememberAsyncImagePainter(model = url)
                Pair(painter, painter.intrinsicSize)
            },
            pageDecoration = { _, innerPage ->
                innerPage()
            }
        )

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SheetIconButton(
                        text = stringResource(R.string.save),
                        icon = Icons.Default.Download,
                        onClick = {
                            currentMenuUrl?.let { url ->
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    // Android 9-: 申请权限
                                    pendingSaveUrl = url
                                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                } else {
                                    // Android 10+: 直接保存
                                    scope.launch {
                                        val result = imageHelper.saveImageToDownloads(url)
                                        showResultToast(context, result, savedToDownloadsMessage)
                                    }
                                }
                            }
                            showSheet = false
                        }
                    )

                    SheetIconButton(
                        text = stringResource(R.string.share),
                        icon = Icons.Default.Share,
                        onClick = {
                            currentMenuUrl?.let { url ->
                                scope.launch {
                                    val result = imageHelper.shareImage(url)
                                    if (result.isFailure) {
                                        Toast.makeText(
                                            context,
                                            context.getString(
                                                R.string.share_failed,
                                                result.exceptionOrNull()?.message ?: ""
                                            ),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            showSheet = false
                        }
                    )
                }
            }
        }

        BackHandler(enabled = previewerState.canClose || previewerState.animating || showSheet) {
            if (showSheet) {
                showSheet = false
            } else {
                scope.launch { previewerState.close() }
            }
        }
    }
}

private fun showResultToast(context: Context, result: Result<String>, successMsg: String) {
    if (result.isSuccess) {
        Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.operation_failed, result.exceptionOrNull()?.message ?: ""),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun SheetIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
