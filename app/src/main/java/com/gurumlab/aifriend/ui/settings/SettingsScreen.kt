package com.gurumlab.aifriend.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomText

@Composable
fun SettingScreen(
    onNavUp: () -> Unit,
    viewModel: SettingViewModel
) {
    Scaffold(
        topBar = {
            ChatAppBar(
                onNavIconPressed = onNavUp
            )
        },
    ) { innerPadding ->
        SettingContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        val gptApiKey = viewModel.gptApiKey.collectAsState()
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            ApiKeyDialog(
                setApiKey = gptApiKey.value,
                onDismissRequest = {
                    showDialog = false
                }) { newApiKey ->
                showDialog = false
                viewModel.setGptApiKey(newApiKey)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CustomText(
                    value = stringResource(R.string.api_key),
                    fontStyle = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(15.dp))

                CustomText(
                    value = gptApiKey.value.ifEmpty { stringResource(R.string.not_set_yet) },
                    color = Color.Black,
                    textAlign = TextAlign.End,
                    fontStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
            }

            Row(
                modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(5.dp)
                ) {
                    Text(text = "편집")
                }
            }
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
                    contentDescription = stringResource(R.string.btn_back)
                )
            }
        }
    )
}