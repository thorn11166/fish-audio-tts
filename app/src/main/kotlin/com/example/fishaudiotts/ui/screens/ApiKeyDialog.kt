package com.example.fishaudiotts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fishaudiotts.ui.theme.TorBoxCard
import com.example.fishaudiotts.ui.theme.TorBoxGreenLight
import com.example.fishaudiotts.ui.theme.TorBoxGreen
import com.example.fishaudiotts.ui.theme.TorBoxBlack
import com.example.fishaudiotts.ui.theme.TorBoxText

/**
 * First-run API key dialog
 * Shows when the app launches for the first time without an API key configured
 */
@Composable
fun ApiKeyDialog(
    onSave: (String) -> Unit,
    onLater: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { /* Don't dismiss on outside click */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = TorBoxBlack
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎙️ Welcome to Fish Audio TTS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TorBoxGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "To use this app, you need a Fish Audio API key.",
                    fontSize = 14.sp,
                    color = TorBoxText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Get your free API key at fish.audio",
                    fontSize = 12.sp,
                    color = TorBoxGreenLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    placeholder = { Text("Paste your API key here...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TorBoxGreen,
                        unfocusedBorderColor = TorBoxCard,
                        focusedTextColor = TorBoxText,
                        unfocusedTextColor = TorBoxText,
                        focusedLabelColor = TorBoxGreenLight,
                        unfocusedLabelColor = TorBoxGreenLight
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onLater,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TorBoxCard,
                            contentColor = TorBoxText
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Later",
                            color = TorBoxText
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                    Button(
                        onClick = {
                            if (apiKey.isNotBlank()) {
                                onSave(apiKey.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TorBoxGreen,
                            contentColor = TorBoxBlack
                        ),
                        modifier = Modifier.weight(1f),
                        enabled = apiKey.isNotBlank()
                    ) {
                        Text(
                            text = "Save",
                            color = TorBoxBlack
                        )
                    }
                }
            }
        }
    }
}
