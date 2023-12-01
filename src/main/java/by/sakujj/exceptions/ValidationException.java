package by.sakujj.exceptions;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class ValidationException extends RuntimeException{
    private final Set<String> violations;

    public Set<String> getViolations() {
        return Collections.unmodifiableSet(violations);
    }
}
