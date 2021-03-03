package com.java.micarro.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
    public static final String IDENTIFICACION_SESION = "identificacionSesion";
    public static final String ACTUALIZAR_KILOMETRAJE = "actualizarKilometraje";
    public static final String NO = "NO";
    public static final String SI = "SI";
    public static final String CADENA_VACIA = "";
    public static final String PERSONA = "Persona";
    public static final String MODIFICADO = "Modificado";
    public static final String MENSAJE_INGRESE_NUMEROS = "Por favor ingresar valores numéricos";
    public static final String CONSUMO = "Consumo";

    public static final String ACEITE = "A";
    public static final String GASOLINA = "G";
    public static final String LLANTAS = "L";
    public static final String BATERIA = "B";
    public static final String ELECTICIDAD = "E";
    public static final String KILOMETRAJE_ACTUAL = "KilometrajeActual";
    public static final String GRAFICO_CONSUMIBLES = "Gráfico Consumibles";

    private int amarillo = Color.rgb(255, 255, 0);
    private int verde = Color.rgb(60, 220, 78);
    private int rojo = Color.rgb(255, 0, 0);

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private HomeViewModel homeViewModel;

    private String identificacion = "";
    private String banderaActualizarKilometraje = "";

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

        identificacion = obtenerIdentificacion();
        inicializarFireBase();

        inicializarVariables(root);
        cargarEntidadGlobalPersona(); // vuelve a consultar a la bdd
        cargarEtiquetasAuto(identificacion, root); // consulta a la bdd

        banderaActualizarKilometraje = obtenerBanderaActualizarKilometra();

        if (banderaActualizarKilometraje.equals(SI)) {
            actualizarKilometrajeFrond(true);
            buttonActualizarKilometraje.setOnClickListener(this);
        }

        if (banderaActualizarKilometraje.equals(NO)) {
            //gráfico desde el kilometraje de sesion
            dibujarGraficoHorizontal();
        }

        return root;
    }


    /**
     * Método para dibujar gráfico horizontal.
     */
    private void dibujarGraficoHorizontal() {

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        CargarValoresBarras(barEntries);
        ArrayList<String> etiquetas = cargarEtiquetas();
        cargarFormatoGraficoBarras(etiquetas);
        cargarEtiquetaPieGrafico();
        cargaColoresBarras(barEntries);
    }


    /**
     * Cargar colores para cada barra graficada.
     *
     * @param barEntries
     */
    private void cargaColoresBarras(ArrayList<BarEntry> barEntries) {

        float anchoBarras = 0.5f;
        int kilometrajeActual = obtenerKilometraje();

        BarDataSet barDataSet = new BarDataSet(barEntries, CONSUMO);
        BarData data = new BarData(barDataSet);

        data.setBarWidth(anchoBarras);
        barDataSet.setColors(cargarColor(kilometrajeActual, ACEITE), cargarColor(kilometrajeActual, GASOLINA), cargarColor(kilometrajeActual, LLANTAS), cargarColor(kilometrajeActual, BATERIA), cargarColor(kilometrajeActual, ELECTICIDAD));

        BarData barData = new BarData(barDataSet);
        horizontalBarChart.setData(barData);
        horizontalBarChart.setData(data);
    }

    /**
     * Método usado para cargar kilometraje.
     * @return
     */
    private int obtenerKilometraje() {
        int salida;

        if (banderaActualizarKilometraje.equals(NO)) {
            salida = obtenerKilometrajeSesion();
        } else {
            Auto auto = persona.getAuto();
            salida = obtenerKilometrajeBaseDatos();
        }

        return salida;
    }

    /**
     * Método usado para obtener el kilometraje de la sesión.
     * @return
     */
    private int obtenerKilometrajeSesion() {
        String salida = CADENA_VACIA;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(KILOMETRAJE_ACTUAL, CADENA_VACIA);

        return Integer.parseInt(salida);
    }

    /**
     * Método usado para obener el kilometraje desade la base de datos.
     *
     * @return
     */
    private int obtenerKilometrajeBaseDatos() {
        Auto auto = persona.getAuto();
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        int kilometrajeActualizado = kilometrajeActual;
        return kilometrajeActualizado;
    }

    /**
     * Método usado para poner color a cada barra en el gráfico de barras.
     *
     * @param kilometraje dependiendo del kilometraje el color de la barra cambiara (verde, amarillo, rojo).
     * @param consumible  consumible sobre el cual se levanta la alerta.
     * @return
     */
    private int cargarColor(int kilometraje, String consumible) {

        int salida = 0;

        switch (consumible) {
            case ACEITE:
                salida = obtenerSalidaAceite(kilometraje);
                break;
            case GASOLINA:
                salida = obtenerSalidaGasolina(kilometraje);
                break;

            case LLANTAS:
                salida = obtenerSalidaLlantas(kilometraje);
                break;
            case BATERIA:
                salida = obtenerSalidaBateria(kilometraje);
                break;

            case ELECTICIDAD:
                salida = obtenerSalidaElectricidad(kilometraje);
                break;

            default:
                salida = 0;
        }


        return salida;
    }

    /**
     * Método usado para cargar color en gráfico de barras de aceite.
     *
     * @param kilometrajeActualizado
     * @return
     */
    private int obtenerSalidaAceite(int kilometrajeActualizado) {

        int salida = 0;
        int validacion = 5000 - kilometrajeActualizado;

        if (validacion <= 4999 && validacion >= 3000) {
            salida = verde;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = amarillo;
        }
        if (validacion <= 99) {
            salida = rojo;
        }
        return salida;
    }

    /**
     * Método usado para cargar color en gráfico de barras de gasolina.
     *
     * @param kilometrajeActualizado
     * @return
     */
    private int obtenerSalidaGasolina(int kilometrajeActualizado) {

        int salida = 0;
        int validacion = 5000 - kilometrajeActualizado;

        if (validacion <= 4999 && validacion >= 3000) {
            salida = verde;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = amarillo;
        }
        if (validacion <= 99) {
            salida = rojo;
        }
        return salida;
    }

    /**
     * Método usado para cargar color en gráfico de barras de llantas.
     *
     * @param kilometrajeActualizado
     * @return
     */
    private int obtenerSalidaLlantas(int kilometrajeActualizado) {

        int salida = 0;
        int validacion = 10000 - kilometrajeActualizado;

        if (validacion <= 9999 && validacion >= 3000) {
            salida = verde;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = amarillo;
        }
        if (validacion <= 99) {
            salida = rojo;
        }
        return salida;
    }

    /**
     * Método usado para cargar color en gráfico de barras de bateria.
     *
     * @param kilometrajeActualizado
     * @return
     */
    private int obtenerSalidaBateria(int kilometrajeActualizado) {

        int salida = 0;
        int validacion = 15000 - kilometrajeActualizado;

        if (validacion <= 14999 && validacion >= 3000) {
            salida = verde;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = amarillo;
        }
        if (validacion <= 99) {
            salida = rojo;
        }
        return salida;
    }

    /**
     * Método usado para cargar color en gráfico de barras de electicidad.
     *
     * @param kilometrajeActualizado
     * @return
     */
    private int obtenerSalidaElectricidad(int kilometrajeActualizado) {

        int salida = 0;
        int validacion = 10000 - kilometrajeActualizado;

        if (validacion <= 9999 && validacion >= 3000) {
            salida = verde;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = amarillo;
        }
        if (validacion <= 99) {
            salida = rojo;
        }
        return salida;
    }

    /**
     * Método usado para cargar etiquetas del gráfico de barras.
     *
     * @return arreglo de etiquetas para gráfoco de barras
     */
    private ArrayList<String> cargarEtiquetas() {

        ArrayList<String> salida = new ArrayList<>();
        String[] arregloEtiquetas = {"Electricidad", "Batería", "Llantas", "Gasolina", "Aceite"};
        for (int i = 0; arregloEtiquetas.length > i; i++) {
            String tipo = arregloEtiquetas[i];
            salida.add(tipo);
        }
        return salida;
    }

    /**
     * Método usado para cargar formato del gráfico de barras.
     *
     * @param listaEtiquetas arreglo con etiquetas para cada barra.
     */
    private void cargarFormatoGraficoBarras(ArrayList<String> listaEtiquetas) {
        XAxis xaxis = horizontalBarChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter(listaEtiquetas));

        xaxis.setPosition(XAxis.XAxisPosition.TOP);
        xaxis.setDrawAxisLine(false);
        xaxis.setDrawGridLines(false);
        xaxis.setGranularity(1f);
        xaxis.setLabelCount(listaEtiquetas.size());
        xaxis.setLabelRotationAngle(270);
        horizontalBarChart.animateY(2000);
    }

    /**
     * Método usado para poner etiqueta al pie de gráfico.
     */
    private void cargarEtiquetaPieGrafico() {
        Description description = new Description();
        description.setText(GRAFICO_CONSUMIBLES);
        horizontalBarChart.setDescription(description);
    }

    /**
     * Método usado para cargar barras en gráfico de barras.
     *
     * @param barEntries barras a dibujar.
     */
    private void CargarValoresBarras(ArrayList<BarEntry> barEntries) {
        int kilometrajeActual = obtenerKilometraje();

        BarEntry barAceite = new BarEntry(4, 5000 - kilometrajeActual);
        barEntries.add(barAceite);

        BarEntry barGasolina = new BarEntry(3, 5000 - kilometrajeActual);
        barEntries.add(barGasolina);

        BarEntry barLlantas = new BarEntry(2, 10000 - kilometrajeActual);
        barEntries.add(barLlantas);

        BarEntry barBateria = new BarEntry(1, 15000 - kilometrajeActual);
        barEntries.add(barBateria);

        BarEntry barElectricidad = new BarEntry(0, 10000 - kilometrajeActual);
        barEntries.add(barElectricidad);
    }

    @Override
    public void onClick(View v) {

        String kilometrajeIngresado = editTextKilometraje.getText().toString();

        if (esNumero(kilometrajeIngresado)) {
            actualizarKilometraje(kilometrajeIngresado);
            dibujarGraficoHorizontal();
        }
    }

    /**
     * Método usado para cargar variable global persona.
     */
    private void cargarEntidadGlobalPersona() {
        final String identificacion = obtenerIdentificacion();

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (identificacion.equals(p.getUid())) {
                        HomeFragment.this.persona = p;

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
    private String obtenerIdentificacion() {
        String salida = CADENA_VACIA;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(IDENTIFICACION_SESION, CADENA_VACIA);
        return salida;
    }

    /**
     * Método usado para validar si hay que actualizar o no el kilometraje, se permite una actualización por sesión.
     *
     * @return
     */
    private String obtenerBanderaActualizarKilometra() {
        String salida = CADENA_VACIA;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(ACTUALIZAR_KILOMETRAJE, CADENA_VACIA);
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
        buttonActualizarKilometraje = (Button) root.findViewById(R.id.button_actualizar_kilomtraje);
        horizontalBarChart = (HorizontalBarChart) root.findViewById(R.id.graficaHorizontal);
    }

    /**
     * Método usado para cargar entidad persona, del usuaro logueado.
     *
     * @param identificacionSesion identificador del cliente.
     * @return persona en sesión.
     */
    private void cargarEtiquetasAuto(String identificacionSesion, View root) {

        final String identificacion = identificacionSesion;
        final View r = root;

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona per = objDataSnapshot.getValue(Persona.class);

                    if (identificacion.equals(per.getUid())) {

                        textViewPlaca = r.findViewById(R.id.txt_placa2);
                        textViewPlaca.setText(per.getAuto().getPlaca());

                        textViewMarca = r.findViewById(R.id.txt_marca2);
                        textViewMarca.setText(per.getAuto().getMarca());

                        textViewModelo = r.findViewById(R.id.txt_modelo2);
                        textViewModelo.setText(per.getAuto().getModelo());

                        textViewKilometraje = r.findViewById(R.id.txt_kilometraje2);
                        textViewKilometraje.setText(per.getAuto().getKilometraje());
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

        grabarKilometrajeActualSesion(kilometrajeActualizado);
        actualizarKilometrajeFrond(false);
        limpiarCajas();
    }

    /**
     * Método usado para grabar en sesión el kilometraje
     * @param kilometrajeActualizado
     */
    private void grabarKilometrajeActualSesion(int kilometrajeActualizado) {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KILOMETRAJE_ACTUAL, String.valueOf(kilometrajeActualizado));
        editor.putString(ACTUALIZAR_KILOMETRAJE, NO);
        editor.commit();
    }

    /**
     * Método usado para activar/desactivar botón actualizar y entrada de texto.
     * @param actualizar boolena que indica si se activa o desactiva la entrada.
     */
    private void actualizarKilometrajeFrond(boolean actualizar) {
        buttonActualizarKilometraje.setEnabled(actualizar);
        editTextKilometraje.setEnabled(actualizar);
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