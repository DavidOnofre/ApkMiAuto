package com.java.micarro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import static com.java.micarro.Constantes.CADENA_VACIA;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.ui.slideshow.SlideshowFragment.NOTIFICACION_ID;

public class NotificacionActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.cancel(NOTIFICACION_ID);

        String identificacion = obtenerValorSesion(IDENTIFICACION_SESION);

        button = findViewById(R.id.button_notificacion_enviar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Método usado para cargar una cadena de sesión.
     *
     * @param valorSesion nombre de la variable de sesión que se quiere recuperar.
     * @return valor de la variable a recuperar de la sesión.
     */
    private String obtenerValorSesion(String valorSesion) {
        String salida = CADENA_VACIA;
        SharedPreferences prefs = getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(valorSesion, CADENA_VACIA);
        return salida;
    }
}