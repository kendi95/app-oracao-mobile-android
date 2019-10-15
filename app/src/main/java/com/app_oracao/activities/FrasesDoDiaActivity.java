package com.app_oracao.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app_oracao.R;

public class FrasesDoDiaActivity extends AppCompatActivity {

    private String frase_do_dia, cap_verc;
    private String tipo, nome, email, token;
    private TextView id_frase_frase_do_dia;
    private ImageView id_frase_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frases_do_dia);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_frase_frase_do_dia = findViewById(R.id.id_frase_frase_do_dia);
        id_frase_share = findViewById(R.id.id_frase_share);

        frase_do_dia = getIntent().getStringExtra("frase");
        cap_verc = getIntent().getStringExtra("cap_verc");

        email = getIntent().getStringExtra("email");
        nome = getIntent().getStringExtra("nome");
        tipo = getIntent().getStringExtra("tipo");
        token = getIntent().getStringExtra("token");

        id_frase_frase_do_dia.setText(frase_do_dia);
        id_frase_frase_do_dia.setEnabled(false);

        id_frase_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, id_frase_frase_do_dia.getText().toString());
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(FrasesDoDiaActivity.this, AppActivity.class)
                .putExtra("tipo", tipo)
                .putExtra("token", token)
                .putExtra("email", email)
                .putExtra("nome", nome));
        finish();
        return true;
    }
}
