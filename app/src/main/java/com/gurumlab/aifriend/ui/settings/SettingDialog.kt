package com.gurumlab.aifriend.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.theme.primaryLight
import com.gurumlab.aifriend.ui.theme.surfaceVariantLightMediumContrast
import com.gurumlab.aifriend.ui.utils.CustomComposable.CustomText

@Composable
fun ApiKeyDialog(
    setApiKey: String,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            var apikey by remember { mutableStateOf(setApiKey) }

            val onTextChange = { text: String -> apikey = text }

            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = primaryLight
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(surfaceVariantLightMediumContrast)
                    .padding(14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CustomText(
                    value = stringResource(R.string.set_api_key),
                    fontStyle = MaterialTheme.typography.headlineSmall,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                TextField(
                    value = apikey,
                    onValueChange = onTextChange,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    maxLines = 1,
                    placeholder = { Text(stringResource(R.string.insert_api_key)) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CustomText(
                            value = stringResource(R.string.cancel),
                            fontStyle = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            textAlign = TextAlign.Unspecified
                        )
                    }
                    TextButton(
                        onClick = { onConfirmation(apikey) },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CustomText(
                            value = stringResource(R.string.confirm),
                            fontStyle = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            textAlign = TextAlign.Unspecified
                        )
                    }
                }
            }
        }
    }
}