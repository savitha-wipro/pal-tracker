package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.MeterRegistry;
//import org.springframework.boot.actuate.metrics.CounterService;
//import org.springframework.boot.actuate.metrics.GaugeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private  MeterRegistry counter;
    private MeterRegistry gauge;
    //private final CounterService counter;
    //private final GaugeService gauge;
    private TimeEntryRepository timeEntriesRepo;

    /*
    public TimeEntryController() {
    }

    public TimeEntryController(TimeEntryRepository timeEntriesRepo) {
        this.timeEntriesRepo = timeEntriesRepo;
    }
 */
    public TimeEntryController(
            TimeEntryRepository timeEntriesRepo,
            MeterRegistry counter,
            MeterRegistry gauge
    ) {
        this.timeEntriesRepo = timeEntriesRepo;
        this.counter = counter;
        this.gauge = gauge;
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry createdTimeEntry = timeEntriesRepo.create(timeEntry);
        //counter.increment("TimeEntry.created");
        counter.counter("TimeEntry.created");
        gauge.gauge("timeEntries.count", timeEntriesRepo.list().size());
        //gauge.submit("timeEntries.count", timeEntriesRepo.list().size());

        return new ResponseEntity<>(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable Long id) {
        TimeEntry timeEntry = timeEntriesRepo.find(id);
        if (timeEntry != null) {
            //counter.increment("TimeEntry.read");
            counter.counter("TimeEntry.read");
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        //counter.increment("TimeEntry.listed");
        counter.counter("TimeEntry.listed");
        return new ResponseEntity<>(timeEntriesRepo.list(), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable Long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry updatedTimeEntry = timeEntriesRepo.update(id, timeEntry);
        if (updatedTimeEntry != null) {
            //counter.increment("TimeEntry.updated");
            counter.counter("TimeEntry.updated");
            return new ResponseEntity<>(updatedTimeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable Long id) {
        timeEntriesRepo.delete(id);
       // counter.increment("TimeEntry.deleted");
        counter.counter("TimeEntry.deleted");
        //gauge.submit("timeEntries.count", timeEntriesRepo.list().size());
        gauge.gauge("timeEntries.count", timeEntriesRepo.list().size());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}