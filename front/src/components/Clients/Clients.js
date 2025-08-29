// src/components/Clients/Clients.js
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
import { clientsAPI } from '../../services/api';
import DataTable from '../Common/DataTable';
import LoadingSpinner from '../Common/LoadingSpinner';
import ConfirmDialog from '../Common/ConfirmDialog';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  
  // Dialog states
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingClient, setEditingClient] = useState(null);
  const [formData, setFormData] = useState({ name: '', identityId: '' });
  const [formErrors, setFormErrors] = useState({});
  
  // Delete confirmation
  const [deleteDialog, setDeleteDialog] = useState({ open: false, client: null });
  
  // Notifications
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'success' });

  const columns = [
    { id: 'id', label: 'ID' },
    { id: 'name', label: 'Name' },
    { id: 'identityId', label: 'Identity ID' }
  ];

  useEffect(() => {
    fetchClients();
  }, [page, size]);

  const fetchClients = async () => {
    try {
      setLoading(true);
      const response = await clientsAPI.getAll(page, size);
      const { data, totalElements: total } = response.data.data;
      setClients(data);
      setTotalElements(total);
    } catch (error) {
      showNotification('Error fetching clients', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingClient(null);
    setFormData({ name: '', identityId: '' });
    setFormErrors({});
    setDialogOpen(true);
  };

  const handleEdit = (client) => {
    setEditingClient(client);
    setFormData({ name: client.name, identityId: client.identityId });
    setFormErrors({});
    setDialogOpen(true);
  };

  const handleDelete = (client) => {
    setDeleteDialog({ open: true, client });
  };

  const confirmDelete = async () => {
    try {
      await clientsAPI.delete(deleteDialog.client.id);
      showNotification('Client deleted successfully', 'success');
      fetchClients();
    } catch (error) {
      showNotification('Error deleting client', 'error');
    }
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    try {
      if (editingClient) {
        await clientsAPI.update({ id: editingClient.id, ...formData });
        showNotification('Client updated successfully', 'success');
      } else {
        await clientsAPI.create(formData);
        showNotification('Client created successfully', 'success');
      }
      
      setDialogOpen(false);
      fetchClients();
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
        `Error ${editingClient ? 'updating' : 'creating'} client`;
      showNotification(errorMessage, 'error');
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.name.trim()) {
      errors.name = 'Name is required';
    } else if (formData.name.length > 100) {
      errors.name = 'Name must be less than 100 characters';
    }
    
    if (!formData.identityId.trim()) {
      errors.identityId = 'Identity ID is required';
    } else if (formData.identityId.length > 20) {
      errors.identityId = 'Identity ID must be less than 20 characters';
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
        <Typography variant="h4">Clients Management</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleCreate}
        >
          Add Client
        </Button>
      </Box>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          data={clients}
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
          {editingClient ? 'Edit Client' : 'Add New Client'}
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
            label="Identity ID"
            fullWidth
            variant="outlined"
            value={formData.identityId}
            onChange={(e) => handleFormChange('identityId', e.target.value)}
            error={!!formErrors.identityId}
            helperText={formErrors.identityId}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingClient ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation */}
      <ConfirmDialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, client: null })}
        onConfirm={confirmDelete}
        title="Delete Client"
        message={`Are you sure you want to delete "${deleteDialog.client?.name}"?`}
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

export default Clients;