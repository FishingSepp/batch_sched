package com.example.job.service;

import com.example.job.dao.ExecutionRepository;
import com.example.job.dao.JobRepository;
import com.example.job.domain.Execution;
import com.example.job.domain.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.support.CronExpression;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Component
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final ExecutionRepository executionRepository;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public JobService(JobRepository jobRepository, ExecutionRepository executionRepository) {
        this.jobRepository = jobRepository;
        this.executionRepository = executionRepository;
    }

    public ResponseEntity<?> executeJob(Long jid) {
        Optional<Job> jobOptional = jobRepository.findById(jid);
        if (!jobOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("executeJob(): No job found with id: " + jid);
        }

        Job job = jobOptional.get();

        LocalDateTime startTime = LocalDateTime.now();

        // Check if null job script, if so -> success
        String command = job.getCommand();
        if (command == null) {
            Execution execution = createAndSaveExecution(job, true, 0, "No job script provided.", startTime);
            return ResponseEntity.ok(execution);
        }

        // exec job logic
        Process process;
        int exitCode = -1;
        String output = "";
        String errorOutput = "";
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            CompletableFuture<String> outputFuture = CompletableFuture.supplyAsync(() -> reader.lines().collect(Collectors.joining("\n")));
            CompletableFuture<String> errorOutputFuture = CompletableFuture.supplyAsync(() -> errorReader.lines().collect(Collectors.joining("\n")));
            exitCode = process.waitFor();

            // get results of output and error reading
            output = outputFuture.get();
            errorOutput = errorOutputFuture.get();

        } catch (IOException | InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("executeJob(): Exception - " + e.getMessage());
        }

        // success based on exit code
        boolean success = exitCode == 0;
        // if execution failed -> set output to be error output
        // and cut in length to not get backend errors for too long outputs
        if (output.length() >= 10000) output = output.substring(0, 10000);
        if (!success) {
            output = errorOutput.length() >= 10000 ? errorOutput.substring(0, 10000) : errorOutput;
        }

        Execution execution = createAndSaveExecution(job, success, exitCode, output, startTime);
        return ResponseEntity.ok(execution);
    }


    // Cron expression field with 6 fields, one additional field for seconds. Assumed its needed this way.
    // Cron expression explained:
    // * * * * * *
    // | | | | | |
    // | | | | | +-- Day of the Week         (0-7, 0 = Sunday, 1 = Monday, ...)
    // | | | | +---- Month                   (1-12, 1 = January, ...,)
    // | | | +------ Day of the Month        (1-31)
    // | | +-------- Hour of the Day         (0-23)
    // | +---------- Minute of the Hour      (0-59)
    // +------------ Second of the Minute    (0-59)
    // Additional: */int = "every int of x unit"

    // check jobs in interval of 5 seconds:
    @Scheduled(cron = "*/5 * * * * *")
    public void checkAndExecuteJobs() {
        //System.out.println("Checking jobs...");
        log.trace("checkAndExecuteJobs(): checking jobs");
        LocalDateTime currentTime = LocalDateTime.now();
        List<Job> jobs = jobRepository.findAll();

        for (Job job : jobs) {
            if (!job.isStatus()) continue;

            String cronExpressionStr = job.getCronExpression();
            CronExpression cronExpression = CronExpression.parse(cronExpressionStr);
            LocalDateTime nextExecutionTime = cronExpression.next(currentTime);
            Duration duration = Duration.between(currentTime, nextExecutionTime);

            if (duration.getSeconds() <= 5) {
                //System.out.println("Executing Job with ID: " + job.getId());
                log.trace("checkAndExecuteJobs(): executing job: id=" + job.getId());
                executorService.submit(() -> {
                    this.executeJob(job.getId());
                });
            }
        }
    }

    private Execution createAndSaveExecution(Job job, boolean success, int exitCode, String output, LocalDateTime startTime) {
        Execution execution = new Execution();

        execution.setStartTime(startTime);
        execution.setSuccess(success);
        execution.setExitCode(exitCode);
        execution.setOutput(output);
        execution.setEndTime(LocalDateTime.now());
        execution.setJob(job);

        executionRepository.save(execution);

        return execution;
    }

}



