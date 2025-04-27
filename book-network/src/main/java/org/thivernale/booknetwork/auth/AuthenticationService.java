package org.thivernale.booknetwork.auth;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thivernale.booknetwork.email.EmailService;
import org.thivernale.booknetwork.email.EmailTemplateName;
import org.thivernale.booknetwork.role.Role;
import org.thivernale.booknetwork.role.RoleRepository;
import org.thivernale.booknetwork.security.JwtService;
import org.thivernale.booknetwork.user.Token;
import org.thivernale.booknetwork.user.TokenRepository;
import org.thivernale.booknetwork.user.User;
import org.thivernale.booknetwork.user.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
class AuthenticationService {

    private static final int ACTIVATION_CODE_LENGTH = 6;
    private static final int ACTIVATION_CODE_VALIDITY = 15;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public void register(RegistrationRequest registrationRequest) throws MessagingException {
        // register:
        // 1. assign default role USER to user
        Role role = roleRepository.findByName("USER")
            .orElseThrow(() -> new IllegalStateException("Role USER was not found"));
        // 2. create and save user
        User user = User.builder()
            .firstname(registrationRequest.getFirstname())
            .lastname(registrationRequest.getLastname())
            .email(registrationRequest.getEmail())
            .password(passwordEncoder.encode(registrationRequest.getPassword()))
            .enabled(false)
            .accountLocked(false)
            .roles(List.of(role))
            .build();
        userRepository.save(user);
        // 3. send activation email
        sendActivationEmail(user);
    }

    private void sendActivationEmail(User user) throws MessagingException {
        emailService.sendEmail(
            user.getEmail(),
            "Account activation",
            EmailTemplateName.ACTIVATE_ACCOUNT,
            Map.of(
                "username", user.getFullName(),
                "activationCode", generateAndSaveActivationToken(user),
                "activationUrl", activationUrl
            )
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String activationCode = generateActivationCode(ACTIVATION_CODE_LENGTH);
        var token = Token.builder()
            .token(activationCode)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now()
                .plusMinutes(ACTIVATION_CODE_VALIDITY))
            .user(user)
            .build();
        tokenRepository.save(token);
        return activationCode;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        // make sure the generated value is cryptographically secure
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        User user = (User) authentication.getPrincipal();
        Map<String, Object> claims = Map.of("fullName", user.getFullName(), "id", user.getId());

        return AuthenticationResponse.builder()
            .token(jwtService.generateToken(claims, user))
            .build();
    }

    @Transactional(dontRollbackOn = {AuthenticationException.class})
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new NoSuchElementException("Invalid token"));

        if (LocalDateTime.now()
            .isAfter(savedToken.getExpiresAt())) {
            sendActivationEmail(savedToken.getUser());
            throw new CredentialsExpiredException(
                "Activation token has expired. A new token has been sent to the user."
            );
        }
        // TODO do not let user generate multiple tokens over and over
        if (savedToken.getValidatedAt() != null) {
            return;
        }

        var user = userRepository.findById(savedToken.getUser()
                .getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
