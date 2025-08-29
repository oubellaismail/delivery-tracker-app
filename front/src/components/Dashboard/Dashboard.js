// src/components/Dashboard/Dashboard.js
import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Paper,
  List,
  ListItem,
  ListItemText,
  Chip
} from '@mui/material';
import {
  People as PeopleIcon,
  LocalShipping as TruckIcon,
  Assignment as LogsIcon,
  TrendingUp as TrendingIcon
} from '@mui/icons-material';
import { clientsAPI, driversAPI, transportLogsAPI } from '../../services/api';
import LoadingSpinner from '../Common/LoadingSpinner';

const StatCard = ({ title, value, icon, color = 'primary' }) => (
  <Card>
    <CardContent>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box>
          <Typography color="textSecondary" gutterBottom variant="body2">
            {title}
          </Typography>
          <Typography variant="h4" component="h2">
            {value}
          </Typography>
        </Box>
        <Box
          sx={{
            backgroundColor: `${color}.light`,
            borderRadius: '50%',
            p: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
        >
          {icon}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalClients: 0,
    totalDrivers: 0,
    totalTransportLogs: 0
  });
  const [recentLogs, setRecentLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Fetch statistics
      const [clientsRes, driversRes, logsRes] = await Promise.all([
        clientsAPI.getAll(0, 1), // Just get first page to get total count
        driversAPI.getAll(0, 1),
        transportLogsAPI.getAll(0, 5) // Get recent 5 logs
      ]);

      setStats({
        totalClients: clientsRes.data.data.totalElements,
        totalDrivers: driversRes.data.data.totalElements,
        totalTransportLogs: logsRes.data.data.totalElements
      });

      setRecentLogs(logsRes.data.data.data);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading dashboard..." />;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      
      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={4}>
          <StatCard
            title="Total Clients"
            value={stats.totalClients}
            icon={<PeopleIcon color="primary" />}
            color="primary"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatCard
            title="Total Drivers"
            value={stats.totalDrivers}
            icon={<TruckIcon color="secondary" />}
            color="secondary"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatCard
            title="Transport Logs"
            value={stats.totalTransportLogs}
            icon={<LogsIcon color="success" />}
            color="success"
          />
        </Grid>
      </Grid>

      {/* Recent Transport Logs */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Box display="flex" alignItems="center" mb={2}>
              <TrendingIcon sx={{ mr: 1 }} />
              <Typography variant="h6">
                Recent Transport Logs
              </Typography>
            </Box>
            
            {recentLogs.length === 0 ? (
              <Typography color="textSecondary" textAlign="center" py={4}>
                No transport logs available
              </Typography>
            ) : (
              <List>
                {recentLogs.map((log) => (
                  <ListItem key={log.id} divider>
                    <ListItemText
                      primary={
                        <Box display="flex" alignItems="center" gap={1}>
                          <Typography variant="subtitle1">
                            {log.destinationName}
                          </Typography>
                          <Chip 
                            label={`#${log.id}`} 
                            size="small" 
                            variant="outlined" 
                          />
                        </Box>
                      }
                      secondary={
                        <Box>
                          <Typography variant="body2" color="textSecondary">
                            Client: {log.client.name} | Driver: {log.driver.name}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            {log.loadLocation} → {log.unloadLocation}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            Load Date: {new Date(log.loadDate).toLocaleDateString()}
                          </Typography>
                        </Box>
                      }
                    />
                    <Box textAlign="right">
                      <Typography variant="h6" color="primary">
                        ${log.tripPrice.toFixed(2)}
                      </Typography>
                    </Box>
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Quick Stats
            </Typography>
            <Box display="flex" flexDirection="column" gap={2}>
              <Box>
                <Typography variant="body2" color="textSecondary">
                  Average Trip Value
                </Typography>
                <Typography variant="h5">
                  ${recentLogs.length > 0 
                    ? (recentLogs.reduce((sum, log) => sum + log.tripPrice, 0) / recentLogs.length).toFixed(2)
                    : '0.00'
                  }
                </Typography>
              </Box>
              
              <Box>
                <Typography variant="body2" color="textSecondary">
                  Active Routes
                </Typography>
                <Typography variant="h5">
                  {new Set(recentLogs.map(log => `${log.loadLocation}-${log.unloadLocation}`)).size}
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;