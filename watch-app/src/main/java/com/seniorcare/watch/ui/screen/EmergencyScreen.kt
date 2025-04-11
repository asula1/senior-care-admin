package com.seniorcare.watch.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavController
import com.seniorcare.watch.domain.model.Location
import com.seniorcare.watch.ui.viewmodel.EmergencyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun EmergencyScreen(
    navController: SwipeDismissableNavController,
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // 비상 알림 상태 관찰
    val emergencyState by viewModel.emergencyState.collectAsState()
    
    // SOS 카운트다운 상태
    var isSosCountdown by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }
    
    // 상태에 따른 UI 표시 여부
    val isSending = emergencyState is EmergencyViewModel.EmergencyState.Sending
    val isSent = emergencyState is EmergencyViewModel.EmergencyState.Sent
    
    Scaffold(
        timeText = {
            TimeText()
        }
    ) {
        if (isSosCountdown) {
            // 카운트다운 UI
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.error.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = countdown.toString(),
                        style = MaterialTheme.typography.title1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "취소하려면 화면을 터치하세요",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center
                    )
                    
                    // 카운트다운 로직
                    LaunchedEffect(isSosCountdown) {
                        while (countdown > 0) {
                            delay(1000)
                            countdown--
                        }
                        isSosCountdown = false
                        isSending = true
                        // 실제 SOS 신호 전송 로직 호출
                        Timber.d("SOS 알림 전송 시작")
                        viewModel.sendSosAlert()
                    }
                }
            }
        } else if (isSending) {
            // 전송 중 UI
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "SOS 전송 중...",
                    modifier = Modifier.padding(top = 60.dp)
                )
            }
        } else if (isSent) {
            // 전송 완료 UI
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SOS 전송 완료",
                        style = MaterialTheme.typography.title2,
                        textAlign = TextAlign.Center
                    )
                    
                    // 전송된 알림 정보 표시
                    if (emergencyState is EmergencyViewModel.EmergencyState.Sent) {
                        val alert = (emergencyState as EmergencyViewModel.EmergencyState.Sent).alert
                        Text(
                            text = "알림 ID: ${alert.id}",
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            viewModel.resetState()
                            navController.popBackStack() 
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("확인")
                    }
                }
            }
        } else {
            // 기본 비상 화면 UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "비상 호출",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Button(
                    onClick = {
                        isSosCountdown = true
                        countdown = 5
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "SOS",
                        style = MaterialTheme.typography.title1
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "SOS 버튼을 누르면 5초 후 비상 신호가 발송됩니다",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("뒤로")
                }
                
                // 오류 메시지 표시
                if (emergencyState is EmergencyViewModel.EmergencyState.Error) {
                    val errorMessage = (emergencyState as EmergencyViewModel.EmergencyState.Error).message
                    Snackbar(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}
