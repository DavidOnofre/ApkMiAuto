package com.java.micarro.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.Comun;
import com.java.micarro.R;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Mantenimiento;
import com.java.micarro.model.Persona;
import com.java.micarro.ui.gallery.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.PERSONA;

public class ShareFragment extends Fragment {

    private Comun comun;
    private DatabaseReference databaseReference;
    private GalleryViewModel galleryViewModel;
    private ListView listViewMantenimientos;
    private Persona persona = new Persona();
    private List<Mantenimiento> mantenimientos = new ArrayList<Mantenimiento>();
    private ArrayAdapter<Mantenimiento> arrayAdapterMantenimiento;

    private ShareViewModel shareViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        inicializarVariables(root);
        cargarDatosPersona(comun.obtenerValorSesion(getActivity(), IDENTIFICACION_SESION));

        return root;
    }

    /**
     * Método usado para inicializar variables.
     */
    private void inicializarVariables(View root) {
        comun = new Comun();
        listViewMantenimientos = root.findViewById(R.id.listViewShareMantenimientos);
        databaseReference = comun.ObtenerDataBaseReference(getActivity());
    }

    /**
     * Método usado para cargar datos del usuario logeado.
     */
    private void cargarDatosPersona(String clave) {
        final String usuarioLogeado = clave;
        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mantenimientos.clear();

                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (usuarioLogeado.equals(p.getUid())) {
                        persona = p;
                        mantenimientos = p.getMantenimiento();
                    }
                }
                arrayAdapterMantenimiento = new ArrayAdapter<Mantenimiento>(getActivity(), android.R.layout.simple_list_item_1, mantenimientos);
                listViewMantenimientos.setAdapter(arrayAdapterMantenimiento);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}