package com.app_oracao.adapters;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app_oracao.R;
import com.app_oracao.dtos.PedidoOracaoDTO;
import com.app_oracao.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class IntercessaoAdapter extends RecyclerView.Adapter<IntercessaoAdapter.MyViewHolder> {

    private List<PedidoOracaoDTO> dtos = new ArrayList<>();
    private String email;

    public IntercessaoAdapter(List<PedidoOracaoDTO> dtos, String email){
        this.email = email;
        this.dtos = dtos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_intercessao_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.id_intercessao_check.setVisibility(View.GONE);
        if(dtos.get(i).getMotivoGeral() == null){
            myViewHolder.intercessao_motivo.setText(dtos.get(i).getMotivoPessoal());
            if(dtos.get(i).getMotivoDescricao().length() >= 100){
                String descricao = dtos.get(i).getMotivoDescricao().substring(0, 99);
                myViewHolder.intercessao_descricao.setText(descricao+"...");
            }

            List<String> emailUsuario = new ArrayList<>();

            for(Usuario u: dtos.get(i).getUsuarios()){
                emailUsuario.add(u.getEmail());
            }

            if(emailUsuario.contains(email)){
                myViewHolder.id_intercessao_check.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.id_intercessao_check.setVisibility(View.GONE);
            }

        } else {
            myViewHolder.intercessao_motivo.setText(dtos.get(i).getMotivoGeral());
            myViewHolder.intercessao_descricao.setText("Nenhuma descrição informado.");
            myViewHolder.intercessao_descricao.setTextSize(12);
            myViewHolder.intercessao_descricao.setTypeface(null, Typeface.ITALIC);

            List<String> emailUsuario = new ArrayList<>();

            for(Usuario u: dtos.get(i).getUsuarios()){
                emailUsuario.add(u.getEmail());
            }

            if(emailUsuario.contains(email)){
                myViewHolder.id_intercessao_check.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.id_intercessao_check.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return dtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView intercessao_descricao, intercessao_motivo;
        public ImageView id_intercessao_check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            intercessao_descricao = itemView.findViewById(R.id.id_intercessao_descricao);
            intercessao_motivo = itemView.findViewById(R.id.id_intercessao_motivo);
            id_intercessao_check = itemView.findViewById(R.id.id_intercessao_check);
        }
    }

}
