package com.app_oracao.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.app_oracao.R;
import com.app_oracao.activities.LoginActivity;
import com.app_oracao.controllers.UsuarioController;
import com.app_oracao.dtos.ConviteDTO;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.utils.BaseURL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConviteFragment extends Fragment {

    private TextInputEditText id_email_destino;
    private Spinner spinner;
    private Button id_button_invite;
    private UsuarioController controller;
    private Retrofit retrofit;
    private ProgressBar progressBar;
    private String emailSender;
    private String tipo, token, email, nome;
    private OkHttpClient client;

    public ConviteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convite, container, false);

        emailSender = getArguments().get("email").toString();
        token = getArguments().get("token").toString();
        tipo = getArguments().get("tipo").toString();
        nome = getArguments().get("nome").toString();

        id_email_destino = view.findViewById(R.id.id_email_destino);
        id_button_invite = view.findViewById(R.id.id_button_invite);
        progressBar = view.findViewById(R.id.progressBar_invite);

        progressBar.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.tipos));
        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Selecione...")){
                    tipo = spinner.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        createInvite();

        toAnotherFragment();

        return view;
    }

    private void createInvite(){
        id_button_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_email_destino.getText().toString().trim().equalsIgnoreCase("") || id_email_destino.getText() == null){
                    progressBar.setVisibility(View.INVISIBLE);
                    showAlertDialog("Alerta", "O campo 'Email de destino' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
                } else if(spinner.getSelectedItem().toString().equalsIgnoreCase("Selecione...")){
                    progressBar.setVisibility(View.INVISIBLE);
                    showAlertDialog("Alerta","O 'tipo' deve ser selecionado.", R.drawable.ic_warning_orange_24dp);
                } else {
                    createInviteWithRetrofit();
                }
            }
        });
    }


    private void createInviteWithRetrofit(){
        progressBar.setVisibility(View.VISIBLE);
        ConviteDTO dto = new ConviteDTO(emailSender, id_email_destino.getText().toString(), tipo);

        controller = retrofit.create(UsuarioController.class);
        Call<Void> call = controller.createConvite(dto, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 201){
                    if(response.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        id_email_destino.setText("");
                        showAlertDialog("Sucesso", "Convite criado com sucesso.", R.drawable.ic_check_circle_green_24dp);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
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
                showAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });

    }

    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setIcon(icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                    toFrameLayout(new HomeFragment(), emailSender, token, tipo, nome);
                } else if (id == R.id.id_menu_perfil) {
                    toFrameLayout(new PerfilFragment(), emailSender, token, tipo, nome);
                } else if (id == R.id.id_menu_convite) {
                    toFrameLayout(new ConviteFragment(), emailSender, token, tipo, nome);
                } else if (id == R.id.id_menu_pedidos) {
                    toFrameLayout(new PedidosFragment(), emailSender, token, tipo, nome);
                } else if (id == R.id.id_menu_intercessao) {
                    toFrameLayout(new IntercessaoFragment(), emailSender, token, tipo, nome);
                } else if (id == R.id.id_fale_conosco) {

                } else if (id == R.id.id_menu_sobre) {
                    toFrameLayout(new SobreFragment(), emailSender, token, tipo, nome);
                } else if(id == R.id.id_menu_logout){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
                return true;
            }
        };
    }
}
