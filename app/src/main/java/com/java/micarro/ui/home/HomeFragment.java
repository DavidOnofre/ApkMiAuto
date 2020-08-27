package com.java.micarro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.java.micarro.R;
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    EditText identificacionBack;
    EditText nombreBack;
    EditText apellidoBack;
    EditText correoBack ;
    EditText telefonoBack;
    ListView personasBack;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Persona> listaPersona = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    Persona personaSeleccionada;



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

        identificacionBack = root.findViewById(R.id.txt_identificacionFrond2);
        nombreBack = root.findViewById(R.id.txt_nombreFrond2);
        apellidoBack = root.findViewById(R.id.txt_apellidoFrond2);
        //correoBack = root.findViewById(R.id.txt_correoFrond2);
        //telefonoBack = root.findViewById(R.id.txt_telefonoFrond2);

       // personasBack = root.findViewById(R.id.lv_personasFrond2);

        inicializarFireBase();
        //listarDatos();



        return root;
    }

    private void inicializarFireBase() {
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}