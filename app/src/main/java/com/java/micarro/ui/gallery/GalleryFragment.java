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
import com.java.micarro.model.Auto;
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String IDENTIFICACION_SESION = "identificacionSesion";
    public static final String CADENA_VACIA = "";
    public static final String PERSONA = "Persona";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private GalleryViewModel galleryViewModel;

    private ListView listViewPersonas;
    private List<Persona> personas = new ArrayList<Persona>();
    private List<Auto> autos = new ArrayList<Auto>();
    private ArrayAdapter<Persona> arrayAdapterPersona;
    private ArrayAdapter<Auto> arrayAdapterAuto;

    private EditText editTextPlaca;
    private EditText editTextModelo;
    private EditText editTextMarca;

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

        inicializarVariables(root);
        inicializarFireBase();
        cargarDatosPersona(obtenerValorSesion(IDENTIFICACION_SESION));
        cargarAutoSeleccionado();

        return root;
    }

    /**
     * Método usado para cargar datos del auto seleccionado.
     */
    private void cargarAutoSeleccionado() {
        listViewPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);

                editTextPlaca.setText(personaSeleccionada.getAuto().get(0).getPlaca());
                editTextModelo.setText(personaSeleccionada.getAuto().get(0).getModelo());
                editTextMarca.setText(personaSeleccionada.getAuto().get(0).getMarca());
            }
        });
    }

    /**
     * Método usado para cargar datos solo del usuario logeado.
     */
    private void cargarDatosPersona(String clave) {
        final String usuarioLogeado = clave;
        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                personas.clear();

                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (usuarioLogeado.equals(p.getUid())) {
                        personas.add(p);
                        autos = p.getAuto();
                    }
                }
                arrayAdapterAuto = new ArrayAdapter<Auto>(getActivity(), android.R.layout.simple_list_item_1, autos);
                listViewPersonas.setAdapter(arrayAdapterAuto);
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
     * Método usado para cargar una cadena de sesión.
     * @param valorSesion nombre de la variable de sesión que se quiere recuperar.
     * @return valor de la variable a recuperar de la sesión.
     */
    private String obtenerValorSesion(String valorSesion) {
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        String salida = prefs.getString(valorSesion, CADENA_VACIA);
        return salida;
    }

    /**
     * Método usado para inicializar variables.
     */
    private void inicializarVariables(View root) {
        editTextPlaca = root.findViewById(R.id.editTextGalleryPlaca);
        editTextModelo = root.findViewById(R.id.editTextGalleryModelo);
        editTextMarca = root.findViewById(R.id.editTextGalleryMarca);
        listViewPersonas = root.findViewById(R.id.listViewGalleryPersonas);
    }

    /**
     * Método usado para limpiar cajas del formulario.
     */
    private void limpiarCajas() {
        editTextPlaca.setText("");
        editTextModelo.setText("");
        editTextMarca.setText("");
    }
}