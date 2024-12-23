package example.deliverycalculator

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

    private fun postRequest(request: CartRequest): Pair<HttpStatusCode, Map<out Any?, Any?>> {
        val response = restTemplate.postForEntity(baseUrl, request, Map::class.java)
        return response.statusCode to (response.body ?: emptyMap<String, Any?>())
    }

    @Test
    fun `delivery fee is free for cart value above 200 euros`() {
        val request = CartRequest(cartValue = 20000, deliveryDistance = 500, numberOfItems = 2, time = "2024-02-21T14:40:00Z")
        val (status, body) = postRequest(request)
        assertEquals(HttpStatus.OK, status)
        assertEquals(0, body["deliveryFee"])
    }

}