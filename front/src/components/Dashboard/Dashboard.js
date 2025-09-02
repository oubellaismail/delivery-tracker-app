// src/components/Dashboard/Dashboard.js
import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Paper,
  Divider,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from '@mui/material';
import {
  People as PeopleIcon,
  LocalShipping as TruckIcon,
  Assignment as LogsIcon,
  TrendingUp as TrendingIcon
} from '@mui/icons-material';
import { clientsAPI, driversAPI, transportLogsAPI } from '../../services/api';
import LoadingSpinner from '../Common/LoadingSpinner';

const StatCard = ({ title, value, icon, subtitle }) => (
  <Card 
    variant="outlined"
    sx={{ 
      height: '100%',
      transition: 'box-shadow 0.2s ease',
      '&:hover': {
        boxShadow: 2
      }
    }}
  >
    <CardContent sx={{ p: 3 }}>
      <Box display="flex" alignItems="flex-start" justifyContent="space-between" mb={2}>
        <Box sx={{ color: 'text.secondary' }}>
          {icon}
        </Box>
      </Box>
      
      <Typography variant="h4" component="div" sx={{ fontWeight: 600, mb: 0.5 }}>
        {value.toLocaleString()}
      </Typography>
      
      <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
        {title}
      </Typography>
      
      {subtitle && (
        <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
          {subtitle}
        </Typography>
      )}
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
        transportLogsAPI.getAll(0, 10) // Get more logs for better overview
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

  const avgTripValue = recentLogs.length > 0 
    ? (recentLogs.reduce((sum, log) => sum + log.tripPrice, 0) / recentLogs.length)
    : 0;

  const totalRevenue = recentLogs.reduce((sum, log) => sum + log.tripPrice, 0);

  return (
    <Box sx={{ p: 3 }}>
      <Box mb={4}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 600, mb: 1 }}>
          Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Overview of your transport operations
        </Typography>
      </Box>
      
      {/* Key Metrics */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Clients"
            value={stats.totalClients}
            icon={<PeopleIcon sx={{ fontSize: 32 }} />}
            subtitle="Registered customers"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Active Drivers"
            value={stats.totalDrivers}
            icon={<TruckIcon sx={{ fontSize: 32 }} />}
            subtitle="Available for dispatch"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Deliveries"
            value={stats.totalTransportLogs}
            icon={<LogsIcon sx={{ fontSize: 32 }} />}
            subtitle="Completed shipments"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Avg Trip Value"
            value={`$${avgTripValue.toFixed(0)}`}
            icon={<TrendingIcon sx={{ fontSize: 32 }} />}
            subtitle="Revenue per delivery"
          />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Recent Transport Logs */}
        <Grid item xs={12} lg={8}>
          <Paper variant="outlined" sx={{ p: 3 }}>
            <Box mb={3}>
              <Typography variant="h6" component="h2" sx={{ fontWeight: 600 }}>
                Recent Transport Logs
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Latest shipment activities
              </Typography>
            </Box>
            
            {recentLogs.length === 0 ? (
              <Box textAlign="center" py={6}>
                <LogsIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No transport logs found
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Transport logs will appear here once created
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ fontWeight: 600 }}>ID</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>Destination</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>Client</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>Driver</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>Route</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>Date</TableCell>
                      <TableCell align="right" sx={{ fontWeight: 600 }}>Value</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recentLogs.map((log) => (
                      <TableRow 
                        key={log.id}
                        sx={{ 
                          '&:hover': { 
                            backgroundColor: 'action.hover' 
                          } 
                        }}
                      >
                        <TableCell>
                          <Chip 
                            label={`#${log.id}`} 
                            variant="outlined" 
                            size="small"
                            sx={{ fontWeight: 500 }}
                          />
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" sx={{ fontWeight: 500 }}>
                            {log.destinationName}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            {log.client.name}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            {log.driver.name}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" color="text.secondary">
                            {log.loadLocation} → {log.unloadLocation}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            {new Date(log.loadDate).toLocaleDateString()}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">
                          <Typography variant="body2" sx={{ fontWeight: 600, color: 'success.main' }}>
                            ${log.tripPrice.toFixed(2)}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Paper>
        </Grid>
        
        {/* Summary Panel */}
        <Grid item xs={12} lg={4}>
          <Paper variant="outlined" sx={{ p: 3, height: 'fit-content' }}>
            <Typography variant="h6" component="h2" sx={{ fontWeight: 600, mb: 1 }}>
              Summary
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Key performance indicators
            </Typography>
            
            <Box display="flex" flexDirection="column" gap={3}>
              <Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Total Revenue (Recent)
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 600, color: 'success.main' }}>
                  ${totalRevenue.toFixed(2)}
                </Typography>
              </Box>
              
              <Divider />
              
              <Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Average Trip Value
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 600 }}>
                  ${avgTripValue.toFixed(2)}
                </Typography>
              </Box>
              
              <Divider />
              
              <Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Active Routes
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 600 }}>
                  {new Set(recentLogs.map(log => `${log.loadLocation}-${log.unloadLocation}`)).size}
                </Typography>
              </Box>
              
              <Divider />
              
              <Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Completed Trips (Recent)
                </Typography>
                <Typography variant="h5" sx={{ fontWeight: 600 }}>
                  {recentLogs.length}
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