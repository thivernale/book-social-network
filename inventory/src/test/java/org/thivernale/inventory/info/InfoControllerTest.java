package org.thivernale.inventory.info;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.thivernale.inventory.InventoryApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = InventoryApplication.class)
class InfoControllerTest {
    private final String USER_EMAIL = "user@example.com";
    private final String ADMIN_EMAIL = "admin@example.com";
    @Autowired
    private InfoController infoController;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUserDataSelfOrAdmin() {
        assertThat(infoController.getUserDataSelfOrAdmin(USER_EMAIL))
            .isNotNull();
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = USER_EMAIL)
    void getUserDataSelf() {
        assertThat(infoController.getUserDataSelfOrAdmin(USER_EMAIL))
            .isNotNull();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUserDataAdminSuccess() {
        assertThat(infoController.getUserDataAdmin(USER_EMAIL))
            .isNotNull();
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = USER_EMAIL)
    void getUserDataAdminAccessDenied() {
        assertThatThrownBy(() -> infoController.getUserDataAdmin(USER_EMAIL))
            .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getUserDataAny() {
        assertThat(infoController.getUserDataAny(USER_EMAIL))
            .isNotNull();
    }

    @Test
    @WithAnonymousUser
    void getUserDataAnyUnauthenticatedOk() {
        assertThat(infoController.getUserDataAny(USER_EMAIL))
            .isNotNull();
    }

    @Test
    @WithAnonymousUser
    void getUserDataSelfUnauthenticatedAccessDenied() {
        assertThatThrownBy(() -> infoController.getUserDataSelfOrAdmin(USER_EMAIL))
            .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
