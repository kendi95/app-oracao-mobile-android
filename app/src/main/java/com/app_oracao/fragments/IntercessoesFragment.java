package com.app_oracao.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.app_oracao.R;
import com.app_oracao.activities.IntercessaoActivity;
import com.app_oracao.activities.LoginActivity;
import com.app_oracao.adapters.IntercessaoAdapter;
import com.app_oracao.controllers.PedidoController;
import com.app_oracao.dtos.PedidoOracaoDTO;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.utils.BaseURL;
import com.app_oracao.utils.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntercessoesFragment extends Fragment {

    private Retrofit retrofit;
    private PedidoController controller;
    private RecyclerView recyclerView_intercessoes;
    private ProgressBar progressBar_intercessoes;
    private List<PedidoOracaoDTO> dtos = new ArrayList<>();
    private String tipo, token, email, nome;

    public IntercessoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intercessoes, container, false);

        recyclerView_intercessoes = view.findViewById(R.id.id_recyclerView_intercessoes);
        progressBar_intercessoes = view.findViewById(R.id.id_progressBar_intercessoes);

        progressBar_intercessoes.setVisibility(View.INVISIBLE);

        tipo = getArguments().get("tipo").toString();
        token = getArguments().get("token").toString();
        email = getArguments().get("email").toString();
        nome = getArguments().get("nome").toString();

        initializeRetrofit();
        getPedidos();

        return view;
    }

    private void initializeRetrofit(){
        retrofit = new Retrofit.Builder()
            .baseUrl(new BaseURL().getBASE_URL())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    private void getPedidos(){
        progressBar_intercessoes.setVisibility(View.VISIBLE);
        controller = retrofit.create(PedidoController.class);
        Call<List<PedidoOracaoDTO>> call = controller.findAllPedidos(token);

        call.enqueue(new Callback<List<PedidoOracaoDTO>>() {
            @Override
            public void onResponse(Call<List<PedidoOracaoDTO>> call, Response<List<PedidoOracaoDTO>> response) {
                if(response.code() == 200){
                    progressBar_intercessoes.setVisibility(View.INVISIBLE);
                    dtos.addAll(response.body());

                    //config adapter
                    IntercessaoAdapter adapter = new IntercessaoAdapter(dtos, email);

                    //config recyclerView
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
                    recyclerView_intercessoes.setLayoutManager(manager);
                    recyclerView_intercessoes.setHasFixedSize(true);
                    recyclerView_intercessoes.setAdapter(adapter);

                    eventTouch(recyclerView_intercessoes);
                } else {
                    progressBar_intercessoes.setVisibility(View.INVISIBLE);
                    try{
                        Gson gson = new GsonBuilder().create();
                        StandardError error = new StandardError();
                        error = gson.fromJson(response.errorBody().string(), StandardError.class);
                        showErrorAlertDialog("Erro: "+response.code(),
                                "Não autorizado, por favor faça login novamente.", R.drawable.ic_error_red_24dp);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PedidoOracaoDTO>> call, Throwable t) {
                progressBar_intercessoes.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void eventTouch(RecyclerView view){
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), view,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                PedidoOracaoDTO dto = dtos.get(position);
                                if(dto.getMotivoGeral() == null){
                                    startActivity(new Intent(getActivity(), IntercessaoActivity.class)
                                            .putExtra("motivo", dto.getMotivoPessoal())
                                            .putExtra("descricao", dto.getMotivoDescricao())
                                            .putExtra("autor", dto.getNome_autor())
                                            .putExtra("data", dto.getData_pedido())
                                            .putExtra("id", dto.getId())
                                            .putExtra("nome", nome)
                                            .putExtra("email", email)
                                            .putExtra("token", token)
                                            .putExtra("tipo", tipo));
                                    getActivity().finish();
                                    Log.i("DTO", dto.getId().toString());
                                } else {
                                    startActivity(new Intent(getActivity(), IntercessaoActivity.class)
                                            .putExtra("motivo", dto.getMotivoGeral())
                                            .putExtra("descricao", dto.getMotivoDescricao())
                                            .putExtra("autor", dto.getNome_autor())
                                            .putExtra("data", dto.getData_pedido())
                                            .putExtra("id", dto.getId())
                                            .putExtra("nome", nome)
                                            .putExtra("email", email)
                                            .putExtra("token", token)
                                            .putExtra("tipo", tipo));
                                    getActivity().finish();
                                    Log.i("DTO", dto.getId().toString());
                                }


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));
    }

    private void showErrorAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
        dialog.create();
        dialog.show();
    }
}
