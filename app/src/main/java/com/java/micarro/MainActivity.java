package com.java.micarro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";

    //constantes de sesión.
    public static final String IDENTIFICACION_SESION = "identificacionSesion";
    public static final String ACTUALIZAR_KILOMETRAJE = "actualizarKilometraje";
    public static final String SI = "SI";
    public static final String CONTRASENA_10_DIGITOS = "Longitud de contraseña 10 dígitos";
    public static final String CONTRASENA_INCORRECTA = "Usuario o Contraseña Incorrectos.";



    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Persona> listaPersona = new ArrayList<Persona>();

    private EditText txtContrasena;
    private TextInputLayout impContrasena;
    private Button btnIniciar;
    private Boolean banderaLongitudContrasena = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logueo);

        txtContrasena = (EditText) findViewById(R.id.txtContrasena);
        impContrasena = (TextInputLayout) findViewById(R.id.impContrasena);

        btnIniciar = (Button) findViewById(R.id.btnIniciar);

        inicializarFireBase();
        listarDatos();

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern p = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
                if (p.matcher(txtContrasena.getText().toString()).matches() == false) {
                    impContrasena.setError(CONTRASENA_10_DIGITOS);
                    banderaLongitudContrasena = false;
                } else {
                    banderaLongitudContrasena = true;
                    impContrasena.setError(null);
                }

                if (banderaLongitudContrasena) {

                    String identificacion = txtContrasena.getText().toString();

                    Boolean banderaLogin = false;
                    for (Persona persona : listaPersona) {
                        if (identificacion.equals(persona.getUid())) {
                            banderaLogin = true;
                        }
                    }

                    if (banderaLogin) {

                        Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);

                        SharedPreferences prefs = getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(IDENTIFICACION_SESION, identificacion);
                        editor.putString(ACTUALIZAR_KILOMETRAJE, SI);
                        editor.commit();

                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(), CONTRASENA_INCORRECTA, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Inicializar llamados a firebase.
     */
    private void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /**
     * Método usado para listar datos de la bdd.
     */
    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPersona.clear();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);
                    listaPersona.add(p);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
