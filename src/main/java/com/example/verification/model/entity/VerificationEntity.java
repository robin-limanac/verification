package com.example.verification.model.entity;

import com.example.verification.model.enums.VerificationSource;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "verifications")
public class VerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "verification_id", nullable = false, length = 64)
    private String verificationId;

    @Column(name = "query_text", nullable = false, length = 512)
    private String queryText;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 16)
    private VerificationSource source;

    @Lob
    @Column(name = "result_json", nullable = false)
    private String resultJson;

    public String getVerificationId() { return verificationId; }
    public void setVerificationId(String verificationId) { this.verificationId = verificationId; }

    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public VerificationSource getSource() { return source; }
    public void setSource(VerificationSource source) { this.source = source; }

    public String getResultJson() { return resultJson; }
    public void setResultJson(String resultJson) { this.resultJson = resultJson; }
}
