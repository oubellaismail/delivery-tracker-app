// src/components/Dashboard/Dashboard.js
import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Avatar,
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

const MetricCard = ({ title, value, icon, color = 'primary' }) => (
  <Card sx={{ height: '100%', borderRadius: 2 }}>
    <CardContent sx={{ p: 3 }}>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 700, color: `${color}.main`, mb: 0.5 }}>
            {typeof value === 'number' ? value.toLocaleString() : value}
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 500 }}>
            {title}
          </Typography>
        </Box>
        <Avatar sx={{ bgcolor: `${color}.main`, width: 56, height: 56 }}>
          {icon}
        </Avatar>
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
      
      const [clientsRes, driversRes, logsRes] = await Promise.all([
        clientsAPI.getAll(0, 1),
        driversAPI.getAll(0, 1),
        transportLogsAPI.getAll(0, 5) // Only get 5 recent logs for simplicity
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
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <LoadingSpinner message="Loading dashboard..." />
      </Box>
    );
  }

  const totalRevenue = recentLogs.reduce((sum, log) => sum + log.tripPrice, 0);

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box mb={4}>
        <Typography variant="h3" sx={{ fontWeight: 700, mb: 1 }}>
          Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ fontSize: '1.1rem' }}>
          Transport management overview
        </Typography>
      </Box>
      
      {/* Key Metrics */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Total Clients"
            value={stats.totalClients}
            icon={<PeopleIcon sx={{ fontSize: 28 }} />}
            color="primary"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Active Drivers"
            value={stats.totalDrivers}
            icon={<TruckIcon sx={{ fontSize: 28 }} />}
            color="warning"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Total Logs"
            value={stats.totalTransportLogs}
            icon={<LogsIcon sx={{ fontSize: 28 }} />}
            color="info"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Revenue"
            value={`$${totalRevenue.toFixed(0)}`}
            icon={<TrendingIcon sx={{ fontSize: 28 }} />}
            color="success"
          />
        </Grid>
      </Grid>

      {/* Recent Activity */}
      <Card sx={{ borderRadius: 2 }}>
        <CardContent sx={{ p: 0 }}>
          <Box sx={{ p: 3, pb: 2 }}>
            <Typography variant="h5" sx={{ fontWeight: 600, mb: 0.5 }}>
              Recent Transport Logs
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Latest transport activities
            </Typography>
          </Box>
          
          {recentLogs.length === 0 ? (
            <Box textAlign="center" py={6}>
              <LogsIcon sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }} />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                No recent logs
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Transport logs will appear here once created
              </Typography>
            </Box>
          ) : (
            <TableContainer>
              <Table>
                <TableHead sx={{ bgcolor: 'grey.50' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 600, py: 2 }}>Log ID</TableCell>
                    <TableCell sx={{ fontWeight: 600, py: 2 }}>Client</TableCell>
                    <TableCell sx={{ fontWeight: 600, py: 2 }}>Driver</TableCell>
                    <TableCell sx={{ fontWeight: 600, py: 2 }}>Destination</TableCell>
                    <TableCell sx={{ fontWeight: 600, py: 2 }}>Date</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 600, py: 2 }}>Value</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentLogs.map((log, index) => (
                    <TableRow 
                      key={log.id}
                      sx={{ 
                        '&:hover': { bgcolor: 'action.hover' },
                        '&:last-child td': { border: 0 }
                      }}
                    >
                      <TableCell sx={{ py: 2 }}>
                        <Chip 
                          label={`#${log.id}`} 
                          size="small"
                          variant="outlined"
                          sx={{ fontWeight: 500 }}
                        />
                      </TableCell>
                      <TableCell sx={{ py: 2 }}>
                        <Box display="flex" alignItems="center" gap={1.5}>
                          <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main' }}>
                            {log.client.name.charAt(0).toUpperCase()}
                          </Avatar>
                          <Typography variant="body2" sx={{ fontWeight: 500 }}>
                            {log.client.name}
                          </Typography>
                        </Box>
                      </TableCell>
                      <TableCell sx={{ py: 2 }}>
                        <Box display="flex" alignItems="center" gap={1.5}>
                          <Avatar sx={{ width: 32, height: 32, bgcolor: 'warning.main' }}>
                            {log.driver.name.charAt(0).toUpperCase()}
                          </Avatar>
                          <Box>
                            <Typography variant="body2" sx={{ fontWeight: 500 }}>
                              {log.driver.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {log.driver.plateNumber}
                            </Typography>
                          </Box>
                        </Box>
                      </TableCell>
                      <TableCell sx={{ py: 2 }}>
                        <Typography variant="body2" sx={{ fontWeight: 500 }}>
                          {log.destinationName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {log.loadLocation} → {log.unloadLocation}
                        </Typography>
                      </TableCell>
                      <TableCell sx={{ py: 2 }}>
                        <Typography variant="body2">
                          {new Date(log.loadDate).toLocaleDateString()}
                        </Typography>
                      </TableCell>
                      <TableCell align="right" sx={{ py: 2 }}>
                        <Typography 
                          variant="body2" 
                          sx={{ 
                            fontWeight: 600, 
                            color: 'success.main',
                            fontSize: '0.95rem'
                          }}
                        >
                          ${log.tripPrice.toFixed(2)}
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default Dashboard;