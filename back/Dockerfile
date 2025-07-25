# ---------- Stage 1: Build the application ----------
    FROM eclipse-temurin:21-jdk-jammy AS build

    WORKDIR /app
    
    # Copy pom.xml and download dependencies first (for better Docker caching)
    COPY pom.xml .
    
    # Add this line if you have a `.mvn` folder (for Maven Wrapper support)
    COPY .mvn .mvn
    
    # Copy the Maven wrapper
    COPY mvnw .
    
    # Pre-fetch dependencies
    RUN ./mvnw dependency:resolve
    
    # Copy the rest of the project
    COPY src ./src
    
    # Build the Spring Boot app without running tests
    RUN ./mvnw clean package -DskipTests
    
    
    # ---------- Stage 2: Create runtime image ----------
    FROM eclipse-temurin:21-jre-jammy
    
    WORKDIR /app
    
    # Expose Spring Boot's default port
    EXPOSE 8080
    
    # Copy the built jar from the first stage
    COPY --from=build /app/target/*.jar app.jar
    
    # Run the Spring Boot app
    ENTRYPOINT ["java", "-jar", "app.jar"]
    