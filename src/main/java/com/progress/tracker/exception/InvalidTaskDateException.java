package com.progress.tracker.exception;

public class InvalidTaskDateException extends RuntimeException {
    public InvalidTaskDateException(String message) {
        super(message);
    }
}
