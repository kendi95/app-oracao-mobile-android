package com.app_oracao.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.app_oracao.R;
import com.app_oracao.controllers.CredencialController;
import com.app_oracao.dtos.SenhaDTO;
import com.app_oracao.utils.BaseURL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewPasswordActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private CredencialController controller;
    private TextInputEditText id_newPass, id_confirmNewPass;
    private EditText id_securityCode;
    private ProgressBar id_confirm_progressBar;
    private Button id_button_confirmPassword, id_button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        id_newPass = findViewById(R.id.id_newPass);
        id_confirmNewPass = findViewById(R.id.id_confirmNewPass);
        id_securityCode = findViewById(R.id.id_securityCode);
        id_confirm_progressBar = findViewById(R.id.id_confirm_progressBar);
        id_button_confirmPassword = findViewById(R.id.id_button_confirmPassword);
        id_button_cancel = findViewById(R.id.id_button_cancel);

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        id_button_confirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewPassword();
            }
        });

        id_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLoginActivity();
            }
        });

        id_confirm_progressBar.setVisibility(View.INVISIBLE);
    }


    private void setNewPassword(){
        if(id_newPass.getText().toString().trim().isEmpty()){
            id_confirm_progressBar.setVisibility(View.INVISIBLE);
            showDialogMessage("Alerta!", "O campo 'Nova senha' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
        } else if(id_confirmNewPass.getText().toString().trim().isEmpty()){
            id_confirm_progressBar.setVisibility(View.INVISIBLE);
            showDialogMessage("Alerta!", "O campo 'Confirmar senha' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
        } else if(id_securityCode.getText().toString().trim().isEmpty()){
            id_confirm_progressBar.setVisibility(View.INVISIBLE);
            showDialogMessage("Alerta!", "O campo 'Código de segurança' deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
        } else if(isPassword()){
            id_confirm_progressBar.setVisibility(View.INVISIBLE);
            showDialogMessage("Alerta!", "A 'Nova senha' não corresponde com 'Confirmar senha'", R.drawable.ic_warning_orange_24dp);
        } else {
            setNewPasswordWithRetrofit();
        }

    }

    private void showDialogMessage(String title, String message, int icon){
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

    private void showDialogMessagePositive(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setIcon(icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toLoginActivity();
                    }
                });
        dialog.create();
        dialog.show();
    }


    private boolean isPassword(){
        if(id_confirmNewPass.getText().toString().equalsIgnoreCase(id_newPass.getText().toString())){
            return false;
        }
        return true;
    }

    private void setNewPasswordWithRetrofit(){
        id_confirm_progressBar.setVisibility(View.VISIBLE);
        SenhaDTO dto = new SenhaDTO(id_newPass.getText().toString().trim(), id_securityCode.getText().toString().trim());

        controller = retrofit.create(CredencialController.class);
        Call<Void> call = controller.setNewPassword(dto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    id_confirm_progressBar.setVisibility(View.INVISIBLE);
                    showDialogMessagePositive("Sucesso", "Nova senha atualizada com sucesso.", R.drawable.ic_check_circle_green_24dp);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void toLoginActivity(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
