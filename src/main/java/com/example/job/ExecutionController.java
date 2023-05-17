package com.example.job;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.annotations.*;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;



@RestController
@RequestMapping("/execution")
public class ExecutionController {

    private ExecutionRepository executionRepository;
    private JobRepository jobRepository;

    public ExecutionController(ExecutionRepository executionRepository, JobRepository jobRepository) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
    }

    /*//not needed anymore?
    @Transactional
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
        List<Execution> executionDTOs = new ArrayList<>();
        for (Execution execution : executions) {
            Execution executionDTO = new Execution();
            executionDTO.setSuccess(execution.getSuccess());
            executionDTO.setExitCode(execution.getExitCode());
            executionDTO.setOutput(execution.getOutput());
            executionDTO.setJobId(execution.getJob().getId());
            executionDTOs.add(executionDTO);
        }
        System.out.println("Getting all executions...");
        return ResponseEntity.ok(executionDTOs);
    }*/

    //took away 404 response as it would flood console for not-yet executed jobs
    @GetMapping("/{jid}")
    @ApiOperation(value = "Gets executions of a job by ID", notes = "Gets the executions of a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Executions of job received successfully"),
            //@ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<List<Execution>> getExecutionsByJobId(@PathVariable Long jid) {
        List<Execution> executions = executionRepository.findByJobJid(jid);
        if (executions.isEmpty()) {
            //System.out.println("Empty response for executions of job with Id "+jid+"...");
            return ResponseEntity.ok(Collections.emptyList());
        }
        //System.out.println("Getting executions of job with Id "+jid+"...");
        //return statement gives json format list and doesn't use the overwritten toString method
        //when execution object is returned in response entity spring auto converts objects to json by using the getters to construct the json
        return ResponseEntity.ok(executions);
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
        System.out.println("Deleting executions with eId "+eid+"...");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/job/{jid}")
    @ApiOperation(value = "Delete executions of a job by job ID", notes = "Deletes all executions of a job with the given job ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Executions deleted successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Void> deleteExecutionsOfJob(@PathVariable("jid") Long jid) {
        List<Execution> executions = executionRepository.findByJobJid(jid);

        if (executions.isEmpty()) {
            throw new ExecutionNotFoundException("No executions found with jobId: " + jid);
        }

        executionRepository.deleteAll(executions);
        System.out.println("Deleting executions with jobId " + jid + "...");
        return ResponseEntity.noContent().build();
    }


}

