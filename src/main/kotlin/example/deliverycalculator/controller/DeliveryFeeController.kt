package example.deliverycalculator.controller

import example.deliverycalculator.model.CartRequest
import example.deliverycalculator.model.DeliveryFeeResponse
import example.deliverycalculator.service.FeeCalculatorService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/1/fee")
class DeliveryFeeController(private val feeCalculatorService: FeeCalculatorService) {

    @PostMapping
    fun calculateFee(@Valid @RequestBody request: CartRequest): ResponseEntity<DeliveryFeeResponse> {
        // Step 1: Calculate the delivery fee using the service
        val deliveryFee = feeCalculatorService.calculateDeliveryFee(request)

        // Step 2: Wrap the fee in a DeliveryFeeResponse object
        val response = DeliveryFeeResponse(deliveryFee)

        // Step 3: Return the response as JSON
        return ResponseEntity.ok(response)
    }
}