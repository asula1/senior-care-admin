import axios from 'axios';

const API_URL = '/api/users';

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

// 사용자 관련 서비스 함수들
const userService = {
  // 모든 사용자 가져오기
  getAllUsers: async () => {
    const response = await api.get('/');
    return response.data;
  },

  // 특정 역할의 사용자들 가져오기
  getUsersByRole: async (role) => {
    const response = await api.get(`/role/${role}`);
    return response.data;
  },

  // 특정 사용자 정보 가져오기
  getUserById: async (userId) => {
    const response = await api.get(`/${userId}`);
    return response.data;
  },

  // 현재 로그인한 사용자 정보 가져오기
  getCurrentUser: async () => {
    const response = await api.get('/me');
    return response.data;
  },

  // 사용자 정보 업데이트
  updateUser: async (userId, userData) => {
    const response = await api.put(`/${userId}`, userData);
    return response.data;
  },

  // 사용자 비활성화
  deactivateUser: async (userId) => {
    const response = await api.put(`/${userId}/deactivate`);
    return response.data;
  },

  // 사용자 재활성화
  reactivateUser: async (userId) => {
    const response = await api.put(`/${userId}/reactivate`);
    return response.data;
  },

  // 사용자 통계 가져오기 (관리자 대시보드용)
  getUserStats: async () => {
    const seniors = await userService.getUsersByRole('ROLE_SENIOR');
    const guardians = await userService.getUsersByRole('ROLE_GUARDIAN');
    const socialWorkers = await userService.getUsersByRole('ROLE_SOCIAL_WORKER');
    
    return {
      seniorCount: seniors.length,
      guardianCount: guardians.length,
      socialWorkerCount: socialWorkers.length,
    };
  },
};

export default userService;
