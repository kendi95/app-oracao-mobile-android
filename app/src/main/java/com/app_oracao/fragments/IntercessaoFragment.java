package com.app_oracao.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app_oracao.R;
import com.app_oracao.activities.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntercessaoFragment extends Fragment {

    private String tipo, token, email, nome;
    private BottomNavigationView bottomNavigationView;

    public IntercessaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intercessao, container, false);

        tipo = getArguments().get("tipo").toString();
        token = getArguments().get("token").toString();
        email = getArguments().get("email").toString();
        nome = getArguments().get("nome").toString();

        bottomNavigationView = view.findViewById(R.id.id_bottomNavigationView);

        toFrameLayout(new IntercessoesFragment(), email, token, tipo, nome);
        onNavigationBottom();
        toAnotherFragment();

        return view;
    }


    private void toFrameLayout(Fragment fragmentLayout, String email, String token, String tipo, String nome){
        FragmentTransaction fragment = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("token", token);
        bundle.putString("tipo", tipo);
        bundle.putString("nome", nome);
        fragmentLayout.setArguments(bundle);
        fragment.replace(R.id.frameLayout_container_intercessao, fragmentLayout).commit();
    }

    private void toFragmentLayoutMain(Fragment fragmentLayout, String email, String token, String tipo, String nome){
        FragmentTransaction fragment = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("token", token);
        bundle.putString("tipo", tipo);
        bundle.putString("nome", nome);
        fragmentLayout.setArguments(bundle);
        fragment.replace(R.id.frameLayout_container, fragmentLayout).commit();
    }

    private void toAnotherFragment(){
        new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.id_menu_home) {
                    toFragmentLayoutMain(new HomeFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_perfil) {
                    toFragmentLayoutMain(new PerfilFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_convite) {
                    toFragmentLayoutMain(new ConviteFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_pedidos) {
                    toFragmentLayoutMain(new PedidosFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_menu_intercessao) {
                    toFragmentLayoutMain(new IntercessaoFragment(), email, token, tipo, nome);
                } else if (id == R.id.id_fale_conosco) {

                } else if (id == R.id.id_menu_sobre) {
                    toFragmentLayoutMain(new SobreFragment(), email, token, tipo, nome);
                } else if(id == R.id.id_menu_logout){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
                return true;
            }
        };
    }

    private void onNavigationBottom(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch(id){
                    case R.id.bottom_nav_intercessoes:
                        toFrameLayout(new IntercessoesFragment(), email, token, tipo, nome);
                        break;
                    case R.id.bottom_nav_minha_agenda:
                        toFrameLayout(new MinhaAgendaFragment(), email, token, tipo, nome);
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch(id){
                    case R.id.bottom_nav_intercessoes:
                        toFrameLayout(new IntercessoesFragment(), email, token, tipo, nome);
                        break;
                    case R.id.bottom_nav_minha_agenda:
                        toFrameLayout(new MinhaAgendaFragment(), email, token, tipo, nome);
                        break;
                }
            }
        });
    }
}
