package org.thivernale.inventory.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.thivernale.inventory.info.SchemaInfoService;
import org.thivernale.inventory.security.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SchemaInfoService schemaInfoService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
        @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password()));

            SecurityContextHolder.getContext()
                .setAuthentication(authentication);

            return ResponseEntity.ok(new AuthenticationResponse(
                jwtUtils.generateToken(authentication), jwtUtils.calculateExpiresAt(LocalDateTime.now())));
        } catch (DataAccessException | AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<List<String>> getTables() {
        return ResponseEntity.ok(schemaInfoService.getTables());
    }
}
