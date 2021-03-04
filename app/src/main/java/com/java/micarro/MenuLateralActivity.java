package com.java.micarro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.model.Persona;

public class MenuLateralActivity extends AppCompatActivity {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String DATO_01 = "dato01";
    public static final String CADENA_VACIA = "";
    public static final String PERSONA = "Persona";

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private TextView textViewUsuarioLogeado;
    private TextView textViewAutoLogeado;
    private String uid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lateral);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        uid = obtenerUid();
        inicializarFireBase();
        cargarCliente(uid, navigationView, navigationView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lateral, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * Método usado para recuperar el uid del usuario logeado.
     *
     * @return uid.
     */
    private String obtenerUid() {
        String salida = CADENA_VACIA;
        SharedPreferences prefs = this.getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(DATO_01, CADENA_VACIA);
        return salida;
    }

    /**
     * Método usado para cargar entidad persona, del usuaro logueado.
     *
     * @param uid identificador del cliente.
     * @return persona en sesión.
     */
    private void cargarCliente(String uid, View root, NavigationView navigationView) {
        final String usuarioLogeado = uid;
        final View r = root;
        final NavigationView view = navigationView;

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona persona = objDataSnapshot.getValue(Persona.class);

                    if (usuarioLogeado.equals(persona.getUid())) {

                        textViewUsuarioLogeado = view.getHeaderView(0).findViewById(R.id.textViewUsuarioLogeado);
                        textViewUsuarioLogeado.setText("Bienvenido: " + persona.getNombre() + " " + persona.getApellido());

                        textViewAutoLogeado = findViewById(R.id.textViewAutoLogeado);
                        textViewAutoLogeado.setText(persona.getAuto().get(0).getMarca() + " : " + persona.getAuto().get(0).getPlaca());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Método usado para instanciar api firebase.
     */
    private void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}