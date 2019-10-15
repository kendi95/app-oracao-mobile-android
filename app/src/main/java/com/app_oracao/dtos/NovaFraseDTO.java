package com.app_oracao.dtos;

import java.io.Serializable;
import java.util.Date;

public class NovaFraseDTO implements Serializable {

    private String frase;
    private String data;

    public NovaFraseDTO(){}

    public NovaFraseDTO(String frase, String data){
        this.frase = frase;
        this.data = data;
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
