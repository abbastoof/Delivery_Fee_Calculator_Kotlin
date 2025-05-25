# Delivery Fee Calculator - Wolt Assignment 2024 🚀

My implementation of the Wolt Internship assignment 2024, showcasing a robust delivery fee calculation system built with Kotlin and Spring Boot. Having previously tackled this challenge with FastAPI, I'm now exploring the Spring ecosystem to demonstrate versatility across different frameworks.

This REST API intelligently calculates delivery fees by considering multiple factors including cart value, delivery distance, item quantity, and delivery timing.

## Technology Stack

- **Kotlin** + **Spring Boot** - Core framework
- **Bean Validation** - Input validation
- **JUnit 5** + **JSONPath** - Testing
- **Gradle** - Build tool

## API Endpoint

### Calculate Delivery Fee

```http
POST /api/1/fee
```

**Request:**
```json
{
  "cartValue": 1000,
  "deliveryDistance": 1500,
  "numberOfItems": 5,
  "time": "2024-02-23T16:00:00Z"
}
```

**Response:**
```json
{
  "deliveryFee": 420
}
```

**Fields:**
- `cartValue`: Cart value in cents (min: 1)
- `deliveryDistance`: Distance in meters (min: 1) 
- `numberOfItems`: Item count (min: 1)
- `time`: ISO 8601 format (e.g., "2024-02-23T16:00:00Z")

## Fee Calculation Rules

1. **Free Delivery**: Cart value ≥ €200 → €0 fee
2. **Small Order Surcharge**: Cart < €10 → surcharge = €10 - cart value
3. **Base Fee**: €2 for distances up to 1km
4. **Distance Fee**: +€1 per 500m beyond 1km (rounded up)
5. **Item Surcharge**: €0.50 per item starting from 5th item
6. **Bulk Fee**: +€1.20 for 13+ items
7. **Rush Hour**: 1.2x multiplier on Fridays 3-7 PM UTC
8. **Maximum Cap**: €15 maximum fee

## Project Structure

```
src/main/kotlin/example/deliverycalculator/
├── controller/DeliveryFeeController.kt    # REST endpoint
├── service/FeeCalculatorService.kt        # Business logic
├── model/                                 # DTOs
├── exception/                             # Error handling
└── DeliveryCalculatorApplication.kt       # Main app

src/test/kotlin/example/deliverycalculator/
└── DeliveryFeeCalculatorTests.kt          # Main test suite
```

## Getting Started

```bash
# Build and run
./gradlew build
./gradlew bootRun

# Run tests
./gradlew test

# API available at: http://localhost:8080/api/1/fee
```

## Example Usage

### Basic Request
```bash
curl -X POST http://localhost:8080/api/1/fee \
  -H "Content-Type: application/json" \
  -d '{"cartValue": 1000, "deliveryDistance": 1000, "numberOfItems": 2, "time": "2024-02-21T14:40:00Z"}'
```
Response: `{"deliveryFee": 200}`

### Free Delivery
```bash
curl -X POST http://localhost:8080/api/1/fee \
  -H "Content-Type: application/json" \
  -d '{"cartValue": 25000, "deliveryDistance": 1500, "numberOfItems": 5, "time": "2024-02-21T14:40:00Z"}'
```
Response: `{"deliveryFee": 0}`

### Rush Hour
```bash
curl -X POST http://localhost:8080/api/1/fee \
  -H "Content-Type: application/json" \
  -d '{"cartValue": 1000, "deliveryDistance": 1500, "numberOfItems": 5, "time": "2024-02-23T16:00:00Z"}'
```
Response: `{"deliveryFee": 420}`

## Error Handling

**Validation Errors:**
```json
{
  "cartValue": "Cart value must be greater than 0",
  "time": "Time must be provided in ISO 8601 format"
}
```

## Test Coverage

Comprehensive tests covering all business rules, edge cases, validation, and error scenarios with 100% coverage of fee calculation logic.