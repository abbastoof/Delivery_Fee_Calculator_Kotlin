package example.deliverycalculator.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CartRequest(
    @field:Min(1, message = "Cart value must be greater than 0") val cartValue: Int,
    @field:Min(1, message = "Delivery distance must be greater than 0") val deliveryDistance: Int,
    @field:Min(1, message = "Number of items must be greater than 0") val numberOfItems: Int,
    @field:NotBlank(message = "Time must be provided")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?Z\$",
        message = "Time must be provided in ISO 8601 format"
    )
    val time: String
)