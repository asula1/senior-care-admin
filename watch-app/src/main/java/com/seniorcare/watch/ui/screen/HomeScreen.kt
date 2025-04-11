package com.seniorcare.watch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavController

@Composable
fun HomeScreen(navController: SwipeDismissableNavController) {
    val scalingLazyListState = rememberScalingLazyListState()
    
    Scaffold(
        timeText = {
            TimeText()
        },
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = scalingLazyListState
            )
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 32.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 앱 이름 표시
            item {
                Text(
                    text = "노인 케어",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // 건강 데이터 버튼
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = { navController.navigate("health") },
                    label = { Text("건강 데이터") },
                    colors = ChipDefaults.primaryChipColors()
                )
            }
            
            // 약물 관리 버튼
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = { navController.navigate("medication") },
                    label = { Text("약물 관리") },
                    colors = ChipDefaults.primaryChipColors()
                )
            }
            
            // 비상 호출 버튼
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = { navController.navigate("emergency") },
                    label = { Text("비상 호출") },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }
    }
}
