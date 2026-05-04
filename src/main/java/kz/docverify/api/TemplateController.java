package kz.docverify.api;

import kz.docverify.domain.RuleTemplate;
import kz.docverify.repository.RuleTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final RuleTemplateRepository ruleTemplateRepository;

    @GetMapping
    public Flux<RuleTemplate> listAll() {
        return Flux.fromIterable(ruleTemplateRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{id}")
    public Mono<RuleTemplate> getById(@PathVariable UUID id) {
        return Mono.fromCallable(() -> ruleTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RuleTemplate> create(@RequestBody RuleTemplate template) {
        return Mono.fromCallable(() -> ruleTemplateRepository.save(template))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
