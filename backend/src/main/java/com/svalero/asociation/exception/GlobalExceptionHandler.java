package com.svalero.asociation.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.svalero.asociation.controller.SocioController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;


// mensaje de error customizado implantado en los Elsethrow in capa service va 'subiendo' por *NotFoundException,
// los cuales son hijos de ResourceNotFound y este de RunTimeExcepction, el handler lo maneja haciendo un objeto ErrorResponse
//Exception clase reemplazado por RunTime , evitar tener que hacer un throw excepcion en los metodos que necesitas manejar

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(SocioController.class);

    @ExceptionHandler(ResourceNotFoundException.class)//si el .class deja de ser el padre, se produce error 500
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        logger.error("Resource not found", ex);
        ErrorResponse error = ErrorResponse.generalError(404, ex.getMessage(), "Resource Not Found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        ErrorResponse error = ErrorResponse.generalError(400, "Validation failed for one or more fields", "Bad Request");
        logger.error("Not valid", ex);
        ex.getBindingResult().getAllErrors().forEach(f -> {
            if (f instanceof FieldError fieldError) {
                error.addError(fieldError.getField(), f.getDefaultMessage());
            } else {
                error.addError(f.getObjectName(), f.getDefaultMessage());
            }
        });

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = ErrorResponse.generalError(
                400,
                "Malformed request body",
                "Bad Request"
        );

        Throwable root = ex.getMostSpecificCause();
        String rootMessage = root != null ? root.getMessage() : ex.getMessage();

        if (root instanceof InvalidFormatException invalidFormat) {
            String fieldPath = invalidFormat.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                    .reduce((a, b) -> a + "." + b)
                    .orElse("requestBody");
            String targetType = invalidFormat.getTargetType() != null
                    ? invalidFormat.getTargetType().getSimpleName()
                    : "required type";
            String invalidValue = invalidFormat.getValue() != null ? invalidFormat.getValue().toString() : "null";

            error.addError(fieldPath, "Invalid value '" + invalidValue + "' for " + targetType);
            if (targetType.contains("LocalDate")) {
                error.addError(fieldPath + "Format", "Use format yyyy-MM-dd");
            }
        } else if (root instanceof MismatchedInputException mismatchedInput) {
            String fieldPath = mismatchedInput.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                    .reduce((a, b) -> a + "." + b)
                    .orElse("requestBody");
            error.addError(fieldPath, "JSON structure/type does not match expected request body");
        } else if (root instanceof JsonParseException) {
            error.addError("requestBody", "Malformed JSON syntax");
        } else if (rootMessage != null && !rootMessage.isBlank()) {
            error.addError("requestBody", rootMessage);
        } else if (ex instanceof HttpMessageConversionException) {
            error.addError("requestBody", "Request body could not be converted");
        } else {
            error.addError("requestBody", "Request body could not be deserialized");
        }

        logger.error("Request body could not be deserialized", ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestManual(BusinessRuleException ex) {
        ErrorResponse error = ErrorResponse.generalError(409, ex.getMessage(), "Conflict");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation", ex);
        ErrorResponse error = ErrorResponse.generalError(
                409,
                "No se puede eliminar o modificar este registro porque tiene relaciones asociadas",
                "Conflict"
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGlobalError(Exception ex) {
        logger.error("Unexpected server error", ex);
        ErrorResponse error =  ErrorResponse.generalError(500, "An unexpected error occurred", "Internal Server Error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
