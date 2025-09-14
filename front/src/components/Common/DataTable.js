// src/components/Common/DataTable.js
import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Paper,
  IconButton,
  Box,
  Typography
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';

const DataTable = ({
  data = [],
  columns = [],
  totalElements = 0,
  page = 0,
  size = 10,
  onPageChange,
  onRowsPerPageChange,
  onEdit,
  onDelete
}) => {
  const handleChangePage = (event, newPage) => {
    onPageChange(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    onRowsPerPageChange(parseInt(event.target.value, 10));
    onPageChange(0);
  };

  if (data.length === 0) {
    return (
      <Paper sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h6" color="textSecondary">
          No data available
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow sx={{ bgcolor: 'grey.50' }}>
              {columns.map((column) => (
                <TableCell key={column.id} sx={{ fontWeight: 'bold' }}>
                  {column.label}
                </TableCell>
              ))}
              <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data.map((row) => (
              <TableRow key={row.id} hover>
                {columns.map((column) => (
                  <TableCell key={column.id}>
                    {column.render ? column.render(row) : row[column.id]}
                  </TableCell>
                ))}
                <TableCell>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <IconButton
                      size="small"
                      color="primary"
                      onClick={() => onEdit(row)}
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => onDelete(row)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[5, 10, 25]}
        component="div"
        count={totalElements}
        rowsPerPage={size}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </Paper>
  );
};

export default DataTable;