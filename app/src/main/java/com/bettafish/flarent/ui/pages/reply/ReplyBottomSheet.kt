package com.bettafish.flarent.ui.pages.reply

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.utils.GlobalPostUpdateManager
import com.mikepenz.markdown.m3.Markdown
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
@OptIn(ExperimentalMaterial3Api::class)
fun ReplyBottomSheet(discussionId: String? = null,
                     postId: String? = null,
                     title: String? = null,
                     content: String? = null,
                     navigator: DestinationsNavigator? = null){
    val replyViewModel : ReplyViewModel = getViewModel{ parametersOf(discussionId, postId, content) }
    val fileViewModel : FileViewModel = getViewModel()
    val content by replyViewModel.content.collectAsState()

    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    val options = listOf("编辑", "预览")
    val pagerState = rememberPagerState { options.size }
    val coroutineScope = rememberCoroutineScope()
    val isSending = remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp)
        .height(screenHeight / 2)){
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)){
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.weight(0.5f),) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        selected = pagerState.currentPage == index,
                        label = { Text(label) }
                    )
                }
            }
            Box(modifier = Modifier.height(48.dp).width(48.dp),
                contentAlignment = Alignment.Center
            ){
                if(isSending.value){
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                }
                else{
                    IconButton(onClick = {
                        isSending.value = true
                        coroutineScope.launch {
                            val post = replyViewModel.send()
                            if(post != null)
                            {
                                navigator?.popBackStack()
                                if(postId != null){ // edit
                                    GlobalPostUpdateManager.emitPost(post)
                                }
                                else{
                                    navigator?.navigate(
                                        PostBottomSheetDestination(post.id)
                                    )
                                }

                            }

                            isSending.value = false
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, "发送")
                    }
                }
            }

        }
        HorizontalPager(state = pagerState) {
            when (it) {
                0 -> {
                    MarkdownEditBox(replyViewModel, fileViewModel)
                }
                1 -> {
                    Markdown(content, modifier = Modifier.fillMaxSize().padding(16.dp))
                }
            }
        }
    }
}
@Composable
fun MarkdownEditBox(replyViewModel: ReplyViewModel, fileViewModel: FileViewModel){
    Column{
        var textState: TextFieldValue by remember {
            mutableStateOf(
                TextFieldValue(
                    text = replyViewModel.content.value,
                    selection = TextRange(replyViewModel.content.value.length)
                )
            )
        }
        val focusRequester = remember { FocusRequester() }
        val coroutineScope = rememberCoroutineScope()
        val isUploading = remember { mutableStateOf(false) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let{ link ->
                isUploading.value = true
                coroutineScope.launch {
                    try{
                        val files = fileViewModel.upload(link)
                        if(files.isNotEmpty()){
                            files[0].bbcode?.let {
                                textState = handleMarkdownAction(textState, it)
                            }
                        }
                    }
                    catch(e: Exception){

                    }
                    finally {
                        isUploading.value = false
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly){
            Box(modifier = Modifier.width(48.dp).height(48.dp)){
                if(isUploading.value){
                    CircularProgressIndicator(modifier = Modifier.padding(12.dp))

                }
                else{
                    ToolbarButton(Icons.Default.Upload, "上传") {
                        launcher.launch("*/*")
                    }
                }
            }
            ToolbarButton(Icons.Default.FormatBold, "加粗") {
                textState = handleMarkdownAction(textState, "**", "**")
            }
            ToolbarButton(Icons.Default.FormatItalic, "斜体") {
                textState = handleMarkdownAction(textState, "*", "*")
            }
            ToolbarButton(Icons.AutoMirrored.Filled.FormatListBulleted, "列表") {
                textState = handleMarkdownAction(textState, "\n- ")
            }
            ToolbarButton(Icons.Default.Title, "标题") {
                textState = handleMarkdownAction(textState, "\n# ")
            }
            ToolbarButton(Icons.Default.Code, "代码") {
                textState = handleMarkdownAction(textState, "`", "`")
            }
            ToolbarButton(Icons.Default.Link, "链接") {
                textState = handleMarkdownAction(textState, "[", "](url)")
            }
        }
        TextField(
            value = textState,
            onValueChange = {
                textState = it
                replyViewModel.onContentChange(it.text) },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .weight(1f, fill = false)
                .navigationBarsPadding(),
            placeholder = { Text("开始书写...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

fun handleMarkdownAction(
    currentValue: TextFieldValue,
    prefix: String,
    suffix: String = ""
): TextFieldValue {
    val selection = currentValue.selection
    val text = currentValue.text

    val newText = StringBuilder(text)
        .insert(selection.end, suffix)
        .insert(selection.start, prefix)
        .toString()

    val newCursorPosition = if (selection.collapsed) {
        selection.start + prefix.length
    } else {
        selection.end + prefix.length + suffix.length
    }

    return currentValue.copy(
        text = newText,
        selection = TextRange(newCursorPosition)
    )
}

@Composable
fun ToolbarButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Preview(showBackground = true)
@Composable
fun ReplyBottomSheetPreview(){
    ReplyBottomSheet("")
}