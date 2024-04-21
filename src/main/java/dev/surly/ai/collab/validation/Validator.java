package dev.surly.ai.collab.validation;

public interface Validator<T> {
    boolean validate(T input);
}
