package com.app_oracao.dtos;

import java.io.Serializable;

public class MotivoGeralDescricaoDTO implements Serializable {

    private Long id;
    private String descricao;

    public MotivoGeralDescricaoDTO(){}

    public MotivoGeralDescricaoDTO(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
