package com.gurumlab.aifriend.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.ui.theme.primaryLight
import com.gurumlab.aifriend.util.Role
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavUp: () -> Unit,
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
            scrollState = scrollState
        )
    }
}

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    viewModel: ChatViewModel = hiltViewModel<ChatViewModel>()
) {
    val chatMessages = viewModel.chatMessages.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var inputFieldHeight by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val jumpToBottomButtonEnabled by remember {
        derivedStateOf {
            val visibleItems = scrollState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val lastVisibleItem = visibleItems.last()
                lastVisibleItem.index < chatMessages.itemCount - 1
            } else {
                false
            }
        }
    }

    val goToBottom: suspend () -> Unit = {
        if (chatMessages.itemCount > 0) {
            scrollState.scrollToItem(chatMessages.itemCount - 1)
        }
    }

    LaunchedEffect(chatMessages.itemCount) {
        if (chatMessages.itemCount > 0) {
            goToBottom()
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Messages(
                messages = chatMessages,
                scrollState = scrollState,
                modifier = Modifier.weight(1f)
            )
            ChatInput(
                isLoading = isLoading,
                onFocus = {
                    scope.launch {
                        goToBottom()
                    }
                },
                onMessageSent = { content ->
                    viewModel.getResponse(
                        content = content,
                        loadingMessage = context.getString(R.string.loading)
                    )
                },
                onSizeChanged = { height ->
                    inputFieldHeight = height
                },
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .consumeWindowInsets(WindowInsets.navigationBars.asPaddingValues())
                    .imePadding()
            )
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    goToBottom()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = with(LocalDensity.current) {
                    inputFieldHeight.toDp() + 16.dp
                })
        )
    }
}

@Composable
fun Messages(
    messages: LazyPagingItems<ChatMessage>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val context = LocalContext.current

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(messages.itemCount) { index ->
                messages[messages.itemCount - 1 - index]?.let { message ->

                    if (message.role == Role.USER) {
                        UserMessage(message = message.content)
                    } else {
                        BotMessage(message = message.content.ifEmpty { context.getString(R.string.fail_response) })
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun ChatInput(
    isLoading: Boolean,
    onFocus: () -> Unit,
    onMessageSent: (String) -> Unit,
    onSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
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
                .padding(horizontal = 7.dp)
                .onFocusEvent { onFocus() },
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

            IconButton(
                onClick = onClick,
                enabled = !isLoading
            ) {
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