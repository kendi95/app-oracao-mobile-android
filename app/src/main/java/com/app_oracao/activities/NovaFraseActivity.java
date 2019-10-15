package com.app_oracao.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.app_oracao.R;
import com.app_oracao.dtos.NovaFraseDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NovaFraseActivity extends AppCompatActivity {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference frases = reference.child("frases");
    private String tipo, token, nome, email;
    private EditText id_multiline_frase;
    private ProgressBar id_progressBar_nova_frase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_frase);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_multiline_frase = findViewById(R.id.id_multiline_frase);
        id_progressBar_nova_frase = findViewById(R.id.id_progressBar_nova_frase);

        id_progressBar_nova_frase.setVisibility(View.INVISIBLE);
        getValuesOfIntent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        toAppActivity();
        return true;
    }

    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(NovaFraseActivity.this)
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

    private void getValuesOfIntent(){
        tipo = getIntent().getStringExtra("tipo");
        token = getIntent().getStringExtra("token");
        nome = getIntent().getStringExtra("nome");
        email = getIntent().getStringExtra("email");
    }

    private void toAppActivity(){
        startActivity(new Intent(NovaFraseActivity.this, AppActivity.class)
                .putExtra("tipo", tipo)
                .putExtra("token", token)
                .putExtra("email", email)
                .putExtra("nome", nome));
        finish();
    }

    public void saveNovaFrase(View view){
        id_progressBar_nova_frase.setVisibility(View.VISIBLE);
        if(id_multiline_frase.getText().toString().trim().isEmpty()){
            showAlertDialog("Alerta!", "O campo deve ser preenchido.", R.drawable.ic_warning_orange_24dp);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            frases.push().setValue(new NovaFraseDTO(id_multiline_frase.getText().toString(), format.format(new Date(System.currentTimeMillis()))))
                    .addOnCompleteListener(NovaFraseActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                id_progressBar_nova_frase.setVisibility(View.INVISIBLE);
                                toAppActivity();
                            } else {
                                id_progressBar_nova_frase.setVisibility(View.INVISIBLE);
                                showAlertDialog("Erro", "Não foi possível salvar.", R.drawable.ic_error_red_24dp);
                            }
                        }
                    });
        }
    }

}
