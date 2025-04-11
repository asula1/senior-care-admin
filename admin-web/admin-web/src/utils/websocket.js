import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;
let subscriptions = [];

export const connectWebSocket = (onConnect = () => {}, onError = () => {}) => {
  // 이미 연결된 경우 재연결하지 않음
  if (stompClient && stompClient.active) {
    console.log('WebSocket is already connected');
    return;
  }

  // 이미 연결된 Client가 있다면 닫기
  if (stompClient) {
    stompClient.deactivate();
  }

  // API 서버 기본 URL 정의 (환경 변수 또는 설정 파일에서 가져오는 것이 좋음)
  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
  
  // 새 STOMP 클라이언트 생성
  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
    connectHeaders: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
    debug: function(str) {
      // 로그 비활성화
      // console.log(str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onConnect: () => {
      console.log('WebSocket connected');
      onConnect(stompClient);
    },
    onStompError: (frame) => {
      console.error('WebSocket STOMP error:', frame);
      onError(frame);
    },
    onWebSocketError: (error) => {
      console.error('WebSocket connection error:', error);
      onError(error);
    }
  });

  // 연결 시작
  stompClient.activate();
  
  return stompClient;
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    // 모든 구독 해제
    subscriptions.forEach((subscription) => {
      if (subscription && subscription.unsubscribe) {
        subscription.unsubscribe();
      }
    });
    subscriptions = [];
    
    // 연결 해제
    stompClient.deactivate();
    console.log('WebSocket disconnected');
  }
};

export const subscribeToTopic = (topic, callback) => {
  if (!stompClient || !stompClient.active) {
    console.error('WebSocket is not connected');
    return null;
  }
  
  const subscription = stompClient.subscribe(topic, (message) => {
    try {
      const parsedBody = JSON.parse(message.body);
      callback(parsedBody);
    } catch (error) {
      console.error('Error parsing message:', error);
      callback(message.body);
    }
  });
  
  subscriptions.push(subscription);
  return subscription;
};

export const sendMessage = (destination, body = {}) => {
  if (!stompClient || !stompClient.active) {
    console.error('WebSocket is not connected');
    return;
  }
  
  stompClient.publish({
    destination: destination,
    body: JSON.stringify(body)
  });
};

export default {
  connectWebSocket,
  disconnectWebSocket,
  subscribeToTopic,
  sendMessage,
};
