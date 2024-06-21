package com.gurumlab.aifriend.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomImage
import com.gurumlab.aifriend.R

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToVideoCall: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    Scaffold { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            CustomImage(
                modifier = Modifier.fillMaxSize(),
                imageId = R.drawable.bg_main,
                contentDescription = stringResource(R.string.background_image)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                GradientBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
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

                BottomComposable(
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToVideoCall = onNavigateToVideoCall
                )
            }
        }
    }
}

