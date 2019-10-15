package com.app_oracao.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.app_oracao.App;
import com.app_oracao.R;
import com.app_oracao.fragments.ConviteFragment;
import com.app_oracao.fragments.HomeFragment;
import com.app_oracao.fragments.IntercessaoFragment;
import com.app_oracao.fragments.PedidosFragment;
import com.app_oracao.fragments.PerfilFragment;
import com.app_oracao.fragments.SobreFragment;
import com.app_oracao.servicies.CreateDBService;
import com.google.firebase.auth.FirebaseAuth;

public class AppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView id_nav_nome, id_nav_email;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Menu menu;
    private MenuItem menuItem;
    private String tipo, email, nome, token;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        id_nav_nome = navigationView.getHeaderView(0).findViewById(R.id.id_nav_nome);
        id_nav_email = navigationView.getHeaderView(0).findViewById(R.id.id_nav_email);

        menu = navigationView.getMenu();

        getValuesOfIntent();

        toFrameLayout(new HomeFragment(), email, token, tipo, nome);

        id_nav_nome.setText(nome);
        id_nav_email.setText(email);

        verifyTipo();

        intent = new Intent(this, CreateDBService.class).setAction(App.CREATE_DB_BROADCAST_RECEIVER);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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
            startActivity(Intent.createChooser(
                    new Intent(Intent.ACTION_SEND)
                            .setType("message/rfc822")
                            .putExtra(Intent.EXTRA_EMAIL, new String[]{"abc@gmail.com"}),
                    "Selecione o app de email.")
                    );
        } else if (id == R.id.id_menu_sobre) {
            toFrameLayout(new SobreFragment());
        } else if(id == R.id.id_menu_logout){
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(intent);
    }



    private void toFrameLayout(Fragment fragmentLayout, String email, String token, String tipo, String nome){
        FragmentTransaction fragment = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("token", token);
        bundle.putString("tipo", tipo);
        bundle.putString("nome", nome);
        fragmentLayout.setArguments(bundle);
        fragment.replace(R.id.frameLayout_container, fragmentLayout).commit();
    }

    private void toFrameLayout(Fragment fragmentLayout){
        FragmentTransaction fragment = getSupportFragmentManager().beginTransaction();

        fragment.replace(R.id.frameLayout_container, fragmentLayout).commit();
    }

    private void getValuesOfIntent(){
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        nome = intent.getStringExtra("nome");
        tipo = intent.getStringExtra("tipo");
        token = intent.getStringExtra("token");
    }

    private void verifyTipo(){
        if(tipo.equalsIgnoreCase("ROLE_COMUM")){
            menuItem = menu.findItem(R.id.id_menu_convite);
            menuItem.setEnabled(false);
            menuItem.setVisible(false);
        }
    }

}
