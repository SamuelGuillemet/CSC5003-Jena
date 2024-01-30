package itsudparis.tools;

public class QuitException extends Exception {
  public QuitException() {
    super();
  }

  public QuitException(String message) {
    super(message);
  }

  public QuitException(String message, Throwable cause) {
    super(message, cause);
  }

  public QuitException(Throwable cause) {
    super(cause);
  }
}
