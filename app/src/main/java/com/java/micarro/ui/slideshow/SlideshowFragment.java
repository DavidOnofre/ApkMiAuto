package com.java.micarro.ui.slideshow;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.R;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Persona;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

    public static final String IDENTIFICACION_SESION = "identificacionSesion";
    public static final String CADENA_VACIA = "";
    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String PERSONA = "Persona";

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

    private Handler handler;
    private Boolean activo;
    private int contador;

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

        if (v.getId() == R.id.button_consumibles_aceite) {

            if (!activo) {
                Thread hilo = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (contador <= 100) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    textViewKilometrajeActual.setText(kilometrajeActual + " km.");

                                    textViewPorcentaje5000.setText(contador + " %");
                                    textViewPorcentaje10000.setText(contador + " %");
                                    textViewPorcentaje15000.setText(contador + " %");
                                    textViewPorcentaje20000.setText(contador + " %");
                                    textViewPorcentaje30000.setText(contador + " %");
                                    textViewPorcentaje55000.setText(contador + " %");
                                    textViewPorcentaje60000.setText(contador + " %");


                                    progressBar5000.setProgress(kilometrajeActual/5000);

                                    progressBar10000.setProgress(contador);
                                    progressBar15000.setProgress(contador);
                                    progressBar20000.setProgress(contador);
                                    progressBar30000.setProgress(contador);
                                    progressBar55000.setProgress(contador);
                                    progressBar60000.setProgress(contador);
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
                    }
                });
                hilo.start();
            }
        }
    }

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
        String salida = CADENA_VACIA;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(valorSesion, CADENA_VACIA);
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
