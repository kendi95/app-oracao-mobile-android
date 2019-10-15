package com.app_oracao.models;

import com.app_oracao.database.model.EventoDB;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cidade;
    private String estado;

    private List<EventoDB> eventoDBS = new ArrayList<>();


    public Usuario(Long id, String nome, String email, String senha, String telefone, String cidade, String estado) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cidade = cidade;
        this.estado = estado;
    }

    public Usuario() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<EventoDB> getEventoDBS() {
        return eventoDBS;
    }

    public void setEventoDBS(List<EventoDB> eventoDBS) {
        this.eventoDBS = eventoDBS;
    }
}
