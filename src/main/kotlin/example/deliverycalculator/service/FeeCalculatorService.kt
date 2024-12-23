package example.deliverycalculator.service

import example.deliverycalculator.model.CartRequest
import org.springframework.stereotype.Service

@Service
class FeeCalculatorService {

    companion object {
        const val FREE_DELIVERY_THRESHOLD = 20000 // 200 euros in cents
        const val SMALL_ORDER_THRESHOLD = 1000 // 10 euros in cents
        const val BASE_DELIVERY_FEE = 200 // Base fee in cents
    }

    fun calculateDeliveryFee(request: CartRequest): Int {
        if (request.cartValue >= FREE_DELIVERY_THRESHOLD) {
            return 0
        }
        var deliveryFee = BASE_DELIVERY_FEE

        if (request.cartValue < SMALL_ORDER_THRESHOLD) {
            deliveryFee += SMALL_ORDER_THRESHOLD - request.cartValue
        }
        return deliveryFee
    }

}

