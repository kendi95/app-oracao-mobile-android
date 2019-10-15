package com.app_oracao.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.app_oracao.R;
import com.app_oracao.activities.FrasesDoDiaActivity;
import com.app_oracao.activities.LoginActivity;
import com.app_oracao.activities.NovaFraseActivity;
import com.app_oracao.adapters.HomeAdapter;
import com.app_oracao.dtos.NovaFraseDTO;
import com.app_oracao.utils.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference frases = reference.child("frases");
    private RecyclerView id_recyclerView_home;
    private String tipo, nome, email, token;
    private String frase_do_dia, cap_verc;
    private List<NovaFraseDTO> dtos = new ArrayList<>();
    private List<String> ids = new ArrayList<>();
    private FloatingActionButton fab_home;
    private ProgressBar id_progressBar_home;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tipo = getArguments().get("tipo").toString();
        token = getArguments().get("token").toString();
        nome = getArguments().get("nome").toString();
        email = getArguments().get("email").toString();

        id_recyclerView_home = view.findViewById(R.id.id_recyclerView_home);
        fab_home = view.findViewById(R.id.fab_home);
        id_progressBar_home = view.findViewById(R.id.id_progressBar_home);

        id_progressBar_home.setVisibility(View.INVISIBLE);

        fab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NovaFraseActivity.class)
                        .putExtra("tipo", tipo)
                        .putExtra("email", email)
                        .putExtra("nome", nome)
                        .putExtra("token", token));
                getActivity().finish();
            }
        });

        getFrasesOfFirebase();

        eventTouch(id_recyclerView_home);

        toAnotherFragment();

        return view;
    }

    private void toAnotherFragment(){
        new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.id_menu_home) {
                    toFrameLayout(new HomeFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_perfil) {
                    toFrameLayout(new PerfilFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_convite) {
                    toFrameLayout(new ConviteFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_pedidos) {
                    toFrameLayout(new PedidosFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_intercessao) {
                    toFrameLayout(new IntercessaoFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_fale_conosco) {

                } else if (id == R.id.id_menu_sobre) {
                    toFrameLayout(new SobreFragment(), email, token, tipo, nome);
                } else if(id == R.id.id_menu_logout){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
                return true;
            }
        };
    }

    private void toFrameLayout(Fragment fragmentLayout, String email, String token, String tipo, String nome){
        FragmentTransaction fragment = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("token", token);
        bundle.putString("tipo", tipo);
        bundle.putString("nome", nome);
        fragmentLayout.setArguments(bundle);
        fragment.replace(R.id.frameLayout_container, fragmentLayout).commit();
    }

    private void eventTouch(RecyclerView view){
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), view,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                NovaFraseDTO dto = dtos.get(position);
                                startActivity(new Intent(getActivity(), FrasesDoDiaActivity.class)
                                        .putExtra("frase", dto.getFrase())
                                        .putExtra("cap_verc", cap_verc)
                                        .putExtra("tipo", tipo)
                                        .putExtra("email", email)
                                        .putExtra("nome", nome)
                                        .putExtra("token", token));
                                getActivity().finish();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
                            }
                        }));
    }

    private void getFrasesOfFirebase(){
        frases.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dtos = new ArrayList<>();
                id_progressBar_home.setVisibility(View.VISIBLE);
                for(DataSnapshot dS: dataSnapshot.getChildren()){
                    NovaFraseDTO dto = new NovaFraseDTO();
                    dto.setFrase(dS.getValue(NovaFraseDTO.class).getFrase());
                    dto.setData(dS.getValue(NovaFraseDTO.class).getData());
                    dtos.add(dto);
                    ids.add(dS.getKey());
                }
                id_progressBar_home.setVisibility(View.INVISIBLE);
                //config adapter
                HomeAdapter adapter = new HomeAdapter(ids, dtos);

                //config recyclerView
                RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
                id_recyclerView_home.setLayoutManager(manager);
                id_recyclerView_home.setHasFixedSize(true);

                id_recyclerView_home.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showAlertDialog("Erro", databaseError.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }

    private void showAlertDialog(String title, String message, int icon){
        if(getActivity() != null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
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
    }

}
