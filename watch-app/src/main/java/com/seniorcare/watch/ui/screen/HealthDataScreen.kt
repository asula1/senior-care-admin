package com.seniorcare.watch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HealthDataScreen(navController: SwipeDismissableNavController) {
    val scalingLazyListState = rememberScalingLazyListState()
    
    // 건강 데이터 상태 (실제로는 ViewModel에서 가져와야 함)
    var heartRate by remember { mutableStateOf(78) }
    var steps by remember { mutableStateOf(3458) }
    var lastUpdated by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // 시뮬레이션을 위한 데이터 업데이트
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000) // 10초마다 업데이트
            heartRate = (70..90).random()
            steps += (10..50).random()
            lastUpdated = System.currentTimeMillis()
        }
    }
    
    // 시간 형식 포맷터
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    
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
            // 화면 제목
            item {
                Text(
                    text = "건강 데이터",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // 심박수 카드
            item {
                Card(
                    onClick = { /* 상세 보기 화면으로 이동 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "심박수",
                            style = MaterialTheme.typography.title3
                        )
                        Text(
                            text = "$heartRate BPM",
                            style = MaterialTheme.typography.title1
                        )
                    }
                }
            }
            
            // 걸음 수 카드
            item {
                Card(
                    onClick = { /* 상세 보기 화면으로 이동 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "걸음 수",
                            style = MaterialTheme.typography.title3
                        )
                        Text(
                            text = "$steps 걸음",
                            style = MaterialTheme.typography.title1
                        )
                    }
                }
            }
            
            // 마지막 업데이트 시간
            item {
                Text(
                    text = "마지막 업데이트: ${dateFormat.format(Date(lastUpdated))}",
                    style = MaterialTheme.typography.caption2,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // 새로고침 버튼
            item {
                Button(
                    onClick = {
                        // 실제 앱에서는 데이터 새로고침 로직 구현
                        heartRate = (70..90).random()
                        lastUpdated = System.currentTimeMillis()
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("새로고침")
                }
            }
            
            // 뒤로가기 버튼
            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("뒤로")
                }
            }
        }
    }
}
