package com.example.licenta.mealplanner.controller;

import com.example.licenta.auth.payload.response.MessageResponse;
import com.example.licenta.mealplanner.model.FileEntity;
import com.example.licenta.mealplanner.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping(path = "/{username}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            fileStorageService.store(file, username);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> getFile(@PathVariable String username) {
        FileEntity fileDB = fileStorageService.getFile(username);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filetype=\"" + fileDB.getType() + "\"")
                .body(fileDB.getData());
    }
}
