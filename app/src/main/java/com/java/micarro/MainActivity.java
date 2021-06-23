package com.java.micarro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Persona;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.java.micarro.Constantes.ACTUALIZAR_KILOMETRAJE;
import static com.java.micarro.Constantes.BIENVENIDO;
import static com.java.micarro.Constantes.CONSULTA_EN_LINEA;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.INGRESAR_UNA_PASSWORD;
import static com.java.micarro.Constantes.INGRESAR_UN_EMAIL;
import static com.java.micarro.Constantes.KILOMETRAJE_ACTUAL;
import static com.java.micarro.Constantes.REGISTRO_EN_LINEA;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.Constantes.SI;
import static com.java.micarro.Constantes.USUARIO_NO_EXISTE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Comun comun;
    private DatabaseReference databaseReference;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonIngresar;
    private Button buttonRegistrar;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private List<Persona> listaPersona = new ArrayList<Persona>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logueo);
        inicializarVariables();
    }

    /**
     * Método usado para inicializar variables
     */
    private void inicializarVariables() {
        comun = new Comun();
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.txtEmail);
        editTextPassword = findViewById(R.id.txtContrasena);
        buttonIngresar = findViewById(R.id.button_Ingresar);
        buttonRegistrar = findViewById(R.id.button_Registrar);

        progressDialog = new ProgressDialog(this);

        buttonRegistrar.setOnClickListener(this);
        buttonIngresar.setOnClickListener(this);

        databaseReference = comun.ObtenerDataBaseReference(this);

        listarDatos();  // kodigo
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Registrar:
                //registrarUsuario();
                registrarUsuarioDD();
                break;
            case R.id.button_Ingresar:
                loguearUsuario();
                break;
        }
    }

    private void registrarUsuarioDD() {
        Intent i = new Intent(getApplicationContext(), RegistrarUsuarioActivity.class);
        startActivity(i);
    }

    private void loguearUsuario() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validarCadena(email, INGRESAR_UN_EMAIL)) {
            return;
        }

        if (validarCadena(password, INGRESAR_UNA_PASSWORD)) {
            return;
        }

        mostrarProgresoDialogo();
        loguearUsuarioFirebase(email, password);
    }

    /**
     * Método usado para loguear el usuario.
     *
     * @param email    email
     * @param password password
     */
    private void loguearUsuarioFirebase(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, BIENVENIDO + editTextEmail.getText(), Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);
                    grabarSesion(email);
                    startActivity(i);

                } else {
                    Toast.makeText(MainActivity.this, USUARIO_NO_EXISTE + editTextEmail.getText(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void grabarSesion(String email) {

        Persona p = recuperarCliente(email);

        SharedPreferences prefs = getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IDENTIFICACION_SESION, p.getUid());
        editor.putString(KILOMETRAJE_ACTUAL, p.getAuto().get(0).getKilometraje());
        editor.putString(ACTUALIZAR_KILOMETRAJE, SI);
        editor.commit();


    }

    private Persona recuperarCliente(String email) {
        listarDatos();  // kodigo

        Persona salida = new Persona();
        for (Persona persona : listaPersona) {
            if (email.equals(persona.getCorreo())) {

                salida = persona;

            }
        }

        return salida;
    }


    private void registrarUsuario() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validarCadena(email, INGRESAR_UN_EMAIL)) {
            return;
        }

        if (validarCadena(password, INGRESAR_UNA_PASSWORD)) {
            return;
        }

        mostrarProgresoDialogo();
        registrarUsuarioFirebase(email, password);
    }

    /**
     * Método usado para crear un nuevo usuario.
     *
     * @param email    email
     * @param password password
     */
    private void registrarUsuarioFirebase(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    enviarEmailVerificacion();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) { // si se presenta una colisión.
                        Toast.makeText(MainActivity.this, "Ese usuario ya existe.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No se pudo registrar el email.", Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Método usado para envíar email de activación.
     */
    private void enviarEmailVerificacion() {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Usuario registrado correctamente, verifique su email para activación: " + editTextEmail.getText(), Toast.LENGTH_LONG).show();
                    limpiarCajas();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Método usado para limpiar cajas.
     */
    private void limpiarCajas() {
        editTextEmail.setText("");
        editTextPassword.setText("");
    }

    /**
     * Método usado para validar que el texto en una caja de texto no sea vacía.
     *
     * @param cadena string a validar
     * @return bandera de la validación.
     */
    private boolean validarCadena(String cadena, String mensaje) {
        if (TextUtils.isEmpty(cadena)) {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    /**
     * Método usado para mostrar el progreso de la barra de dialogo.
     */
    private void mostrarProgresoDialogo() {
        progressDialog.setMessage(CONSULTA_EN_LINEA);
        progressDialog.show();
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
