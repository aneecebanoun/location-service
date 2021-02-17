package banoun.aneece.exceptions;

public class LocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LocationException(Throwable t) {
		super(t);
	}

	public LocationException(String m) {
		super(m);
	}

	public LocationException() {
		super();
	}

}
