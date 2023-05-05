package com.example.job;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.annotations.*;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;


// Top: JobController, ExecutionController auf /job /execution :))
@RestController
@RequestMapping("/execution")
public class ExecutionController {

    // Analog JobController: toll, dass du final verwendest, hier fände ich aber @Autowired besser
    private final ExecutionRepository executionRepository;
    private final JobRepository jobRepository;

    // Braucht man nicht bei @Autowired, s.o.
    public ExecutionController(ExecutionRepository executionRepository, JobRepository jobRepository) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
    }

    // Super :)
    @Transactional
    @GetMapping
    @ApiOperation(value = "Get all executions", notes = "Retrieves all executions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved executions"),
            @ApiResponse(code = 404, message = "Executions not found")
    })
    public ResponseEntity<List<Execution>> getAllExecutions() {
        List<Execution> executions = executionRepository.findAll();

        // Das ist doch kein Fehler?! Ist doch normal, wenn ein Job noch nicht ausgeführt wurde?
        // Exceptions sind für Fehlerfälle => das passt her m.E. nicht
        if (executions.isEmpty()) {
            // In Log-Messages (gilt auch für Exceptions, denn die werden gelogged) IMMER den Kontext reinschreiben,
            // in dem der Fehler aufgetreten ist. Wenn da nur "No executions found" kannst auch du das in 3 Wochen
            // nicht mehr zuordnen, fremde Menschen, die das Programm betreiben können gar nix damit anfangen.
            // Daher: throw new ExecutionNotFoundException("ExecutionController.getAllExecutions(): no executions found");
            // (Und ja: wenn der Stacktrace gelogged wird, würde man das dort auch sehen, aber es gibt auch Kontexte
            // in denen nur die Message angezeigt wird => daher immer reinschreiben.)
            // Beim Logging (also nicht hier, siehe andere Stellen): dort wird der Klassenname automatisch gelogged
            // (wenn man das Logging richtig konfiguriert hat), der Methodenname jedoch nicht. Beim Logging würde man
            // daher schreiben: log.info("getAllExecutions(): no executions found");
            // Weiterer Punkt, GENERELLE ANMERKUNG:
            // Selbstdefinierte Exception Klassen (ExecutionNotFoundException) macht man nur, wenn man sie braucht.
            // Brauchen tut man sie, wenn man sie spezifisch fangen und verarbeiten will. Dann ist es typischerweise
            // eine Checked Exception (abgeleitet von Exception, nicht von RuntimeException). Das willst du hier aber
            // nicht. Daher stattdessen: throw new RuntimeException("ExecutionController.getAllExecutions(): no executions found");
            // ABER:
            // Habe gesehen, dass die du die ExecutionNotFoundException mit @ResponseStatus(HttpStatus.NOT_FOUND)
            // annotiert hast. Dann scheint Spring das in diesem Fall zurückzugeben. Funktioniert das so, hast du das
            // getestet?
            // Wenns tut, ist das naturlich eine von dir sehr gut adaptierte Standard-Vorgehensweise von Spring :)
            // Personlich finde ich es trotzdem nicht schön:
            // - du kannst hier statt throw einfach return NOT_FOUND machen (kenne die Notation von Spring dafür nicht,
            //   wird aber gehen)
            // - das ist dann für den Leser übersichtlicher, weil er direkt sieht was hier passiert (sonst muss er sich
            //   erstmal die Deklaration der Exception anschauen um zu sehen, dass die das macht)
            // - und es ist für dich in der Entwicklung einfacher, weil du dir die Deklaration dieser Exceptions komplett
            //   sparen kannst
            throw new ExecutionNotFoundException("No executions found");
        }

        // Verstehe ich nicht: Wieso kopierst du die vorhandene Execution-List in eine neue Liste?
        // Du scheinst das zu tun um in der Response die Liste auszugeben. Das einzige, das mir da spezifisch auffällt
        // ist: executionDTO.setJobId(execution.getJob().getJob_id());
        // Aber das kann doch Execution.toString() selbst machen?!
        // Also ich finde das Kopieren der Listen hier doof.
        List<Execution> executionDTOs = new ArrayList<>();
        for (Execution execution : executions) {
            Execution executionDTO = new Execution();
            executionDTO.setSuccess(execution.getSuccess());
            executionDTO.setExit_code(execution.getExit_code());
            executionDTO.setOutput(execution.getOutput());
            executionDTO.setJobId(execution.getJob().getJob_id()); // <-- include jid attribute
            executionDTOs.add(executionDTO);
        }
        // log.trace mit Kontext...
        System.out.println("Getting all executions...");
        return ResponseEntity.ok(executionDTOs);
    }

    // Ja, das ist gut so. Damit man es besser versteht, dann auch gleich die 404-Respnse auskommentieren ;)
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
            // Sehr schön :)
            return ResponseEntity.ok(Collections.emptyList());
        }
        //System.out.println("Getting executions of job with Id "+jid+"...");
        // Ich vermute Spring gibt hier (und auch oben bei getAllExecutions()) nur executions.toString() aus,
        // also keine JSON-Liste der Executions. Dann kann aber ein Aufrufer doch gar nix mit der Response anfangen
        // oder muss sie kompliziert parsen.
        // Also ich finde, man sollte die Ergebnisliste als JSON ausgeben.
        return ResponseEntity.ok(executions);
    }

    // Sehr schön, dass du @ApiOperation Kommentare reinschreibst
    @DeleteMapping("/{eid}")
    @ApiOperation(value = "Delete execution by ID", notes = "Deletes an execution with the given ID")
    @ApiResponses(value = {
            // Sehr schön :)
            @ApiResponse(code = 204, message = "Execution deleted successfully"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    public ResponseEntity<Void> deleteExecution(@PathVariable("eid") Long eid) {
        // Du hast oben ja schon als @ApiResponse 404 definiert. Dann solltest du das her auch ausgeben, wenn die
        // Execution nicht gefunden wird => also KEINE Exception.
        Execution execution = executionRepository.findById(eid)
                .orElseThrow(() -> new ExecutionNotFoundException("Execution not found with id: " + eid));
        executionRepository.delete(execution);
        // log.trace...
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

        // Analog deleteExecution(): 404 statt Exception.
        if (executions.isEmpty()) {
            throw new ExecutionNotFoundException("No executions found with jobId: " + jid);
        }

        executionRepository.deleteAll(executions);
        // log.trace...
        System.out.println("Deleting executions with jobId " + jid + "...");
        return ResponseEntity.noContent().build();
    }


}

