


// src/components/Common/LoadingSpinner.js
import React from 'react';
import { CircularProgress, Box } from '@mui/material';

const LoadingSpinner = ({ size = 40 }) => {
  return (
    <Box display="flex" justifyContent="center" alignItems="center" p={3}>
      <CircularProgress size={size} />
    </Box>
  );
};

export default LoadingSpinner;