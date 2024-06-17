package com.gurumlab.aifriend.ui.chat

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.chat.BubbleValues.BUBBLE_MAX_RATIO
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomImage
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomText
import com.gurumlab.aifriend.ui.utils.CustomShape
import com.gurumlab.aifriend.ui.utils.CustomShape.CustomRectangle
import com.gurumlab.aifriend.ui.utils.CustomShape.CustomTriangle
import com.gurumlab.aifriend.ui.theme.bubble_incoming
import com.gurumlab.aifriend.ui.theme.bubble_outgoing
import com.gurumlab.aifriend.ui.theme.edit_text_background
import com.gurumlab.aifriend.ui.theme.primaryLight

@Composable
fun BotMessage(
    modifier: Modifier = Modifier,
    message: String
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val desiredWidth = screenWidth * BUBBLE_MAX_RATIO

        Row(
            modifier = modifier.width(desiredWidth)
        ) {
            CustomImage(
                modifier = Modifier.size(50.dp),
                imageId = R.drawable.img_profile,
                contentDescription = stringResource(R.string.profile_image)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                CustomText(
                    value = stringResource(R.string.character_name),
                    fontStyle = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.size(4.dp))
                Box {
                    BubbleInComing(modifier = Modifier.matchParentSize())
                    CustomText(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .height(IntrinsicSize.Min)
                            .padding(top = 4.dp, bottom = 4.dp, start = 10.dp, end = 6.dp),
                        value = message,
                        fontStyle = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun UserMessage(
    modifier: Modifier = Modifier,
    message: String
) {
    BoxWithConstraints(
        modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        val screenWidth = maxWidth
        val desiredWidth = screenWidth * BUBBLE_MAX_RATIO

        Row(
            modifier = Modifier.width(desiredWidth),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                BubbleOutgoing(modifier = Modifier.matchParentSize())
                CustomText(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .height(IntrinsicSize.Min)
                        .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 10.dp),
                    value = message,
                    fontStyle = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun BubbleInComing(modifier: Modifier) {
    var size by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .onSizeChanged { newSize ->
                size = Size(newSize.width.toFloat(), newSize.height.toFloat())
            }
    ) {
        CustomTriangle(
            color = bubble_incoming,
            firstPoint = Offset(0f, 0f),
            secondPoint = Offset(8.5.dp.value, 0f),
            thirdPoint = Offset(8.5.dp.value, 16.dp.value)
        )

        CustomRectangle(
            topLeft = Offset(8.dp.value, 0f),
            topRight = Offset(size.width, 0f),
            radius = CustomShape.CornerRadius(
                topLeft = 0f,
                topRight = 25f,
                bottomRight = 25f,
                bottomLeft = 25f
            ),
            size = Offset(size.width, size.height),
            color = bubble_incoming
        )
    }
}

@Composable
fun BubbleOutgoing(modifier: Modifier) {
    var size by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .onSizeChanged { newSize ->
                size = Size(newSize.width.toFloat(), newSize.height.toFloat())
            }
    ) {
        CustomRectangle(
            topLeft = Offset(0f, 0f),
            topRight = Offset(size.width - 8.dp.value, 0f),
            radius = CustomShape.CornerRadius(
                topLeft = 25f,
                topRight = 0f,
                bottomRight = 25f,
                bottomLeft = 25f
            ),
            size = Offset(size.width, size.height),
            color = bubble_outgoing
        )

        CustomTriangle(
            color = bubble_outgoing,
            firstPoint = Offset(size.width, 0f),
            secondPoint = Offset(size.width - 8.5.dp.value, 0f),
            thirdPoint = Offset(size.width - 8.5.dp.value, 16.dp.value)
        )
    }
}

@Composable
fun CustomEditTextBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = edit_text_background,
                shape = RoundedCornerShape(
                    topStart = 45.dp,
                    topEnd = 45.dp,
                    bottomEnd = 45.dp,
                    bottomStart = 45.dp
                )
            )
            .border(
                width = 1.dp,
                color = primaryLight,
                shape = RoundedCornerShape(
                    topStart = 45.dp,
                    topEnd = 45.dp,
                    bottomEnd = 45.dp,
                    bottomStart = 45.dp
                )
            )
    )
}

@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(
        if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = stringResource(R.string.jump_to_bottom_label)
    )
    val bottomOffset by transition.animateDp(label = stringResource(R.string.jump_to_bottom_label)) {
        if (it == Visibility.GONE) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        SmallFloatingActionButton(
            onClick = onClicked,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset(y = -bottomOffset)
                .height(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
    }
}

private enum class Visibility {
    VISIBLE,
    GONE
}


object BubbleValues {
    const val BUBBLE_MAX_RATIO = 0.7f
}