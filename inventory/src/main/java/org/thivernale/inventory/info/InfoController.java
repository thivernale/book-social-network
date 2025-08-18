package org.thivernale.inventory.info;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtBearerAuth")
@Tag(name = "Info")
public class InfoController {
    private final JdbcTemplate jdbcTemplate;
    private final InfoService infoService;

    @GetMapping("/tables")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @ApiResponses({
        @ApiResponse(description = "Success", responseCode = "200"),
        @ApiResponse(
            description = "Forbidden",
            responseCode = "403",
            content = @Content(
                mediaType = "application/json",
                examples = {@ExampleObject(value = "{\n" +
                    "  \"status\": 403,\n" +
                    "  \"title\": \"Forbidden\",\n" +
                    "  \"detail\": \"Access is denied\"\n" +
                    "}")}))
    })
    public ResponseEntity<List<String>> getTables() {
        var tables = jdbcTemplate.queryForList(
            "SELECT table_name FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE NOT TABLE_SCHEMA IN ('INFORMATION_SCHEMA', 'SYSTEM_LOBS')", String.class);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/process")
    public ResponseEntity<Void> processInfo() {
        try {
            infoService.step1();
            infoService.step2();
            infoService.step3();
            infoService.step4();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.noContent()
            .build();
    }
}
