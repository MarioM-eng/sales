package com.mmdevelopment.utils.exceptions;

public class NonexistentEntityException extends Exception{
    public NonexistentEntityException() {
        super();
    }

    public NonexistentEntityException(String message) {
        super(message);
    }
}
