package ru.test.taskmanagementsystem.expectionhandler;

public class ForbiddenException extends RuntimeException {
  public ForbiddenException(String message) {
    super(message);
  }
}
