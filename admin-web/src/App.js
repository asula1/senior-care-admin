import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Box } from '@mui/material';
import Dashboard from './pages/Dashboard';
import SeniorList from './pages/SeniorList';
import SeniorDetail from './pages/SeniorDetail';
import EmergencyAlerts from './pages/EmergencyAlerts';
import Login from './pages/Login';
import Layout from './components/Layout';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import NotFound from './pages/NotFound';

// Lazy load 페이지들 (비동기적 로딩)
const MedicationManagement = React.lazy(() => import('./pages/MedicationManagement'));
const VisitSchedule = React.lazy(() => import('./pages/VisitSchedule'));

// 보호된 라우트 컴포넌트
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return <div>로딩 중...</div>;
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

// App 컴포넌트
function AppContent() {
  const { isAuthenticated } = useAuth();
  
  return (
    <Box sx={{ display: 'flex', height: '100vh' }}>
      <Routes>
        <Route path="/login" element={
          !isAuthenticated ? <Login /> : <Navigate to="/" replace />
        } />
        
        <Route path="/" element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }>
          <Route index element={<Dashboard />} />
          <Route path="seniors" element={<SeniorList />} />
          <Route path="seniors/:id" element={<SeniorDetail />} />
          <Route path="medications" element={
            <React.Suspense fallback={<div>로딩 중...</div>}>
              <MedicationManagement />
            </React.Suspense>
          } />
          <Route path="emergency-alerts" element={<EmergencyAlerts />} />
          <Route path="visit-schedule" element={
            <React.Suspense fallback={<div>로딩 중...</div>}>
              <VisitSchedule />
            </React.Suspense>
          } />
        </Route>
        
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Box>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;