package com.example.job;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
// Konvention: Tabellen-Namen schreibt man wie den Klassen-Namen groß: Job
@Table(name = "job")
public class Job {

    @Id
    // ##Top: @GeneratedValue
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Einfach nur id nennen, in der DB und in Java
    @Column(name = "job_id")
    // Ist Long (statt long) hier erforderlich wegen @GeneratedValue? Wenn nicht, dann long verwenden
    // ##Discussion: Java primitives vs. corresponding classes
    private Long job_id;

    // ##Top: Java Validation: wo hast du denn das gelernt?? Wie/wo wird das angezeigt, wenn nicht erfüllt?
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Description must be at most 200 characters")
    private String description;

    // ##Bad: Java Konvention: camelCase verwenden: jobScript, startDate, endDate, etc.
    // Gilt dann natürlich auch für alle getter und setter
    // Ich würde das hier auch nicht jobScript nennen, sondern command.
    // Auch NICHT jobCommand, denn es ist ja schon im Job-Objekt
    private String job_script;
    private Boolean status;
    // S.o.: camelCase
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private String cronExpression;


    // Die Lazy-Thematik hast du ja selbst schon erkannt :) Siehe Anmerkungen in Execution.
    @JsonIgnore
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    // Keine Zuweisung = new ArrayList<>(); machen. Sollte null sein, wenn nix da.
    private List<Execution> history = new ArrayList<>();


    // Brauchst du diesen Konstruktor wirklich? Ich glaube, du rufst überall die setter auf, was in diesem Fall auch schöner ist:
    // Lange Parameter-Listen sind beim Aufruf schwer zuzuordnen
    public Job(long job_id, String name, String description, String job_script, boolean status, LocalDateTime start_date,
               LocalDateTime end_date, String cronExpression) {
        this.job_id = job_id;
        this.name = name;
        this.description = description;
        this.job_script = job_script;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.cronExpression = cronExpression;

        // Das brauchst du nicht, weil du es ja bei der Deklaration schon setzt.
        // Du solltest es weder hier noch bei der Deklaration setzen.
        this.history = new ArrayList<>();
    }

    public Job() {

    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public long getJob_id() {
        return job_id;
    }

    public void setJob_id(long id) {
        this.job_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getJob_script() {
        return job_script;
    }

    public void setJob_script(String job_script) {
        this.job_script = job_script;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Entweder die Methode so benennen, dass unmittelbar klar ist, was sie bedeutet <- das ist die beste Option ;)
    // Oder einen JavaDoc-Kommentar dazuschreiben, der erklärt, wozu sie gut ist.
    // Ich vermute, hier geht es um die Frage, ob der Job enabled/disabled ist?
    // => dann wäre isEnabled() ein guter Name ;)
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime startDate) {
        this.start_date = startDate;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDateTime endDate) {
        this.end_date = endDate;
    }

    public List<Execution> getHistory() {
        return history;
    }

    public void setHistory(List<Execution> history) {
        this.history = history;
    }

    @Override
    // ##Top: toString()-Methode vorhanden :)
    public String toString() {
        // ##Top: sehr gutes Ausageformat, verwenden wir auch so. Bist du da selbst draufgekommen, oder hast du das von uns kopiert?
        return "Job{" +
                "jid=" + job_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startDate=" + start_date +
                ", endDate=" + end_date +
                ", history=" + history +
                '}';
    }
}
