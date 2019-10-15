package com.app_oracao.controllers;

import com.app_oracao.dtos.ConviteDTO;
import com.app_oracao.dtos.DefaultUsuarioDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UsuarioController {

    @POST("api/new_account")
    Call<Void> insert(@Body DefaultUsuarioDTO dto);

    @POST("api/create_convite")
    Call<Void> createConvite(@Body ConviteDTO dto, @Header("Authorization") String token);

    @GET("api/usuario")
    Call<DefaultUsuarioDTO> findByEmail(@Header("Authorization") String token);

    @PUT("api/usuario")
    Call<DefaultUsuarioDTO> update(@Body DefaultUsuarioDTO dto, @Header("Authorization") String token);

}
