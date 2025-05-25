package example.deliverycalculator.service

import example.deliverycalculator.exception.InvalidTimeFormatException
import example.deliverycalculator.model.CartRequest
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import kotlin.math.ceil
import kotlin.math.min

@Service
class  FeeCalculatorService {

    companion object {
        const val FREE_DELIVERY_THRESHOLD = 20000 // 200 euros in cents
        const val SMALL_ORDER_THRESHOLD = 1000 // 10 euros in cents
        const val BASE_DELIVERY_FEE = 200 // Base fee in cents
        const val ADDITIONAL_DISTANCE_UNIT = 500 // Additional fee per 500 meters
        const val ADDITIONAL_DISTANCE_FEE = 100 // 1 euro per additional 500 meters
        const val ITEM_SURCHARGE_THRESHOLD = 5 // Surcharge starts from the 5th item
        const val ITEM_SURCHARGE = 50 // 50 cents per additional item starting from the 5th
        const val BULK_ITEM_SURCHARGE_THRESHOLD = 13 // Bulk surcharge threshold
        const val BULK_ITEM_SURCHARGE = 120 // 1.20 euros for more than 12 items
        const val MAX_DELIVERY_FEE = 1500 // 15 euros in cents
        const val RUSH_HOUR_MULTIPLIER = 1.2 // 1.2x multiplier during rush hours
    }

    fun calculateDeliveryFee(request: CartRequest): Int {

        var deliveryFee = 0

        // Apply free delivery threshold
        deliveryFee += calculateFreeDelivery(request.cartValue)

        // Add small order surcharge
        deliveryFee += calculateSmallOrderSurcharge(request.cartValue)

        // Add distance-based fee
        deliveryFee += calculateDistanceFee(request.deliveryDistance)

        // Add distance-based fee
        deliveryFee += calculateItemSurcharge(request.numberOfItems)


        // Apply rush hour multiplier if applicable
        if (isRushHour(request.time)) {
            deliveryFee = (deliveryFee * RUSH_HOUR_MULTIPLIER).toInt()
        }

        return min(deliveryFee, MAX_DELIVERY_FEE)
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

    private fun calculateItemSurcharge(numberOfItems: Int): Int {
        if (numberOfItems < ITEM_SURCHARGE_THRESHOLD) return 0

        val extraItems = numberOfItems - ITEM_SURCHARGE_THRESHOLD + 1 // Include 5th item
        var itemSurcharge = extraItems * ITEM_SURCHARGE

        // Add bulk surcharge if items are greater than or equal to the bulk threshold
        if (numberOfItems >= BULK_ITEM_SURCHARGE_THRESHOLD) {
            itemSurcharge += BULK_ITEM_SURCHARGE
        }
        return itemSurcharge
    }

    private fun isRushHour(timeString: String): Boolean {
        try {
            val dateTime = ZonedDateTime.parse(timeString)
            return dateTime.dayOfWeek == DayOfWeek.FRIDAY && dateTime.hour in 15..18
        } catch (e: DateTimeParseException) {
            throw InvalidTimeFormatException("${e.message}")
        }
    }
}
