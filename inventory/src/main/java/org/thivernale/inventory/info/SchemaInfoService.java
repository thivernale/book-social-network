package org.thivernale.inventory.info;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchemaInfoService {
    private final JdbcTemplate jdbcTemplate;

    public @NonNull List<String> getTables() {
        return jdbcTemplate.queryForList(
            "SELECT table_name FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE NOT TABLE_SCHEMA IN ('INFORMATION_SCHEMA', 'SYSTEM_LOBS')", String.class);
    }
}
