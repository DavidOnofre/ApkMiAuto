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

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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

public class HomeFragment extends Fragment implements View.OnClickListener {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String DATO_01 = "dato01";
    public static final String CADENA_VACIA = "";
    public static final String PERSONA = "Persona";
    public static final String MODIFICADO = "Modificado";
    public static final String MENSAJE_INGRESE_NUMEROS = "Por favor ingresar valores numéricos";

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

    private Persona persona;

    private HorizontalBarChart horizontalBarChart;


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

        visualizarDatosVehiculoPrincipal();

        buttonActualizarKilometraje = (Button) root.findViewById(R.id.button_actualizar_kilomtraje);
        buttonActualizarKilometraje.setOnClickListener(this);

        horizontalBarChart = (HorizontalBarChart) root.findViewById(R.id.graficaHorizontal);

        return root;
    }

    /**
     * Método para digubar gráfico horizontal.
     *
     * @param kilometraje ingresado en la caja de texto, valor que se actualizara en las barras.
     */
    private void dibujarGraficoHorizontal(String kilometraje) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        float anchoBarras = 0.5f;

        CargarValoresBarras(kilometraje, barEntries);

        BarDataSet barDataSet;
        barDataSet = new BarDataSet(barEntries, "Consumo");
        BarData data = new BarData(barDataSet);
        data.setBarWidth(anchoBarras);

        //arregloEtiquetas
        ArrayList<String> listaEtiquetas = new ArrayList<>();
        String[] arregloEtiquetas = {"Electicidad", "Bateria", "Llantas", "Gasolina", "Aceite"};
        for (int i = 0; arregloEtiquetas.length > i; i++) {
            String tipo = arregloEtiquetas[i];
            listaEtiquetas.add(tipo);
        }

        XAxis xaxis = horizontalBarChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter(listaEtiquetas));

        //etiqueta dentro del gráfico.
        Description description = new Description();
        description.setText("Gráfico Consumibles");
        horizontalBarChart.setDescription(description);

        //colores
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        horizontalBarChart.setData(barData);

        horizontalBarChart.setData(data);
    }

    /**
     * Método usado para cargar barras en gráfico de barras.
     *
     * @param kilometraje kilometraje de la caja de texto
     * @param barEntries  barras a dibujar.
     */
    private void CargarValoresBarras(String kilometraje, ArrayList<BarEntry> barEntries) {

        int kilometrajeCajaTexto = Integer.parseInt(kilometraje);
        Auto auto = persona.getAuto();
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        int kilometrajeActualizado = kilometrajeActual + kilometrajeCajaTexto;

        BarEntry barAceite = new BarEntry(4, 5000 - kilometrajeActualizado);
        barEntries.add(barAceite);

        BarEntry barGasolina = new BarEntry(3, 5000 - kilometrajeActualizado);
        barEntries.add(barGasolina);

        BarEntry barLlantas = new BarEntry(2, 10000 - kilometrajeActualizado);
        barEntries.add(barLlantas);

        BarEntry barBateria = new BarEntry(1, 15000 - kilometrajeActualizado);
        barEntries.add(barBateria);

        BarEntry barElecticidad = new BarEntry(0, 10000 - kilometrajeActualizado);
        barEntries.add(barElecticidad);


    }

    @Override
    public void onClick(View v) {

        String kilometrajeIngresado = editTextKilometraje.getText().toString();

        if (esNumero(kilometrajeIngresado)) {
            actualizarKilometraje(kilometrajeIngresado);
            dibujarGraficoHorizontal(kilometrajeIngresado);
        }
    }

    /**
     * Método usado para actualizar kilometraje de un vehículo.
     */
    private void visualizarDatosVehiculoPrincipal() {
        final String uid = obtenerUid();

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona persona = objDataSnapshot.getValue(Persona.class);

                    if (uid.equals(persona.getUid())) {
                        HomeFragment.this.persona = persona;
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
        persona = new Persona();
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

    /**
     * Método usado para actualizar kilometraje
     *
     * @param kilometrajeIngresado kilometraje a sumar al kilometraje actual.
     */
    private void actualizarKilometraje(String kilometrajeIngresado) {
        int kilometrajeCajaTexto = Integer.parseInt(kilometrajeIngresado);

        Auto auto = persona.getAuto();
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        int kilometrajeActualizado = kilometrajeActual + kilometrajeCajaTexto;

        auto.setKilometraje(String.valueOf(kilometrajeActualizado));
        persona.setAuto(auto);

        databaseReference.child(PERSONA).child(persona.getUid()).setValue(persona);
        Toast.makeText(getActivity().getApplicationContext(), MODIFICADO, Toast.LENGTH_SHORT).show();
        limpiarCajas();
    }

    /**
     * Método usado para validar que el kilometraje ingresado sea un número.
     *
     * @param cadena a validar si es número
     * @return Bandera que indica que la validación es exitosa.
     */
    public boolean esNumero(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            Toast.makeText(getActivity().getApplicationContext(), MENSAJE_INGRESE_NUMEROS, Toast.LENGTH_SHORT).show();
            resultado = false;
            limpiarCajas();
        }
        return resultado;
    }
}