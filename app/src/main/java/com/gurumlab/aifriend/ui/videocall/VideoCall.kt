package com.gurumlab.aifriend.ui.videocall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomText

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(
    recordPermissionState: PermissionState,
    modifier: Modifier = Modifier
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

@Composable
fun LottieAnimationLoader(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_on_record))
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
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