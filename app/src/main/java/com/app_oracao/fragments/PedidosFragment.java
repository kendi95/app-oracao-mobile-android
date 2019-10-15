package com.app_oracao.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app_oracao.R;
import com.app_oracao.activities.AppActivity;
import com.app_oracao.activities.LoginActivity;
import com.app_oracao.activities.NovoPedidoActivity;
import com.app_oracao.adapters.PedidosAdapter;
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
public class PedidosFragment extends Fragment {

    private String tipo, token, email, nome;
    private FloatingActionButton fab;
    private Retrofit retrofit;
    private PedidoController controller;
    private RecyclerView id_recyclerView_pedidos;
    private ProgressBar id_progressBar_pedidos;
    private List<PedidoOracaoDTO> dtos = new ArrayList<>();

    public PedidosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        tipo = getArguments().get("tipo").toString();
        token = getArguments().get("token").toString();
        email = getArguments().get("email").toString();
        nome = getArguments().get("nome").toString();

        id_recyclerView_pedidos = view.findViewById(R.id.id_recyclerView_pedidos);
        id_progressBar_pedidos = view.findViewById(R.id.id_progressBar_pedidos);
        id_progressBar_pedidos.setVisibility(View.INVISIBLE);

        showFloatingActionButton(view);
        getValuesOfIntent();

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getMyPedidos();

        eventTouch();

        toAnotherFragment();

        return view;
    }

    private void showFloatingActionButton(View view){
        fab = view.findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NovoPedidoActivity.class)
                        .putExtra("tipo", tipo)
                        .putExtra("token", token)
                        .putExtra("nome", nome)
                        .putExtra("email", email));
                getActivity().finish();
            }
        });
    }

    private void getValuesOfIntent(){
        tipo = getActivity().getIntent().getStringExtra("tipo");
        token = getActivity().getIntent().getStringExtra("token");
    }


    private void getMyPedidos(){
        id_progressBar_pedidos.setVisibility(View.VISIBLE);
        controller = retrofit.create(PedidoController.class);
        Call<List<PedidoOracaoDTO>> call = controller.findPedidosByUsuario(token);

        call.enqueue(new Callback<List<PedidoOracaoDTO>>() {
            @Override
            public void onResponse(Call<List<PedidoOracaoDTO>> call, Response<List<PedidoOracaoDTO>> response) {
                if(response.code() == 200){
                    id_progressBar_pedidos.setVisibility(View.INVISIBLE);
                    dtos.addAll(response.body());

                    //config adapter
                    PedidosAdapter adapter = new PedidosAdapter(dtos);

                    //config recyclerView
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
                    id_recyclerView_pedidos.setLayoutManager(manager);
                    id_recyclerView_pedidos.setHasFixedSize(true);
                    id_recyclerView_pedidos.setAdapter(adapter);
                } else {
                    id_progressBar_pedidos.setVisibility(View.INVISIBLE);
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
                id_progressBar_pedidos.setVisibility(View.INVISIBLE);
                showAlertDialog(t.getMessage());
            }
        });
    }


    private void toFrameLayout(Fragment fragmentLayout, String email, String token, String tipo, String nome){
        FragmentTransaction fragment = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("token", token);
        bundle.putString("tipo", tipo);
        bundle.putString("nome", nome);
        fragmentLayout.setArguments(bundle);
        fragment.replace(R.id.frameLayout_container, fragmentLayout).commit();
    }

    private void toAnotherFragment(){
        new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.id_menu_home) {
                    toFrameLayout(new HomeFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_perfil) {
                    toFrameLayout(new PerfilFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_convite) {
                    toFrameLayout(new ConviteFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_pedidos) {
                    toFrameLayout(new PedidosFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_intercessao) {
                    toFrameLayout(new IntercessaoFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_fale_conosco) {

                } else if (id == R.id.id_menu_sobre) {
                    toFrameLayout(new SobreFragment(), email, token, tipo, nome);
                } else if(id == R.id.id_menu_logout){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
                return true;
            }
        };
    }

    private void eventTouch(){
        id_recyclerView_pedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), id_recyclerView_pedidos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                showAlertDialog("Aviso", "Deseja excluir o pedido selecionado?",
                                        R.drawable.ic_warning_orange_24dp, dtos.get(position).getId());
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));
    }

    private void showAlertDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Erro")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", null);
        dialog.create();
        dialog.show();
    }

    private void showAlertDialog(String title, String message, int icon, final Long id){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deletePedido(id);
                    }
                })
                .setNegativeButton("não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dtos = new ArrayList<>();
                        getMyPedidos();
                    }
                });
        dialog.create();
        dialog.show();
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


    private void deletePedido(Long id){
        id_progressBar_pedidos.setVisibility(View.VISIBLE);
        controller = retrofit.create(PedidoController.class);
        Call<Void> call = controller.delete(id, token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 204){
                    id_progressBar_pedidos.setVisibility(View.INVISIBLE);
                    showAlertDialog("Sucesso.", "Pedido deletado com sucesso.", R.drawable.ic_check_circle_green_24dp);
                } else {
                    id_progressBar_pedidos.setVisibility(View.INVISIBLE);
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
            public void onFailure(Call<Void> call, Throwable t) {
                id_progressBar_pedidos.setVisibility(View.INVISIBLE);
                showAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }
}
