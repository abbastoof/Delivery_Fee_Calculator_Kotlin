package example.deliverycalculator.service

import example.deliverycalculator.model.CartRequest
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class FeeCalculatorService {

    companion object {
        const val FREE_DELIVERY_THRESHOLD = 20000 // 200 euros in cents
        const val SMALL_ORDER_THRESHOLD = 1000 // 10 euros in cents
        const val BASE_DELIVERY_FEE = 200 // Base fee in cents
        const val ADDITIONAL_DISTANCE_UNIT = 500 // Additional fee per 500 meters
        const val ADDITIONAL_DISTANCE_FEE = 100 // 1 euro per additional 500 meters
    }

    fun calculateDeliveryFee(request: CartRequest): Int {
        var deliveryFee = 0

        // Apply free delivery threshold
        deliveryFee += calculateFreeDelivery(request.cartValue)

        // Add small order surcharge
        deliveryFee += calculateSmallOrderSurcharge(request.cartValue)

        // Add distance-based fee
        deliveryFee += calculateDistanceFee(request.deliveryDistance)

        return deliveryFee
    }

    private fun calculateFreeDelivery(cartValue: Int): Int {
        return if (cartValue >= FREE_DELIVERY_THRESHOLD) 0 else BASE_DELIVERY_FEE
    }

    private fun calculateSmallOrderSurcharge(cartValue: Int): Int {
        return if (cartValue < SMALL_ORDER_THRESHOLD) SMALL_ORDER_THRESHOLD - cartValue else 0
    }

    private fun calculateDistanceFee(distance: Int): Int {
        if (distance <= 1000) return 0
        // To calculate the "extra distance" beyond the first kilometer, we subtract 1000 from the total distance.
        val extraMeters = distance - 1000
        return ceil(extraMeters / ADDITIONAL_DISTANCE_UNIT.toDouble()).toInt() * ADDITIONAL_DISTANCE_FEE
    }
}
