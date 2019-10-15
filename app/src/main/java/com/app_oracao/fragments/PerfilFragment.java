package com.app_oracao.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.app_oracao.R;
import com.app_oracao.activities.LoginActivity;
import com.app_oracao.controllers.UsuarioController;
import com.app_oracao.dtos.DefaultUsuarioDTO;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.models.Usuario;
import com.app_oracao.utils.BaseURL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private TextInputEditText id_editText_perfil_nome, id_editText_perfil_email,
            id_editText_perfil_telefone, id_editText_perfil_cidade, id_editText_perfil_estado;
    private Switch id_switch_perfil;
    private Button id_button_perfil;
    private ProgressBar progressBar_perfil;
    private Retrofit retrofit;
    private UsuarioController controller;
    private Usuario usuario;
    private DefaultUsuarioDTO dto;
    private String tipo, token, email, nome;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        tipo = getArguments().get("tipo").toString();
        token = getArguments().get("token").toString();
        email = getArguments().get("email").toString();
        nome = getArguments().get("nome").toString();

        id_switch_perfil = view.findViewById(R.id.id_switch_perfil);
        id_button_perfil = view.findViewById(R.id.id_button_perfil);
        progressBar_perfil = view.findViewById(R.id.progressBar_perfil);
        id_editText_perfil_nome = view.findViewById(R.id.id_editText_perfil_nome);
        id_editText_perfil_email = view.findViewById(R.id.id_editText_perfil_email);
        id_editText_perfil_telefone = view.findViewById(R.id.id_editText_perfil_telefone);
        id_editText_perfil_cidade = view.findViewById(R.id.id_editText_perfil_cidade);
        id_editText_perfil_estado = view.findViewById(R.id.id_editText_perfil_estado);

        disableOrEnableInputs();
        progressBar_perfil.setVisibility(View.INVISIBLE);

        email = getArguments().get("email").toString();
        token = getArguments().get("token").toString();

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        setDisableInputs();
        getPerfilByEmail();

        id_button_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataPerfil();
            }
        });

        toAnotherFragment();

        return view;
    }

    private void getPerfilByEmail(){
        progressBar_perfil.setVisibility(View.VISIBLE);
        controller = retrofit.create(UsuarioController.class);
        Call<DefaultUsuarioDTO> call = controller.findByEmail(token);
        call.enqueue(new Callback<DefaultUsuarioDTO>() {
            @Override
            public void onResponse(Call<DefaultUsuarioDTO> call, Response<DefaultUsuarioDTO> response) {
                if(response.code() == 200){
                    progressBar_perfil.setVisibility(View.INVISIBLE);
                    dto = response.body();
                    valuesToUsuario(dto);
                } else {
                    progressBar_perfil.setVisibility(View.INVISIBLE);
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
            public void onFailure(Call<DefaultUsuarioDTO> call, Throwable t) {
                progressBar_perfil.setVisibility(View.INVISIBLE);
                showAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }

    private void updateDataPerfil(){
        progressBar_perfil.setVisibility(View.VISIBLE);
        dto = new DefaultUsuarioDTO();
        dto.setEmail(id_editText_perfil_email.getText().toString());
        dto.setTelefone(id_editText_perfil_telefone.getText().toString());
        dto.setCidade(id_editText_perfil_cidade.getText().toString());
        dto.setEstado(id_editText_perfil_estado.getText().toString());

        controller = retrofit.create(UsuarioController.class);
        Call<DefaultUsuarioDTO> call = controller.update(dto, token);
        call.enqueue(new Callback<DefaultUsuarioDTO>() {
            @Override
            public void onResponse(Call<DefaultUsuarioDTO> call, Response<DefaultUsuarioDTO> response) {
                if(response.isSuccessful()){
                    dto = response.body();
                    Log.i("DTO", dto.getNome());
                    valuesToUsuario(dto);
                    progressBar_perfil.setVisibility(View.INVISIBLE);
                    ShowDialogMessageWithSuccessful("Sucesso", "Perfil atualizado com sucesso.", R.drawable.ic_check_circle_green_24dp);
                } else {
                    progressBar_perfil.setVisibility(View.INVISIBLE);
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
            public void onFailure(Call<DefaultUsuarioDTO> call, Throwable t) {
                progressBar_perfil.setVisibility(View.INVISIBLE);
                showAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }



    private void disableOrEnableInputs(){
        id_switch_perfil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    setDisableInputs();
                } else {
                    id_editText_perfil_nome.setEnabled(false);
                    id_editText_perfil_email.setEnabled(true);
                    id_editText_perfil_telefone.setEnabled(true);
                    id_editText_perfil_cidade.setEnabled(true);
                    id_editText_perfil_estado.setEnabled(true);
                    id_button_perfil.setEnabled(true);
                }

            }
        });
    }

    private void setDisableInputs(){
        id_editText_perfil_nome.setEnabled(false);
        id_editText_perfil_email.setEnabled(false);
        id_editText_perfil_telefone.setEnabled(false);
        id_editText_perfil_cidade.setEnabled(false);
        id_editText_perfil_estado.setEnabled(false);
        id_button_perfil.setEnabled(false);

    }

    private void valuesToUsuario(DefaultUsuarioDTO dto){
        usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setCidade(dto.getCidade());
        usuario.setEstado(dto.getEstado());
        valuesToInputs(usuario);

    }

    private void valuesToInputs(Usuario Usuario){
        id_editText_perfil_nome.setText(Usuario.getNome());
        id_editText_perfil_email.setText(Usuario.getEmail());
        id_editText_perfil_telefone.setText(Usuario.getTelefone());
        id_editText_perfil_cidade.setText(Usuario.getCidade());
        id_editText_perfil_estado.setText(Usuario.getEstado());
    }

    private void isEmailEqualsPerfilEmail(){
        if(usuario.getEmail().equalsIgnoreCase(email)){
            id_switch_perfil.setChecked(false);
            return;
        }
        ShowDialogMessageToRedirect("Alerta!", "Será redirecionado para tela de Login, pois o seu email foi alterado.", R.drawable.ic_warning_orange_24dp);
    }


    private void ShowDialogMessageWithSuccessful(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isEmailEqualsPerfilEmail();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private void ShowDialogMessageToRedirect(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
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

    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();

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
}
