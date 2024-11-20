package com.gurumlab.aifriend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.theme.gradation_end
import com.gurumlab.aifriend.ui.theme.gradation_mid
import com.gurumlab.aifriend.ui.theme.gradation_start
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomImage
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomText

@Composable
fun HomeBackground() {
    Box(Modifier.fillMaxSize()) {
        CustomImage(
            imageId = R.drawable.bg_main,
            contentDescription = stringResource(R.string.background_image),
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            GradientBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun SettingButton(
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = { onNavigateToSettings() }) {
            CustomImage(
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp),
                imageId = R.drawable.ic_setting,
                contentDescription = stringResource(R.string.ic_setting),
            )
        }
    }
}

@Composable
fun ProfileAndButtonsArea(
    onNavigateToChat: () -> Unit,
    onNavigateToVideoCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CustomText(
            value = stringResource(R.string.character_name),
            fontStyle = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomText(
            value = stringResource(R.string.profile_message),
            fontStyle = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ImageTextButton(
                text = stringResource(R.string.chat),
                fontStyle = MaterialTheme.typography.bodyMedium,
                imageId = R.drawable.ic_chat_start,
                contentDescription = stringResource(R.string.chat_start_icon),
                onClick = onNavigateToChat
            )
            ImageTextButton(
                text = stringResource(R.string.videocall),
                fontStyle = MaterialTheme.typography.bodyMedium,
                imageId = R.drawable.ic_video_call,
                contentDescription = stringResource(R.string.video_chat_start_icon),
                onClick = onNavigateToVideoCall
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ImageTextButton(
    text: String,
    fontStyle: TextStyle,
    imageId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { onClick() }) {
            CustomImage(
                imageId = imageId,
                contentDescription = contentDescription,
                modifier = Modifier.padding(3.dp)
            )
        }
        CustomText(
            value = text,
            fontStyle = fontStyle,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GradientBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        gradation_start,
                        gradation_mid,
                        gradation_end
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    )
}