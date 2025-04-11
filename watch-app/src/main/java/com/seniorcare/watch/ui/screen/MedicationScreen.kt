package com.seniorcare.watch.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavController
import java.text.SimpleDateFormat
import java.util.*

data class MedicationItem(
    val id: Long,
    val name: String,
    val dosage: String,
    val time: String,
    val taken: Boolean = false
)

@Composable
fun MedicationScreen(navController: SwipeDismissableNavController) {
    val scalingLazyListState = rememberScalingLazyListState()
    
    // 약물 목록 상태 (실제로는 ViewModel에서 가져와야 함)
    var medications by remember {
        mutableStateOf(
            listOf(
                MedicationItem(1, "혈압약", "1정", "08:00"),
                MedicationItem(2, "당뇨약", "1정", "08:00"),
                MedicationItem(3, "관절약", "1정", "12:00"),
                MedicationItem(4, "혈압약", "1정", "18:00"),
                MedicationItem(5, "당뇨약", "1정", "18:00"),
                MedicationItem(6, "수면제", "1정", "22:00")
            )
        )
    }
    
    // 현재 시간을 기준으로 다음 복용 약물 표시
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    
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
                    text = "약물 관리",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // 오늘의 약물 복용 안내
            item {
                Text(
                    text = "오늘의 약물",
                    style = MaterialTheme.typography.title3,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // 약물 목록
            medications.forEachIndexed { index, medication ->
                item {
                    MedicationItemCard(
                        medication = medication,
                        onCheckedChange = { checked ->
                            medications = medications.toMutableList().apply {
                                this[index] = medication.copy(taken = checked)
                            }
                        }
                    )
                }
            }
            
            // 뒤로가기 버튼
            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("뒤로")
                }
            }
        }
    }
}

@Composable
fun MedicationItemCard(
    medication: MedicationItem,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onCheckedChange(!medication.taken) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "${medication.dosage} - ${medication.time}",
                    style = MaterialTheme.typography.caption2
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 복용 여부 체크 표시
            if (medication.taken) {
                Icon(
                    androidx.wear.compose.material.Icons.Default.Check,
                    contentDescription = "복용 완료",
                    tint = Color.Green
                )
            } else {
                Icon(
                    androidx.wear.compose.material.Icons.Default.Close,
                    contentDescription = "미복용",
                    tint = Color.Red.copy(alpha = 0.7f)
                )
            }
        }
    }
}
