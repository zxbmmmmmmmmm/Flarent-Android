package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.Markdown
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@Composable
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun ReplyBottomSheet(discussionId: String, title: String?){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("编辑", "预览")
    val pagerState = rememberPagerState { options.size }
    val coroutineScope = rememberCoroutineScope()

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
            IconButton(onClick = {}) {
                Icon(Icons.AutoMirrored.Filled.Send, "发送")
            }
        }
        HorizontalPager(state = pagerState) {
            when (it) {
                0 -> {
                    MarkdownEditBox()
                }
                1 -> {
                    Markdown("预览", modifier = Modifier.fillMaxSize().padding(16.dp))
                }
            }
        }
    }
}
@Composable
fun MarkdownEditBox(){
    Column{
        var textState: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        val focusRequester = remember { FocusRequester() }
        Row(horizontalArrangement = Arrangement.SpaceEvenly){
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
            onValueChange = { textState = it },
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
    ReplyBottomSheet("",null)
}