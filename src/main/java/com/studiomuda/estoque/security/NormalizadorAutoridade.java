package com.studiomuda.estoque.security;

import java.text.Normalizer;

public final class NormalizadorAutoridade {
    private NormalizadorAutoridade() {
    }

    public static String paraRole(String nomePerfil) {
        if (nomePerfil == null || nomePerfil.trim().isEmpty()) {
            return "ROLE_SEM_PERFIL";
        }

        String semAcento = Normalizer.normalize(nomePerfil, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String normalizado = semAcento
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .toUpperCase();
        if (normalizado.isEmpty()) {
            normalizado = "SEM_PERFIL";
        }
        return "ROLE_" + normalizado;
    }
}
