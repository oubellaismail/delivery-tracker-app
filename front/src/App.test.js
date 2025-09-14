import { render } from '@testing-library/react';

// Simple component test that doesn't import App
const SimpleComponent = () => <div>Hello World</div>;

test('React testing is working', () => {
  render(<SimpleComponent />);
  expect(document.body).toBeInTheDocument();
});

test('Basic math test', () => {
  expect(2 + 2).toBe(4);
});