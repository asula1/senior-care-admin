import axios from 'axios';

const API_URL = '/api/visits';

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

// 방문 일정 관련 서비스 함수들
const visitService = {
  // 모든 예정된 방문 가져오기
  getUpcomingVisits: async () => {
    const response = await api.get('/upcoming');
    return response.data;
  },

  // 특정 노인의 예정된 방문 가져오기
  getUpcomingVisitsBySenior: async (seniorId) => {
    const response = await api.get(`/senior/${seniorId}/upcoming`);
    return response.data;
  },

  // 특정 노인의 모든 방문 가져오기
  getVisitsBySenior: async (seniorId) => {
    const response = await api.get(`/senior/${seniorId}`);
    return response.data;
  },

  // 특정 복지사의 방문 가져오기
  getVisitsBySocialWorker: async (socialWorkerId) => {
    const response = await api.get(`/social-worker/${socialWorkerId}`);
    return response.data;
  },

  // 특정 날짜 범위의 방문 가져오기
  getVisitsByDateRange: async (userId, startDate, endDate) => {
    const response = await api.get(`/range`, {
      params: { userId, startDate, endDate },
    });
    return response.data;
  },

  // 방문 일정 등록하기
  scheduleVisit: async (visitData) => {
    const response = await api.post('/', visitData);
    return response.data;
  },

  // 방문 상태 업데이트하기
  updateVisitStatus: async (visitId, statusData) => {
    const response = await api.put(`/${visitId}/status`, statusData);
    return response.data;
  },

  // 방문 일정 변경하기
  rescheduleVisit: async (visitId, newStartTime, newEndTime) => {
    const response = await api.put(`/${visitId}/reschedule`, null, {
      params: { newStartTime, newEndTime },
    });
    return response.data;
  },

  // 방문 취소하기
  cancelVisit: async (visitId, reason) => {
    const response = await api.put(`/${visitId}/cancel`, null, {
      params: { reason },
    });
    return response.data;
  },
};

export default visitService;
