package bsep.hospital.api;

import bsep.hospital.dto.FilterParamsDTO;
import bsep.hospital.dto.LogDTO;
import bsep.hospital.logging.LogModel;
import bsep.hospital.logging.LogSource;
import bsep.hospital.logging.LogType;
import bsep.hospital.model.FilterParams;
import bsep.hospital.model.LogConfig;
import bsep.hospital.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "https://localhost:4205")
@RestController
@RequestMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
public class LogController {

    @Autowired
    LogService logService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Logger logger = LogManager.getLogger(LogController.class);

    // @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/get-logs", method = RequestMethod.GET)
    public ResponseEntity<?> getLogs() {
        logger.info("Attempting to get all logs.");
        List<LogModel> logs = logService.findAll();
        List<LogDTO> logDTOS = new ArrayList<LogDTO>();
        for(LogModel log: logs){
            logDTOS.add(new LogDTO(log.getLevel(), log.getMessage(), log.getLogTime().format(formatter), log.getLogSource(), log.getIp(), log.isAlarm(), log.getAlarmDescription()));
        }
        logger.info("Successfully retrieved all logs.");
        return new ResponseEntity<>(logDTOS, HttpStatus.OK);
    }

    // @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/get-logs/alarm", method = RequestMethod.GET)
    public ResponseEntity<?> getAlarmedLogs() {
        logger.info("Attempting to get all alarm logs.");
        List<LogModel> logs = logService.findAllByAlarm();
        List<LogDTO> logDTOS = new ArrayList<LogDTO>();
        for(LogModel log: logs){
            logDTOS.add(new LogDTO(log.getLevel(), log.getMessage(), log.getLogTime().format(formatter), log.getLogSource(), log.getIp(), log.isAlarm(), log.getAlarmDescription()));
        }
        logger.info("Successfully retrieved all alarm logs.");
        return new ResponseEntity<>(logDTOS, HttpStatus.OK);
    }

    // @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/filter-logs", method = RequestMethod.POST)
    public ResponseEntity<?> filterLogs(@RequestBody @Valid FilterParamsDTO filterParamsDTO) {
        LocalDateTime ldcFrom = null;
        LocalDateTime ldcTo = null;

        if(filterParamsDTO.getDateFrom() != null) {
            ldcFrom = filterParamsDTO.getDateFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        if(filterParamsDTO.getDateTo() != null) {
            ldcTo = filterParamsDTO.getDateTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        FilterParams filterParams = new FilterParams(filterParamsDTO.getLogType(), filterParamsDTO.getLogSource(), ldcFrom, ldcTo, "");
        logger.info("Attempting to filter logs.");
        List<LogModel> logs = logService.filterLogs(filterParams);
        List<LogDTO> logDTOS = new ArrayList<LogDTO>();
        for(LogModel log: logs){
            logDTOS.add(new LogDTO(log.getLevel(), log.getMessage(), log.getLogTime().format(formatter), log.getLogSource(), log.getIp(), log.isAlarm(), log.getAlarmDescription()));
        }
        logger.info("Successfully filtered logs.");
        return new ResponseEntity<>(logDTOS, HttpStatus.OK);
    }
}
