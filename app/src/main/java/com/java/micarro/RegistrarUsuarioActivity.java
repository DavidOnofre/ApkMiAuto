package com.java.micarro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.jetbrains.annotations.NotNull;

import static com.java.micarro.Constantes.INGRESAR_UNA_PASSWORD;
import static com.java.micarro.Constantes.INGRESAR_UN_EMAIL;
import static com.java.micarro.Constantes.REGISTRO_EN_LINEA;

public class RegistrarUsuarioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
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
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_RegistrarUsuario_email);
        editTextPassword = findViewById(R.id.editText_RegistrarUsuario_Contrasena);
        buttonRegistrar = findViewById(R.id.button_RegistrarUsuario_Registrar);
        buttonRegresar = findViewById(R.id.button_RegistrarUsuario_Regresar);

        progressDialog = new ProgressDialog(this);

        buttonRegistrar.setOnClickListener(this);
        buttonRegresar.setOnClickListener(this);

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
                        Toast.makeText(RegistrarUsuarioActivity.this, "Ese usuario ya existe.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegistrarUsuarioActivity.this, "No se pudo registrar el email.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(RegistrarUsuarioActivity.this, "Usuario registrado correctamente, verifique su email para activación: " + editTextEmail.getText(), Toast.LENGTH_LONG).show();
                    limpiarCajas();
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
        editTextEmail.setText("");
        editTextPassword.setText("");
    }
}