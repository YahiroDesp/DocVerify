package kz.docverify.repository;

import kz.docverify.domain.RuleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RuleTemplateRepository extends JpaRepository<RuleTemplate, UUID> {
    Optional<RuleTemplate> findByStandard(String standard);
}
