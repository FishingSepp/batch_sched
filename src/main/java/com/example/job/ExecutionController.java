package com.example.job;

import com.example.job.entities.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/execution")
public class ExecutionController {

    @Autowired
    private ExecutionRepository executionRepository;

    @GetMapping
    @ApiOperation(value = "Get all executions", notes = "Retrieves all executions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved executions"),
            @ApiResponse(code = 404, message = "Executions not found")
    })
    public ResponseEntity<List<Execution>> getAllExecutions() {
        List<Execution> executions = executionRepository.findAll();
        if (executions.isEmpty()) {
            throw new ExecutionNotFoundException("No executions found");
        }
        return ResponseEntity.ok(executions);
    }


    @PostMapping("/")
    @ApiOperation(value = "Create new execution", notes = "Creates a new execution for a given job")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Execution created successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<Execution> createExecution(@Validated @RequestBody Execution execution) {
        Execution newExecution = executionRepository.save(execution);
        return new ResponseEntity<>(newExecution, HttpStatus.CREATED);
    }


    @DeleteMapping("/{eid}")
    @ApiOperation(value = "Delete execution by ID", notes = "Deletes an execution with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Execution deleted successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Void> deleteExecution(@PathVariable("eid") Long eid) {
        Execution execution = executionRepository.findById(eid)
                .orElseThrow(() -> new ExecutionNotFoundException("Execution not found with id: " + eid));
        executionRepository.delete(execution);
        return ResponseEntity.noContent().build();
    }
}

