package com.java.micarro.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class HomeFragment extends Fragment {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String DATO_01 = "dato01";
    public static final String CADENA_VACIA = "";
    public static final String PERSONA = "Persona";
    public static final String MODIFICADO = "Modificado";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private HomeViewModel homeViewModel;

    private String uid = "";

    private TextView textViewPlaca;
    private TextView textViewMarca;
    private TextView textViewModelo;
    private TextView textViewKilometraje;

    private EditText editTextKilometraje;

    private Button buttonActualizarKilometraje;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        uid = obtenerUid();
        inicializarFireBase();
        cargarCliente(uid, root);
        inicializarVariables(root);

        buttonActualizarKilometraje = (Button) root.findViewById(R.id.button_actualizar_kilomtraje);
        buttonActualizarKilometraje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarKilometraje();

            }
        });

        return root;
    }

    /**
     * Método usado para actualizar kilometraje de un vehículo.
     */
    private void actualizarKilometraje() {
        final String uid = obtenerUid();
        final String kilometraje = editTextKilometraje.getText().toString();
        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona persona = objDataSnapshot.getValue(Persona.class);

                    if (uid.equals(persona.getUid())) {

                        Auto auto = persona.getAuto();
                        auto.setKilometraje(kilometraje);
                        persona.setAuto(auto);

                        databaseReference.child(PERSONA).child(persona.getUid()).setValue(persona);
                        Toast.makeText(getActivity().getApplicationContext(), MODIFICADO, Toast.LENGTH_SHORT).show();
                        limpiarCajas();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Método usado para limpiar cajas del formulario.
     */
    private void limpiarCajas() {
        editTextKilometraje.setText(CADENA_VACIA);
    }

    /**
     * Método usado para recuperar el uid del usuario logeado.
     *
     * @return uid.
     */
    private String obtenerUid() {
        String salida = CADENA_VACIA;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(DATO_01, CADENA_VACIA);
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
     * Método usado para inicializar variables.
     */
    private void inicializarVariables(View root) {
        editTextKilometraje = root.findViewById(R.id.txt_kilometraje3);
    }

    /**
     * Método usado para cargar entidad persona, del usuaro logueado.
     *
     * @param uid identificador del cliente.
     * @return persona en sesión.
     */
    private void cargarCliente(String uid, View root) {
        final String usuarioLogeado = uid;
        final View r = root;

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona persona = objDataSnapshot.getValue(Persona.class);

                    if (usuarioLogeado.equals(persona.getUid())) {

                        textViewPlaca = r.findViewById(R.id.txt_placa2);
                        textViewPlaca.setText(persona.getAuto().getPlaca());

                        textViewMarca = r.findViewById(R.id.txt_marca2);
                        textViewMarca.setText(persona.getAuto().getMarca());

                        textViewModelo = r.findViewById(R.id.txt_modelo2);
                        textViewModelo.setText(persona.getAuto().getModelo());

                        textViewKilometraje = r.findViewById(R.id.txt_kilometraje2);
                        textViewKilometraje.setText(persona.getAuto().getKilometraje());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}