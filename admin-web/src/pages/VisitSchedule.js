import React, { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  CircularProgress,
} from '@mui/material';
import { CalendarMonth as CalendarIcon } from '@mui/icons-material';

export default function VisitSchedule() {
  const [loading, setLoading] = useState(false);
  
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
        <CalendarIcon color="primary" sx={{ mr: 1 }} /> 방문 일정 관리
      </Typography>
      
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
          <CircularProgress />
        </Box>
      ) : (
        <Typography>
          이 페이지는 개발 중입니다. 곧 구현될 예정입니다.
        </Typography>
      )}
    </Box>
  );
}
