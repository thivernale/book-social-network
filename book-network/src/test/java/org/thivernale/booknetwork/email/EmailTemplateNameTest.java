package org.thivernale.booknetwork.email;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailTemplateNameTest {

    @Test
    public void testEnum() {
        EmailTemplateName activateAccount = EmailTemplateName.ACTIVATE_ACCOUNT;
        assertEquals("ACTIVATE_ACCOUNT", activateAccount.name());
        assertEquals("activate_account", activateAccount.getName());
    }
}
