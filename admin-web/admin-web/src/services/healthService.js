import axios from 'axios';

const API_URL = '/api/health';

// API 호출을 위한 인스턴스 생성
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 설정 - 인증 토큰 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 건강 데이터 관련 서비스 함수들
const healthService = {
  // 특정 사용자의 건강 데이터 가져오기
  getUserHealthData: async (userId) => {
    const response = await api.get(`/user/${userId}`);
    return response.data;
  },

  // 특정 사용자의 특정 기간 건강 데이터 가져오기
  getUserHealthDataByTimeRange: async (userId, startTime, endTime) => {
    const response = await api.get(`/user/${userId}/range`, {
      params: { startTime, endTime },
    });
    return response.data;
  },

  // 특정 사용자의 최신 건강 데이터 가져오기
  getLatestHealthData: async (userId) => {
    const response = await api.get(`/user/${userId}/latest`);
    return response.data;
  },

  // 건강 데이터 기록하기
  recordHealthData: async (userId, healthData) => {
    const response = await api.post(`/user/${userId}/record`, healthData);
    return response.data;
  },

  // 건강 데이터 통계 가져오기 (대시보드용)
  getHealthStats: async () => {
    // 실제로는 서버에서 통계 API를 호출해야 함
    // 여기서는 예시로 하드코딩된 값 반환
    return {
      todayAvgHeartRate: 78,
      todayAvgSteps: 3200,
    };
  },

  // 전체 노인들의 평균 건강 데이터 가져오기
  getAverageHealthData: async (startDate, endDate) => {
    const response = await api.get('/average', {
      params: { startDate, endDate },
    });
    return response.data;
  },
};

export default healthService;
