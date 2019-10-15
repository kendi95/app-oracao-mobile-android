package com.app_oracao.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.app_oracao.App;
import com.app_oracao.R;
import com.app_oracao.controllers.CredencialController;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.servicies.CreateDBService;
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

public class ConfirmEmailActivity extends AppCompatActivity {

    private EditText id_confirm_email;
    private Button id_confirm_prosseguir;
    private ProgressBar id_confirm_progressBar;
    private Retrofit retrofit;
    private OkHttpClient client;
    private CredencialController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_confirm_email = findViewById(R.id.id_confirm_email);
        id_confirm_prosseguir = findViewById(R.id.id_confirm_prosseguir);
        id_confirm_progressBar = findViewById(R.id.id_confirm_progressBar);

        id_confirm_prosseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosseguir();
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

        id_confirm_progressBar.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }


    private void prosseguir(){
        if(id_confirm_email.getText().toString().trim().isEmpty() || id_confirm_email.getText().toString().trim() == null){
            id_confirm_progressBar.setVisibility(View.INVISIBLE);
            showDialogMessage("Alerta!", "O campo 'Email' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
            return;
        }
        confirmEmailWithRetrofit();
    }

    private void showDialogMessage(String title, String message, Integer icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
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

    private void confirmEmailWithRetrofit(){
        id_confirm_progressBar.setVisibility(View.VISIBLE);
        controller = retrofit.create(CredencialController.class);
        Call<Void> call = controller.confirmEmail(id_confirm_email.getText().toString());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    if(response.isSuccessful()){
                        id_confirm_progressBar.setVisibility(View.INVISIBLE);
                        toNewPasswordAvtivity();
                    }
                } else {
                    id_confirm_progressBar.setVisibility(View.INVISIBLE);
                    id_confirm_email.setText("");
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
                showDialogMessage("Erro", t.getMessage(), R.drawable.ic_warning_orange_24dp);
                Log.i("Email", t.getMessage());
            }
        });
    }

    private void toNewPasswordAvtivity(){
        startActivity(new Intent(this, NewPasswordActivity.class));
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


}
