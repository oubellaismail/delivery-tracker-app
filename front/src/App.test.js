import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import React from 'react';

// Mock the AuthContext since it's likely missing
const MockAuthProvider = ({ children }) => {
  const mockContextValue = {
    isAuthenticated: false,
    loading: false,
    login: jest.fn(),
    logout: jest.fn(),
    user: null
  };
  
  return (
    <div data-testid="mock-auth-provider">
      {children}
    </div>
  );
};

// Mock the AuthContext hook
jest.mock('./contexts/AuthContext', () => ({
  AuthProvider: ({ children }) => <MockAuthProvider>{children}</MockAuthProvider>,
  useAuth: () => ({
    isAuthenticated: false,
    loading: false,
    login: jest.fn(),
    logout: jest.fn(),
    user: null
  })
}));

// Mock all the component imports that might be missing
jest.mock('./components/Auth/Login', () => {
  return function Login() {
    return <div data-testid="login">Login Component</div>;
  };
});

jest.mock('./components/Dashboard/Dashboard', () => {
  return function Dashboard() {
    return <div data-testid="dashboard">Dashboard Component</div>;
  };
});

jest.mock('./components/Clients/Clients', () => {
  return function Clients() {
    return <div data-testid="clients">Clients Component</div>;
  };
});

jest.mock('./components/Drivers/Drivers', () => {
  return function Drivers() {
    return <div data-testid="drivers">Drivers Component</div>;
  };
});

jest.mock('./components/TransportLogs/TransportLogs', () => {
  return function TransportLogs() {
    return <div data-testid="transport-logs">Transport Logs Component</div>;
  };
});

jest.mock('./components/Layout/Layout', () => {
  return function Layout({ children }) {
    return <div data-testid="layout">{children}</div>;
  };
});

jest.mock('./components/Common/LoadingSpinner', () => {
  return function LoadingSpinner() {
    return <div data-testid="loading-spinner">Loading...</div>;
  };
});

import App from './App';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

const renderWithProviders = (ui) => {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <ThemeProvider theme={theme}>
        {ui}
      </ThemeProvider>
    </MemoryRouter>
  );
};

describe('App Component', () => {
  test('renders without crashing', () => {
    renderWithProviders(<App />);
    // Just test that it renders without throwing an error
    expect(document.body).toBeInTheDocument();
  });

  test('renders login component when not authenticated', () => {
    renderWithProviders(<App />);
    expect(screen.getByTestId('login')).toBeInTheDocument();
  });
});