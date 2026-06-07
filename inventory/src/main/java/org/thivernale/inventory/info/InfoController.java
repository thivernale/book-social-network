package org.thivernale.inventory.info;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);
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
            log.info("Starting process in {}", Thread.currentThread()
                .getName());
            infoService.step1();
            infoService.step2();
            infoService.step3()
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s))
                .thenApplyAsync(x -> {
                    log.info("{} with dedicated executor: {}", x, Thread.currentThread()
                        .getName());
                    return x;
                }, asyncTaskExecutor)
                .thenAcceptAsync(x -> log.info("{} with default FJP: {}", x, Thread.currentThread()
                    .getName()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("/process56")
    public ResponseEntity<String> process56() throws InterruptedException {
        CompletableFuture<Long> step5Completion = infoService.step5();
        CompletableFuture<Double> step6Completion = infoService.step6();
/*
        // if we don't wait for both to complete, we might exit after the fast one completes and the slow one is still running (it will still complete)
        String randomResult = Math.random() < 0.5 ? "Timestamp: " + step5Completion.join() : "Gaussian: " + step6Completion.join();
        log.info("Random result: {}", randomResult);
        return ResponseEntity.ok(randomResult);
*/
        // wait for both to complete and then return the result
        return CompletableFuture.allOf(step5Completion, step6Completion)
            .thenApply(v -> {
                String randomResult = Math.random() < 0.5 ? "Timestamp: " + step5Completion.join() : "Gaussian: " + step6Completion.join();
                return ResponseEntity.ok(randomResult);
            })
            .join();
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
