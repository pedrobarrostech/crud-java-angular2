/**
 * Created by Pedro Barros
 */
package com.sample.domain;

public class EntityNotFoundException extends Exception {

    private static final long serialVersionUID = 6103739170311479802L;
    public EntityNotFoundException(String message) {
        super(message);
    }
}
