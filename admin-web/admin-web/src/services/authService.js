import axios from 'axios';

const API_URL = '/api/auth';

// API 요청을 위한 Axios 인스턴스 생성
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

// 응답 인터셉터 설정 - 401 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 인증 관련 서비스 함수들
const authService = {
  // 로그인
  login: async (username, password) => {
    const response = await api.post('/signin', { username, password });
    return response.data;
  },

  // 회원가입
  register: async (userData) => {
    const response = await api.post('/signup', userData);
    return response.data;
  },

  // 현재 사용자 정보 가져오기
  getCurrentUser: async () => {
    const response = await axios.get('/api/users/me', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
    return response.data;
  },

  // 비밀번호 변경
  changePassword: async (oldPassword, newPassword) => {
    const response = await axios.put(
      '/api/users/password',
      { oldPassword, newPassword },
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      }
    );
    return response.data;
  },
};

export default authService;
