package banoun.aneece.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LocationExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<String> handleLocationErrors(LocationException ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_GATEWAY);
	}
}
