package kz.docverify.service;

import kz.docverify.domain.User;
import kz.docverify.dto.AuthResponse;
import kz.docverify.dto.LoginRequest;
import kz.docverify.dto.RegisterRequest;
import kz.docverify.repository.UserRepository;
import kz.docverify.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(), jwtUtil.generate(user));
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role() != null ? request.role() : "AUTHOR");
        userRepository.save(user);
        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(), jwtUtil.generate(user));
    }
}
