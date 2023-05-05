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

// ##Top: Die ganze Klasse ist SUPER gemacht :))
@Component
public class JobScheduler {

    @Autowired
    private JobRepository jobRepository;

    // ##Bad: Kannst du nicht wissen, siehe Kommentar unten: Controller sollten NIEMALS direkt vom Programm aufgerufen werden,
    // daher sollte keine Referenz auf den jobController gehalten werden
    @Autowired
    private JobController jobController;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    // Kommentare ÜBER die betreffende Zeile schreiben.
    // Da nicht alle Menschen Cron Expressions verstehen wäre es nett, in den Kommentar reinzuschreiben, was die
    // Cron Expression bedeutet. Erschwerend kommt hinzu, dass Spring die Standard-Cron Expression von Unix/Linux
    // um ein Sekundenfeld erweitert hat.
    // ##Advanced: Polling durch Berechnung der nächsten Job-Ausführung ersetzen.
    // Note: This is a Spring Cron Expression: in contrast to standard Unix Cron Expressions with 5 fields, it has an
    //       additional seconds field at the beginning.
    @Scheduled(cron = "*/5 * * * * *") // check interval
    public void checkAndExecuteJobs() {

        // ##Advanced: Logging-Framework einsetzen, statt System.out.println:
        // https://www.baeldung.com/spring-boot-logging
        // ##Discussion: Logging-Framework: Vorteile erklären
        // Dann hier Trace Log machen, mit Angabe des Kontexts:
        //     log.trace("checkAndExecuteJobs(): checking jobs");
        // Das erzeugt dann eine Log-Ausgabe der Form:
        //     JobScheduler.checkAndExecuteJobs(): checking jobs
        // Damit erkennst du (in einem größeren System) dann auch tatsächlich, was er gerade macht
        System.out.println("Checking jobs...");
        LocalDateTime currentTime = LocalDateTime.now();
        List<Job> jobs = jobRepository.findAll();

        // ##Top: Die folgende Implementierung ist SEHR GUT :))
        for (Job job : jobs) {

            // Ja ok, das schreiben die meisten Leute so :(
            // Ich finde es aber besser und übersichtlicher, Methoden nicht unnötig aufzublähen und in die Länge zu ziehen,
            // daher bevorzuge ich:
            //     if (!job.isStatus()) continue;
            //
            // ##Top: Was WIRKLICH TOLL ist: Die meisten Leute hätten hier geschrieben:
            //     if (job.isStatus()) {
            // (also ohne !) und dann den gesamten Inhalt der Methode ein Ebene eingerückt. Auf diese Weise entstehen
            // oft sehr tief und unübersichtliche Schachtellungen.
            // Deine Vorgehensweise, die Abbruchbedingung am Anfang zu prüfen, ist VIEL besser :)
            if (!job.isStatus()) {
                continue;
            }
            String cronExpressionStr = job.getCronExpression();
            CronExpression cronExpression = CronExpression.parse(cronExpressionStr);
            LocalDateTime nextExecutionTime = cronExpression.next(currentTime);
            Duration duration = Duration.between(currentTime, nextExecutionTime);

            if (duration.getSeconds() <= 5) {
                //s.o.: log.trace(""checkAndExecuteJobs(): executing job: id=" +job.getJob_id()));
                System.out.println("Executing Job with ID: " + job.getJob_id());
                Execution executionRequest = new Execution();
                executorService.submit(() -> {
                    // Kannst du nicht wissen, aber:
                    // ##Bad: Controller Methoden sollten NIEMALS direkt vom Programm aufgerufen werden.
                    // In einer Standard-Struktur gehört die executeJob()-Methode in die Service-Klasse:
                    // Deine Service-Klasse ist Job-Scheduler: die Implementierung von executeJob() gehört also hier in JobScheduler
                    // JobController ruft das hier auf.
                    //
                    // ##Discussion: Standard Package Structure
                    jobController.executeJob(job.getJob_id(), executionRequest);
                });
            }
        }
    }
}
