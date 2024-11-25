package org.thivernale.booknetwork.file;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestPropertySource(properties = "application.file.upload.photos-output-path=${java.io.tmpdir}")
@ContextConfiguration(classes = FileStorageService.class)
@ExtendWith(SpringExtension.class)
class FileStorageServiceTest {
    private static final String USERS_UPLOAD_DIR = "users";

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    @Autowired
    private FileStorageService underTest;

    @AfterEach
    void tearDown() {
        Path usersPath = Paths.get(fileUploadPath, USERS_UPLOAD_DIR);
        if (usersPath.toFile()
            .exists()) {
            try {
                FileSystemUtils.deleteRecursively(usersPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void saveFile() {
        assertEquals(System.getProperty("java.io.tmpdir"), fileUploadPath);

        long userId = 1L;
        assertThrows(NullPointerException.class, () -> underTest.saveFile(null, userId));

        MultipartFile file = new MockMultipartFile(
            "file",
            "my text.txt",
            MediaType.TEXT_PLAIN_VALUE,
            ("my text content").getBytes()
        );

        String savedFilePath = underTest.saveFile(file, userId);
        Path targetFolder = Paths.get(fileUploadPath, USERS_UPLOAD_DIR, String.valueOf(userId));
        Path savedFile = Paths.get(savedFilePath);

        assertThat(targetFolder).exists()
            .isDirectoryContaining("regex:.*.txt");

        assertThat(savedFile).exists()
            .hasExtension("txt")
            .satisfies(new Condition<>(path -> path.getFileName()
                .toString()
                .matches("^\\d+\\.txt$"), ""))
            .hasBinaryContent(("my text content").getBytes());
    }
}
