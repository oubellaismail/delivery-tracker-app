# Development Dockerfile for Angular with hot reloading

FROM node:22-alpine

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install all dependencies (including dev dependencies)
RUN npm install

# Copy source code
COPY . .

# Expose port 4200
EXPOSE 4200

# Start the development server with hot reloading
CMD ["npx", "ng", "serve", "--host", "0.0.0.0", "--port", "4200"]
