package example.deliverycalculator.service

import example.deliverycalculator.model.CartRequest
import org.springframework.stereotype.Service

@Service
class FeeCalculatorService {

    companion object {
        const val FREE_DELIVERY_THRESHOLD = 20000 // 200 euros in cents
    }

    fun calculateDeliveryFee(request: CartRequest): Int {
        if (request.cartValue >= FREE_DELIVERY_THRESHOLD) {
            return 0
        }
        return -1
    }
}

