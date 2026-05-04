package kz.docverify.api;

import jakarta.validation.Valid;
import kz.docverify.dto.AuthResponse;
import kz.docverify.dto.LoginRequest;
import kz.docverify.dto.RegisterRequest;
import kz.docverify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return Mono.fromCallable(() -> userService.register(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return Mono.fromCallable(() -> userService.login(request))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
