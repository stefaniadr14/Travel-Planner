package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun StartScreen(navController: NavHostController) {
    val purpleLight = Color(0xFFECECFF)
    val purpleDark = Color(0xFF572CA8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(purpleLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌍 Explore the World with Your Personal Travel Planner! ✈️",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = purpleDark,
                modifier = Modifier.padding(bottom = 50.dp)
            )
            Text(
                text = "Discover new destinations, craft your perfect itinerary, and make unforgettable memories.\n" +
                        " Ready to embark on your next adventure?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { navController.navigate("planner") },
                colors = ButtonDefaults.buttonColors(containerColor = purpleDark)
            ) {
                Text("Let's Begin!", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}
