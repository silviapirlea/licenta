package com.example.licenta.mealplanner.service;

import com.example.licenta.email.service.EmailNotificationService;
import com.example.licenta.mealplanner.model.FileEntity;
import com.example.licenta.mealplanner.repository.FileRepository;
import com.example.licenta.userboard.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    private final FileRepository fileRepository;
    private final UserProfileService userProfileService;
    private final EmailNotificationService emailNotificationService;

    public FileEntity store(MultipartFile file, String username) throws IOException {
        if(fileRepository.existsByUsername(username)) {
            var existingFile = fileRepository.findByUsername(username).get();
            fileRepository.delete(existingFile);
        }

        userProfileService.updateMealPlanInProfile(username, true);
        FileEntity FileDB = FileEntity.builder()
                .username(username)
                .type(file.getContentType())
                .data(file.getBytes())
                .build();
        emailNotificationService.sendMealPlanUploadedEmail(username);
        return fileRepository.save(FileDB);
    }

    @Transactional
    public FileEntity getFile(String username) {
        return fileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
