import React, { useState, useEffect } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  AppBar,
  Box,
  CssBaseline,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Button,
  Badge,
  Avatar,
  Menu,
  MenuItem,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  Medication as MedicationIcon,
  NotificationsActive as AlertIcon,
  Event as ScheduleIcon,
  Notifications as NotificationIcon,
  AccountCircle,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-toastify';
import emergencyService from '../services/emergencyService';

const drawerWidth = 240;

export default function Layout() {
  const { user, logout } = useAuth();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [activeAlerts, setActiveAlerts] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();

  // 메뉴 아이템 정의
  const menuItems = [
    { text: '대시보드', icon: <DashboardIcon />, path: '/' },
    { text: '노인 관리', icon: <PeopleIcon />, path: '/seniors' },
    { text: '약물 관리', icon: <MedicationIcon />, path: '/medications' },
    { text: '비상 알림', icon: <AlertIcon />, path: '/emergency-alerts' },
    { text: '방문 일정', icon: <ScheduleIcon />, path: '/visit-schedule' },
  ];

  // 알림 관련 메뉴 토글
  const handleNotificationMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  // 비상 알림 데이터 가져오기
  useEffect(() => {
    const fetchActiveAlerts = async () => {
      try {
        const alerts = await emergencyService.getActiveAlerts();
        setActiveAlerts(alerts.length);
        
        if (alerts.length > 0 && !location.pathname.includes('emergency-alerts')) {
          toast.error(`비상 알림 ${alerts.length}건이 있습니다!`, {
            onClick: () => navigate('/emergency-alerts')
          });
        }
      } catch (error) {
        console.error('비상 알림 정보 가져오기 실패:', error);
      }
    };
    
    fetchActiveAlerts();
    
    // 실시간 비상 알림을 위한 타이머 설정 (실제로는 WebSocket 사용)
    const intervalId = setInterval(fetchActiveAlerts, 30000);
    
    return () => clearInterval(intervalId);
  }, [location.pathname, navigate]);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const drawer = (
    <div>
      <Toolbar sx={{ display: 'flex', justifyContent: 'center' }}>
        <Typography variant="h6" component="div" color="primary" fontWeight="bold">
          노인 케어 시스템
        </Typography>
      </Toolbar>
      <Divider />
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              selected={location.pathname === item.path}
              onClick={() => {
                navigate(item.path);
                if (isMobile) setMobileOpen(false);
              }}
            >
              <ListItemIcon>
                {item.text === '비상 알림' && activeAlerts > 0 ? (
                  <Badge badgeContent={activeAlerts} color="error">
                    {item.icon}
                  </Badge>
                ) : (
                  item.icon
                )}
              </ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <Divider />
      <Box sx={{ p: 2, position: 'absolute', bottom: 0, width: '100%' }}>
        {user && (
          <Typography variant="body2" color="text.secondary" align="center">
            {user.fullName || user.username}님 로그인됨
          </Typography>
        )}
        <Button
          fullWidth
          variant="outlined"
          color="primary"
          onClick={logout}
          sx={{ mt: 1 }}
        >
          로그아웃
        </Button>
      </Box>
    </div>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          bgcolor: 'background.paper',
          color: 'text.primary',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            {menuItems.find((item) => item.path === location.pathname)?.text || '대시보드'}
          </Typography>
          
          <IconButton color="inherit" onClick={handleNotificationMenu}>
            <Badge badgeContent={notifications.length} color="error">
              <NotificationIcon />
            </Badge>
          </IconButton>
          <Menu
            id="notifications-menu"
            anchorEl={anchorEl}
            keepMounted
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            {notifications.length > 0 ? (
              notifications.map((notification, index) => (
                <MenuItem key={index} onClick={handleClose}>
                  {notification.message}
                </MenuItem>
              ))
            ) : (
              <MenuItem onClick={handleClose}>알림이 없습니다</MenuItem>
            )}
          </Menu>
          
          <Avatar sx={{ ml: 1, bgcolor: 'primary.main' }}>
            {user?.fullName?.[0] || user?.username?.[0] || 'U'}
          </Avatar>
        </Toolbar>
      </AppBar>
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          overflow: 'auto',
          bgcolor: 'background.default',
          minHeight: '100vh',
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}
