package com.java.micarro.ui.slideshow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DatabaseReference;
import com.java.micarro.Comun;
import com.java.micarro.NotificacionActivity;
import com.java.micarro.R;
import com.java.micarro.model.Persona;

import static com.java.micarro.Constantes.CHANNEL_ID;
import static com.java.micarro.Constantes.KILOMETRAJE_ACTUAL;
import static com.java.micarro.Constantes.KM;
import static com.java.micarro.Constantes.MANTENIMIENTO_NECESARIO;
import static com.java.micarro.Constantes.NOTIFICACION;
import static com.java.micarro.Constantes.NOTIFICACION_ID;
import static com.java.micarro.Constantes.SIGNO_PORCENTAJE;
import static com.java.micarro.Constantes.USTED_YA_REALIZO_EL_CAMBIO_SI_NO_EL_COSTO_DEL_CAMBIO_FUE;

public class SlideshowFragment extends Fragment {

    private Comun comun;
    private SlideshowViewModel slideshowViewModel;

    private ProgressBar progressBar5000;
    private ProgressBar progressBar10000;
    private ProgressBar progressBar15000;
    private ProgressBar progressBar20000;
    private ProgressBar progressBar30000;
    private ProgressBar progressBar55000;
    private ProgressBar progressBar60000;

    private TextView textViewPorcentaje5000;
    private TextView textViewPorcentaje10000;
    private TextView textViewPorcentaje15000;
    private TextView textViewPorcentaje20000;
    private TextView textViewPorcentaje30000;
    private TextView textViewPorcentaje55000;
    private TextView textViewPorcentaje60000;

    private TextView textViewKilometrajeActual;

    private Handler handler;
    private Boolean activo;

    int contador;

    private String identificacion = "";
    private Persona persona;

    private DatabaseReference databaseReference;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);

        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        inicializarVariables(root);

        calcularConsumibles();

        return root;
    }

    private void calcularConsumibles() {
        final int kilometrajeActual = obtenerKilometraje();
        textViewKilometrajeActual.setText(kilometrajeActual + KM);

        if (!activo) {
            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    graficarProgessBar(kilometrajeActual, 5000);
                    graficarProgessBar(kilometrajeActual, 10000);
                    graficarProgessBar(kilometrajeActual, 15000);
                    graficarProgessBar(kilometrajeActual, 20000);
                    graficarProgessBar(kilometrajeActual, 30000);
                    graficarProgessBar(kilometrajeActual, 55000);
                    graficarProgessBar(kilometrajeActual, 60000);
                }
            });
            hilo.start();
        }
    }



    /**
     * Método usado para gráficar el progreso de la barra progressBar para cada consumible.
     *
     * @param kilometraje
     * @param banderaKilometraje
     */
    private void graficarProgessBar(int kilometraje, final int banderaKilometraje) {
        while (contador <= obtenerLimiteContador(kilometraje, banderaKilometraje)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (banderaKilometraje) {
                        case 5000:
                            textViewPorcentaje5000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar5000.setProgress(contador);
                            break;
                        case 10000:
                            textViewPorcentaje10000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar10000.setProgress(contador);
                            break;
                        case 15000:
                            textViewPorcentaje15000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar15000.setProgress(contador);
                            break;
                        case 20000:
                            textViewPorcentaje20000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar20000.setProgress(contador);
                            break;
                        case 30000:
                            textViewPorcentaje30000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar30000.setProgress(contador);
                            break;
                        case 55000:
                            textViewPorcentaje55000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar55000.setProgress(contador);
                            break;
                        case 60000:
                            textViewPorcentaje60000.setText(contador + SIGNO_PORCENTAJE);
                            progressBar60000.setProgress(contador);
                            break;
                    }
                }
            });
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            contador++;
            activo = true;
        }
        contador = 0;
    }

    /**
     * Método usado para obtener el kilometraje límite del contador.
     *
     * @param kilometraje
     * @param banderaKilometraje
     * @return
     */
    private int obtenerLimiteContador(int kilometraje, int banderaKilometraje) {
        int salida = 0;
        salida = (kilometraje * 100) / banderaKilometraje;

        //mostrar notificación cuanado el % sea mayor a 80%
        if (salida >= 80) {
        }
        return salida;
    }

    /**
     * Método usado para inicializar variables.
     *
     * @param root
     */

    private void inicializarVariables(View root) {
        comun = new Comun();
        databaseReference = comun.ObtenerDataBaseReference(getActivity());

        handler = new Handler();

        progressBar5000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_5000);
        progressBar10000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_10000);
        progressBar15000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_15000);
        progressBar20000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_20000);
        progressBar30000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_30000);
        progressBar55000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_55000);
        progressBar60000 = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_60000);

        textViewPorcentaje5000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_5000);
        textViewPorcentaje10000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_10000);
        textViewPorcentaje15000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_15000);
        textViewPorcentaje20000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_20000);
        textViewPorcentaje30000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_30000);
        textViewPorcentaje55000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_55000);
        textViewPorcentaje60000 = (TextView) root.findViewById(R.id.textView_consumibles_porcentaje_60000);
        textViewKilometrajeActual = (TextView) root.findViewById(R.id.textView_consumibles_kilometraje_actual);

        activo = false;
        contador = 0;
        persona = new Persona();
    }

    /**
     * Método usado para cargar kilometraje.
     *
     * @return
     */
    private int obtenerKilometraje() {
        String kilometraje = comun.obtenerValorSesion(getActivity(), KILOMETRAJE_ACTUAL);
        return Integer.parseInt(kilometraje);
    }
}
