package com.example.verification.repo;

import com.example.verification.model.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRepository extends JpaRepository<VerificationEntity, String> {
    boolean existsByVerificationId(String verificationId);

    List<VerificationEntity> findAllByVerificationId(String verificationId);
}
