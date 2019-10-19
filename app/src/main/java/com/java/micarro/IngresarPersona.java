package com.java.micarro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.List;

public class IngresarPersona extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_persona);

        identificacionBack = findViewById(R.id.txt_identificacionFrond);
        nombreBack = findViewById(R.id.txt_nombreFrond);
        apellidoBack = findViewById(R.id.txt_apellidoFrond);
        correoBack = findViewById(R.id.txt_correoFrond);
        telefonoBack = findViewById(R.id.txt_telefonoFrond);

        personasBack = findViewById(R.id.lv_personasFrond);

        inicializarFireBase();
        listarDatos();

        personasBack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);

                identificacionBack.setText(personaSeleccionada.getUid());
                nombreBack.setText(personaSeleccionada.getNombre());
                apellidoBack.setText(personaSeleccionada.getApellido());
                correoBack.setText(personaSeleccionada.getCorreo());
                telefonoBack.setText(personaSeleccionada.getTelefono());
            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPersona.clear();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()){
                    Persona p = objDataSnapshot.getValue(Persona.class);
                    listaPersona.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(IngresarPersona.this, android.R.layout.simple_list_item_1, listaPersona);
                    personasBack.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String identificacion = identificacionBack.getText().toString();
        String nombre = nombreBack.getText().toString();
        String apellido = apellidoBack.getText().toString();
        String correo = correoBack.getText().toString();
        String telefono = telefonoBack.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{

                if(nombre.equals("") || apellido.equals("") || correo.equals("") || telefono.equals("")){
                    validacion();
                }
                else {
                    Persona p = new Persona();
                    p.setUid(identificacion);
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setCorreo(correo);
                    p.setTelefono(telefono);

                    databaseReference.child("Persona").child(p.getUid()).setValue(p);

                    Toast.makeText(getApplicationContext(),"Agregado",Toast.LENGTH_SHORT).show();
                    limpiarCajas();

                }
                break;
            }
            case R.id.icon_save:{

                Persona p = new Persona();
                p.setUid(personaSeleccionada.getUid());
                p.setNombre(nombreBack.getText().toString().trim());
                p.setApellido(apellidoBack.getText().toString().trim());
                p.setCorreo(correoBack.getText().toString().trim());
                p.setTelefono(telefonoBack.getText().toString().trim());

                databaseReference.child("Persona").child(p.getUid()).setValue(p);

                Toast.makeText(getApplicationContext(),"Actualizado",Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            case R.id.icon_delete:{

                Persona p = new Persona();
                p.setUid(personaSeleccionada.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();

                Toast.makeText(getApplicationContext(),"Eliminado",Toast.LENGTH_SHORT).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        identificacionBack.setText("");
        nombreBack.setText("");
        apellidoBack.setText("");
        correoBack.setText("");
        telefonoBack.setText("");
    }

    private void validacion() {
        String identificacion = identificacionBack.getText().toString();
        String nombre = nombreBack.getText().toString();
        String apellido = apellidoBack.getText().toString();
        String correo = correoBack.getText().toString();
        String telefono = telefonoBack.getText().toString();

        if(identificacion.equals("") ){
            identificacionBack.setError("Required");
        }
        else if(nombre.equals("") ){
            nombreBack.setError("Required");
        }else if (apellido.equals("")){
            apellidoBack.setError("Required");
        }else if (correo.equals("")){
            correoBack.setError("Required");
        }else if (telefono.equals("")){
            telefonoBack.setError("Required");
        }
    }

}