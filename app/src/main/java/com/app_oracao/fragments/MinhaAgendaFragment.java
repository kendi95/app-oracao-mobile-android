package com.app_oracao.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.app_oracao.R;
import com.app_oracao.adapters.MinhaAgendaAdapter;
import com.app_oracao.database.model.EventoDB;
import com.app_oracao.database.model.UsuarioDB;
import com.app_oracao.database.service.EventoService;
import com.app_oracao.database.service.UsuarioService;
import com.app_oracao.utils.RecyclerItemClickListener;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MinhaAgendaFragment extends Fragment {

    private String tipo, token, email, nome;
    private CalendarView id_calendarView;
    private RecyclerView id_recyclerView_agenda;
    private Calendar currentDate;
    private List<EventoDB> eventoDBS;
    private EventoService service;
    private UsuarioService userService;
    private UsuarioDB usuarioDB;
    private List<Calendar> datas;
    private List<EventDay> events;


    public MinhaAgendaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getValueOfFragment();
        View view = inflater.inflate(R.layout.fragment_minha_agenda, container, false);
        id_calendarView = view.findViewById(R.id.calendarView);

        currentDate = Calendar.getInstance();
        try {
            id_calendarView.setDate(currentDate);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        getDateOfDB();

        id_calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                View viewChild = getLayoutInflater().inflate(R.layout.recyclerview_minha_agenda_lista, null);
                showDialogOfCalendarView(viewChild, eventDay);
            }
        });

        return view;
    }


    private void getValueOfFragment(){
        tipo = getArguments().get("tipo").toString();
        token = getArguments().get("token").toString();
        email = getArguments().get("email").toString();
        nome = getArguments().get("nome").toString();
    }


    private void getDateOfDB(){
        service = new EventoService(getActivity());
        userService = new UsuarioService(getActivity());
        usuarioDB = userService.findByEmail(email);
        eventoDBS = new ArrayList<>();
        datas = new ArrayList<>();
        events = new ArrayList<>();
        eventoDBS = service.findAll();

        if(usuarioDB != null){
            for(int i = 0; i< eventoDBS.size(); i++){
                if(eventoDBS.get(i).getUsuarioId() == usuarioDB.getId()){
                    Calendar data = Calendar.getInstance();
                    data.setTimeInMillis(eventoDBS.get(i).getData());
                    datas.add(data);
                }
            }

            if(!datas.isEmpty()){
                for(int i=0; i<datas.size(); i++){
                    try {
                        id_calendarView.setDate(datas.get(i));
                        events.add(new EventDay(datas.get(i), R.drawable.ic_check_green_24dp));
                    } catch (OutOfDateRangeException e) {
                        e.printStackTrace();
                    }
                }
                id_calendarView.setEvents(events);
            }
        }
    }

    private void showDialogOfCalendarView(View viewChild, EventDay eventDay){

        List<EventoDB> events = new ArrayList<>();
        for (int i = 0; i < eventoDBS.size(); i++) {
            if(eventoDBS.get(i).getUsuarioId() == usuarioDB.getId()){
                if(eventDay.getCalendar().getTimeInMillis() == eventoDBS.get(i).getData()){
                    events.add(eventoDBS.get(i));
                }
            }
        }

        if(events.isEmpty()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(viewChild.getContext());
            dialog.setTitle("Aviso");
            dialog.setMessage("Não há eventos nesse dia.");
            dialog.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.create();
            dialog.show();
        } else {
            //config adapter
            MinhaAgendaAdapter adapter = new MinhaAgendaAdapter(viewChild.getContext(), events);

            //config recyclerView
            RecyclerView.LayoutManager manager = new LinearLayoutManager(viewChild.getContext());

            id_recyclerView_agenda = viewChild.findViewById(R.id.id_recyclerView_agenda);

            id_recyclerView_agenda.setLayoutManager(manager);
            id_recyclerView_agenda.setHasFixedSize(true);
            id_recyclerView_agenda.setAdapter(adapter);

            AlertDialog.Builder dialog = new AlertDialog.Builder(viewChild.getContext());

            if(id_recyclerView_agenda.getParent() != null){
                ((ViewGroup)id_recyclerView_agenda.getParent()).removeView(id_recyclerView_agenda);
            }
            eventTouch(id_recyclerView_agenda);
            dialog.setView(id_recyclerView_agenda);
            dialog.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.create();
            dialog.show();
        }

    }

    private void eventTouch(RecyclerView view){
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), view,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.i("clique", "clicou");
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                                        .setTitle("Aviso")
                                        .setMessage("Deseja realmente excluir essa agenda?")
                                        .setIcon(R.drawable.ic_warning_orange_24dp)
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create().show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));
    }

}
