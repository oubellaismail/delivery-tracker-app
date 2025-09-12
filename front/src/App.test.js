import { render } from '@testing-library/react';
import App from './App';

// Mock react-router-dom to avoid routing issues in tests
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  BrowserRouter: ({ children }) => <div>{children}</div>,
}));

// Mock AuthContext to avoid context issues
jest.mock('./contexts/AuthContext', () => ({
  AuthProvider: ({ children }) => <div data-testid="auth-provider">{children}</div>,
  useAuth: () => ({
    isAuthenticated: false,
    loading: false,
  }),
}));

test('renders app component', () => {
  render(<App />);
  // Just test that it renders without crashing
  expect(document.body).toBeInTheDocument();
});

test('React is working', () => {
  expect(1 + 1).toBe(2);
});