package org.thivernale.booknetwork.book;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.thivernale.booknetwork.user.User;
import org.thivernale.booknetwork.user.UserRepository;

import java.util.Collections;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookServiceIntegrationTest {
    private final User currentUser = User.builder()
        .id(1L)
        .email("test@test.com")
        .build();
    private final Authentication authentication;
    @Autowired
    private BookService underTest;
    @Autowired
    private UserRepository userRepository;

    public BookServiceIntegrationTest() {
        authentication = new UsernamePasswordAuthenticationToken(currentUser, "", Collections.emptyList());
        SecurityContextHolder.getContext()
            .setAuthentication(authentication);
    }

    @BeforeAll
    void beforeAll() {
        userRepository.save(currentUser);
    }

    @Test
    //@WithUserDetails(value = "test@test.com", userDetailsServiceBeanName = "userDetailsServiceImpl")
    public void audit() {

        BookRequest request = new BookRequest(null, "Title", "Author Name", "ISBN", "Synopsis", true);
        Long savedId = underTest.save(request, authentication);

        Map<Number, Book> audit = underTest.getAudit(savedId);
        Assertions.assertEquals(1, audit.size());
    }
}
