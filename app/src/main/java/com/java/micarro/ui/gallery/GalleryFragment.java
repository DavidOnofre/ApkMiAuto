package com.java.micarro.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputLayout;
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

    private GalleryViewModel galleryViewModel;
    private ListView personasBack;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Persona> listaPersona = new ArrayList<Persona>();
    private ArrayAdapter<Persona> arrayAdapterPersona;

    private EditText txtContrasena;
    private TextInputLayout impContrasena;

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

        inicializarFireBase();
        listarDatos();

        return root;
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPersona.clear();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()){
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    //String clave = recibirClave();
                    String clave = "1716985469";

                    if(clave.equals(p.getUid())){
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

    private void inicializarFireBase() {
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private String recibirClave() {
       return  getArguments().getString("dato01");
    }

}