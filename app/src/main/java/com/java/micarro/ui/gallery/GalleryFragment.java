package com.java.micarro.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String DATO_01 = "dato01";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private GalleryViewModel galleryViewModel;

    private ListView personasBack;
    private List<Persona> listaPersona = new ArrayList<Persona>();
    private ArrayAdapter<Persona> arrayAdapterPersona;

    private String uid = "";

    private EditText textPlaca;
    private EditText textModelo;
    private EditText textMarca;

    private Persona personaSeleccionada;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        personasBack = root.findViewById(R.id.lv_personasFrond2);

        uid = obtenerUid();
        inicializarFireBase();
        cargarDatosCliente(uid);
        inicializarVariables(root);

        personasBack.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);

                textPlaca.setText(personaSeleccionada.getAuto().getPlaca());
                textModelo.setText(personaSeleccionada.getAuto().getModelo());
                textMarca.setText(personaSeleccionada.getAuto().getMarca());
            }
        });

        return root;
    }


    /**
     * Método usado para cargar datos solo del usuario logeado.
     */
    private void cargarDatosCliente(String clave) {
        final String usuarioLogeado = clave;
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPersona.clear();

                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (usuarioLogeado.equals(p.getUid())) {
                        listaPersona.add(p);
                    }
                }
                arrayAdapterPersona = new ArrayAdapter<Persona>(getActivity(), android.R.layout.simple_list_item_1, listaPersona);
                personasBack.setAdapter(arrayAdapterPersona);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
     * Método usado para recuperar el uid del usuario logeado.
     *
     * @return uid.
     */
    private String obtenerUid() {
        String salida = "";
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(DATO_01, "");
        return salida;
    }

    /**
     * Método usado para inicializar variables.
     */
    private void inicializarVariables(View root) {
        textPlaca = root.findViewById(R.id.txt_placa4);
        textModelo = root.findViewById(R.id.txt_modelo4);
        textMarca = root.findViewById(R.id.txt_marca4);
    }

    /**
     * Método usado para limpiar cajas del formulario.
     */
    private void limpiarCajas() {
        textPlaca.setText("");
        textModelo.setText("");
        textMarca.setText("");
    }
}