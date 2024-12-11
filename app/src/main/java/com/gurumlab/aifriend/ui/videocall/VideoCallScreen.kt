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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomImage
import com.gurumlab.aifriend.util.MediaHandler

@Composable
fun VideoCallScreen(
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
                imageId = R.drawable.bg_room,
                contentDescription = stringResource(R.string.background_image),
                modifier = Modifier.fillMaxSize()
            )

            VideoCallContent(
                snackbarHostState = snackbarHostState,
                mediaHandler = MediaHandler(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoCallContent(
    snackbarHostState: SnackbarHostState,
    mediaHandler: MediaHandler,
    modifier: Modifier = Modifier,
    viewModel: VideoChatViewModel = hiltViewModel<VideoChatViewModel>()
) {
    LaunchedEffect(Unit) {
        viewModel.checkAlreadyGPTKeySet()
    }

    val recordPermissionState =
        rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isGptKeySet by viewModel.isGPTKeySet.collectAsState(false)
    val isLoading by viewModel.isLoading.collectAsState(false)
    val character by viewModel.characterEmotion.collectAsState()
    val snackbarMessageRes by viewModel.snackbarMessage.collectAsState(-1)

    val onClick = {
        if (!isGptKeySet) {
            viewModel.showIsNotGPTKeySetSnackBar()
        } else if (!isRecording) {
            mediaHandler.startRecording(context, viewModel) { state ->
                isRecording = state
            }
        } else {
            mediaHandler.stopRecording(viewModel) { state ->
                isRecording = state
            }
        }
    }

    LaunchedEffect(snackbarMessageRes) {
        if (snackbarMessageRes != -1) {
            snackbarHostState.showSnackbar(context.getString(snackbarMessageRes))
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        CustomImage(
            imageId = character,
            contentDescription = stringResource(R.string.character),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )

        if (isLoading) {
            CustomImage(
                imageId = R.drawable.img_waiting,
                contentDescription = stringResource(R.string.waiting_response),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 75.dp, bottom = 200.dp)
            )
        }

        if (recordPermissionState.status.isGranted) {
            if (isRecording) {
                LottieAnimationLoader(
                    Modifier
                        .size(150.dp)
                        .align(Alignment.BottomCenter)
                        .padding(top = 50.dp)
                )
            }

            IconButton(
                enabled = !isLoading,
                onClick = onClick,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
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