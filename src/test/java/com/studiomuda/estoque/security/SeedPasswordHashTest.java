package com.studiomuda.estoque.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeedPasswordHashTest {

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    void hashesBcryptDosUsuariosDeDemonstracaoCorrespondemAsSenhasDocumentadas() {
        assertTrue(encoder.matches("Admin@123",
                "{bcrypt}$2y$10$g0LlknzvHwnbcxNZUqwqNe5Cf7akFs6HZLqHA8jcKf1qqZznkUQGW"));
        assertTrue(encoder.matches("Gerente@123",
                "{bcrypt}$2y$10$hJCAgOl0r.UG62AIDEQ8N.c5mMAr8sOU9fL6Ozm.5auqHYlvXCaD."));
        assertTrue(encoder.matches("Operador@123",
                "{bcrypt}$2y$10$zboD1cTxc2.94QKCP0v9V.We12Guw.2rug5mDF0.SmKT.NmVtyMyi"));
    }
}
