package com.seniorcare.watch.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.seniorcare.watch.ui.screen.EmergencyScreen
import com.seniorcare.watch.ui.screen.HealthDataScreen
import com.seniorcare.watch.ui.screen.HomeScreen
import com.seniorcare.watch.ui.screen.MedicationScreen
import com.seniorcare.watch.ui.theme.SeniorCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 스플래시 화면 설정 (선택사항)
        // installSplashScreen()
        
        setContent {
            SeniorCareApp()
        }
    }
}

@Composable
fun SeniorCareApp() {
    SeniorCareTheme {
        val navController = rememberSwipeDismissableNavController()
        
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            
            composable("health") {
                HealthDataScreen(navController = navController)
            }
            
            composable("medication") {
                MedicationScreen(navController = navController)
            }
            
            composable("emergency") {
                EmergencyScreen(navController = navController)
            }
            
            // 추가 화면 경로 설정
        }
    }
}
