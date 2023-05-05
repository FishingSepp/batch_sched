package com.example.job;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Ist gut adaptierte Spring-Vorgehensweise :) gefällt mir aber trotzdem nicht :(
// Liste der Nachteile siehe Kommentar in ExecutionController.getAllExecutions()
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExecutionNotFoundException extends RuntimeException {
    public ExecutionNotFoundException(String message) {
        super(message);
    }
}
