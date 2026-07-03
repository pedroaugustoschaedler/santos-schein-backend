package br.com.santoseschein.reserva.controller;

import br.com.santoseschein.reserva.dto.LoginRequest;
import br.com.santoseschein.reserva.dto.LoginResponse;
import br.com.santoseschein.reserva.dto.RegisterRequest;
import br.com.santoseschein.reserva.model.User;
import br.com.santoseschein.reserva.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        User user = new User(request.getName(), request.getEmail(), request.getPassword(), "CLIENT");
        userRepository.save(user);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(u -> u.getPasswordHash().equals(request.getPassword()))
                .map(u -> ResponseEntity.ok(new LoginResponse(u.getId(), u.getName(), u.getEmail(), u.getRole())))
                .orElse(ResponseEntity.status(401).body(null));
    }
}
