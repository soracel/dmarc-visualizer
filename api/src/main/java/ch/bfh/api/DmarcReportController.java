package ch.bfh.api;

import ch.bfh.api.entities.DmarcReport;
import ch.bfh.api.services.DmarcReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class DmarcReportController {
    @Autowired
    private DmarcReportService service;

    @GetMapping
    public ResponseEntity<List<DmarcReport>> getAllReports() {
        try {
            return ResponseEntity.ok(service.getAllReports());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}