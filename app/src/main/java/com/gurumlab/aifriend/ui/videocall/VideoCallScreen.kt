package com.gurumlab.aifriend.ui.videocall

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.theme.black
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomImage
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomText
import com.gurumlab.aifriend.util.DateTimeConverter
import java.io.File

private var outputPath: String? = null
private var mediaRecorder: MediaRecorder? = null

@Composable
fun VideoCallScreen(
    viewModel: VideoChatViewModel,
    onNavUp: () -> Unit
) {

    Scaffold(
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
                viewModel = viewModel
            )
        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoCallContent(
    modifier: Modifier = Modifier,
    viewModel: VideoChatViewModel
) {
    val recordPermissionState =
        rememberPermissionStateSafe(Manifest.permission.RECORD_AUDIO)
    var recordingState by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState(false)
    val character by viewModel.characterEmotion.collectAsState()

    val onClick = {
        if (!recordingState) {
            startRecording(context) { state ->
                recordingState = state
            }
        } else {
            stopRecording(viewModel) { state ->
                recordingState = state
            }
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

//        CustomImage(
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(end = 75.dp, bottom = 200.dp),
//            imageId = R.drawable.img_angry_right, contentDescription = ""
//        )

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
                onClick = { onClick() }) {
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

@Composable
fun LottieAnimationLoader(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_on_record))
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestPermission(
    modifier: Modifier = Modifier,
    recordPermissionState: PermissionState
) {
    if (!recordPermissionState.status.isGranted) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = if (recordPermissionState.status.shouldShowRationale) {
                stringResource(R.string.request_record_permission_again)
            } else {
                stringResource(R.string.request_record_permission)
            }
            CustomText(
                value = textToShow,
                fontStyle = MaterialTheme.typography.bodyLarge,
                color = black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
            Button(onClick = {
                recordPermissionState.launchPermissionRequest()
            }) {
                CustomText(
                    value = stringResource(R.string.btn_record_permission),
                    fontStyle = MaterialTheme.typography.bodyLarge,
                    color = black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun startRecording(context: Context, isStateChanged: (Boolean) -> Unit) {
    val fileName = DateTimeConverter.getCurrentDateString() + ".mp3"
    val file = File(context.filesDir, fileName)
    outputPath = file.absolutePath

    mediaRecorder = MediaRecorder(context).apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(outputPath)

        try {
            prepare()
            start()
            isStateChanged(true)
        } catch (e: Exception) {
            Log.d("VideoCallScreen", "${e.message}")
        }
    }
}

private fun stopRecording(viewModel: VideoChatViewModel, isStateChanged: (Boolean) -> Unit) {
    mediaRecorder?.apply {
        stop()
        reset()
        release()
        isStateChanged(false)
    }

    if (!outputPath.isNullOrEmpty()) {
        val audio = File(outputPath!!)
        viewModel.getResponse(audio)
        outputPath = null
    } else {
        Log.d("VideoCallScreen", "outputPath is null")
    }
}

@ExperimentalPermissionsApi
@Composable
fun rememberPermissionStateSafe(permission: String, onPermissionResult: (Boolean) -> Unit = {}) =
    when {
        LocalInspectionMode.current -> remember {
            object : PermissionState {
                override val permission = permission
                override val status = PermissionStatus.Granted
                override fun launchPermissionRequest() = Unit
            }
        }

        else -> rememberPermissionState(permission, onPermissionResult)
    }