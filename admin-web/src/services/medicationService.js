import axios from 'axios';

const API_URL = '/api/medications';

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

// 약물 관리 관련 서비스 함수들
const medicationService = {
  // 특정 사용자의 모든 약물 정보 가져오기
  getUserMedications: async (userId) => {
    const response = await api.get(`/user/${userId}`);
    return response.data;
  },

  // 특정 사용자의 활성화된 약물 정보 가져오기
  getActiveMedications: async (userId) => {
    const response = await api.get(`/user/${userId}/active`);
    return response.data;
  },

  // 약물 정보 추가하기
  addMedication: async (userId, medicationData) => {
    const response = await api.post(`/user/${userId}`, medicationData);
    return response.data;
  },

  // 약물 정보 업데이트
  updateMedication: async (medicationId, medicationData) => {
    const response = await api.put(`/${medicationId}`, medicationData);
    return response.data;
  },

  // 약물 정보 삭제
  deleteMedication: async (medicationId) => {
    const response = await api.delete(`/${medicationId}`);
    return response.data;
  },

  // 약물 복용 알림 추가
  addReminder: async (reminderData) => {
    const response = await api.post(`/reminders`, reminderData);
    return response.data;
  },

  // 특정 사용자의 약물 복용 알림 가져오기
  getUserReminders: async (userId) => {
    const response = await api.get(`/reminders/user/${userId}`);
    return response.data;
  },

  // 약물 복용 완료 처리
  markReminderAsTaken: async (reminderId) => {
    const response = await api.put(`/reminders/${reminderId}/taken`);
    return response.data;
  },

  // 약물 미복용 처리
  markReminderAsMissed: async (reminderId) => {
    const response = await api.put(`/reminders/${reminderId}/missed`);
    return response.data;
  },

  // 약물 통계 가져오기 (대시보드용)
  getMedicationStats: async () => {
    // 실제로는 서버에서 통계 API를 호출해야 함
    // 여기서는 예시로 하드코딩된 값 반환
    return {
      totalMedications: 45,
      takenToday: 23,
      missedToday: 4,
    };
  },
};

export default medicationService;
