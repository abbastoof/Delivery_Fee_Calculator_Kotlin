package example.deliverycalculator.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidTimeFormatException::class)
    fun handleInvalidTimeFormatException(ex: InvalidTimeFormatException): ResponseEntity<Map<String, String>> {
        val error = mapOf("time" to (ex.message ?: "Invalid time format"))
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
}
