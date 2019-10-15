package com.app_oracao.dtos;


import java.io.Serializable;

public class CredencialDTO implements Serializable {

    private String email;
    private String senha;

    public CredencialDTO(){}

    public CredencialDTO(String email, String senha){
        this.email = email;
        this.senha = senha;
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

}
