package com.app_oracao.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.app_oracao.R;
import com.app_oracao.controllers.PedidoController;
import com.app_oracao.dtos.MotivoGeralDescricaoDTO;
import com.app_oracao.dtos.PedidoOracaoDTO;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.models.MotivoGeral;
import com.app_oracao.utils.BaseURL;
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

public class NovoPedidoActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter, adapter2;
    private Spinner spinner, spinner2;
    private Switch id_switch_motivos_gerais, id_switch_motivo_pessoal;
    private TextView character_count, character_count2;
    private EditText id_multilineText, id_editText_motivo;
    private CheckBox id_isAnonimo;
    private String isAnonimo = "false";
    private String tipo, token, nome, email;
    private Retrofit retrofit;
    private PedidoController controller;
    private ProgressBar id_progressBar_pedido;
    private PedidoOracaoDTO dto = new PedidoOracaoDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_pedido);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getValuesOfIntent();

        spinner = findViewById(R.id.id_spinner_motivos_gerais);
        spinner2 = findViewById(R.id.id_spinner_motivos_gerais_por_categoria);
        id_switch_motivos_gerais = findViewById(R.id.id_switch_motivos_gerais);
        id_switch_motivo_pessoal = findViewById(R.id.id_switch_motivo_pessoal);
        character_count = findViewById(R.id.id_character_count);
        character_count2 = findViewById(R.id.id_character_count2);
        id_editText_motivo = findViewById(R.id.id_editText_motivo);
        id_multilineText = findViewById(R.id.id_multilineText);
        id_isAnonimo = findViewById(R.id.id_isAnonimo);
        id_progressBar_pedido = findViewById(R.id.id_progressBar_pedido);

        id_progressBar_pedido.setVisibility(View.INVISIBLE);

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        enableSwitchMotivosGerais();
        enableSwitchMotivoPessoal();
        countCharactersEvent();

    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(NovoPedidoActivity.this, AppActivity.class)
                .putExtra("tipo", tipo)
                .putExtra("token", token)
                .putExtra("email", email)
                .putExtra("nome", nome));
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.id_menu_done:
                toMain();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getValuesOfIntent(){
        tipo = getIntent().getStringExtra("tipo");
        token = getIntent().getStringExtra("token");
        nome = getIntent().getStringExtra("nome");
        email = getIntent().getStringExtra("email");
    }

    private void toMain(){
        Intent intent = new Intent(this, AppActivity.class);
        intent.putExtra("tipo", tipo);
        intent.putExtra("token", token);
        intent.putExtra("nome", nome);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void countCharactersEvent(){
        character_count.setText(0+"/400");
        id_multilineText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                character_count.setText(s.length()+"/400");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 400){
                    character_count.setText(s.length()+"/400");
                    character_count.setTextColor(Color.RED);
                } else {
                    character_count.setText(s.length()+"/400");
                    character_count.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        character_count2.setText(0+"/20");
        id_editText_motivo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                character_count2.setText(s.length()+"/20");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 20){
                    character_count2.setText(s.length()+"/20");
                    character_count2.setTextColor(Color.RED);
                } else {
                    character_count2.setText(s.length()+"/20");
                    character_count2.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void showAlertDialogSuccessful(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setIcon(icon)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toMain();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private void showErrorAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(NovoPedidoActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(NovoPedidoActivity.this, LoginActivity.class));
                        finish();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private void enableSwitchMotivosGerais(){
        spinner.setEnabled(false);
        spinner2.setEnabled(false);
        id_switch_motivos_gerais.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    id_switch_motivo_pessoal.setChecked(false);
                    dto.setMotivoPessoal(null);
                    dto.setMotivoDescricao(null);
                    getMotivoGeralOfRetrofit();
                } else {
                    id_progressBar_pedido.setVisibility(View.INVISIBLE);
                    spinner.setEnabled(false);
                    spinner.setAdapter(null);
                    spinner2.setEnabled(false);
                    spinner2.setAdapter(null);
                }
            }
        });

    }

    private void enableSwitchMotivoPessoal(){
        id_editText_motivo.setEnabled(false);
        id_multilineText.setEnabled(false);
        id_switch_motivo_pessoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    id_switch_motivos_gerais.setChecked(false);
                    spinner.setEnabled(false);
                    spinner2.setEnabled(false);
                    dto.setMotivoGeral(null);
                    id_editText_motivo.setEnabled(true);
                    id_multilineText.setEnabled(true);
                } else {
                    id_editText_motivo.setEnabled(false);
                    id_editText_motivo.setText(null);
                    id_multilineText.setEnabled(false);
                    id_multilineText.setText(null);
                }
            }
        });
    }

    private void getMotivoGeralOfRetrofit(){
        id_progressBar_pedido.setVisibility(View.VISIBLE);
        controller = retrofit.create(PedidoController.class);
        Call<List<MotivoGeral>> call = controller.findAll(token);

        call.enqueue(new Callback<List<MotivoGeral>>() {
            @Override
            public void onResponse(Call<List<MotivoGeral>> call, Response<List<MotivoGeral>> response) {
                if(response.code() == 200){
                    List<String> strings = new ArrayList<>();
                    for(int i = 0; i < response.body().size(); i++){
                        strings.add(response.body().get(i).getDescricao());
                    }
                    id_progressBar_pedido.setVisibility(View.INVISIBLE);
                    spinner.setEnabled(true);
                    adapter = new ArrayAdapter<String>(NovoPedidoActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                    spinner.setAdapter(adapter);
                    getValuesOfMotivoGeral();

                } else {
                    try{
                        Gson gson = new GsonBuilder().create();
                        StandardError error = new StandardError();
                        error = gson.fromJson(response.errorBody().string(), StandardError.class);
                        showErrorAlertDialog("Erro: "+response.code(), error.getMessage(), R.drawable.ic_error_red_24dp);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MotivoGeral>> call, Throwable t) {
                id_progressBar_pedido.setVisibility(View.INVISIBLE);
                showErrorAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }

    private void getValuesOfMotivoGeral(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_progressBar_pedido.setVisibility(View.VISIBLE);
                controller = retrofit.create(PedidoController.class);
                Call<List<MotivoGeralDescricaoDTO>> call = controller.findByDescricao(parent.getItemAtPosition(position).toString(), token);

                call.enqueue(new Callback<List<MotivoGeralDescricaoDTO>>() {
                    @Override
                    public void onResponse(Call<List<MotivoGeralDescricaoDTO>> call, Response<List<MotivoGeralDescricaoDTO>> response) {
                        if(response.code() == 200){
                            List<String> strings = new ArrayList<>();
                            for(int i = 0; i < response.body().size(); i++){
                                strings.add(response.body().get(i).getDescricao());
                            }
                            id_progressBar_pedido.setVisibility(View.INVISIBLE);
                            spinner2.setEnabled(true);
                            adapter2 = new ArrayAdapter<String>(NovoPedidoActivity.this, android.R.layout.simple_spinner_dropdown_item, strings);
                            spinner2.setAdapter(adapter2);
                                getValueOfSpinner2();
                        } else {
                            id_progressBar_pedido.setVisibility(View.INVISIBLE);
                            try{
                                Gson gson = new GsonBuilder().create();
                                StandardError error = new StandardError();
                                error = gson.fromJson(response.errorBody().string(), StandardError.class);
                                showErrorAlertDialog("Erro: "+response.code(), error.getMessage(), R.drawable.ic_error_red_24dp);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MotivoGeralDescricaoDTO>> call, Throwable t) {
                        id_progressBar_pedido.setVisibility(View.INVISIBLE);
                        showErrorAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getValueOfSpinner2(){
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dto.setMotivoGeral(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void savePedidoMotivos(PedidoOracaoDTO dto){
        id_progressBar_pedido.setVisibility(View.VISIBLE);
        controller = retrofit.create(PedidoController.class);
        Call<Void> call = controller.createPedido(dto, token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 201){
                    id_progressBar_pedido.setVisibility(View.INVISIBLE);
                    showAlertDialogSuccessful("Sucesso", "Pedido realizado com sucesso.", R.drawable.ic_check_circle_green_24dp);
                } else {
                    id_progressBar_pedido.setVisibility(View.INVISIBLE);
                    try{
                        Gson gson = new GsonBuilder().create();
                        StandardError error = new StandardError();
                        error = gson.fromJson(response.errorBody().string(), StandardError.class);
                        showErrorAlertDialog("Erro: "+response.code(), error.getMessage(), R.drawable.ic_error_red_24dp);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                id_progressBar_pedido.setVisibility(View.INVISIBLE);
                showErrorAlertDialog("Erro", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }



    public void savePedido(View view){
        if(id_switch_motivos_gerais.isChecked() == false && id_switch_motivo_pessoal.isChecked() == false){
            showAlertDialog("Alerta", "Habilite um dos motivos para que possa realizar o pedido",
                    R.drawable.ic_warning_orange_24dp);
        } else {
            if(id_switch_motivos_gerais.isChecked()){
                if(id_isAnonimo.isChecked()){
                    isAnonimo = "true";
                    dto.setIsAnonimo(isAnonimo);
                    dto.setMotivoPessoal(null);
                    dto.setMotivoDescricao(null);
                    dto.setData_pedido(null);
                    dto.setUsuarios(null);
                    dto.setNome_autor(null);
                    dto.setId(null);
                    savePedidoMotivos(dto);
                } else {
                    isAnonimo = "false";
                    dto.setIsAnonimo(isAnonimo);
                    dto.setMotivoPessoal(null);
                    dto.setMotivoDescricao(null);
                    dto.setData_pedido(null);
                    dto.setUsuarios(null);
                    dto.setNome_autor(null);
                    dto.setId(null);
                    savePedidoMotivos(dto);
                }

            } else if(id_switch_motivo_pessoal.isChecked()){
                if(id_editText_motivo.getText().toString().trim().isEmpty()){
                    showAlertDialog("Alerta", "O campo 'Motivo' deve ser preenchido.",
                            R.drawable.ic_warning_orange_24dp);
                } else if(id_multilineText.getText().toString().trim().isEmpty()){
                    showAlertDialog("Alerta", "O campo 'Descrição do motivo' deve ser preenchido.",
                            R.drawable.ic_warning_orange_24dp);
                } else {
                    if(id_isAnonimo.isChecked()){
                        isAnonimo = "true";
                        dto.setIsAnonimo(isAnonimo);
                        dto.setMotivoPessoal(id_editText_motivo.getText().toString());
                        dto.setMotivoDescricao(id_multilineText.getText().toString());
                        dto.setData_pedido(null);
                        dto.setUsuarios(null);
                        dto.setNome_autor(null);
                        dto.setId(null);
                        savePedidoMotivos(dto);
                    } else {
                        isAnonimo = "false";
                        dto.setIsAnonimo(isAnonimo);
                        dto.setMotivoPessoal(id_editText_motivo.getText().toString());
                        dto.setMotivoDescricao(id_multilineText.getText().toString());
                        dto.setData_pedido(null);
                        dto.setUsuarios(null);
                        dto.setNome_autor(null);
                        dto.setId(null);
                        savePedidoMotivos(dto);
                    }

                }
            }
        }
    }
}
