package example.deliverycalculator.controller

import example.deliverycalculator.model.CartRequest
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
    fun calculateFee(@Valid @RequestBody request: CartRequest): ResponseEntity<Map<String, Any>> {
        val deliveryFee = feeCalculatorService.calculateDeliveryFee(request)
        return ResponseEntity.ok(mapOf("deliveryFee" to deliveryFee))
    }
}