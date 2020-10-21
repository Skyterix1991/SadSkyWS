package pl.skyterix.sadsky.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        return responseEntity(new ApiError(BAD_REQUEST, Errors.BAD_REQUEST.getErrorMessage(), exception));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(Errors.BAD_REQUEST.getErrorMessage());
        apiError.addValidationErrors(exception.getBindingResult().getFieldErrors());
        apiError.addValidationError(exception.getBindingResult().getGlobalErrors());

        return responseEntity(apiError);
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException exception
    ) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(Errors.BAD_REQUEST.getErrorMessage());
        apiError.addValidationErrors(exception.getConstraintViolations());

        return responseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return responseEntity(new ApiError(HttpStatus.BAD_REQUEST, Errors.BAD_REQUEST.getErrorMessage(), exception));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return responseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, Errors.BAD_REQUEST.getErrorMessage(), exception));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("Nie znaleziono metody %s dla endpointu %s", exception.getHttpMethod(), exception.getRequestURL()));
        apiError.setDebugMessage(exception.getMessage());
        return responseEntity(apiError);
    }

    @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException exception) {
        return responseEntity(new ApiError(HttpStatus.NOT_FOUND, exception));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException exception,
                                                                  WebRequest request) {
        if (exception.getCause() instanceof ConstraintViolationException) {
            return responseEntity(new ApiError(HttpStatus.CONFLICT, "Błąd bazy danych", exception.getCause()));
        }
        return responseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("Parametr '%s' pola '%s' nie mógł być przenkonwertowany na '%s'", exception.getName(), exception.getValue(), exception.getRequiredType().getSimpleName()));
        apiError.setDebugMessage(exception.getMessage());
        return responseEntity(apiError);
    }

    @ExceptionHandler({
            AgeNotMeetingRequired.class,
            BlacklistedSortException.class,
            DayDeadlineException.class,
            FriendsCountExceededMaximumException.class,
            GroupNotFoundException.class,
            GroupUnauthorizedException.class,
            PendingFriendInvitesExceededMaximumException.class,
            PredictionIsExpiredException.class,
            PredictionResultIsAlreadyGeneratedException.class,
            PredictionResultIsNotReadyToGenerateException.class,
            RecordAlreadyExistsException.class,
            RecordNotFoundException.class,
            RecordNotFoundInCollectionException.class,
            RecordAlreadyExistsInCollectionException.class,
            SentFriendInvitesExceededMaximumException.class,
            TargetRecordIsTheSameAsSourceException.class
    })
    protected ResponseEntity<Object> handleEntityNotFound(
            RestException exception) {
        ApiError apiError = new ApiError(exception.getStatus(), exception.getMessage(), (Throwable) exception);

        return responseEntity(apiError);
    }

    private ResponseEntity<Object> responseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}