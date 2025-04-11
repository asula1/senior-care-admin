import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Paper,
  Typography,
  Card,
  CardContent,
  CardActions,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  CircularProgress,
  Alert,
  useTheme,
} from '@mui/material';
import {
  People as PeopleIcon,
  Medication as MedicationIcon,
  NotificationsActive as AlertIcon,
  Event as ScheduleIcon,
  Favorite as HeartIcon,
  DirectionsWalk as WalkIcon,
} from '@mui/icons-material';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer,
  LineChart,
  Line,
} from 'recharts';

// 서비스 가져오기
import userService from '../services/userService';
import emergencyService from '../services/emergencyService';
import healthService from '../services/healthService';
import visitService from '../services/visitService';

// 대시보드 페이지 컴포넌트
export default function Dashboard() {
  const navigate = useNavigate();
  const theme = useTheme();
  
  // 상태 관리
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    seniorCount: 0,
    activeAlerts: [],
    upcomingVisits: [],
    medicationStats: { totalMedications: 0, takenToday: 0, missedToday: 0 },
    healthStats: { todayAvgHeartRate: 0, todayAvgSteps: 0 },
  });
  
  // 차트 데이터 상태
  const [alertChartData, setAlertChartData] = useState([]);
  const [healthChartData, setHealthChartData] = useState([]);
  
  // 데이터 가져오기
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        
        // 실제 앱에서는 아래 데이터를 API 호출로 가져옴
        // 현재는 예시 데이터로 대체
        
        // 사용자 통계
        //const usersData = await userService.getUserStats();
        const usersData = { seniorCount: 12, guardianCount: 18, socialWorkerCount: 5 };
        
        // 활성 알림
        //const alertsData = await emergencyService.getActiveAlerts();
        const alertsData = [
          { id: 1, userId: 1, userName: '홍길동', alertType: 'FALL_DETECTED', triggeredAt: new Date().toISOString() },
          { id: 2, userId: 3, userName: '김영희', alertType: 'SOS_BUTTON', triggeredAt: new Date(Date.now() - 30*60000).toISOString() },
        ];
        
        // 방문 일정
        //const visitsData = await visitService.getUpcomingVisits();
        const visitsData = [
          { id: 1, seniorName: '홍길동', socialWorkerName: '박복지', scheduledStartTime: new Date(Date.now() + 2*3600000).toISOString(), visitPurpose: '정기 건강 체크' },
          { id: 2, seniorName: '김영희', socialWorkerName: '이도움', scheduledStartTime: new Date(Date.now() + 5*3600000).toISOString(), visitPurpose: '약물 복용 확인' },
        ];
        
        // 약물 통계
        //const medicationData = await medicationService.getMedicationStats();
        const medicationData = { totalMedications: 45, takenToday: 23, missedToday: 4 };
        
        // 건강 데이터 통계
        //const healthData = await healthService.getHealthStats();
        const healthData = { todayAvgHeartRate: 78, todayAvgSteps: 3200 };
        
        // 차트 데이터 - 알림 유형별 통계
        const alertChart = [
          { name: '낙상', value: 8 },
          { name: 'SOS', value: 5 },
          { name: '심박이상', value: 3 },
          { name: '활동이상', value: 4 },
          { name: '안전구역이탈', value: 2 },
        ];
        
        // 차트 데이터 - 지난 7일간 건강 데이터
        const healthChart = [
          { date: '5/1', heartRate: 75, steps: 3400 },
          { date: '5/2', heartRate: 78, steps: 2900 },
          { date: '5/3', heartRate: 76, steps: 3100 },
          { date: '5/4', heartRate: 74, steps: 3600 },
          { date: '5/5', heartRate: 77, steps: 3200 },
          { date: '5/6', heartRate: 79, steps: 2800 },
          { date: '5/7', heartRate: 76, steps: 3300 },
        ];
        
        setStats({
          seniorCount: usersData.seniorCount,
          activeAlerts: alertsData,
          upcomingVisits: visitsData,
          medicationStats: medicationData,
          healthStats: healthData,
        });
        
        setAlertChartData(alertChart);
        setHealthChartData(healthChart);
        
        setLoading(false);
      } catch (err) {
        console.error('대시보드 데이터 로딩 실패:', err);
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
        setLoading(false);
      }
    };
    
    fetchDashboardData();
  }, []);
  
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress />
      </Box>
    );
  }
  
  if (error) {
    return (
      <Box sx={{ p: 2 }}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }
  
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Typography variant="h4" gutterBottom>
        대시보드
      </Typography>
      
      {/* 요약 통계 카드 */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'primary.light', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <PeopleIcon sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h3">{stats.seniorCount}</Typography>
                  <Typography variant="body2">등록 노인</Typography>
                </Box>
              </Box>
            </CardContent>
            <CardActions>
              <Button 
                size="small" 
                color="inherit" 
                onClick={() => navigate('/seniors')}
              >
                노인 목록 보기
              </Button>
            </CardActions>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'error.light', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <AlertIcon sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h3">{stats.activeAlerts.length}</Typography>
                  <Typography variant="body2">활성 알림</Typography>
                </Box>
              </Box>
            </CardContent>
            <CardActions>
              <Button 
                size="small" 
                color="inherit" 
                onClick={() => navigate('/emergency-alerts')}
              >
                알림 확인하기
              </Button>
            </CardActions>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'success.light', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <MedicationIcon sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h3">
                    {stats.medicationStats.takenToday}/{stats.medicationStats.totalMedications}
                  </Typography>
                  <Typography variant="body2">오늘 복약 현황</Typography>
                </Box>
              </Box>
            </CardContent>
            <CardActions>
              <Button 
                size="small" 
                color="inherit" 
                onClick={() => navigate('/medications')}
              >
                약물 관리
              </Button>
            </CardActions>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'info.light', color: 'white' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <ScheduleIcon sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h3">{stats.upcomingVisits.length}</Typography>
                  <Typography variant="body2">오늘 예정된 방문</Typography>
                </Box>
              </Box>
            </CardContent>
            <CardActions>
              <Button 
                size="small" 
                color="inherit" 
                onClick={() => navigate('/visit-schedule')}
              >
                일정 보기
              </Button>
            </CardActions>
          </Card>
        </Grid>
      </Grid>
      
      {/* 차트 및 리스트 */}
      <Grid container spacing={3}>
        {/* 비상 알림 리스트 */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              <AlertIcon sx={{ mr: 1, verticalAlign: 'middle', color: 'error.main' }} />
              활성 비상 알림
            </Typography>
            <Divider sx={{ my: 1 }} />
            
            {stats.activeAlerts.length > 0 ? (
              <List>
                {stats.activeAlerts.map((alert) => (
                  <React.Fragment key={alert.id}>
                    <ListItem 
                      button
                      onClick={() => navigate(`/emergency-alerts`)}
                    >
                      <ListItemIcon sx={{ color: 'error.main' }}>
                        <AlertIcon />
                      </ListItemIcon>
                      <ListItemText 
                        primary={`${alert.userName} - ${getAlertTypeText(alert.alertType)}`}
                        secondary={new Date(alert.triggeredAt).toLocaleString()}
                      />
                    </ListItem>
                    <Divider />
                  </React.Fragment>
                ))}
              </List>
            ) : (
              <Box sx={{ p: 2, textAlign: 'center' }}>
                <Typography color="textSecondary">
                  활성 알림이 없습니다
                </Typography>
              </Box>
            )}
          </Paper>
        </Grid>
        
        {/* 알림 유형 통계 차트 */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              알림 유형별 통계 (지난 30일)
            </Typography>
            <Divider sx={{ my: 1 }} />
            
            <Box sx={{ height: 300, pt: 2 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={alertChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar 
                    dataKey="value" 
                    name="알림 수" 
                    fill={theme.palette.error.main} 
                  />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>
        
        {/* 예정된 방문 */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              <ScheduleIcon sx={{ mr: 1, verticalAlign: 'middle', color: 'info.main' }} />
              오늘 예정된 방문
            </Typography>
            <Divider sx={{ my: 1 }} />
            
            {stats.upcomingVisits.length > 0 ? (
              <List>
                {stats.upcomingVisits.map((visit) => (
                  <React.Fragment key={visit.id}>
                    <ListItem 
                      button
                      onClick={() => navigate('/visit-schedule')}
                    >
                      <ListItemText 
                        primary={`${visit.seniorName} - ${visit.visitPurpose}`}
                        secondary={`${new Date(visit.scheduledStartTime).toLocaleTimeString()} | 담당: ${visit.socialWorkerName}`}
                      />
                    </ListItem>
                    <Divider />
                  </React.Fragment>
                ))}
              </List>
            ) : (
              <Box sx={{ p: 2, textAlign: 'center' }}>
                <Typography color="textSecondary">
                  오늘 예정된 방문이 없습니다
                </Typography>
              </Box>
            )}
          </Paper>
        </Grid>
        
        {/* 건강 데이터 통계 차트 */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, height: '100%' }}>
            <Typography variant="h6" gutterBottom>
              평균 건강 지표 (지난 7일)
            </Typography>
            <Divider sx={{ my: 1 }} />
            
            <Box sx={{ mt: 2, mb: 2, display: 'flex', justifyContent: 'space-around' }}>
              <Box sx={{ textAlign: 'center' }}>
                <HeartIcon sx={{ color: 'error.main', fontSize: 40 }} />
                <Typography variant="h5">{stats.healthStats.todayAvgHeartRate}</Typography>
                <Typography variant="body2" color="textSecondary">평균 심박수</Typography>
              </Box>
              
              <Box sx={{ textAlign: 'center' }}>
                <WalkIcon sx={{ color: 'info.main', fontSize: 40 }} />
                <Typography variant="h5">{stats.healthStats.todayAvgSteps}</Typography>
                <Typography variant="body2" color="textSecondary">평균 걸음 수</Typography>
              </Box>
            </Box>
            
            <Box sx={{ height: 200 }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={healthChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis yAxisId="left" />
                  <YAxis yAxisId="right" orientation="right" />
                  <Tooltip />
                  <Legend />
                  <Line 
                    yAxisId="left"
                    type="monotone" 
                    dataKey="heartRate" 
                    name="심박수" 
                    stroke={theme.palette.error.main} 
                    activeDot={{ r: 8 }} 
                  />
                  <Line 
                    yAxisId="right"
                    type="monotone" 
                    dataKey="steps" 
                    name="걸음 수" 
                    stroke={theme.palette.info.main} 
                  />
                </LineChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

// 알림 유형 텍스트 변환 함수
function getAlertTypeText(alertType) {
  const alertTypes = {
    FALL_DETECTED: '낙상 감지',
    SOS_BUTTON: 'SOS 버튼',
    ABNORMAL_HEART_RATE: '비정상 심박수',
    ABNORMAL_ACTIVITY: '비정상 활동',
    LEAVING_SAFE_ZONE: '안전구역 이탈',
    INACTIVITY: '장시간 비활동',
  };
  
  return alertTypes[alertType] || alertType;
}
