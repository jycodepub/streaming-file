package com.jyeh.streaming.streaming.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @GetMapping("/{remoteDir}/{filename}")
    public ResponseEntity<?> streamFile(@PathVariable String remoteDir, @PathVariable String filename) {
        try {
            streamingService.remoteUnzip(remoteDir + "/" + filename);
            return ResponseEntity.ok("Streaming file " + filename + " to ECS");
        } catch (Exception e) {
            log.error("Failed to stream file to ECS", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
