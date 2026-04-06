package stokapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stokapp.dto.AuthResponse;
import stokapp.dto.LoginRequest;
import stokapp.dto.RegisterRequest;
import stokapp.entity.User;
import stokapp.repository.UserRepository;
import stokapp.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kayıtlı");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername());

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                "Kayıt başarılı"
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                user.getUsername(),
                "Giriş başarılı"
        );
    }
}