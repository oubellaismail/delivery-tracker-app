// src/components/TransportLogs/TransportLogs.js
import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Snackbar,
  Grid,
  Autocomplete,
  Chip
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { transportLogsAPI, clientsAPI, driversAPI } from '../../services/api';
import DataTable from '../Common/DataTable';
import LoadingSpinner from '../Common/LoadingSpinner';
import ConfirmDialog from '../Common/ConfirmDialog';

const TransportLogs = () => {
  const [logs, setLogs] = useState([]);
  const [clients, setClients] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  
  // Dialog states
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingLog, setEditingLog] = useState(null);
  const [formData, setFormData] = useState({
    clientId: null,
    driverId: null,
    loadDate: '',
    loadLocation: '',
    unloadDate: '',
    unloadLocation: '',
    destinationName: '',
    deliveryNote: '',
    advance: '',
    fuelQuantity: '',
    fuelPricePerLiter: '',
    variableCharge: '',
    chargePrice: '',
    clientTariff: '',
    tripPrice: '',
    operator: '',
    commercial: ''
  });
  const [formErrors, setFormErrors] = useState({});
  
  // Delete confirmation
  const [deleteDialog, setDeleteDialog] = useState({ open: false, log: null });
  
  // Notifications
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'success' });

  const columns = [
    { id: 'id', label: 'ID' },
    { 
      id: 'client', 
      label: 'Client', 
      render: (row) => row.client?.name || 'N/A' 
    },
    { 
      id: 'driver', 
      label: 'Driver', 
      render: (row) => row.driver?.name || 'N/A' 
    },
    { id: 'destinationName', label: 'Destination' },
    { 
      id: 'loadDate', 
      label: 'Load Date', 
      render: (row) => new Date(row.loadDate).toLocaleDateString() 
    },
    { 
      id: 'unloadDate', 
      label: 'Unload Date', 
      render: (row) => new Date(row.unloadDate).toLocaleDateString() 
    },
    { 
      id: 'tripPrice', 
      label: 'Trip Price', 
      render: (row) => `${parseFloat(row.tripPrice).toFixed(2)}` 
    }
  ];

  useEffect(() => {
    fetchInitialData();
  }, []);

  useEffect(() => {
    fetchLogs();
  }, [page, size]);

  const fetchInitialData = async () => {
    try {
      const [clientsRes, driversRes] = await Promise.all([
        clientsAPI.getAll(0, 100), // Get all clients for dropdown
        driversAPI.getAll(0, 100)  // Get all drivers for dropdown
      ]);
      
      setClients(clientsRes.data.data.data || []);
      setDrivers(driversRes.data.data.data || []);
    } catch (error) {
      showNotification('Error fetching clients and drivers', 'error');
    }
  };

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const response = await transportLogsAPI.getAll(page, size);
      const { data, totalElements: total } = response.data.data;
      setLogs(data);
      setTotalElements(total);
    } catch (error) {
      showNotification('Error fetching transport logs', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingLog(null);
    setFormData({
      clientId: null,
      driverId: null,
      loadDate: '',
      loadLocation: '',
      unloadDate: '',
      unloadLocation: '',
      destinationName: '',
      deliveryNote: '',
      advance: '',
      fuelQuantity: '',
      fuelPricePerLiter: '',
      variableCharge: '',
      chargePrice: '',
      clientTariff: '',
      tripPrice: '',
      operator: '',
      commercial: ''
    });
    setFormErrors({});
    setDialogOpen(true);
  };

  const handleEdit = (log) => {
    setEditingLog(log);
    setFormData({
      clientId: log.client?.id || null,
      driverId: log.driver?.id || null,
      loadDate: log.loadDate,
      loadLocation: log.loadLocation,
      unloadDate: log.unloadDate,
      unloadLocation: log.unloadLocation,
      destinationName: log.destinationName,
      deliveryNote: log.deliveryNote || '',
      advance: log.advance.toString(),
      fuelQuantity: log.fuelQuantity.toString(),
      fuelPricePerLiter: log.fuelPricePerLiter.toString(),
      variableCharge: log.variableCharge.toString(),
      chargePrice: log.chargePrice.toString(),
      clientTariff: log.clientTariff.toString(),
      tripPrice: log.tripPrice.toString(),
      operator: log.operator,
      commercial: log.commercial
    });
    setFormErrors({});
    setDialogOpen(true);
  };

  const handleDelete = (log) => {
    setDeleteDialog({ open: true, log });
  };

  const confirmDelete = async () => {
    try {
      await transportLogsAPI.delete(deleteDialog.log.id);
      showNotification('Transport log deleted successfully', 'success');
      fetchLogs();
    } catch (error) {
      showNotification('Error deleting transport log', 'error');
    }
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    try {
      const submitData = {
        ...formData,
        advance: parseFloat(formData.advance),
        fuelQuantity: parseFloat(formData.fuelQuantity),
        fuelPricePerLiter: parseFloat(formData.fuelPricePerLiter),
        variableCharge: parseFloat(formData.variableCharge),
        chargePrice: parseFloat(formData.chargePrice),
        clientTariff: parseFloat(formData.clientTariff),
        tripPrice: parseFloat(formData.tripPrice)
      };

      if (editingLog) {
        await transportLogsAPI.update({ id: editingLog.id, ...submitData });
        showNotification('Transport log updated successfully', 'success');
      } else {
        await transportLogsAPI.create(submitData);
        showNotification('Transport log created successfully', 'success');
      }
      
      setDialogOpen(false);
      fetchLogs();
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
        `Error ${editingLog ? 'updating' : 'creating'} transport log`;
      showNotification(errorMessage, 'error');
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.clientId) errors.clientId = 'Client is required';
    if (!formData.driverId) errors.driverId = 'Driver is required';
    if (!formData.loadDate) errors.loadDate = 'Load date is required';
    if (!formData.loadLocation.trim()) errors.loadLocation = 'Load location is required';
    if (!formData.unloadDate) errors.unloadDate = 'Unload date is required';
    if (!formData.unloadLocation.trim()) errors.unloadLocation = 'Unload location is required';
    if (!formData.destinationName.trim()) errors.destinationName = 'Destination name is required';
    if (!formData.operator.trim()) errors.operator = 'Operator is required';
    if (!formData.commercial.trim()) errors.commercial = 'Commercial is required';
    
    // Validate numeric fields
    const numericFields = ['advance', 'fuelQuantity', 'fuelPricePerLiter', 'variableCharge', 'chargePrice', 'clientTariff', 'tripPrice'];
    numericFields.forEach(field => {
      if (!formData[field] || isNaN(formData[field])) {
        errors[field] = `${field.replace(/([A-Z])/g, ' $1').toLowerCase()} must be a valid number`;
      }
    });
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleFormChange = (field, value) => {
    setFormData({ ...formData, [field]: value });
    if (formErrors[field]) {
      setFormErrors({ ...formErrors, [field]: null });
    }
  };

  const showNotification = (message, severity) => {
    setNotification({ open: true, message, severity });
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Transport Logs Management</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleCreate}
        >
          Add Transport Log
        </Button>
      </Box>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          data={logs}
          columns={columns}
          totalElements={totalElements}
          page={page}
          size={size}
          onPageChange={setPage}
          onRowsPerPageChange={setSize}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}

      {/* Create/Edit Dialog */}
      <Dialog 
        open={dialogOpen} 
        onClose={() => setDialogOpen(false)} 
        maxWidth="md" 
        fullWidth
        PaperProps={{ sx: { maxHeight: '90vh' } }}
      >
        <DialogTitle>
          {editingLog ? 'Edit Transport Log' : 'Add New Transport Log'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            {/* Client and Driver Selection */}
            <Grid item xs={12} md={6}>
              <Autocomplete
                options={clients}
                getOptionLabel={(option) => option.name}
                value={clients.find(c => c.id === formData.clientId) || null}
                onChange={(event, newValue) => handleFormChange('clientId', newValue?.id || null)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Client"
                    error={!!formErrors.clientId}
                    helperText={formErrors.clientId}
                  />
                )}
                renderTags={(value, getTagProps) =>
                  value.map((option, index) => (
                    <Chip variant="outlined" label={option.name} {...getTagProps({ index })} />
                  ))
                }
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Autocomplete
                options={drivers}
                getOptionLabel={(option) => `${option.name} (${option.plateNumber})`}
                value={drivers.find(d => d.id === formData.driverId) || null}
                onChange={(event, newValue) => handleFormChange('driverId', newValue?.id || null)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Driver"
                    error={!!formErrors.driverId}
                    helperText={formErrors.driverId}
                  />
                )}
              />
            </Grid>

            {/* Date Fields */}
            <Grid item xs={12} md={6}>
              <TextField
                label="Load Date"
                type="date"
                fullWidth
                value={formData.loadDate}
                onChange={(e) => handleFormChange('loadDate', e.target.value)}
                error={!!formErrors.loadDate}
                helperText={formErrors.loadDate}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                label="Unload Date"
                type="date"
                fullWidth
                value={formData.unloadDate}
                onChange={(e) => handleFormChange('unloadDate', e.target.value)}
                error={!!formErrors.unloadDate}
                helperText={formErrors.unloadDate}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>

            {/* Location Fields */}
            <Grid item xs={12} md={6}>
              <TextField
                label="Load Location"
                fullWidth
                value={formData.loadLocation}
                onChange={(e) => handleFormChange('loadLocation', e.target.value)}
                error={!!formErrors.loadLocation}
                helperText={formErrors.loadLocation}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                label="Unload Location"
                fullWidth
                value={formData.unloadLocation}
                onChange={(e) => handleFormChange('unloadLocation', e.target.value)}
                error={!!formErrors.unloadLocation}
                helperText={formErrors.unloadLocation}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                label="Destination Name"
                fullWidth
                value={formData.destinationName}
                onChange={(e) => handleFormChange('destinationName', e.target.value)}
                error={!!formErrors.destinationName}
                helperText={formErrors.destinationName}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                label="Delivery Note"
                fullWidth
                multiline
                rows={3}
                value={formData.deliveryNote}
                onChange={(e) => handleFormChange('deliveryNote', e.target.value)}
              />
            </Grid>

            {/* Financial Fields */}
            <Grid item xs={12} md={6}>
              <TextField
                label="Advance"
                type="number"
                fullWidth
                value={formData.advance}
                onChange={(e) => handleFormChange('advance', e.target.value)}
                error={!!formErrors.advance}
                helperText={formErrors.advance}
                InputProps={{ startAdornment: ' ' }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                label="Fuel Quantity"
                type="number"
                fullWidth
                value={formData.fuelQuantity}
                onChange={(e) => handleFormChange('fuelQuantity', e.target.value)}
                error={!!formErrors.fuelQuantity}
                helperText={formErrors.fuelQuantity}
                InputProps={{ endAdornment: 'L' }}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Fuel Price Per Liter"
                type="number"
                fullWidth
                value={formData.fuelPricePerLiter}
                onChange={(e) => handleFormChange('fuelPricePerLiter', e.target.value)}
                error={!!formErrors.fuelPricePerLiter}
                helperText={formErrors.fuelPricePerLiter}
                InputProps={{ startAdornment: '' }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                label="Variable Charge"
                type="number"
                fullWidth
                value={formData.variableCharge}
                onChange={(e) => handleFormChange('variableCharge', e.target.value)}
                error={!!formErrors.variableCharge}
                helperText={formErrors.variableCharge}
                InputProps={{ startAdornment: '' }}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Charge Price"
                type="number"
                fullWidth
                value={formData.chargePrice}
                onChange={(e) => handleFormChange('chargePrice', e.target.value)}
                error={!!formErrors.chargePrice}
                helperText={formErrors.chargePrice}
                InputProps={{ startAdornment: '' }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                label="Client Tariff"
                type="number"
                fullWidth
                value={formData.clientTariff}
                onChange={(e) => handleFormChange('clientTariff', e.target.value)}
                error={!!formErrors.clientTariff}
                helperText={formErrors.clientTariff}
                InputProps={{ startAdornment: '' }}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                label="Trip Price"
                type="number"
                fullWidth
                value={formData.tripPrice}
                onChange={(e) => handleFormChange('tripPrice', e.target.value)}
                error={!!formErrors.tripPrice}
                helperText={formErrors.tripPrice}
                InputProps={{ startAdornment: ' '}}
              />
            </Grid>

            {/* Operator and Commercial */}
            <Grid item xs={12} md={6}>
              <TextField
                label="Operator"
                fullWidth
                value={formData.operator}
                onChange={(e) => handleFormChange('operator', e.target.value)}
                error={!!formErrors.operator}
                helperText={formErrors.operator}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                label="Commercial"
                fullWidth
                value={formData.commercial}
                onChange={(e) => handleFormChange('commercial', e.target.value)}
                error={!!formErrors.commercial}
                helperText={formErrors.commercial}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingLog ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation */}
      <ConfirmDialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, log: null })}
        onConfirm={confirmDelete}
        title="Delete Transport Log"
        message={`Are you sure you want to delete transport log #${deleteDialog.log?.id}?`}
        confirmText="Delete"
        severity="error"
      />

      {/* Notification Snackbar */}
      <Snackbar
        open={notification.open}
        autoHideDuration={6000}
        onClose={() => setNotification({ ...notification, open: false })}
      >
        <Alert
          onClose={() => setNotification({ ...notification, open: false })}
          severity={notification.severity}
          sx={{ width: '100%' }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default TransportLogs; 