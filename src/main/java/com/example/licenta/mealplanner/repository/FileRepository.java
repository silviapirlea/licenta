package com.example.licenta.mealplanner.repository;

import com.example.licenta.mealplanner.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {
    boolean existsByUsername(String username);
    Optional<FileEntity> findByUsername(String username);
}
