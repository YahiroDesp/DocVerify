package kz.docverify.repository;

import kz.docverify.domain.ValidationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ValidationReport, UUID> {
    Optional<ValidationReport> findByDocumentId(UUID documentId);
}
