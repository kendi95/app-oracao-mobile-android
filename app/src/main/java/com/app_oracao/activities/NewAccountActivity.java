package com.app_oracao.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.app_oracao.R;
import com.app_oracao.controllers.UsuarioController;
import com.app_oracao.database.model.UsuarioDB;
import com.app_oracao.database.service.UsuarioService;
import com.app_oracao.dtos.DefaultUsuarioDTO;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.models.Usuario;
import com.app_oracao.utils.BaseURL;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NewAccountActivity extends AppCompatActivity {

    private CheckBox id_checkTermo;
    private EditText id_convite;
    private TextInputEditText id_nome, id_email, id_senha, id_confSenha, id_telefone, id_cidade, id_estado;
    private UsuarioController controller;
    private ProgressBar progressBar;
    private Retrofit retrofit;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private UsuarioService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_checkTermo = findViewById(R.id.id_checkTermo);
        id_nome = findViewById(R.id.id_nome);
        id_email = findViewById(R.id.id_email);
        id_senha = findViewById(R.id.id_senha);
        id_confSenha = findViewById(R.id.id_confSenha);
        id_telefone = findViewById(R.id.id_telefone);
        id_cidade = findViewById(R.id.id_cidade);
        id_estado = findViewById(R.id.id_estado);
        id_convite = findViewById(R.id.id_convite);
        progressBar = findViewById(R.id.id_progressBar_new_account);

        progressBar.setVisibility(View.INVISIBLE);

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = new UsuarioService(NewAccountActivity.this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.id_menu_done:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backToLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void save(){
        if(id_nome.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Nome'");
        } else if(id_email.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Email'");
        } else if(id_senha.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Nova senha'");
        } else if(id_confSenha.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Confirmar a senha'");
        } else if(id_telefone.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Telefone'");
        } else if(id_cidade.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Cidade'");
        } else if(id_estado.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Estado'");
        } else if(id_convite.getText().toString().equals("")){
            showDialogMessageError("Por favor preencher o campo 'Convite'");
        } else if(!id_checkTermo.isChecked()){
            showDialogMessageError("Por favor concordar com o termo de compromisso");
        } else {
            if(isSenhaWithConfSenha()){
                saveDateWithRetrofit();
            } else {
                showDialogMessageError("A 'Nova senha' não corresponde com o valor informado no campo 'Confirmar a senha'");
            }

        }

    }

    private void showDialogMessageError(String messageError){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Alerta!")
                .setMessage(messageError)
                .setIcon(R.drawable.ic_warning_orange_24dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private boolean isSenhaWithConfSenha(){
        if(id_senha.getText().toString().equalsIgnoreCase(id_confSenha.getText().toString())){
            return true;
        }
        return false;
    }

    private void saveDateWithRetrofit(){
        progressBar.setVisibility(View.VISIBLE);
        controller = retrofit.create(UsuarioController.class);

        Usuario usuario = new Usuario(
                null,
                id_nome.getText().toString(),
                id_email.getText().toString(),
                id_senha.getText().toString(),
                id_telefone.getText().toString(),
                id_cidade.getText().toString(),
                id_estado.getText().toString());

        final UsuarioDB usuarioDB2 = new UsuarioDB(
                null,
                id_nome.getText().toString(),
                id_email.getText().toString());
        DefaultUsuarioDTO dto = new DefaultUsuarioDTO(usuario);
        dto.setConviteEncrypt(id_convite.getText().toString());

        Call<Void> call = controller.insert(dto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 201){
                        auth.createUserWithEmailAndPassword(id_email.getText().toString(), id_senha.getText().toString())
                                .addOnCompleteListener(NewAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            service.insert(usuarioDB2);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            showAlertDialog("Sucesso", "Conta criada com sucesso.", R.drawable.ic_check_circle_green_24dp);
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            showAlertDialog("Erro", task.getException().getMessage(), R.drawable.ic_error_red_24dp);
                                        }
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        try{
                            Gson gson = new GsonBuilder().create();
                            StandardError error = new StandardError();
                            error = gson.fromJson(response.errorBody().string(), StandardError.class);
                            showAlertDialog("Erro: "+response.code(), error.getMessage(), R.drawable.ic_error_red_24dp);
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showTermoDeUso(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Termo de Uso")
                .setMessage(R.string.termo_de_uso)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setIcon(icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        backToLogin();
                    }
                });
        dialog.create();
        dialog.show();
    }

}
