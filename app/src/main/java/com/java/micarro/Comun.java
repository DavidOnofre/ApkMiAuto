package com.java.micarro;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.model.Persona;

import static com.java.micarro.Constantes.ESPACIO_VACIO;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.PERSONA;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;

public class Comun {

    private FirebaseDatabase firebaseDatabase;

    /**
     * Método usado para obtener la referencia a la base de datos de firebase.
     *
     * @param activity
     * @return
     */
    public DatabaseReference ObtenerDataBaseReference(FragmentActivity activity) {
        DatabaseReference databaseReference;

        FirebaseApp.initializeApp(activity);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        return databaseReference;
    }

    /**
     * Método usado para validar que el kilometraje ingresado sea un número.
     *
     * @param cadena a validar si es número
     * @return Bandera que indica que la validación es exitosa.
     */
    public boolean esNumero(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }

    /**
     * Método usado para cargar una cadena de sesión.
     *
     * @param valorSesion nombre de la variable de sesión que se quiere recuperar.
     * @return valor de la variable a recuperar de la sesión.
     */
    public String obtenerValorSesion(FragmentActivity activity, String valorSesion) {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        String salida = prefs.getString(valorSesion, ESPACIO_VACIO);
        return salida;
    }
}
