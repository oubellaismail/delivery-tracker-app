// src/components/Drivers/Drivers.js
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
  Snackbar
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { driversAPI } from '../../services/api';
import DataTable from '../Common/DataTable';
import LoadingSpinner from '../Common/LoadingSpinner';
import ConfirmDialog from '../Common/ConfirmDialog';

const Drivers = () => {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  
  // Dialog states
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingDriver, setEditingDriver] = useState(null);
  const [formData, setFormData] = useState({ name: '', plateNumber: '' });
  const [formErrors, setFormErrors] = useState({});
  
  // Delete confirmation
  const [deleteDialog, setDeleteDialog] = useState({ open: false, driver: null });
  
  // Notifications
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'success' });

  const columns = [
    { id: 'id', label: 'ID' },
    { id: 'name', label: 'Name' },
    { id: 'plateNumber', label: 'Plate Number' }
  ];

  useEffect(() => {
    fetchDrivers();
  }, [page, size]);

  const fetchDrivers = async () => {
    try {
      setLoading(true);
      const response = await driversAPI.getAll(page, size);
      const { data, totalElements: total } = response.data.data;
      setDrivers(data);
      setTotalElements(total);
    } catch (error) {
      showNotification('Error fetching drivers', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingDriver(null);
    setFormData({ name: '', plateNumber: '' });
    setFormErrors({});
    setDialogOpen(true);
  };

  const handleEdit = (driver) => {
    setEditingDriver(driver);
    setFormData({ name: driver.name, plateNumber: driver.plateNumber });
    setFormErrors({});
    setDialogOpen(true);
  };

  const handleDelete = (driver) => {
    setDeleteDialog({ open: true, driver });
  };

  const confirmDelete = async () => {
    try {
      await driversAPI.delete(deleteDialog.driver.id);
      showNotification('Driver deleted successfully', 'success');
      fetchDrivers();
    } catch (error) {
      showNotification('Error deleting driver', 'error');
    }
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    try {
      if (editingDriver) {
        await driversAPI.update({ id: editingDriver.id, ...formData });
        showNotification('Driver updated successfully', 'success');
      } else {
        await driversAPI.create(formData);
        showNotification('Driver created successfully', 'success');
      }
      
      setDialogOpen(false);
      fetchDrivers();
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
        `Error ${editingDriver ? 'updating' : 'creating'} driver`;
      showNotification(errorMessage, 'error');
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.name.trim()) {
      errors.name = 'Name is required';
    } else if (formData.name.length > 20) {
      errors.name = 'Name must be less than 20 characters';
    }
    
    if (!formData.plateNumber.trim()) {
      errors.plateNumber = 'Plate Number is required';
    } else if (formData.plateNumber.length > 20) {
      errors.plateNumber = 'Plate Number must be less than 20 characters';
    }
    
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
        <Typography variant="h4">Drivers Management</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleCreate}
        >
          Add Driver
        </Button>
      </Box>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          data={drivers}
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
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {editingDriver ? 'Edit Driver' : 'Add New Driver'}
        </DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            variant="outlined"
            value={formData.name}
            onChange={(e) => handleFormChange('name', e.target.value)}
            error={!!formErrors.name}
            helperText={formErrors.name}
            sx={{ mb: 2 }}
          />
          <TextField
            margin="dense"
            label="Plate Number"
            fullWidth
            variant="outlined"
            value={formData.plateNumber}
            onChange={(e) => handleFormChange('plateNumber', e.target.value.toUpperCase())}
            error={!!formErrors.plateNumber}
            helperText={formErrors.plateNumber}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingDriver ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation */}
      <ConfirmDialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, driver: null })}
        onConfirm={confirmDelete}
        title="Delete Driver"
        message={`Are you sure you want to delete "${deleteDialog.driver?.name}" (${deleteDialog.driver?.plateNumber})?`}
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

export default Drivers;