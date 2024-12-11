package com.gurumlab.aifriend.ui.chat

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.paging.compose.itemKey
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.ui.theme.edit_text_background
import com.gurumlab.aifriend.ui.theme.primaryLight
import com.gurumlab.aifriend.util.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    onNavUp: () -> Unit,
) {
    Scaffold(
        topBar = { ChatAppBar(onNavIconPressed = onNavUp) },
    ) { innerPadding ->
        ChatContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        )
    }
}

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel<ChatViewModel>()
) {
    LaunchedEffect(Unit) {
        viewModel.checkAlreadyGPTKeySet()
    }

    val chatMessages = viewModel.chatMessages.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isGPTKeySet by viewModel.isGPTKeySet.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var inputFieldHeightPixel by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackBarYOffSet = with(density) {
        (inputFieldHeightPixel).toDp()
    } + 10.dp

    val goToBottom: suspend () -> Unit = {
        if (chatMessages.itemCount > 0) {
            listState.scrollToItem(0)
        }
    }

    val currentKeyboardHeight =
        WindowInsets.ime.asPaddingValues().calculateBottomPadding().value

    val lastMessage = chatMessages.itemSnapshotList.items.lastOrNull()

    LaunchedEffect(currentKeyboardHeight, lastMessage) {
        goToBottom()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Messages(
                messages = chatMessages,
                listState = listState,
                context = context,
                modifier = Modifier.weight(1f)
            )
            ChatInput(
                isLoading = isLoading,
                isGPTKeySet = isGPTKeySet,
                snackbarHostState = snackbarHostState,
                scope = scope,
                context = context,
                onMessageSent = { content ->
                    viewModel.getResponse(
                        content = content,
                        loadingMessage = context.getString(R.string.loading)
                    )
                },
                onSizeChanged = { height ->
                    inputFieldHeightPixel = height
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .consumeWindowInsets(WindowInsets.navigationBars.asPaddingValues())
                    .imePadding()
            )
        }

        ErrorSnackBar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -snackBarYOffSet)
                .padding(horizontal = 15.dp)
                .consumeWindowInsets(WindowInsets.navigationBars.asPaddingValues())
                .imePadding()
        )
    }
}

@Composable
fun Messages(
    messages: LazyPagingItems<ChatMessage>,
    listState: LazyListState,
    context: Context,
    modifier: Modifier = Modifier
) {
    var isShowList by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(180)
        isShowList = true
    }

    Box(modifier.fillMaxSize()) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = isShowList,
            enter = fadeIn(animationSpec = tween(durationMillis = 100, easing = Ease))
        ) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    count = messages.itemCount,
                    key = messages.itemKey { it.id }
                ) { index ->
                    val message = messages[index]!!
                    if (message.role == Role.USER) {
                        UserMessage(message = message.content)
                    } else {
                        BotMessage(message = message.content.ifEmpty { context.getString(R.string.fail_response) })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun ChatInput(
    isLoading: Boolean,
    isGPTKeySet: Boolean,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    context: Context,
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

    val showSnackbar: () -> Unit = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.no_gpt_key_set),
                duration = SnackbarDuration.Short
            )
        }
    }

    val onClick = {
        if (!isGPTKeySet) {
            showSnackbar()
        } else if (textState.isNotBlank()) {
            onMessageSent(textState)
            textState = ""
        } else {
            //Nothing
        }
    }

    Row(
        modifier = modifier
            .padding(horizontal = 7.dp)
            .background(
                color = edit_text_background,
                shape = RoundedCornerShape(45.dp)
            )
            .border(
                width = 1.dp,
                color = primaryLight,
                shape = RoundedCornerShape(45.dp)
            )
            .padding(horizontal = 7.dp)
            .onSizeChanged { size -> onSizeChanged(size.height) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    onNavIconPressed: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            //No title
        },
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