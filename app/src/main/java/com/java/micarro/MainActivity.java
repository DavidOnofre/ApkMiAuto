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
import com.java.micarro.model.Auto;
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.java.micarro.Constantes.ACTUALIZAR_KILOMETRAJE;
import static com.java.micarro.Constantes.CONTRASENA_10_DIGITOS;
import static com.java.micarro.Constantes.CONTRASENA_INCORRECTA;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_ACTUAL;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.Constantes.SI;

public class MainActivity extends AppCompatActivity {

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
                    String kilometraje = "";

                    Boolean banderaLogin = false;
                    for (Persona persona : listaPersona) {
                        if (identificacion.equals(persona.getUid())) {
                            banderaLogin = true;

                            Auto auto = persona.getAuto().get(0);
                            kilometraje = auto.getKilometraje();
                        }
                    }

                    if (banderaLogin) {

                        Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);

                        SharedPreferences prefs = getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(IDENTIFICACION_SESION, identificacion);
                        editor.putString(KILOMETRAJE_ACTUAL, kilometraje);
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
