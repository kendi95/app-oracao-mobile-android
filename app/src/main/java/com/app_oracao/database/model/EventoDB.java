package com.app_oracao.database.model;


import java.io.Serializable;

public class EventoDB implements Serializable {

    private Integer id;
    private String titulo;
    private Long data;
    private Long horas_inicio;
    private Long horas_fim;

    private Integer usuarioId;

    public EventoDB(){}

    public EventoDB(Integer id, String titulo, Long data, Long horas_inicio){
        this.id = id;
        this.titulo = titulo;
        this.data = data;
        this.horas_inicio = horas_inicio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public Long getHoras_inicio() {
        return horas_inicio;
    }

    public void setHoras_inicio(Long horas_inicio) {
        this.horas_inicio = horas_inicio;
    }

    public Long getHoras_fim() {
        return horas_fim;
    }

    public void setHoras_fim(Long horas_fim) {
        this.horas_fim = horas_fim;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}
