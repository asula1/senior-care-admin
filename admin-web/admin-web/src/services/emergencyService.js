import axios from 'axios';

const API_URL = '/api/alerts';

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

// 비상 알림 관련 서비스 함수들
const emergencyService = {
  // 모든 활성 알림 가져오기
  getActiveAlerts: async () => {
    const response = await api.get('/active');
    return response.data;
  },

  // 해결된 알림 가져오기
  getResolvedAlerts: async () => {
    const response = await api.get('/resolved');
    return response.data;
  },

  // 특정 사용자의 활성 알림 가져오기
  getActiveAlertsByUser: async (userId) => {
    const response = await api.get(`/user/${userId}/active`);
    return response.data;
  },

  // 특정 사용자의 모든 알림 가져오기
  getAllAlertsByUser: async (userId) => {
    const response = await api.get(`/user/${userId}`);
    return response.data;
  },

  // 알림 해결 처리
  resolveAlert: async (alertId, resolution) => {
    const response = await api.put(`/${alertId}/resolve`, resolution);
    return response.data;
  },

  // 알림 확인 처리
  acknowledgeAlert: async (alertId) => {
    const response = await api.put(`/${alertId}/acknowledge`);
    return response.data;
  },

  // WebSocket 연결 설정 (실제 구현에서는 SockJS/STOMP 사용)
  connectWebSocket: (onMessageReceived) => {
    // WebSocket 연결 로직
    console.log('WebSocket 연결 설정');
    
    // 연결 해제 함수 반환
    return () => {
      console.log('WebSocket 연결 해제');
    };
  }
};

export default emergencyService;
