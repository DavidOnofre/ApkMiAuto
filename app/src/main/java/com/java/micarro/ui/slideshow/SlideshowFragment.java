package com.java.micarro.ui.slideshow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.NotificacionActivity;
import com.java.micarro.R;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Persona;

import static com.java.micarro.Constantes.ESPACIO_VACIO;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.KM;
import static com.java.micarro.Constantes.MANTENIMIENTO_NECESARIO;
import static com.java.micarro.Constantes.PERSONA;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.Constantes.SIGNO_PORCENTAJE;
import static com.java.micarro.Constantes.USTED_YA_REALIZO_EL_CAMBIO_SI_NO_EL_COSTO_DEL_CAMBIO_FUE;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

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

    private Button button;

    //variables necesarias para la notificación.
    private PendingIntent pendingIntent;
    private static final String CHANNEL_ID = "NOTIFICACION";
    public static final int NOTIFICACION_ID = 0;

    private Handler handler;
    private Boolean activo;

    int contador;

    private String identificacion = "";
    private Persona persona;
    private FirebaseDatabase firebaseDatabase;
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

        identificacion = obtenerValorSesion(IDENTIFICACION_SESION);
        inicializarFireBase();
        inicializarVariables(root);

        cargarEntidadGlobalPersona();

        button.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {

        final int kilometrajeActual = obtenerKilometraje();
        textViewKilometrajeActual.setText(kilometrajeActual + KM);

        if (v.getId() == R.id.button_consumibles_aceite) {

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


    }

    /**
     * Método usado para orquestar métodos necesarios para notificar en pantalla.
     */
    private void ejecutarNotificacion(int banderaKilometraje) {
        setPendingIntent();
        crearNotificaionChannel();
        crearNotificaion(banderaKilometraje);
    }

    private void setPendingIntent() {
        Intent intent = new Intent(getActivity().getApplicationContext(), NotificacionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity().getApplicationContext());
        stackBuilder.addParentStack(NotificacionActivity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, pendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Método usado para crear el canal de notificación, necesario por la versión actual de android.
     */
    private void crearNotificaionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Método usado para crear notificación con los datos para ver en pantalla.
     */
    private void crearNotificaion(int banderaKilometraje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_commute_24);
        builder.setContentTitle(MANTENIMIENTO_NECESARIO + banderaKilometraje);
        builder.setContentText(USTED_YA_REALIZO_EL_CAMBIO_SI_NO_EL_COSTO_DEL_CAMBIO_FUE);
        builder.setColor(Color.GREEN);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000); // luz en el teléfono al notificar.
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity().getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
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
                Thread.sleep(100);
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

        //mostrar notificación cunado el % sea mayor a 80%
        if (salida >= 80) {
            ejecutarNotificacion(banderaKilometraje);
        }
        return salida;
    }

    /**
     * Método usado para inicializar variables.
     *
     * @param root
     */

    private void inicializarVariables(View root) {
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

        button = (Button) root.findViewById(R.id.button_consumibles_aceite);

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
        int salida;

        Auto auto = persona.getAuto().get(0);
        salida = obtenerKilometrajeBaseDatos();

        return salida;
    }

    /**
     * Método usado para obener el kilometraje desade la base de datos.
     *
     * @return
     */
    private int obtenerKilometrajeBaseDatos() {
        Auto auto = persona.getAuto().get(0);
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        int kilometrajeActualizado = kilometrajeActual;
        return kilometrajeActualizado;
    }

    /**
     * Método usado para cargar una cadena de sesión.
     *
     * @param valorSesion nombre de la variable de sesión que se quiere recuperar.
     * @return valor de la variable a recuperar de la sesión.
     */
    private String obtenerValorSesion(String valorSesion) {
        String salida = ESPACIO_VACIO;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(valorSesion, ESPACIO_VACIO);
        return salida;
    }

    /**
     * Método usado para instanciar api firebase.
     */
    private void inicializarFireBase() {
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /**
     * Método usado para cargar variable global persona.
     */
    private void cargarEntidadGlobalPersona() {
        final String identificacion = obtenerValorSesion(IDENTIFICACION_SESION);

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (identificacion.equals(p.getUid())) {
                        SlideshowFragment.this.persona = p;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
