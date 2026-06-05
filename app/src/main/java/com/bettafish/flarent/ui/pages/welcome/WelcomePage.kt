package com.bettafish.flarent.ui.pages.welcome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.App
import com.bettafish.flarent.R
import com.bettafish.flarent.config.ForumConfig
import com.bettafish.flarent.models.Forum
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.utils.HtmlConverter
import com.bettafish.flarent.utils.appSettings
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.RoundedCornerSpanPainter
import com.mikepenz.markdown.compose.extendedspans.SquigglyUnderlineSpanPainter
import com.mikepenz.markdown.compose.extendedspans.rememberSquigglyUnderlineAnimator
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.markdownExtendedSpans
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.HomePageDestination
import com.ramcosta.composedestinations.generated.destinations.LoginPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
fun WelcomePage(navigator: DestinationsNavigator,
                resultRecipient: ResultRecipient<LoginPageDestination, LoginResult>,
                viewModel: WelcomeViewModel = koinViewModel()){
    val forum = viewModel.forum.collectAsState()
    val canRefreshUser = viewModel.refreshUserCommand.canExecute.collectAsState()
    resultRecipient.onResult(
        onValue = { resultValue ->
            App.INSTANCE.appSettings.userId = resultValue.id
            App.INSTANCE.appSettings.token = resultValue.token
            viewModel.refreshUserCommand.execute(resultValue.id)
            viewModel.refreshUserCommand.onCompleted = {
                navigator.navigate(HomePageDestination) {
                    launchSingleTop = true
                }
            }
        }
    )
    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()){
        if(forum.value == null || !canRefreshUser.value){
            CircularProgressIndicator(modifier = Modifier
                .height(64.dp)
                .width(64.dp)
                .align(Alignment.Center))
        }
        else{
            WelcomePage(forum = forum.value!!,
                navigator = navigator,
                onSave = { viewModel.saveForum() })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomePage(forum: Forum,
                modifier: Modifier = Modifier,
                navigator: DestinationsNavigator? = null,
                onSave : () -> Unit = {}){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)){
        Column(modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)){
            Text(text = forum.welcomeTitle ?: "",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 24.dp))

            val isDarkTheme = isSystemInDarkTheme()
            val markdown = HtmlConverter.convert(forum.welcomeMessage?:"")
            val markdownComponents = remember(isDarkTheme) {
                val highlightsBuilder = Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isDarkTheme))
                markdownComponents(
                    codeBlock = {
                        MarkdownHighlightedCodeBlock(
                            content = it.content,
                            node = it.node,
                            highlightsBuilder = highlightsBuilder,
                            showHeader = true,
                        )
                    },
                    codeFence = {
                        MarkdownHighlightedCodeFence(
                            content = it.content,
                            node = it.node,
                            highlightsBuilder = highlightsBuilder,
                            showHeader = true,
                        )
                    },
                )
            }
            Markdown(
                markdown,
                imageTransformer = Coil3ImageTransformerImpl,
                components = markdownComponents,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                extendedSpans = markdownExtendedSpans {
                    val animator = rememberSquigglyUnderlineAnimator()
                    remember {
                        ExtendedSpans(
                            RoundedCornerSpanPainter(),
                            SquigglyUnderlineSpanPainter(animator = animator)
                        )
                    }
                }
            )
            forum.guidelinesUrl?.let {
                LinkCard(stringResource(R.string.guidelines),
                    it,
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    })
            }
            LinkCard(stringResource(R.string.user_agreement), ForumConfig.userAgreementUrl)
            LinkCard(stringResource(R.string.privacy_policy), ForumConfig.privacyPolicyUrl)
        }


        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    navigator?.navigate(HomePageDestination) {
                        launchSingleTop = true
                    }
                    onSave()}) {
                Text(stringResource(R.string.enter_directly))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navigator?.navigate(LoginPageDestination)
                    onSave()
                }) {
                Text(stringResource(R.string.login))
            }
        }
    }

}

@Composable
fun LinkCard(title: String,
             url: String,
             modifier: Modifier = Modifier,
             icon: @Composable () -> Unit = { Icon(Icons.Filled.Link, contentDescription = null) },
){
    val urlHandler = LocalUriHandler.current
    Card(modifier.clickable{
        urlHandler.openUri(url)
    }) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)){
            icon()
            Text(title,
                modifier = Modifier)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun WelcomePagePreview(){
    val forumName = "wvbCommunity"
    WelcomePage(Forum().apply {
        title = forumName
        description = "$forumName: where Vista booms! A growing circle for PC and digital tech."
        guidelinesUrl = "${baseUrl}d/22/3"
        welcomeTitle = "Welcome to $forumName!"
        welcomeMessage = "Before getting started, please read our \u003Ca href=/d/22\u003Eguidelines\u003C/a\u003E for basic forum information.\n\u003Cbr/\u003E\u003Cb\u003ENote: user posts represent their personal opinions, not the views of $forumName.\u003C/b\u003E"
    })
}
