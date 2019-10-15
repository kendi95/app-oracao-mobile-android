package com.app_oracao.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app_oracao.R;
import com.app_oracao.dtos.NovaFraseDTO;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private List<NovaFraseDTO> dtos = new ArrayList<>();
    private List<String> ids = new ArrayList<>();

    public HomeAdapter(List<String> ids, List<NovaFraseDTO> dtos){
        this.ids = ids;
        this.dtos = dtos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_home_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        if(dtos.get(i).getFrase().length() >= 100){
            String frase = dtos.get(i).getFrase().substring(0, 99);
            myViewHolder.home_frase_do_dia.setText(frase+"...");
        } else {
            myViewHolder.home_frase_do_dia.setText(dtos.get(i).getFrase());
        }
    }

    @Override
    public int getItemCount() {
        return dtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView home_frase_do_dia;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            home_frase_do_dia = itemView.findViewById(R.id.id_home_frase_do_dia);

        }
    }
}
