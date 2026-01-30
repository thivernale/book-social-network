package org.thivernale.inventory.info;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtBearerAuth")
@Tag(name = "Info")
public class InfoController {
    private static final Log log = LogFactory.getLog(InfoController.class);
    private final InfoService infoService;
    private final SchemaInfoService schemaInfoService;
    private final UserDetailsService userService;
    private final Executor asyncTaskExecutor;

    @GetMapping("/tables")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(
        description = "Forbidden",
        responseCode = "403",
        content = @Content(
            mediaType = "application/json",
            examples = {@ExampleObject(value = """
                {
                  "status": 403,
                  "title": "Forbidden",
                  "detail": "Access is denied"
                }""")}))
    public ResponseEntity<List<String>> getTables() {
        return ResponseEntity.ok(schemaInfoService.getTables());
    }

    @GetMapping("/process")
    public ResponseEntity<Void> processInfo() {
        try {
            log.info("Starting process in " + Thread.currentThread()
                .getName());
            infoService.step1();
            infoService.step2();
            infoService.step3()
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s))
                .thenApplyAsync(x -> {
                    log.info(x + " with dedicated executor: " + Thread.currentThread()
                        .getName());
                    return x;
                }, asyncTaskExecutor)
                .thenAcceptAsync(x -> log.info(x + " with default FJP: " + Thread.currentThread()
                    .getName()));
            infoService.step4();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("/user-data/{username}")
    @PreAuthorize(value = "isAuthenticated() and principal.username == #username or hasRole('ROLE_ADMIN')")
    public UserDetails getUserDataSelfOrAdmin(@PathVariable String username) {
        return userService.loadUserByUsername(username);
    }

    @GetMapping("/user-data-role/{username}")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public UserDetails getUserDataAdmin(@PathVariable String username) {
        return userService.loadUserByUsername(username);
    }

    @GetMapping("/user-data-any/{username}")
    public UserDetails getUserDataAny(@PathVariable String username) {
        return userService.loadUserByUsername(username);
    }
}
