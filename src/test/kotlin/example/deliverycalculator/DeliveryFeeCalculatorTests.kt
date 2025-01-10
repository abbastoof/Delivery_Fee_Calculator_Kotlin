package example.deliverycalculator

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import example.deliverycalculator.model.CartRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeliveryFeeCalculatorTests(@Autowired val restTemplate: TestRestTemplate) {
    private val baseUrl = "/api/1/fee"

    private fun postRequest(request: CartRequest): Pair<HttpStatusCode, DocumentContext> {
        // Make the POST request
        val response = restTemplate.postForEntity(baseUrl, request, String::class.java)
        // Parse the JSON response body
        val documentContext = JsonPath.parse(response.body ?: "{}")

        // Return the HTTP status and the parsed JSON document
        return Pair(response.statusCode, documentContext)
    }


    @Test
    fun `delivery fee is free for cart value above 200 euros`() {
        val request = CartRequest(cartValue = 20000, deliveryDistance = 500, numberOfItems = 2, time = "2024-02-21T14:40:00Z")
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(0, deliveryFee)
    }

    @Test
    fun `small order surcharge applied for cart value below 10 euros`() {
        val request = CartRequest(cartValue = 500, deliveryDistance = 500, numberOfItems = 2, time = "2024-02-21T14:40:00Z")
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(700, deliveryFee) // 500 surcharge + 200 base fee = 700
    }

    @Test
    fun `base delivery fee for 1 km`() {
        val request = CartRequest(cartValue = 1000, deliveryDistance = 1000, numberOfItems = 2, time = "2024-02-21T14:40:00Z")
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(200, deliveryFee) // Base fee for 1 km
    }

    @Test
    fun `additional distance fee for every 500 meters over 1 km`() {
        val request = CartRequest(cartValue = 1000, deliveryDistance = 1501, numberOfItems = 2, time = "2024-02-21T14:40:00Z")
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(400, deliveryFee) // Base 200 + 1 euro for each extra 500m
    }

    @Test
    fun `item surcharge for 5 more items`() {
        val request = CartRequest(cartValue = 1000, deliveryDistance = 500, numberOfItems = 5, time = "2024-02-21T14:40:00Z")
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(250, deliveryFee) // 200 base fee + 50 cents for the 5th item
    }

    @Test
    fun `bulk fee for more than 12 items`() {
        val request = CartRequest(cartValue = 1000, deliveryDistance = 500, numberOfItems = 13, time = "2024-02-21T14:40:00Z")
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(770, deliveryFee) // 200 base fee + Total item surcharge (450 + 120 = 570 cents) = 770
    }

    @Test
    fun `rush hour multiplier applied during Friday rush hours`() {
        val request = CartRequest(
            cartValue = 1000,
            deliveryDistance = 1500,
            numberOfItems = 5,
            time = "2024-02-23T16:00:00Z" // Friday at 4 PM UTC
        )
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        // Base calculation: 200 (base) + 100 (distance) + 50 (items) = 350
        // After 1.2x multiplier = 420
        assertEquals(420, deliveryFee)
    }

    @Test
    fun `no rush hour multiplier outside Friday rush hours`() {
        val request = CartRequest(
            cartValue = 1000,
            deliveryDistance = 1500,
            numberOfItems = 5,
            time = "2024-02-23T14:00:00Z" // Friday at 2 PM UTC (before rush hour)
        )
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        // Base calculation: 200 (base) + 100 (distance) + 50 (items) = 350
        assertEquals(350, deliveryFee)
    }

    @Test
    fun `maximum fee limit applied`() {
        val request = CartRequest(
            cartValue = 1000,
            deliveryDistance = 10000, // 10km
            numberOfItems = 15,        // Many items to generate high fee
            time = "2024-02-23T16:00:00Z" // During rush hour to maximize fee
        )
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        assertEquals(1500, deliveryFee) // Should be capped at 15€ (1500 cents)
    }

    @Test
    fun `rush hour on non-Friday does not apply multiplier`() {
        val request = CartRequest(
            cartValue = 1000,
            deliveryDistance = 1500,
            numberOfItems = 5,
            time = "2024-02-22T16:00:00Z" // Thursday at 4 PM UTC
        )
        val (status, documentContext) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        val deliveryFee: Int = documentContext.read("$.deliveryFee")
        // Base calculation: 200 (base) + 100 (distance) + 50 (items) = 350
        assertEquals(350, deliveryFee)
    }

    @Test
    fun `invalid cart value returns bad request`() {
        val request = CartRequest(
            cartValue = -10,
            deliveryDistance = 500,
            numberOfItems = 2,
            time = "2024-02-21T14:40:00Z"
        )
        val (status, documentContext) = postRequest(request)

        // Assert that the status is Bad Request (HTTP 400)
        assertEquals(HttpStatus.BAD_REQUEST, status)

        // Assert that the error message is as expected
        val errorMessage: String = documentContext.read("$.cartValue")
        assertEquals("Cart value must be greater than 0", errorMessage)
    }

//    @Test
//    fun `invalid time format returns bad request`() {
//        val request = CartRequest(
//            cartValue = 1000,
//            deliveryDistance = 500,
//            numberOfItems = 2,
//            time = "invalid-time"
//        )
//        val (status, documentContext) = postRequest(request)
//
//        assertEquals(HttpStatus.BAD_REQUEST, status)
//        // Assert that the error message is as expected
//        val errorMessage: String = documentContext.read("$.time")
//        assertEquals("Time must be provided in ISO 8601 format", errorMessage)
//    }

}