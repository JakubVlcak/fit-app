package com.github.fit_app.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityClass, Long id) {
        super(entityClass.getName() + " not found with id: " + id);
    }
}
