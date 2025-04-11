import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  CardHeader,
  CardActions,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Divider,
  CircularProgress,
  Alert,
  Tab,
  Tabs,
} from '@mui/material';
import {
  NotificationsActive as AlertIcon,
  History as HistoryIcon,
} from '@mui/icons-material';
import { format } from 'date-fns';
import emergencyService from '../services/emergencyService';
import { useAuth } from '../contexts/AuthContext';
import { connectWebSocket, subscribeToTopic, disconnectWebSocket } from '../utils/websocket';

// 탭 패널 컴포넌트
function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`emergency-tabpanel-${index}`}
      aria-labelledby={`emergency-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

export default function EmergencyAlerts() {
  const { user } = useAuth();
  
  // 상태 관리
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeAlerts, setActiveAlerts] = useState([]);
  const [resolvedAlerts, setResolvedAlerts] = useState([]);
  const [selectedAlert, setSelectedAlert] = useState(null);
  const [resolveDialogOpen, setResolveDialogOpen] = useState(false);
  const [resolutionStatus, setResolutionStatus] = useState('RESOLVED');
  const [resolutionNotes, setResolutionNotes] = useState('');
  const [tabValue, setTabValue] = useState(0);
  
  // 알림 데이터 가져오기
  const fetchAlerts = async () => {
    try {
      setLoading(true);
      
      // 활성 알림 가져오기
      const activeAlertsData = await emergencyService.getActiveAlerts();
      setActiveAlerts(activeAlertsData);
      
      // 최근 해결된 알림 가져오기
      try {
        const resolvedAlertsData = await emergencyService.getResolvedAlerts();
        setResolvedAlerts(resolvedAlertsData);
      } catch (err) {
        console.error('해결된 알림 데이터 로딩 실패:', err);
        setResolvedAlerts([]);
      }
      
      setLoading(false);
    } catch (err) {
      console.error('비상 알림 데이터 로딩 실패:', err);
      setError('데이터를 불러오는 중 오류가 발생했습니다.');
      setLoading(false);
    }
  };
  
  // 초기 데이터 로드 및 웹소켓 구독
  useEffect(() => {
    fetchAlerts();
    
    // WebSocket 연결 설정 - 연결 완료 후 구독 시작
    let subscription = null;
    
    const stompClient = connectWebSocket((client) => {
      console.log('WebSocket 연결 완료, 알림 토픽 구독 시작');
      // 연결이 완료된 후 알림 토픽 구독
      subscription = subscribeToTopic('/topic/alerts', (message) => {
        // 새 알림이 오면 목록 새로고침
        fetchAlerts();
      });
    }, (error) => {
      console.error('WebSocket 연결 오류:', error);
      setError('실시간 알림 연결에 실패했습니다. 페이지를 새로고침해 주세요.');
    });
    
    // 컴포넌트 언마운트 시 웹소켓 연결 해제
    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
      disconnectWebSocket();
    };
  }, []);
  
  // 탭 변경 핸들러
  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };
  
  // 알림 해결 다이얼로그 열기
  const handleResolveDialogOpen = (alert) => {
    setSelectedAlert(alert);
    setResolveDialogOpen(true);
    setResolutionStatus('RESOLVED');
    setResolutionNotes('');
  };
  
  // 다이얼로그 닫기
  const handleDialogClose = () => {
    setResolveDialogOpen(false);
    setSelectedAlert(null);
  };
  
  // 알림 해결 처리
  const handleResolveAlert = async () => {
    if (!selectedAlert) return;
    
    try {
      await emergencyService.resolveAlert(selectedAlert.id, {
        alertStatus: resolutionStatus,
        resolutionNotes,
      });
      
      handleDialogClose();
      fetchAlerts();
    } catch (error) {
      console.error('알림 해결 처리 실패:', error);
      setError('알림 해결 처리 중 오류가 발생했습니다.');
    }
  };
  
  // 알림 확인 처리
  const handleAcknowledgeAlert = async (alertId) => {
    try {
      await emergencyService.acknowledgeAlert(alertId);
      fetchAlerts();
    } catch (error) {
      console.error('알림 확인 처리 실패:', error);
      setError('알림 확인 처리 중 오류가 발생했습니다.');
    }
  };
  
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress />
      </Box>
    );
  }
  
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
        <AlertIcon color="error" sx={{ mr: 1 }} /> 비상 알림 관리
      </Typography>
      
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}
      
      <Paper sx={{ width: '100%', mb: 2 }}>
        <Tabs
          value={tabValue}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth"
        >
          <Tab 
            icon={<AlertIcon />} 
            label={`활성 알림 (${activeAlerts.length})`} 
            iconPosition="start"
          />
          <Tab 
            icon={<HistoryIcon />} 
            label="과거 알림" 
            iconPosition="start"
          />
        </Tabs>
        
        <TabPanel value={tabValue} index={0}>
          {activeAlerts.length > 0 ? (
            <Grid container spacing={3}>
              {activeAlerts.map((alert) => (
                <Grid item xs={12} md={6} lg={4} key={alert.id}>
                  <Card 
                    sx={{ 
                      position: 'relative',
                      overflow: 'visible',
                      border: alert.alertStatus === 'ACTIVE' ? '2px solid red' : 'none',
                    }}
                    className={alert.alertStatus === 'ACTIVE' ? "emergency-alert" : ""}
                  >
                    <CardHeader
                      title={alert.userName}
                      subheader={`${getAlertTypeText(alert.alertType)} 발생`}
                      action={
                        <Chip 
                          label={getAlertStatusText(alert.alertStatus)} 
                          color={getStatusColor(alert.alertStatus)}
                        />
                      }
                      sx={{ pb: 0 }}
                    />
                    <CardContent>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" color="text.secondary">
                          발생 시간:
                        </Typography>
                        <Typography variant="body2">
                          {formatDate(alert.triggeredAt)}
                        </Typography>
                      </Box>
                      
                      {alert.locationAddress && (
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                          <Typography variant="body2" color="text.secondary">
                            위치:
                          </Typography>
                          <Typography variant="body2" sx={{ maxWidth: '60%' }}>
                            {alert.locationAddress}
                          </Typography>
                        </Box>
                      )}
                      
                      {alert.heartRate && (
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                          <Typography variant="body2" color="text.secondary">
                            심박수:
                          </Typography>
                          <Typography variant="body2">
                            {alert.heartRate} BPM
                          </Typography>
                        </Box>
                      )}
                    </CardContent>
                    <Divider />
                    <CardActions>
                      {alert.alertStatus === 'ACTIVE' ? (
                        <>
                          <Button 
                            size="small" 
                            variant="outlined"
                            onClick={() => handleAcknowledgeAlert(alert.id)}
                          >
                            확인
                          </Button>
                          <Button 
                            size="small" 
                            variant="contained" 
                            color="primary"
                            onClick={() => handleResolveDialogOpen(alert)}
                          >
                            해결
                          </Button>
                        </>
                      ) : (
                        <Button 
                          size="small" 
                          variant="contained" 
                          color="primary"
                          onClick={() => handleResolveDialogOpen(alert)}
                        >
                          해결 처리
                        </Button>
                      )}
                    </CardActions>
                  </Card>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Typography variant="h6" color="textSecondary">
                활성화된 비상 알림이 없습니다
              </Typography>
            </Box>
          )}
        </TabPanel>
        
        <TabPanel value={tabValue} index={1}>
          {resolvedAlerts.length > 0 ? (
            <Grid container spacing={3}>
              {resolvedAlerts.map((alert) => (
                <Grid item xs={12} md={6} lg={4} key={alert.id}>
                  <Card>
                    <CardHeader
                      title={alert.userName}
                      subheader={`${getAlertTypeText(alert.alertType)} 발생`}
                      action={
                        <Chip 
                          label={getAlertStatusText(alert.alertStatus)} 
                          color={getStatusColor(alert.alertStatus)}
                        />
                      }
                      sx={{ pb: 0 }}
                    />
                    <CardContent>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" color="text.secondary">
                          발생 시간:
                        </Typography>
                        <Typography variant="body2">
                          {formatDate(alert.triggeredAt)}
                        </Typography>
                      </Box>
                      
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" color="text.secondary">
                          해결 시간:
                        </Typography>
                        <Typography variant="body2">
                          {formatDate(alert.resolvedAt)}
                        </Typography>
                      </Box>
                      
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" color="text.secondary">
                          처리자:
                        </Typography>
                        <Typography variant="body2">
                          {alert.resolvedByUserName}
                        </Typography>
                      </Box>
                      
                      {alert.resolutionNotes && (
                        <Box sx={{ mt: 2 }}>
                          <Typography variant="body2" color="text.secondary">
                            메모:
                          </Typography>
                          <Typography variant="body2" sx={{ mt: 0.5 }}>
                            {alert.resolutionNotes}
                          </Typography>
                        </Box>
                      )}
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Typography variant="h6" color="textSecondary">
                과거 비상 알림 기록이 없습니다
              </Typography>
            </Box>
          )}
        </TabPanel>
      </Paper>
      
      {/* 알림 해결 다이얼로그 */}
      <Dialog open={resolveDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>비상 알림 해결 처리</DialogTitle>
        <DialogContent>
          <DialogContentText>
            선택한 알림에 대한 해결 정보를 입력하세요:
          </DialogContentText>
          
          {selectedAlert && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle2">
                {selectedAlert.userName} - {getAlertTypeText(selectedAlert.alertType)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                발생 시간: {formatDate(selectedAlert.triggeredAt)}
              </Typography>
            </Box>
          )}
          
          <FormControl fullWidth margin="normal">
            <InputLabel id="resolution-status-label">처리 상태</InputLabel>
            <Select
              labelId="resolution-status-label"
              value={resolutionStatus}
              label="처리 상태"
              onChange={(e) => setResolutionStatus(e.target.value)}
            >
              <MenuItem value="RESOLVED">해결됨</MenuItem>
              <MenuItem value="FALSE_ALARM">오탐지</MenuItem>
            </Select>
          </FormControl>
          
          <TextField
            autoFocus
            margin="dense"
            id="resolution-notes"
            label="메모"
            type="text"
            fullWidth
            multiline
            rows={4}
            variant="outlined"
            value={resolutionNotes}
            onChange={(e) => setResolutionNotes(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose}>취소</Button>
          <Button onClick={handleResolveAlert} variant="contained" color="primary">
            처리 완료
          </Button>
        </DialogActions>
      </Dialog>
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

// 알림 상태 텍스트 변환 함수
function getAlertStatusText(alertStatus) {
  const statusTexts = {
    ACTIVE: '활성',
    ACKNOWLEDGED: '확인됨',
    RESOLVED: '해결됨',
    FALSE_ALARM: '오탐지',
  };
  
  return statusTexts[alertStatus] || alertStatus;
}

// 알림 상태별 색상 지정 함수
function getStatusColor(alertStatus) {
  const statusColors = {
    ACTIVE: 'error',
    ACKNOWLEDGED: 'warning',
    RESOLVED: 'success',
    FALSE_ALARM: 'default',
  };
  
  return statusColors[alertStatus] || 'default';
}

// 날짜 포맷 함수
function formatDate(dateString) {
  if (!dateString) return '';
  
  try {
    const date = new Date(dateString);
    return format(date, 'yyyy-MM-dd HH:mm');
  } catch (error) {
    console.error('날짜 포맷 오류:', error);
    return dateString;
  }
}
