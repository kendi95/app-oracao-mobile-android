package com.app_oracao.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app_oracao.R;
import com.app_oracao.controllers.CredencialController;
import com.app_oracao.database.service.UsuarioService;
import com.app_oracao.dtos.CredencialDTO;
import com.app_oracao.dtos.LoginResponseDTO;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.utils.BaseURL;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText id_username, id_password;
    private ProgressBar progressBar;
    private TextView id_newAccount, id_forgotPassword;
    private Button id_btn_login;
    private CredencialController controller;
    private Retrofit retrofit;
    private String token, nome, email, tipo;
    private UsuarioService service;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id_newAccount = findViewById(R.id.id_newAccount);
        id_forgotPassword = findViewById(R.id.id_forgotPassword);
        id_btn_login = findViewById(R.id.id_btn_login);
        id_username = findViewById(R.id.id_username);
        id_password = findViewById(R.id.id_password);
        progressBar = findViewById(R.id.progressBar);

        id_newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAppActivity(NewAccountActivity.class);
            }
        });

        id_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toConfirmEmailActivity();
            }
        });

        id_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithRedtrofit();
            }
        });

        progressBar.setVisibility(View.INVISIBLE);

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    private void loginWithRedtrofit(){
        if(id_username.getText().toString().trim().isEmpty()){
            showAlertDialog("Alerta", "O campo 'Email' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
        } else if(id_password.getText().toString().trim().isEmpty()){
            showAlertDialog("Alerta", "O campo 'Senha' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            email = id_username.getText().toString();

            CredencialDTO dto = new CredencialDTO(id_username.getText().toString(), id_password.getText().toString());
            controller = retrofit.create(CredencialController.class);
            Call<LoginResponseDTO> call = controller.login(dto);
            call.enqueue(new Callback<LoginResponseDTO>() {
                @Override
                public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                    if(response.code() == 200){
                        if(response.isSuccessful()){
                            token = response.headers().get("Authorization");
                            nome = response.body().getNome();
                            tipo = response.body().getTipo();
                            signInFirebase();
                        }
                    } else if(response.code() == 403){
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
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {

                }
            });

        }

    }


    private void toAppActivity(Class<?> cls){
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("token", token);
        intent.putExtra("nome", nome);
        intent.putExtra("email", email);
        intent.putExtra("tipo", tipo);
        startActivity(intent);
        finish();
    }

    private void toConfirmEmailActivity(){
        startActivity(new Intent(this, ConfirmEmailActivity.class));
        finish();
    }


    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setIcon(icon)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create();
        dialog.show();
    }


    private void signInFirebase(){
        auth.signInWithEmailAndPassword(id_username.getText().toString(), id_password.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            toAppActivity(AppActivity.class);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            createNewAccountFireabse();

                        }
                    }
                })
                .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showAlertDialog("Erro", e.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }

    private void createNewAccountFireabse(){
        auth.createUserWithEmailAndPassword(id_username.getText().toString(), id_password.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            toAppActivity(AppActivity.class);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            showAlertDialog("Erro", task.getException().getMessage(), R.drawable.ic_error_red_24dp);
                        }
                    }
                });
    }

}
