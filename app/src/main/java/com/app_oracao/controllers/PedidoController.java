package com.app_oracao.controllers;

import com.app_oracao.dtos.MotivoGeralDescricaoDTO;
import com.app_oracao.dtos.PedidoOracaoDTO;
import com.app_oracao.models.MotivoGeral;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PedidoController {

    @GET("api/motivos")
    Call<List<MotivoGeral>> findAll(@Header("Authorization") String token);

    @GET("api/motivos/{descricao}")
    Call<List<MotivoGeralDescricaoDTO>> findByDescricao(@Path("descricao") String descricao, @Header("Authorization") String token);

    @POST("api/create_pedido")
    Call<Void> createPedido(@Body PedidoOracaoDTO dto, @Header("Authorization") String token);

    @GET("api/pedidos")
    Call<List<PedidoOracaoDTO>> findAllPedidos(@Header("Authorization") String token);

    @GET("api/my_pedidos")
    Call<List<PedidoOracaoDTO>> findPedidosByUsuario(@Header("Authorization") String token);

    @PATCH("api/pedidos/{id}")
    Call<Void> insertUsuariosIntoPedido(@Path("id") Long id, @Header("Authorization") String token);

    @DELETE("api/pedidos/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String token);

}
