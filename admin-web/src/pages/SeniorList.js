import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  Avatar,
  Chip,
  TextField,
  InputAdornment,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  CircularProgress,
  Alert,
  Tooltip,
} from '@mui/material';
import {
  People as PeopleIcon,
  Search as SearchIcon,
  Favorite as HeartIcon,
  DirectionsWalk as WalkIcon,
  Message as MessageIcon,
  Phone as PhoneIcon,
  CalendarMonth as CalendarIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import userService from '../services/userService';
import healthService from '../services/healthService';
import { format } from 'date-fns';

export default function SeniorList() {
  const navigate = useNavigate();
  
  // 상태 관리
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [seniors, setSeniors] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  
  // 신규 노인 등록 폼 상태
  const [newSenior, setNewSenior] = useState({
    fullName: '',
    age: '',
    phoneNumber: '',
    address: '',
    guardianName: '',
    guardianPhone: '',
    notes: ''
  });
  
  // 데이터 가져오기
  useEffect(() => {
    const fetchSeniors = async () => {
      try {
        setLoading(true);
        
        // 실제 앱에서는 아래 코드 사용
        // const seniorsData = await userService.getUsersByRole('ROLE_SENIOR');
        
        // 예시 데이터
        const seniorsData = [
          {
            id: 1,
            fullName: '김영수',
            age: 78,
            phoneNumber: '010-1234-5678',
            address: '서울시 강남구 테헤란로 123',
            healthStatus: '양호',
            lastActive: '2023-05-08T10:30:00.000Z',
            healthData: {
              heartRate: 75,
              steps: 3200,
            },
          },
          {
            id: 2,
            fullName: '이미영',
            age: 82,
            phoneNumber: '010-2345-6789',
            address: '서울시 서초구 서초대로 456',
            healthStatus: '주의',
            lastActive: '2023-05-08T09:15:00.000Z',
            healthData: {
              heartRate: 85,
              steps: 1200,
            },
          },
          {
            id: 3,
            fullName: '박재민',
            age: 75,
            phoneNumber: '010-3456-7890',
            address: '서울시 송파구 올림픽로 789',
            healthStatus: '양호',
            lastActive: '2023-05-08T08:45:00.000Z',
            healthData: {
              heartRate: 72,
              steps: 4500,
            },
          },
          {
            id: 4,
            fullName: '최수진',
            age: 80,
            phoneNumber: '010-4567-8901',
            address: '서울시 강서구 강서로 321',
            healthStatus: '주의',
            lastActive: '2023-05-07T19:30:00.000Z',
            healthData: {
              heartRate: 88,
              steps: 800,
            },
          },
          {
            id: 5,
            fullName: '정민호',
            age: 76,
            phoneNumber: '010-5678-9012',
            address: '서울시 마포구 홍대로 654',
            healthStatus: '양호',
            lastActive: '2023-05-08T11:00:00.000Z',
            healthData: {
              heartRate: 70,
              steps: 3800,
            },
          },
          {
            id: 6,
            fullName: '한지은',
            age: 79,
            phoneNumber: '010-6789-0123',
            address: '서울시 용산구 이태원로 987',
            healthStatus: '양호',
            lastActive: '2023-05-08T10:15:00.000Z',
            healthData: {
              heartRate: 73,
              steps: 2900,
            },
          },
        ];
        
        setSeniors(seniorsData);
        setLoading(false);
      } catch (error) {
        console.error('노인 데이터 로딩 실패:', error);
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
        setLoading(false);
      }
    };
    
    fetchSeniors();
  }, []);
  
  // 검색어로 필터링
  const filteredSeniors = seniors.filter((senior) =>
    senior.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    senior.address.toLowerCase().includes(searchQuery.toLowerCase()) ||
    senior.phoneNumber.includes(searchQuery)
  );
  
  // 노인 추가 다이얼로그 열기
  const handleAddDialogOpen = () => {
    setAddDialogOpen(true);
  };
  
  // 다이얼로그 닫기
  const handleDialogClose = () => {
    setAddDialogOpen(false);
    // 폼 초기화
    setNewSenior({
      fullName: '',
      age: '',
      phoneNumber: '',
      address: '',
      guardianName: '',
      guardianPhone: '',
      notes: ''
    });
  };
  
  // 입력 필드 변경 핸들러
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewSenior({
      ...newSenior,
      [name]: value
    });
  };
  
  // 노인 등록 제출 핸들러
  const handleSubmit = async () => {
    // 기본 유효성 검사
    if (!newSenior.fullName || !newSenior.age || !newSenior.phoneNumber || !newSenior.address) {
      setError('필수 정보를 모두 입력해주세요.');
      return;
    }
    
    try {
      // 실제 앱에서는 아래 코드 사용
      // const response = await userService.createUser({
      //   ...newSenior,
      //   roles: ['ROLE_SENIOR']
      // });
      
      // 더미 코드
      const newId = seniors.length + 1;
      const newUser = {
        id: newId,
        ...newSenior,
        healthStatus: '양호',
        lastActive: new Date().toISOString(),
        healthData: {
          heartRate: 75,
          steps: 0,
        },
      };
      
      setSeniors([...seniors, newUser]);
      handleDialogClose();
    } catch (error) {
      console.error('노인 등록 실패:', error);
      setError('노인 등록 중 오류가 발생했습니다.');
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
          <PeopleIcon sx={{ mr: 1 }} /> 노인 관리
        </Typography>
        
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleAddDialogOpen}
        >
          노인 등록
        </Button>
      </Box>
      
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}
      
      {/* 검색 필드 */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="이름, 주소, 전화번호로 검색"
          variant="outlined"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
            endAdornment: searchQuery && (
              <InputAdornment position="end">
                <IconButton onClick={() => setSearchQuery('')} edge="end">
                  <Typography variant="body2" color="primary">
                    지우기
                  </Typography>
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
      </Paper>
      
      {/* 노인 목록 */}
      <Grid container spacing={3}>
        {filteredSeniors.length > 0 ? (
          filteredSeniors.map((senior) => (
            <Grid item xs={12} md={6} lg={4} key={senior.id}>
              <Card>
                <CardHeader
                  avatar={
                    <Avatar sx={{ bgcolor: getHealthStatusColor(senior.healthStatus) }}>
                      {senior.fullName.charAt(0)}
                    </Avatar>
                  }
                  title={senior.fullName}
                  subheader={`${senior.age}세`}
                  action={
                    <Chip 
                      label={senior.healthStatus} 
                      color={getHealthStatusColor(senior.healthStatus, true)}
                      variant="outlined"
                    />
                  }
                />
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <HeartIcon sx={{ color: 'error.main', mr: 1, fontSize: 20 }} />
                    <Typography variant="body2">
                      {senior.healthData.heartRate} BPM
                    </Typography>
                    <Box sx={{ mx: 2 }}>|</Box>
                    <WalkIcon sx={{ color: 'primary.main', mr: 1, fontSize: 20 }} />
                    <Typography variant="body2">
                      {senior.healthData.steps} 걸음
                    </Typography>
                  </Box>
                  
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <PhoneIcon sx={{ color: 'text.secondary', mr: 1, fontSize: 20 }} />
                    <Typography variant="body2">
                      {senior.phoneNumber}
                    </Typography>
                  </Box>
                  
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mb: 1, display: 'flex', alignItems: 'flex-start' }}
                  >
                    <Typography component="span" sx={{ mr: 1, minWidth: '1.2rem' }}>
                      📍
                    </Typography>
                    {senior.address}
                  </Typography>
                  
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ display: 'flex', alignItems: 'center' }}
                  >
                    <CalendarIcon sx={{ color: 'text.secondary', mr: 1, fontSize: 20 }} />
                    마지막 활동: {formatDate(senior.lastActive)}
                  </Typography>
                </CardContent>
                <CardActions>
                  <Tooltip title="메시지 보내기">
                    <IconButton color="primary">
                      <MessageIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="전화 걸기">
                    <IconButton color="primary">
                      <PhoneIcon />
                    </IconButton>
                  </Tooltip>
                  <Button
                    size="small"
                    variant="outlined"
                    sx={{ ml: 'auto' }}
                    onClick={() => navigate(`/seniors/${senior.id}`)}
                  >
                    상세 정보
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))
        ) : (
          <Grid item xs={12}>
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Typography variant="h6" color="textSecondary">
                검색 결과가 없습니다
              </Typography>
            </Box>
          </Grid>
        )}
      </Grid>
      
      {/* 노인 등록 다이얼로그 */}
      <Dialog open={addDialogOpen} onClose={handleDialogClose} maxWidth="sm" fullWidth>
        <DialogTitle>노인 등록</DialogTitle>
        <DialogContent>
          <DialogContentText>
            새로운 노인 사용자 정보를 입력하세요:
          </DialogContentText>
          
          <TextField
            autoFocus
            margin="dense"
            name="fullName"
            label="이름"
            type="text"
            fullWidth
            variant="outlined"
            value={newSenior.fullName}
            onChange={handleInputChange}
            required
          />
          
          <TextField
            margin="dense"
            name="age"
            label="나이"
            type="number"
            fullWidth
            variant="outlined"
            value={newSenior.age}
            onChange={handleInputChange}
            required
          />
          
          <TextField
            margin="dense"
            name="phoneNumber"
            label="전화번호"
            type="text"
            fullWidth
            variant="outlined"
            value={newSenior.phoneNumber}
            onChange={handleInputChange}
            required
          />
          
          <TextField
            margin="dense"
            name="address"
            label="주소"
            type="text"
            fullWidth
            variant="outlined"
            value={newSenior.address}
            onChange={handleInputChange}
            required
          />
          
          <TextField
            margin="dense"
            name="guardianName"
            label="보호자 이름"
            type="text"
            fullWidth
            variant="outlined"
            value={newSenior.guardianName}
            onChange={handleInputChange}
          />
          
          <TextField
            margin="dense"
            name="guardianPhone"
            label="보호자 연락처"
            type="text"
            fullWidth
            variant="outlined"
            value={newSenior.guardianPhone}
            onChange={handleInputChange}
          />
          
          <TextField
            margin="dense"
            name="notes"
            label="비고"
            type="text"
            fullWidth
            multiline
            rows={3}
            variant="outlined"
            value={newSenior.notes}
            onChange={handleInputChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose}>취소</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            등록
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

// 건강 상태에 따른 색상 반환 함수
function getHealthStatusColor(status, isChip = false) {
  switch (status) {
    case '위험':
      return isChip ? 'error' : 'error.main';
    case '주의':
      return isChip ? 'warning' : 'warning.main';
    case '양호':
      return isChip ? 'success' : 'success.main';
    default:
      return isChip ? 'default' : 'grey.500';
  }
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
