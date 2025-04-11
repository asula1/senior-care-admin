import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  CardHeader,
  Button,
  Chip,
  Divider,
  CircularProgress,
  Alert,
} from '@mui/material';
import { MedicationRounded as MedicationIcon } from '@mui/icons-material';
import { format } from 'date-fns';

export default function MedicationManagement() {
  const [loading, setLoading] = useState(false);
  
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Typography variant="h4" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
        <MedicationIcon color="primary" sx={{ mr: 1 }} /> 복약 관리
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
