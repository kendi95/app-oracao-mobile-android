package com.app_oracao.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app_oracao.R;
import com.app_oracao.database.model.EventoDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MinhaAgendaAdapter extends RecyclerView.Adapter<MinhaAgendaAdapter.MyViewHolder> {

    private Context context;
    private List<EventoDB> eventoDBS = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private Calendar calendarInicio = Calendar.getInstance();
    private Calendar calendarFim = Calendar.getInstance();

    public MinhaAgendaAdapter(Context context, List<EventoDB> eventoDBS){
        this.context = context;
        this.eventoDBS = eventoDBS;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_minha_agenda_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        calendarInicio.setTimeInMillis(eventoDBS.get(i).getHoras_inicio());
        calendarFim.setTimeInMillis(eventoDBS.get(i).getHoras_fim());
        if(eventoDBS.get(i).getTitulo().isEmpty()){
            myViewHolder.id_textView_agenda1.setText("Sem título");
            myViewHolder.id_textView_agenda1.setTypeface(null, Typeface.ITALIC);
            myViewHolder.id_textView_agenda2.setText(format.format(calendarInicio.getTime())+" - "+format.format(calendarFim.getTime()));
        } else {
            myViewHolder.id_textView_agenda1.setText(eventoDBS.get(i).getTitulo());
            myViewHolder.id_textView_agenda2.setText(format.format(calendarInicio.getTime())+" - "+format.format(calendarFim.getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return eventoDBS.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView id_textView_agenda1, id_textView_agenda2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id_textView_agenda1 = itemView.findViewById(R.id.id_textView_agenda1);
            id_textView_agenda2 = itemView.findViewById(R.id.id_textView_agenda2);
        }
    }

}
