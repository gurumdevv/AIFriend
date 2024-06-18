package com.gurumlab.aifriend.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.ui.theme.primaryLight
import com.gurumlab.aifriend.util.Role
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavUp: () -> Unit,
    viewModel: ChatViewModel
) {
    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            ChatAppBar(
                scrollBehavior = scrollBehavior,
                onNavIconPressed = onNavUp
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        ChatContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            scrollState = scrollState,
            viewModel = viewModel
        )
    }
}

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    viewModel: ChatViewModel
) {

    val chatMessages by viewModel.chatMessages.collectAsState()
    var inputFieldHeight by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val jumpToBottomButtonEnabled by remember {
        derivedStateOf {
            val visibleItems = scrollState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val lastVisibleItem = visibleItems.last()
                lastVisibleItem.index < chatMessages.size - 1
            } else {
                false
            }
        }
    }

    Box(modifier = modifier.imePadding()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Messages(
                modifier = Modifier.weight(1f),
                messages = chatMessages,
                scrollState = scrollState
            )
            ChatInput(
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .navigationBarsPadding(),
                onMessageSent = { content ->
                    viewModel.addMessage(role = Role.USER, content = content)
                    viewModel.insertLoadingMessage()
                    viewModel.getResponse()
                },
                resetScroll = {
                    scope.launch {
                        if (chatMessages.isNotEmpty()) {
                            scrollState.scrollToItem(chatMessages.size - 1)
                        }
                    }
                },
                onSizeChanged = { height ->
                    inputFieldHeight = height
                }
            )
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(chatMessages.size - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = with(LocalDensity.current) {
                    inputFieldHeight.toDp() + 16.dp
                })
        )
    }

    LaunchedEffect(chatMessages) {
        if (chatMessages.isNotEmpty()) {
            scrollState.animateScrollToItem(chatMessages.size - 1)
        }
    }
}

@Composable
fun Messages(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    scrollState: LazyListState
) {
    Box(
        modifier = modifier
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = messages,
            ) { message ->
                if (message.role == Role.USER) {
                    UserMessage(message = message.content)
                } else {
                    BotMessage(message = message.content)
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
    resetScroll: () -> Unit = {},
    onSizeChanged: (Int) -> Unit
) {
    var textState by rememberSaveable { mutableStateOf("") }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = primaryLight
    )

    val onClick = {
        if (textState.isNotBlank()) {
            onMessageSent(textState)
            textState = ""
            resetScroll()
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { size -> onSizeChanged(size.height) }
    ) {
        CustomEditTextBackground(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Min),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onClick() }),
                placeholder = { Text(stringResource(R.string.please_text_message)) }
            )

            IconButton(onClick = onClick) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = stringResource(R.string.btn_send),
                    tint = primaryLight
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            //No title
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onNavIconPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back)
                )
            }
        }
    )
}