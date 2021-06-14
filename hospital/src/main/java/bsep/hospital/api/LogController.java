package bsep.hospital.api;

import bsep.hospital.logging.LogModel;
import bsep.hospital.model.FilterParams;
import bsep.hospital.model.LogConfig;
import bsep.hospital.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://localhost:4205")
@RestController
@RequestMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
public class LogController {

    @Autowired
    LogService logService;

    // pretrage od do tip regex

    @RequestMapping(value = "/get-logs", method = RequestMethod.GET)
    public ResponseEntity<?> getLogs() {
        List<LogModel> logs = logService.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @RequestMapping(value = "/filter-logs", method = RequestMethod.POST)
    public ResponseEntity<?> filterLogs(@RequestBody FilterParams filterParams) {
        List<LogModel> logs = logService.filterLogs(filterParams);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}