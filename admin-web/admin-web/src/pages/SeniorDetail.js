import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Button,
  Tabs,
  Tab,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
  Chip,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Person as PersonIcon,
  Favorite as HeartIcon,
  LocalHospital as MedicalIcon,
  NotificationsActive as AlertIcon,
  History as HistoryIcon,
  CalendarMonth as CalendarIcon,
  ArrowBack as ArrowBackIcon,
} from '@mui/icons-material';
import userService from '../services/userService';
import healthService from '../services/healthService';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

// 탭 패널 컴포넌트
function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`senior-detail-tabpanel-${index}`}
      aria-labelledby={`senior-detail-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

export default function SeniorDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  // 상태 관리
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [senior, setSenior] = useState(null);
  const [healthData, setHealthData] = useState([]);
  const [tabValue, setTabValue] = useState(0);
  
  // 데이터 가져오기
  useEffect(() => {
    const fetchSeniorData = async () => {
      try {
        setLoading(true);
        
        // 실제 앱에서는 아래 코드 사용
        // const seniorData = await userService.getUserById(id);
        // const healthData = await healthService.getUserHealthDataByTimeRange(
        //   id, 
        //   new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
        //   new Date()
        // );
        
        // 예시 데이터
        const seniorData = {
          id: parseInt(id),
          fullName: '김영수',
          age: 78,
          phoneNumber: '010-1234-5678',
          email: 'youngsoo.kim@example.com',
          address: '서울시 강남구 테헤란로 123',
          healthStatus: '양호',
          lastActive: '2023-05-08T10:30:00.000Z',
          emergencyContact: '박보호 (아들), 010-9876-5432',
          medicalInfo: '고혈압, 당뇨, 관절염',
          notes: '2022년 12월 낙상 경험 있음. 계단 사용 시 주의 필요.',
          registeredDate: '2022-04-15T09:00:00.000Z',
        };
        
        const healthHistoryData = [
          { date: '5/1', heartRate: 75, steps: 3400, sleepHours: 7.2 },
          { date: '5/2', heartRate: 78, steps: 2900, sleepHours: 6.8 },
          { date: '5/3', heartRate: 76, steps: 3100, sleepHours: 7.5 },
          { date: '5/4', heartRate: 74, steps: 3600, sleepHours: 7.0 },
          { date: '5/5', heartRate: 77, steps: 3200, sleepHours: 6.5 },
          { date: '5/6', heartRate: 79, steps: 2800, sleepHours: 7.3 },
          { date: '5/7', heartRate: 76, steps: 3300, sleepHours: 7.1 },
        ];
        
        setSenior(seniorData);
        setHealthData(healthHistoryData);
        setLoading(false);
      } catch (error) {
        console.error('노인 상세 정보 로딩 실패:', error);
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
        setLoading(false);
      }
    };
    
    fetchSeniorData();
  }, [id]);
  
  // 탭 변경 핸들러
  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };
  
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
        <Button
          variant="outlined"
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/seniors')}
          sx={{ mt: 2 }}
        >
          노인 목록으로 돌아가기
        </Button>
      </Box>
    );
  }
  
  if (!senior) {
    return (
      <Box sx={{ p: 2 }}>
        <Alert severity="warning">해당 노인 정보를 찾을 수 없습니다.</Alert>
        <Button
          variant="outlined"
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/seniors')}
          sx={{ mt: 2 }}
        >
          노인 목록으로 돌아가기
        </Button>
      </Box>
    );
  }
  
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box sx={{ mb: 3 }}>
        <Button
          variant="outlined"
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/seniors')}
        >
          목록으로 돌아가기
        </Button>
      </Box>
      
      <Grid container spacing={3}>
        {/* 기본 정보 카드 */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardHeader
              avatar={
                <Avatar sx={{ width: 60, height: 60, bgcolor: 'primary.main', fontSize: '1.5rem' }}>
                  {senior.fullName.charAt(0)}
                </Avatar>
              }
              title={
                <Typography variant="h5" component="div">
                  {senior.fullName}
                </Typography>
              }
              subheader={`${senior.age}세`}
            />
            <CardContent>
              <List>
                <ListItem>
                  <ListItemIcon>
                    <PersonIcon />
                  </ListItemIcon>
                  <ListItemText
                    primary="전화번호"
                    secondary={senior.phoneNumber}
                  />
                </ListItem>
                
                {senior.email && (
                  <ListItem>
                    <ListItemIcon>
                      <PersonIcon />
                    </ListItemIcon>
                    <ListItemText
                      primary="이메일"
                      secondary={senior.email}
                    />
                  </ListItem>
                )}
                
                <ListItem>
                  <ListItemIcon>
                    <PersonIcon />
                  </ListItemIcon>
                  <ListItemText
                    primary="주소"
                    secondary={senior.address}
                  />
                </ListItem>
                
                <Divider sx={{ my: 1 }} />
                
                <ListItem>
                  <ListItemIcon>
                    <PersonIcon />
                  </ListItemIcon>
                  <ListItemText
                    primary="비상 연락처"
                    secondary={senior.emergencyContact}
                  />
                </ListItem>
                
                <Divider sx={{ my: 1 }} />
                
                <ListItem>
                  <ListItemIcon>
                    <MedicalIcon />
                  </ListItemIcon>
                  <ListItemText
                    primary="건강 상태"
                    secondary={
                      <>
                        <Chip 
                          label={senior.healthStatus} 
                          color={
                            senior.healthStatus === '양호' ? 'success' : 
                            senior.healthStatus === '주의' ? 'warning' : 'error'
                          }
                          size="small"
                          sx={{ mr: 1 }}
                        />
                        {senior.medicalInfo}
                      </>
                    }
                  />
                </ListItem>
                
                <ListItem>
                  <ListItemIcon>
                    <CalendarIcon />
                  </ListItemIcon>
                  <ListItemText
                    primary="등록일"
                    secondary={new Date(senior.registeredDate).toLocaleDateString()}
                  />
                </ListItem>
              </List>
              
              {senior.notes && (
                <>
                  <Divider sx={{ my: 1 }} />
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    메모
                  </Typography>
                  <Typography variant="body2">
                    {senior.notes}
                  </Typography>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>
        
        {/* 상세 정보 탭 */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ width: '100%', mb: 2 }}>
            <Tabs
              value={tabValue}
              onChange={handleTabChange}
              indicatorColor="primary"
              textColor="primary"
              variant="fullWidth"
            >
              <Tab icon={<HeartIcon />} label="건강 데이터" iconPosition="start" />
              <Tab icon={<MedicalIcon />} label="약물 정보" iconPosition="start" />
              <Tab icon={<AlertIcon />} label="비상 알림 기록" iconPosition="start" />
              <Tab icon={<HistoryIcon />} label="활동 기록" iconPosition="start" />
            </Tabs>
            
            {/* 건강 데이터 탭 */}
            <TabPanel value={tabValue} index={0}>
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <Card>
                    <CardHeader title="최근 7일 건강 추이" />
                    <CardContent>
                      <Box sx={{ height: 300 }}>
                        <ResponsiveContainer width="100%" height="100%">
                          <LineChart data={healthData}>
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
                              stroke="#FF5252" 
                              activeDot={{ r: 8 }} 
                            />
                            <Line 
                              yAxisId="right"
                              type="monotone" 
                              dataKey="steps" 
                              name="걸음 수" 
                              stroke="#4C84FF" 
                            />
                          </LineChart>
                        </ResponsiveContainer>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Card>
                    <CardHeader title="수면 정보" />
                    <CardContent>
                      <Box sx={{ height: 200 }}>
                        <ResponsiveContainer width="100%" height="100%">
                          <LineChart data={healthData}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="date" />
                            <YAxis domain={[0, 10]} />
                            <Tooltip />
                            <Line 
                              type="monotone" 
                              dataKey="sleepHours" 
                              name="수면 시간" 
                              stroke="#8884d8" 
                              activeDot={{ r: 8 }} 
                            />
                          </LineChart>
                        </ResponsiveContainer>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Card>
                    <CardHeader title="건강 요약" />
                    <CardContent>
                      <List>
                        <ListItem>
                          <ListItemIcon>
                            <HeartIcon color="error" />
                          </ListItemIcon>
                          <ListItemText
                            primary="평균 심박수"
                            secondary={`${Math.round(healthData.reduce((acc, item) => acc + item.heartRate, 0) / healthData.length)} BPM`}
                          />
                        </ListItem>
                        <ListItem>
                          <ListItemIcon>
                            <PersonIcon color="primary" />
                          </ListItemIcon>
                          <ListItemText
                            primary="평균 걸음 수"
                            secondary={`${Math.round(healthData.reduce((acc, item) => acc + item.steps, 0) / healthData.length)} 걸음`}
                          />
                        </ListItem>
                        <ListItem>
                          <ListItemIcon>
                            <CalendarIcon color="info" />
                          </ListItemIcon>
                          <ListItemText
                            primary="평균 수면 시간"
                            secondary={`${(healthData.reduce((acc, item) => acc + item.sleepHours, 0) / healthData.length).toFixed(1)} 시간`}
                          />
                        </ListItem>
                      </List>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>
            </TabPanel>
            
            {/* 약물 정보 탭 */}
            <TabPanel value={tabValue} index={1}>
              <Typography variant="body1">
                약물 정보는 아직 구현되지 않았습니다.
              </Typography>
            </TabPanel>
            
            {/* 비상 알림 기록 탭 */}
            <TabPanel value={tabValue} index={2}>
              <Typography variant="body1">
                비상 알림 기록은 아직 구현되지 않았습니다.
              </Typography>
            </TabPanel>
            
            {/* 활동 기록 탭 */}
            <TabPanel value={tabValue} index={3}>
              <Typography variant="body1">
                활동 기록은 아직 구현되지 않았습니다.
              </Typography>
            </TabPanel>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}
