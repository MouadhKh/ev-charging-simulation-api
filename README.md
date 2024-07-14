# EV Charging Simulation API

This Spring Boot application provides an API for managing and running EV charging simulations.

## Prerequisites

- Java 17 
- Maven
- Docker
- [EV Charging Simulator](https://github.com/MouadhKh/ev-charging-simulator) (has to be installed locally)

## Setup

1. Clone the repository:
```
git clone https://github.com/MouadhKh/ev-charging-simulation-api.git
```
2. Navigate to the project directory:
```
cd ev-charging-simulation-api
```
3. Create a `.env` file in the root directory with the following environment variables:

```
POSTGRES_DB=your_database_name
POSTGRES_URL=jdbc:postgresql://localhost:5432/your_database_name
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password
```

Replace the values with your desired database configuration.
**Don't change the used port**
4. Start the database container:
```
docker-compose up -d
```

5. Run the Spring Boot application:

```
./mvnw spring-boot:run
```
## Usage

Once the application is running, you can access the Swagger UI Documentation to explore the available endpoints :
```http://localhost:8080//api-docs.html```
