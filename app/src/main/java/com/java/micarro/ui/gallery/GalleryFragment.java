package com.java.micarro.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;

import static com.java.micarro.Constantes.ACTUALIZADO;
import static com.java.micarro.Constantes.AGREGADO;
import static com.java.micarro.Constantes.CERO;
import static com.java.micarro.Constantes.ELIMINADO;
import static com.java.micarro.Constantes.ESPACIO_VACIO;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.PERSONA;
import static com.java.micarro.Constantes.REQUIRED;

public class GalleryFragment extends Fragment {

    private Comun comun;
    private DatabaseReference databaseReference;
    private GalleryViewModel galleryViewModel;
    private ListView listViewAutos;
    private Persona persona = new Persona();
    private List<Auto> autos = new ArrayList<Auto>();
    private ArrayAdapter<Auto> arrayAdapterAuto;

    private EditText editTextPlaca;
    private EditText editTextModelo;
    private EditText editTextMarca;

    private Auto autoSeleccionado;

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
        cargarDatosPersona(comun.obtenerValorSesion(getActivity(), IDENTIFICACION_SESION));
        cargarAutoSeleccionado();
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String placa = editTextPlaca.getText().toString();
        String modelo = editTextModelo.getText().toString();
        String marca = editTextMarca.getText().toString();

        switch (item.getItemId()) {
            case R.id.icon_add: {
                if (placa.equals(ESPACIO_VACIO) || modelo.equals(ESPACIO_VACIO) || marca.equals(ESPACIO_VACIO)) {
                    validacion();
                } else {

                    Auto a = new Auto();
                    a.setPlaca(placa);
                    a.setModelo(modelo);
                    a.setMarca(marca);
                    a.setKilometrajeInicial(CERO);
                    a.setKilometraje(CERO);
                    a.setKilometrajeAceite(CERO);
                    a.setKilometrajeBateria(CERO);
                    a.setKilometrajeElectricidad(CERO);
                    a.setKilometrajeGasolina(CERO);
                    a.setKilometrajeLlantas(CERO);

                    Persona p = persona;
                    List<Auto> listAuto;
                    listAuto = p.getAuto();
                    listAuto.add(a);
                    p.setAuto(listAuto);

                    databaseReference.child(PERSONA).child(p.getUid()).setValue(p);
                    Toast.makeText(getActivity().getApplicationContext(), AGREGADO, Toast.LENGTH_SHORT).show();
                    limpiarCajas();
                }
                break;
            }

            case R.id.icon_edit: {

                Auto a = new Auto();
                a.setPlaca(editTextPlaca.getText().toString().trim());
                a.setMarca(editTextMarca.getText().toString().trim());
                a.setModelo(editTextModelo.getText().toString().trim());
                a.setKilometrajeInicial(autoSeleccionado.getKilometrajeInicial());
                a.setKilometraje(autoSeleccionado.getKilometraje());
                a.setKilometrajeAceite(autoSeleccionado.getKilometrajeAceite());
                a.setKilometrajeBateria(autoSeleccionado.getKilometrajeBateria());
                a.setKilometrajeElectricidad(autoSeleccionado.getKilometrajeElectricidad());
                a.setKilometrajeGasolina(autoSeleccionado.getKilometrajeGasolina());
                a.setKilometrajeLlantas(autoSeleccionado.getKilometrajeLlantas());

                Persona p = persona;
                List<Auto> listAuto;
                listAuto = p.getAuto();
                int indice = listAuto.indexOf(autoSeleccionado);
                listAuto.remove(indice);
                listAuto.add(indice, a);
                p.setAuto(listAuto);

                databaseReference.child(PERSONA).child(p.getUid()).setValue(p);
                Toast.makeText(getActivity().getApplicationContext(), ACTUALIZADO, Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            case R.id.icon_delete: {

                Persona p = persona;
                List<Auto> listAuto;
                listAuto = p.getAuto();
                int indice = listAuto.indexOf(autoSeleccionado);
                listAuto.remove(indice);

                databaseReference.child(PERSONA).child(p.getUid()).setValue(p);
                Toast.makeText(getActivity().getApplicationContext(), ELIMINADO, Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            default:
                break;
        }
        return true;
    }

    /**
     * Método usado para validar que las cajas de texto no estén vacías.
     */
    private void validacion() {
        String placa = editTextPlaca.getText().toString();
        String modelo = editTextModelo.getText().toString();
        String marca = editTextMarca.getText().toString();

        if (placa.equals(ESPACIO_VACIO)) {
            editTextPlaca.setError(REQUIRED);
        }

        if (modelo.equals(ESPACIO_VACIO)) {
            editTextModelo.setError(REQUIRED);
        }

        if (marca.equals(ESPACIO_VACIO)) {
            editTextMarca.setError(REQUIRED);
        }
    }


    /**
     * Método usado para cargar datos del auto seleccionado.
     */
    private void cargarAutoSeleccionado() {
        listViewAutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoSeleccionado = (Auto) parent.getItemAtPosition(position);
                editTextPlaca.setText(autoSeleccionado.getPlaca());
                editTextModelo.setText(autoSeleccionado.getModelo());
                editTextMarca.setText(autoSeleccionado.getMarca());
            }
        });
    }

    /**
     * Método usado para cargar datos del usuario logeado.
     */
    private void cargarDatosPersona(String clave) {
        final String usuarioLogeado = clave;
        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                autos.clear();

                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (usuarioLogeado.equals(p.getUid())) {
                        persona = p;
                        autos = p.getAuto();
                    }
                }
                arrayAdapterAuto = new ArrayAdapter<Auto>(getActivity(), android.R.layout.simple_list_item_1, autos);
                listViewAutos.setAdapter(arrayAdapterAuto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Método usado para inicializar variables.
     */
    private void inicializarVariables(View root) {
        comun = new Comun();
        editTextPlaca = root.findViewById(R.id.editTextGalleryPlaca);
        editTextModelo = root.findViewById(R.id.editTextGalleryModelo);
        editTextMarca = root.findViewById(R.id.editTextGalleryMarca);
        listViewAutos = root.findViewById(R.id.listViewGalleryAutos);

        databaseReference = comun.ObtenerDataBaseReference(getActivity());
    }

    /**
     * Método usado para limpiar cajas del formulario.
     */
    private void limpiarCajas() {
        editTextPlaca.setText(ESPACIO_VACIO);
        editTextModelo.setText(ESPACIO_VACIO);
        editTextMarca.setText(ESPACIO_VACIO);
    }
}