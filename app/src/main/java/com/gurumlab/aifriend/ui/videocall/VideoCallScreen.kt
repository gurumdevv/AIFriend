package com.gurumlab.aifriend.ui.videocall

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomImage
import com.gurumlab.aifriend.util.MediaHandler

@Composable
fun VideoCallScreen(
    viewModel: VideoChatViewModel,
    onNavUp: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            ChatAppBar(
                onNavIconPressed = onNavUp
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            CustomImage(
                modifier = Modifier.fillMaxSize(),
                imageId = R.drawable.bg_room,
                contentDescription = stringResource(R.string.background_image)
            )

            VideoCallContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                snackbarHostState = snackbarHostState,
                viewModel = viewModel,
                mediaHandler = MediaHandler()
            )
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoCallContent(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: VideoChatViewModel,
    mediaHandler: MediaHandler
) {
    val recordPermissionState =
        rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var recordingState by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState(false)
    val character by viewModel.characterEmotion.collectAsState()

    val onClick = {
        if (!recordingState) {
            mediaHandler.startRecording(context, viewModel) { state ->
                recordingState = state
            }
        } else {
            mediaHandler.stopRecording(viewModel) { state ->
                recordingState = state
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(context.getString(message))
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        CustomImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            imageId = character,
            contentDescription = stringResource(R.string.character),
            contentScale = ContentScale.FillWidth
        )

        if (isLoading) {
            CustomImage(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 75.dp, bottom = 200.dp),
                imageId = R.drawable.img_waiting,
                contentDescription = stringResource(R.string.waiting_response)
            )
        }

        if (recordPermissionState.status.isGranted) {
            if (recordingState) {
                LottieAnimationLoader(
                    Modifier
                        .size(150.dp)
                        .align(Alignment.BottomCenter)
                        .padding(top = 50.dp)
                )
            }

            IconButton(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                enabled = !isLoading,
                onClick = { onClick() }
            ) {
                CustomImage(
                    imageId = R.drawable.img_mike,
                    contentDescription = stringResource(R.string.microphone)
                )
            }

        } else {
            RequestPermission(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                recordPermissionState = recordPermissionState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
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
        navigationIcon = {
            IconButton(onClick = onNavIconPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                )
            }
        }
    )
}