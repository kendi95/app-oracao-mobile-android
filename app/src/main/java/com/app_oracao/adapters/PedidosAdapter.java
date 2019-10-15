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

import java.text.SimpleDateFormat;
import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.MyViewHolder> {

    private List<PedidoOracaoDTO> dtos;

    public PedidosAdapter(List<PedidoOracaoDTO> dtos){
        this.dtos = dtos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_pedidos_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy");
        myViewHolder.id_pedidos_data.setText(format.format(dtos.get(i).getData_pedido()));
        myViewHolder.id_pedidos_num_pessoas.setText(String.valueOf(dtos.get(i).getUsuarios().size()));

        if(dtos.get(i).getIsAnonimo().equalsIgnoreCase("true")){
            myViewHolder.id_pedidos_isAnonimo.setImageResource(R.drawable.ic_anonimous_24dp);
        } else {
            myViewHolder.id_pedidos_isAnonimo.setImageResource(R.drawable.ic_person_grey_24dp);
        }

        if(dtos.get(i).getMotivoGeral() == null){
            if(dtos.get(i).getUsuarios().isEmpty()){
                myViewHolder.pedidos_motivo.setText(dtos.get(i).getMotivoPessoal());
                myViewHolder.pedidos_descricao.setText(dtos.get(i).getMotivoDescricao());
            } else {
                myViewHolder.pedidos_motivo.setText(dtos.get(i).getMotivoPessoal());
                myViewHolder.pedidos_descricao.setText(dtos.get(i).getMotivoDescricao());
            }
        } else {
            if(dtos.get(i).getUsuarios().isEmpty()){
                myViewHolder.pedidos_motivo.setText(dtos.get(i).getMotivoGeral());
                myViewHolder.pedidos_descricao.setText("Nenhuma descrição informado.");
                myViewHolder.pedidos_descricao.setTextSize(12);
                myViewHolder.pedidos_descricao.setTypeface(null, Typeface.ITALIC);
            } else {
                myViewHolder.pedidos_motivo.setText(dtos.get(i).getMotivoGeral());
                myViewHolder.pedidos_descricao.setText("Nenhuma descrição informado.");
                myViewHolder.pedidos_descricao.setTextSize(12);
                myViewHolder.pedidos_descricao.setTypeface(null, Typeface.ITALIC);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dtos.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView pedidos_motivo, pedidos_descricao, id_pedidos_num_pessoas, id_pedidos_data;
        public ImageView id_pedidos_isAnonimo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pedidos_motivo = itemView.findViewById(R.id.id_pedidos_motivo);
            pedidos_descricao = itemView.findViewById(R.id.id_pedidos_descricao);
            id_pedidos_isAnonimo = itemView.findViewById(R.id.id_pedidos_isAnonimo);
            id_pedidos_num_pessoas = itemView.findViewById(R.id.id_pedidos_num_pessoas);
            id_pedidos_data = itemView.findViewById(R.id.id_pedidos_data);
        }

    }

}
