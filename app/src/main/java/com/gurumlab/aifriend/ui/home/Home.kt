package com.gurumlab.aifriend.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun BottomComposable(
    modifier: Modifier = Modifier,
    onNavigateToChat: () -> Unit,
    onNavigateToVideoCall: () -> Unit
) {
    Column(modifier = modifier) {
        CustomText(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(R.string.character_name),
            fontStyle = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomText(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(R.string.profile_message),
            fontStyle = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center
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
fun ImageButton(
    modifier: Modifier = Modifier,
    imageId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Image(
        modifier = modifier
            .clickable { onClick() }
            .size(30.dp),
        painter = painterResource(id = imageId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop
    )
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
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomImage(imageId = imageId, contentDescription = contentDescription)
        Spacer(modifier = Modifier.height(3.dp))
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