package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

fun parseMarkdownToAnnotatedString(input: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var currentIndex = 0

    while (currentIndex < input.length) {
        val nextBold = input.indexOf("**", currentIndex)
        when {
            // Bold: **text**
            nextBold != -1 -> {
                val endBold = input.indexOf("**", nextBold + 2)
                if (endBold != -1) {
                    builder.append(input.substring(currentIndex, nextBold))
                    builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    builder.append(input.substring(nextBold + 2, endBold))
                    builder.pop()
                    currentIndex = endBold + 2
                } else {
                    builder.append(input.substring(currentIndex))
                    break
                }
            }

            else -> {
                builder.append(input.substring(currentIndex))
                break
            }
        }
    }
    return builder.toAnnotatedString()
}

@Composable
fun PlannerScreen(
    plannerViewModel: PlannerViewModel = viewModel()
) {
    val purpleLight = Color(0xFFD0D1FF)
    val purpleDark = Color(0xFF572CA8)

    val placeholderResult = stringResource(R.string.results_placeholder)

    var userInput by rememberSaveable { mutableStateOf("") }
    var selectedCategories by rememberSaveable { mutableStateOf(setOf<String>()) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }

    val uiState by plannerViewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.planner_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            color = purpleDark
        )

        val categories = listOf("Art", "History", "Architecture", "Fashion", "Nature")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            categories.forEach { category ->
                Button(
                    onClick = {
                        selectedCategories = if (selectedCategories.contains(category)) {
                            selectedCategories - category
                        } else {
                            selectedCategories + category
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategories.contains(category)) purpleDark else purpleLight
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (selectedCategories.contains(category)) Color.White else Color.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            TextField(
                value = userInput,
                label = { Text(stringResource(R.string.label_prompt)) },
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )

            Button(
                onClick = {
                    val finalPrompt = if (selectedCategories.isNotEmpty()) {
                        "${selectedCategories.joinToString(", ")}: $userInput"
                    } else {
                        userInput
                    }
                    plannerViewModel.sendPrompt(finalPrompt)
                },
                enabled = userInput.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                colors = ButtonDefaults.buttonColors(containerColor = purpleDark)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.action_go))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    userInput = ""
                    selectedCategories = setOf()
                    result = placeholderResult
                    plannerViewModel.resetState()
                },
                colors = ButtonDefaults.buttonColors(containerColor = purpleLight),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }

        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = purpleDark)
        } else {
            val textColor: Color
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
            val scrollState = rememberScrollState()

            val parsedResult = parseMarkdownToAnnotatedString(result)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                colors = CardDefaults.cardColors(containerColor = purpleLight),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = parsedResult,
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(scrollState),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}