package com.example.job;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import io.swagger.annotations.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

// Top: JobController, ExecutionController auf /job /execution :))
@RestController
@RequestMapping("/job")
public class JobController {

    // ##Top: final :) Wie bist du darauf gekommen?
    // Im konkreten Fall fände ich aber @Autowired besser...
    private final JobRepository jobRepository;
    private final ExecutionController executionController;
    private final ExecutionRepository executionRepository;

    // Muss man hier nicht @Autowired ranschreiben? Interessant, dass das bei Spring so funktioniert.
    public JobController(JobRepository jobRepository, ExecutionController executionController, ExecutionRepository executionRepository) {
        this.jobRepository = jobRepository;
        this.executionController = executionController;
        this.executionRepository = executionRepository;
    }

    @GetMapping
    // ##Top: Dokumentation :)
    @ApiOperation(value = "Get all jobs", notes = "Gets all existing jobs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Jobs received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public List<Job> getAllJobs() {
        // log.trace()...
        System.out.println("Getting all jobs...");
        return jobRepository.findAll();
    }


    @GetMapping("/{jid}")
    @ApiOperation(value = "Gets job by ID", notes = "Gets a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job received successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    // ##Test: Wenn möglich Primitives verwenden: da jid ein required-Parameter ist, sollte man hier long statt Long schreiben können?!
    // ##Test: was passiert, wenn man jid weg lässt a) bei Long  b) bei long?
    // Bei long müsste es direkt eine NullpointerException geben.
    // Bei Long sollte aber ebenfalls eine Exception kommen:
    //     ## Test: gibt es eine sprechende Exception von Spring (required parameter missing)?
    //     Sonst sollte eine NullpointerException bei jobRepository.findById() kommen
    // Gilt auch für alle anderen Endpunkte und Methoden
    public ResponseEntity<Job> getJobById(@PathVariable Long jid) {
        Optional<Job> jobOptional = jobRepository.findById(jid);
        if (jobOptional.isPresent()) {
            System.out.println("Getting job with Id "+jid+"...");
            return new ResponseEntity<>(jobOptional.get(), HttpStatus.OK);
        // else braucht man nicht, da oben ja return steht
        // Grundsätzlich: bei guter Programmierung braucht man else fast nie => wenn du else verwendest ist meist
        // irgendetwas nicht gut, oder else ist unnötig
        } else {
            // Oben machst du System.out.println() im Erfolgsfall. Stattdessen sollten man den Fehlerfall loggen:
            // log.trace("getJobById(): job not found for id=" jid)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // ##Top: Du hast dir hier offenbar Gedanken gemacht, ob du PUT oder POST verwenden sollst (bei update verwendest du PUT).
    // Aus meiner Sicht ist die Unterscheidung richtig angewendet: https://www.baeldung.com/rest-http-put-vs-post
    @PostMapping
    @ApiOperation(value = "Creates job by ID", notes = "Creates a job with the given ID")
    @ApiResponses(value = {
            // ##Top: return code 201 :)
            @ApiResponse(code = 201, message = "Job created successfully")
    })
    public ResponseEntity<Job> createJob(@Validated @RequestBody Job job) {
        Job createdJob = jobRepository.save(job);
        // Log-Einträge (ok, du loggst nicht ;) sollten IMMER die relevnte Kontextinfo enthalten, hier also den Job, der gespeichert wurde:
        // log.trace("createJob(): job created: " +createdJob);
        System.out.println("Creating job...");
        // ##Top: sehr schön: du gibst den createdJob aus, der (vermutlich) die generierte id als neue Info enthält.
        // Ist das so, hast du das getestet?
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }


    @PutMapping("/{jid}")
    // Der Profi würde "update" statt "edit" sagen, das ist ja auch dein Methoden-Name ;)
    @ApiOperation(value = "Update job by ID", notes = "Updates the job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job edited successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> updateJob(@PathVariable("jid") Long jid, @Validated @RequestBody Job job) {
        // ##Top: orElseThrow() :)
        // ABER: zum Thema selbstdefinierte Exceptions und Exception Messages siehe Kommentare in ExecutionController.getAllExecutions()
        Job existingJob = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        existingJob.setName(job.getName());
        existingJob.setDescription(job.getDescription());
        existingJob.setJob_script(job.getJob_script());
        existingJob.setCronExpression(job.getCronExpression());
        existingJob.setStatus(job.isStatus());
        existingJob.setStart_date(job.getStart_date());
        existingJob.setEnd_date(job.getEnd_date());
        Job updatedJob = jobRepository.save(existingJob);
        // log.trace(), Kontext-Info loggen
        System.out.println("Editing job with Id "+jid+"...");
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }

    // ##Top: Der ERSTE Kritikpunkt, den ich finde: sehr beeindruckend, normalerweise habe ich VIEL mehr zu kritisieren:
    // ##Bad: Wieso gibtst du als RequestBody ne Map rein. Wieso nicht einfach ein boolean: @PutMapping("/{jid}/{status)")
    @PutMapping("/{jid}/status")
    @ApiOperation(value = "Update job status by ID", notes = "Updates the status of a job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Job status edited successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Job> updateJobStatus(@PathVariable("jid") Long jid, @Validated @RequestBody Map<String, Boolean> body) {
        // Zum Thema Exception siehe Kommentar in ExecutionController.getAllExecutions()
        Job existingJob = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        // Man sollte hier keine Map verwenden. Aber wenn man das tut, sollte man auch prüfen, ob überhaupt status enthalten ist
        Boolean newStatus = body.get("status");
        existingJob.setStatus(newStatus);
        // ##Test ##Advanced: was geht hier eigentlich in der DB-Kommunikation ab: wird nur Job.status updated? Oder alle Felder?
        // Stichwort: JPA managed entity
        Job updatedJob = jobRepository.save(existingJob);
        System.out.println("Editing status of job with Id "+jid+"...");
        return new ResponseEntity<>(updatedJob, HttpStatus.OK);
    }


    // Gleiche Anmerkungen wie oben: Primitive long
    @DeleteMapping("/{jid}")
    // @ApiOperation, @ApiResponses fehlt :'(
    public ResponseEntity<Void> deleteJob(@PathVariable("jid") Long jid) {
        Job job = jobRepository.findById(jid)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jid));
        // Hast du hier mal getestet, ob Spring die delete-Operation sauber kaskadiert, also auch die verbundenen Executions löscht?
        jobRepository.delete(job);
        System.out.println("Deleting job with Id "+jid+"...");
        return ResponseEntity.noContent().build();
    }

    // ##Discussion: Standard Java Package Structure
    // ##Bad: Siehe Anmerkung in JobScheduler: die Implementierung von executeJob() gehört in JobScheduler, NICHT in den Controller
    @PostMapping("/{jid}/execute")
    // @ApiOperation, @ApiResponses fehlt :'(
    // ##Bad: wieso gibst du hier einen executionRequest rein? Die Execution enthält doch nur Ausgabe-Daten!?
    public ResponseEntity<Execution> executeJob(@PathVariable Long jid, @RequestBody Execution executionRequest) {

        Optional<Job> jobOptional = jobRepository.findById(jid);
        // ##Bad: die Fehlerprüfung sollte am Anfang kommen, mit Abbruch. Dann spart man sich die if-Schachtelung:
        if ( !jobOptional.isPresent() ) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Mit dem obigen if braucht man das auskommentierte alles nicht mehr
        //if (jobOptional.isPresent()) {
            Job job = jobOptional.get();

            // Eigentlich ist das kein executionRequest sondern ein executionResult, daher braucht man das gar nicht ;)
            // Ohne executionRequest merkst du dir die startTime hier einfach in einer lokalen Variable
            executionRequest.setStart_time(LocalDateTime.now());

            // Check if empty or null job script, if so -> success
            String command = job.getJob_script();
            // ##Bad: Da wäre es schöner, das normalisiert in der DB zu speichern: also null wenn leer nach trim.
            // Dann spart man sich überall solche Abfragen,
            if (command == null || command.trim().isEmpty()) {
                // ##Bad: alles was hier kommt, brauchst du im Erfolgsfall der tatsächlichen Ausführung unten nochmal.
                // Daher dies in eine Hilfsmethode auslagern:
                // Execution getExecutionSuccess(LocalDateTime startTime, String output);
                Execution execution = new Execution();
                execution.setSuccess(true);
                execution.setExit_code(0);
                execution.setOutput("No job script provided.");
                execution.setStart_time(executionRequest.getStart_time());
                execution.setEnd_time(LocalDateTime.now());
                execution.setJob(job);
                execution.setJobId(job.getJob_id());
                executionRepository.save(execution);
                return new ResponseEntity<>(execution, HttpStatus.OK);
            }

            // ##Top: hier hast du auf das unnötige else verzichtet (das ich anderswo kritisiert hatte)
            // exec job logic
            Process process;
            int exitCode = -1;
            String output = "";
            try {
                // ##Top: DAS IST NIEMALS DEIN ERSTES JAVA PROGRAMM!!!!
                process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                // ##Top: DAS IST NIEMALS DEIN ERSTES JAVA PROGRAMM!!!!
                output = reader.lines().collect(Collectors.joining("\n"));
                // ##Top: DAS IST NIEMALS DEIN ERSTES JAVA PROGRAMM!!!!
                exitCode = process.waitFor();

                // log.trace()...
                System.out.println("Script output (jobId "+jid+"): " + output);
                System.out.println("Exit code (jobId "+jid+"): " + exitCode);

                // Siehe Technische Anmerkungen unten
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining("\n"));
                System.out.println("Error output (jobId "+jid+"): " + errorOutput);

                // ##Test: Technische Anmerkungen und Test: SEHR FORTGESCHRITTENE Themen:
                // 1. Man sollte den errorReader VOR process.waitFor() sethen, also direkt nach reader:
                // BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                // BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                // reader lesen
                // errorReader lesen
                //
                // 2. Ich VERMUTE: wenn der Job sehr viel Error Output erzeugt (mehr als die Buffer-Größe vom Buffered Reader)
                // dann hängt die Ausführung, denn mittels: output = reader.lines().collect(Collectors.joining("\n"));
                // liest du ZUERST ALLEN Output und dann alle Errors. Wenn er die Errors nicht mehr schreiben kann,
                // weil Buffer voll, dann wartet er (unendlich lange) bis du anfängst die errors zu lesen. Unendlich lange,
                // weil du ja erst allen normalen Output liest. Um das Probkem zu lösen, soltest du den Error-Output in den
                // normalen Output umleiten. Dafür gibts beim exec() oder beim Process ne Methode.
                // Vorher aber meine Vermutung testen: mit der vorhandenen Implementierung eine Test-Script aufrufen,
                // das 10000 Error-Zeilen ausgibt...
            } catch (IOException | InterruptedException e) {
                // ##Bad: printStackTrace() ist generell nicht sinnvoll.
                // In diesem Fall handelt es sich um Checked Exceptions, die du fangen musst. Da macht man dann einfach
                // ein rethrow. Das wird dann automatisch gelogged:
                // throw new RuntimeException("JobController.executeJob(): Exception", e);
                e.printStackTrace();
            }

            // Ja, alles richtig:
            // success based on exit code?
            // ##Top: DAS IST NIEMALS DEIN ERSTES JAVA PROGRAMM!!!!
            // JEDER Anfänger und leider auch viele fortgeschrittenen Kollegen hätten hier 5 Zeilen geschrieben:
            // if (exitCode==0) {
            //    success = true;
            // else {
            //    success = false;
            // }
            boolean success = exitCode == 0;

            // ##Bad: s.o. auslagern in Hilfsmethoe: Execution getExecutionSuccess(LocalDateTime startTime, String output);
            Execution execution = new Execution();
            execution.setSuccess(success);
            execution.setExit_code(exitCode);
            execution.setOutput(output);
            execution.setStart_time(executionRequest.getStart_time());
            execution.setEnd_time(LocalDateTime.now());
            execution.setJob(job);
            execution.setJobId(job.getJob_id());
            executionRepository.save(execution);
            return new ResponseEntity<>(execution, HttpStatus.OK);
        //} else {
        //    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        //}
    }
}
