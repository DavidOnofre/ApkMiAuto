package com.java.micarro;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Mantenimiento;
import com.java.micarro.model.Persona;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.java.micarro.Constantes.CERO;
import static com.java.micarro.Constantes.INGRESAR_UNA_IDENTIFICACION;
import static com.java.micarro.Constantes.INGRESAR_UNA_MARCA;
import static com.java.micarro.Constantes.INGRESAR_UNA_PASSWORD;
import static com.java.micarro.Constantes.INGRESAR_UNA_PLACA;
import static com.java.micarro.Constantes.INGRESAR_UN_APELLIDO;
import static com.java.micarro.Constantes.INGRESAR_UN_EMAIL;
import static com.java.micarro.Constantes.INGRESAR_UN_KILOMETRAJE;
import static com.java.micarro.Constantes.INGRESAR_UN_MODELO;
import static com.java.micarro.Constantes.INGRESAR_UN_NOMBRE;
import static com.java.micarro.Constantes.INGRESAR_UN_TELEFONO;
import static com.java.micarro.Constantes.PERSONA;
import static com.java.micarro.Constantes.REGISTRO_EN_LINEA;

public class RegistrarUsuarioActivity extends AppCompatActivity implements View.OnClickListener {

    private Comun comun;
    private DatabaseReference databaseReference;

    private EditText editTextIdentificacion;
    private EditText editTextNombre;
    private EditText editTextApellido;
    private EditText editTextTelefono;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private EditText editTextPlaca;
    private EditText editTextMarca;
    private EditText editTextModelo;
    private EditText editTextKilometraje;

    private Button buttonRegistrar;
    private Button buttonRegresar;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        inicializarVariables();
    }

    /**
     * Método usado para inicializar variables
     */
    private void inicializarVariables() {
        comun = new Comun();
        firebaseAuth = FirebaseAuth.getInstance();

        editTextIdentificacion = findViewById(R.id.editText_RegistrarUsuario_Identificacion);
        editTextNombre = findViewById(R.id.editText_RegistrarUsuario_Nombre);
        editTextApellido = findViewById(R.id.editText_RegistrarUsuario_Apellido);
        editTextTelefono = findViewById(R.id.editText_RegistrarUsuario_Telefono);

        editTextEmail = findViewById(R.id.editText_RegistrarUsuario_Email);
        editTextPassword = findViewById(R.id.editText_RegistrarUsuario_Contrasena);

        editTextPlaca = findViewById(R.id.editText_RegistrarUsuario_Placa);
        editTextMarca = findViewById(R.id.editText_RegistrarUsuario_Marca);
        editTextModelo = findViewById(R.id.editText_RegistrarUsuario_Modelo);
        editTextKilometraje = findViewById(R.id.editText_RegistrarUsuario_Kilometraje);

        buttonRegistrar = findViewById(R.id.button_RegistrarUsuario_Registrar);
        buttonRegresar = findViewById(R.id.button_RegistrarUsuario_Regresar);

        progressDialog = new ProgressDialog(this);

        buttonRegistrar.setOnClickListener(this);
        buttonRegresar.setOnClickListener(this);

        databaseReference = comun.ObtenerDataBaseReference(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_RegistrarUsuario_Registrar:
                registrarUsuario();
                break;
            case R.id.button_RegistrarUsuario_Regresar:
                regresar();
                break;
        }
    }

    private void regresar() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void registrarUsuario() {

        String identificacion = editTextIdentificacion.getText().toString().trim();
        String nombre = editTextNombre.getText().toString().trim();
        String apellido = editTextApellido.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        String placa = editTextPlaca.getText().toString().trim();
        String marca = editTextMarca.getText().toString().trim();
        String modelo = editTextModelo.getText().toString().trim();
        String kilometraje = editTextKilometraje.getText().toString().trim();

        Persona persona = new Persona();
        persona.setUid(identificacion);
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setTelefono(telefono);
        persona.setCorreo(email);

        Auto auto = new Auto();
        auto.setPlaca(placa);
        auto.setModelo(modelo);
        auto.setMarca(marca);
        auto.setKilometraje(kilometraje);
        auto.setKilometrajeAceite(CERO);
        auto.setKilometrajeBateria(CERO);
        auto.setKilometrajeElectricidad(CERO);
        auto.setKilometrajeGasolina(CERO);
        auto.setKilometrajeLlantas(CERO);

        List<Auto> autos = new ArrayList<>();
        autos.add(auto);

        List<Mantenimiento> mantenimientos = new ArrayList<>();
        mantenimientos.add(new Mantenimiento());

        persona.setAuto(autos);
        persona.setMantenimiento(mantenimientos);


        if (validarCadena(identificacion, INGRESAR_UNA_IDENTIFICACION)) {
            return;
        }

        if (validarCadena(nombre, INGRESAR_UN_NOMBRE)) {
            return;
        }

        if (validarCadena(apellido, INGRESAR_UN_APELLIDO)) {
            return;
        }

        if (validarCadena(telefono, INGRESAR_UN_TELEFONO)) {
            return;
        }

        if (validarCadena(email, INGRESAR_UN_EMAIL)) {
            return;
        }

        if (validarCadena(password, INGRESAR_UNA_PASSWORD)) {
            return;
        }

        if (validarCadena(placa, INGRESAR_UNA_PLACA)) {
            return;
        }

        if (validarCadena(marca, INGRESAR_UNA_MARCA)) {
            return;
        }

        if (validarCadena(modelo, INGRESAR_UN_MODELO)) {
            return;
        }

        if (validarCadena(kilometraje, INGRESAR_UN_KILOMETRAJE)) {
            return;
        }

        mostrarProgresoDialogo();
        registrarUsuarioFirebase(email, password, persona);
    }

    /**
     * Método usado para crear un nuevo usuario.
     *
     * @param email    email
     * @param password password
     */
    private void registrarUsuarioFirebase(String email, String password, Persona persona) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    enviarEmailVerificacion();

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) { // si se presenta una colisión.
                        Toast.makeText(RegistrarUsuarioActivity.this, "Ese usuario ya existe.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegistrarUsuarioActivity.this, "No se pudo registrar el email.", Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.dismiss();
            }
        });
        registrarAuto(persona);
        limpiarCajas();
    }

    /**
     * Método usado para registrar el vehículo inicial del cliente que se acaba de registrar.
     *
     * @param p entidad con los datos del cliente
     */
    private void registrarAuto(Persona p) {
        databaseReference.child(PERSONA).child(p.getUid()).setValue(p);
    }

    /**
     * Método usado para envíar email de activación.
     */
    private void enviarEmailVerificacion() {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    regresar();
                    Toast.makeText(RegistrarUsuarioActivity.this, "Usuario registrado correctamente, verifique su email para activación: " + editTextEmail.getText(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegistrarUsuarioActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
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
        progressDialog.setMessage(REGISTRO_EN_LINEA);
        progressDialog.show();
    }

    /**
     * Método usado para limpiar cajas.
     */
    private void limpiarCajas() {
        editTextIdentificacion.setText("");
        editTextNombre.setText("");
        editTextApellido.setText("");
        editTextTelefono.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextPlaca.setText("");
        editTextMarca.setText("");
        editTextModelo.setText("");
        editTextKilometraje.setText("");
    }
}