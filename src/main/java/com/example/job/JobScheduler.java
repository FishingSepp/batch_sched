package com.example.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.support.CronExpression;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.time.Duration;

@Component
public class JobScheduler {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobController jobController;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Scheduled(cron = "*/5 * * * * *") // check interval
    public void checkAndExecuteJobs() {
        System.out.println("Checking jobs...");
        LocalDateTime currentTime = LocalDateTime.now();
        List<Job> jobs = jobRepository.findAll();

        for (Job job : jobs) {
            if (!job.isStatus()) {
                continue;
            }
            String cronExpressionStr = job.getCronExpression();
            CronExpression cronExpression = CronExpression.parse(cronExpressionStr);
            LocalDateTime nextExecutionTime = cronExpression.next(currentTime);
            Duration duration = Duration.between(currentTime, nextExecutionTime);

            if (duration.getSeconds() <= 5) {
                System.out.println("Executing Job with ID: " + job.getJob_id());
                Execution executionRequest = new Execution();
                executorService.submit(() -> {
                    jobController.executeJob(job.getJob_id(), executionRequest);
                });
            }
        }
    }
}
