package com.app_oracao.dtos;

import com.app_oracao.models.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoOracaoDTO implements Serializable {

    private Long id;
    private String motivoGeral;
    private String motivoPessoal;
    private String motivoDescricao;

    private String isAnonimo;

    private String nome_autor;

    private Date data_pedido;

    private List<Usuario> usuarios = new ArrayList<>();

    public PedidoOracaoDTO(String motivoGeral, String motivoPessoal, String motivoDescricao,
                           String isAnonimo, String nome_autor, Date data_pedido) {
        this.motivoGeral = motivoGeral;
        this.motivoPessoal = motivoPessoal;
        this.motivoDescricao = motivoDescricao;
        this.isAnonimo = isAnonimo;
        this.nome_autor = nome_autor;
        this.data_pedido = data_pedido;
    }

    public PedidoOracaoDTO(){}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivoGeral() {
        return motivoGeral;
    }

    public void setMotivoGeral(String motivoGeral) {
        this.motivoGeral = motivoGeral;
    }

    public String getMotivoPessoal() {
        return motivoPessoal;
    }

    public void setMotivoPessoal(String motivoPessoal) {
        this.motivoPessoal = motivoPessoal;
    }

    public String getMotivoDescricao() {
        return motivoDescricao;
    }

    public void setMotivoDescricao(String motivoDescricao) {
        this.motivoDescricao = motivoDescricao;
    }

    public String getIsAnonimo() {
        return isAnonimo;
    }

    public void setIsAnonimo(String isAnonimo) {
        this.isAnonimo = isAnonimo;
    }

    public String getNome_autor() {
        return nome_autor;
    }

    public void setNome_autor(String nome_autor) {
        this.nome_autor = nome_autor;
    }

    public Date getData_pedido() {
        return data_pedido;
    }

    public void setData_pedido(Date data_pedido) {
        this.data_pedido = data_pedido;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

}
