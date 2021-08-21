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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.model.Persona;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.java.micarro.Constantes.ACTUALIZAR_KILOMETRAJE;
import static com.java.micarro.Constantes.APELLIDO_SESION;
import static com.java.micarro.Constantes.BIENVENIDO;
import static com.java.micarro.Constantes.CONSULTA_EN_LINEA;
import static com.java.micarro.Constantes.CORREO_SESION;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.INGRESAR_UNA_PASSWORD;
import static com.java.micarro.Constantes.INGRESAR_UN_EMAIL;
import static com.java.micarro.Constantes.KILOMETRAJE_ACEITE_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_BATERIA_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_ELECTRICIDAD_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_GASOLINA_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_INICIAL_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_LLANTAS_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_SESION;
import static com.java.micarro.Constantes.MARCA_SESION;
import static com.java.micarro.Constantes.MODELO_SESION;
import static com.java.micarro.Constantes.NOMBRE_SESION;
import static com.java.micarro.Constantes.PLACA_SESION;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.Constantes.SI;
import static com.java.micarro.Constantes.TELEFONO_SESION;
import static com.java.micarro.Constantes.USUARIO_NO_EXISTE;
import static com.java.micarro.Constantes.VERIFIQUE_CORREO;

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
        listarDatos();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Registrar:
                irRegistrarUsuario();
                break;
            case R.id.button_Ingresar:
                loguearUsuario();
                break;
        }
    }

    /**
     * Método usado para ir hacía la página registrar un nuevo usuario.
     */
    private void irRegistrarUsuario() {
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

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user.isEmailVerified()) {

                        Toast.makeText(MainActivity.this, BIENVENIDO + editTextEmail.getText(), Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);
                        grabarSesion(email);
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, VERIFIQUE_CORREO, Toast.LENGTH_LONG).show();
                        limpiarCajas();
                    }

                } else {
                    Toast.makeText(MainActivity.this, USUARIO_NO_EXISTE + editTextEmail.getText(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Método usado para grabar datos en sesión.
     *
     * @param email email
     */
    private void grabarSesion(String email) {
        Persona p = recuperarCliente(email);
        SharedPreferences prefs = getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //grabar entidad Persona
        editor.putString(IDENTIFICACION_SESION, p.getUid());
        editor.putString(NOMBRE_SESION, p.getNombre());
        editor.putString(APELLIDO_SESION, p.getApellido());
        editor.putString(TELEFONO_SESION, p.getTelefono());
        editor.putString(CORREO_SESION, p.getCorreo());

        // auto
        editor.putString(PLACA_SESION, p.getAuto().get(0).getPlaca());
        editor.putString(MARCA_SESION, p.getAuto().get(0).getMarca());
        editor.putString(MODELO_SESION, p.getAuto().get(0).getModelo());
        editor.putString(KILOMETRAJE_INICIAL_SESION, p.getAuto().get(0).getKilometrajeInicial());
        editor.putString(KILOMETRAJE_SESION, p.getAuto().get(0).getKilometraje());
        editor.putString(KILOMETRAJE_ACEITE_SESION, p.getAuto().get(0).getKilometrajeAceite());
        editor.putString(KILOMETRAJE_BATERIA_SESION, p.getAuto().get(0).getKilometrajeBateria());
        editor.putString(KILOMETRAJE_ELECTRICIDAD_SESION, p.getAuto().get(0).getKilometrajeElectricidad());
        editor.putString(KILOMETRAJE_GASOLINA_SESION, p.getAuto().get(0).getKilometrajeGasolina());
        editor.putString(KILOMETRAJE_LLANTAS_SESION, p.getAuto().get(0).getKilometrajeLlantas());
        //auto

        //grabar entidad Persona

        editor.putString(ACTUALIZAR_KILOMETRAJE, SI);
        editor.commit();
    }

    private Persona recuperarCliente(String email) {

        Persona salida = new Persona();
        for (Persona persona : listaPersona) {
            if (email.equals(persona.getCorreo())) {
                salida = persona;
            }
        }

        return salida;
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
