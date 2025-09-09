// src/components/Layout/Layout.js
import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import {
  AppBar,
  Box,
  CssBaseline,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Menu,
  MenuItem,
  Avatar,
  Divider,
  Badge,
  Chip
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  LocalShipping as TruckIcon,
  Assignment as LogsIcon,
  AccountCircle,
  Logout,
  BusinessCenter
} from '@mui/icons-material';

const drawerWidth = 260;

const menuItems = [
  { 
    text: 'Dashboard', 
    icon: <DashboardIcon />, 
    path: '/dashboard',
    color: 'primary'
  },
  { 
    text: 'Clients', 
    icon: <PeopleIcon />, 
    path: '/clients',
    color: 'primary'
  },
  { 
    text: 'Drivers', 
    icon: <TruckIcon />, 
    path: '/drivers',
    color: 'warning'
  },
  { 
    text: 'Transport Logs', 
    icon: <LogsIcon />, 
    path: '/transport-logs',
    color: 'info'
  },
];

const Layout = () => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleMenuClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    handleMenuClose();
    navigate('/login');
  };

  const currentPage = menuItems.find(item => item.path === location.pathname);

  const drawer = (
    <Box sx={{ height: '100%', bgcolor: 'grey.50' }}>
      {/* Logo/Brand Section */}
      <Box sx={{ p: 3, bgcolor: 'white', borderBottom: '1px solid', borderColor: 'divider' }}>
        <Box display="flex" alignItems="center" gap={2}>
          <Avatar sx={{ bgcolor: 'primary.main', width: 40, height: 40 }}>
            <BusinessCenter />
          </Avatar>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 700, lineHeight: 1.2 }}>
              Transport Pro
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Management System
            </Typography>
          </Box>
        </Box>
      </Box>

      {/* Navigation Menu */}
      <Box sx={{ p: 2 }}>
        <List disablePadding>
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
                <ListItemButton
                  selected={isActive}
                  onClick={() => {
                    navigate(item.path);
                    setMobileOpen(false);
                  }}
                  sx={{
                    borderRadius: 2,
                    py: 1.5,
                    px: 2,
                    ...(isActive && {
                      bgcolor: `${item.color}.main`,
                      color: 'white',
                      '&:hover': {
                        bgcolor: `${item.color}.dark`,
                      },
                      '& .MuiListItemIcon-root': {
                        color: 'white',
                      }
                    }),
                    ...(!isActive && {
                      '&:hover': {
                        bgcolor: `${item.color}.light`,
                        '& .MuiListItemIcon-root': {
                          color: `${item.color}.main`,
                        }
                      }
                    })
                  }}
                >
                  <ListItemIcon
                    sx={{ 
                      minWidth: 40,
                      color: isActive ? 'inherit' : 'text.secondary'
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText 
                    primary={item.text}
                    primaryTypographyProps={{
                      fontWeight: isActive ? 600 : 500,
                      fontSize: '0.95rem'
                    }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      
      {/* App Bar */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          bgcolor: 'white',
          borderBottom: '1px solid',
          borderColor: 'divider'
        }}
      >
        <Toolbar sx={{ py: 1 }}>
          <IconButton
            color="primary"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h5" sx={{ fontWeight: 600, color: 'text.primary' }}>
              {currentPage?.text || 'Dashboard'}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Welcome back, {user?.username}
            </Typography>
          </Box>

          <Box display="flex" alignItems="center" gap={2}>
            <Chip 
              label="Online" 
              color="success" 
              size="small" 
              variant="outlined"
            />
            <IconButton
              size="large"
              edge="end"
              onClick={handleMenuClick}
              sx={{
                border: '1px solid',
                borderColor: 'divider',
                '&:hover': {
                  borderColor: 'primary.main'
                }
              }}
            >
              <Avatar sx={{ width: 36, height: 36, bgcolor: 'primary.main' }}>
                {user?.username?.charAt(0)?.toUpperCase() || <AccountCircle />}
              </Avatar>
            </IconButton>
          </Box>
          
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            sx={{
              mt: 1,
              '& .MuiPaper-root': {
                borderRadius: 2,
                minWidth: 200,
                boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
              }
            }}
          >
            <Box sx={{ p: 2, borderBottom: '1px solid', borderColor: 'divider' }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
                {user?.username}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Administrator
              </Typography>
            </Box>
            <MenuItem 
              onClick={handleLogout}
              sx={{ 
                py: 1.5, 
                color: 'error.main',
                '&:hover': {
                  bgcolor: 'error.light',
                  color: 'error.dark'
                }
              }}
            >
              <ListItemIcon sx={{ color: 'inherit' }}>
                <Logout fontSize="small" />
              </ListItemIcon>
              <Typography variant="body2" fontWeight={500}>
                Sign Out
              </Typography>
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* Sidebar Drawer */}
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        {/* Mobile drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { 
              boxSizing: 'border-box', 
              width: drawerWidth,
              border: 'none'
            },
          }}
        >
          {drawer}
        </Drawer>

        {/* Desktop drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { 
              boxSizing: 'border-box', 
              width: drawerWidth,
              border: 'none',
              borderRight: '1px solid',
              borderColor: 'divider'
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      {/* Main content */}
      <Box
        component="main"
        sx={{ 
          flexGrow: 1, 
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          minHeight: '100vh',
          bgcolor: 'grey.50'
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default Layout;