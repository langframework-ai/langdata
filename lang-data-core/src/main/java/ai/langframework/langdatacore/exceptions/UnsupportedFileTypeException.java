package ai.langframework.langdatacore.exceptions;

public class UnsupportedFileTypeException extends RuntimeException {
  public UnsupportedFileTypeException() {
    super("Unsupported file type");
  }

  public UnsupportedFileTypeException(String message) {
    super(message);
  }

  public UnsupportedFileTypeException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsupportedFileTypeException(Throwable cause) {
    super(cause);
  }
}
