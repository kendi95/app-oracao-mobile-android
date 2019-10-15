package com.app_oracao.dtos;

import com.app_oracao.models.Usuario;

import java.io.Serializable;

public class DefaultUsuarioDTO implements Serializable {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cidade;
    private String estado;
    private String conviteEncrypt;

    public DefaultUsuarioDTO(){}

    public DefaultUsuarioDTO(Usuario usuario){
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.telefone = usuario.getTelefone();
        this.cidade = usuario.getCidade();
        this.estado = usuario.getEstado();
    }

    public DefaultUsuarioDTO(Long id, String nome, String email, String senha, String telefone, String cidade, String estado,
                             String conviteEncrypt){
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cidade = cidade;
        this.estado = estado;
        this.conviteEncrypt = conviteEncrypt;
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

    public String getConviteEncrypt() {
        return conviteEncrypt;
    }

    public void setConviteEncrypt(String conviteEncrypt) {
        this.conviteEncrypt = conviteEncrypt;
    }
}
