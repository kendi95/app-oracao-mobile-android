package com.app_oracao.controllers;

import com.app_oracao.dtos.CredencialDTO;
import com.app_oracao.dtos.LoginResponseDTO;
import com.app_oracao.dtos.SenhaDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CredencialController {

    @POST("login")
    Call<LoginResponseDTO> login(@Body CredencialDTO credencialDTO);

    @GET("auth/verify_email/{email}")
    Call<Void> confirmEmail(@Path("email") String email);

    @PATCH("auth/new_password_with_code")
    Call<Void> setNewPassword(@Body SenhaDTO dto);

}
