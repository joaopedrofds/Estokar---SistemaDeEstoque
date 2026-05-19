package com.studiomuda.estoque.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UsuarioAutenticado implements UserDetails {
    private final int id;
    private final String username;
    private final String nome;
    private final String password;
    private final boolean ativo;
    private final List<Integer> perfilIds;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioAutenticado(int id,
                              String username,
                              String nome,
                              String password,
                              boolean ativo,
                              List<Integer> perfilIds,
                              Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.password = password;
        this.ativo = ativo;
        this.perfilIds = Collections.unmodifiableList(new ArrayList<>(perfilIds));
        this.authorities = authorities;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<Integer> getPerfilIds() {
        return perfilIds;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
