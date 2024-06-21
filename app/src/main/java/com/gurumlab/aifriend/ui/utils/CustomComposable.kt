package com.gurumlab.aifriend.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.gurumlab.aifriend.R

object CustomComposable {

    @Composable
    fun CustomImage(
        modifier: Modifier = Modifier,
        imageId: Int,
        contentDescription: String,
        contentScale: ContentScale = ContentScale.Crop
    ) {
        Image(
            modifier = modifier,
            painter = painterResource(id = imageId),
            contentDescription = contentDescription,
            contentScale = contentScale
        )
    }

    @Composable
    fun CustomText(
        modifier: Modifier = Modifier,
        value: String,
        fontStyle: TextStyle,
        color: Color,
        textAlign: TextAlign,
        fontWeight: FontWeight? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            modifier = modifier,
            text = value,
            textAlign = textAlign,
            color = color,
            style = fontStyle,
            fontFamily = FontFamily(Font(R.font.dongdong)),
            fontWeight = fontWeight,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}