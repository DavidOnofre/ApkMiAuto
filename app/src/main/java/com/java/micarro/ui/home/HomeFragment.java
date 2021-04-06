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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.java.micarro.Comun;
import com.java.micarro.R;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Mantenimiento;
import com.java.micarro.model.Persona;

import java.util.ArrayList;

import static com.java.micarro.Constantes.ACEITE;
import static com.java.micarro.Constantes.ACEITE_BANDERA;
import static com.java.micarro.Constantes.ACTUALIZADO;
import static com.java.micarro.Constantes.ACTUALIZAR_KILOMETRAJE;
import static com.java.micarro.Constantes.AMARILLO;
import static com.java.micarro.Constantes.BATERIA;
import static com.java.micarro.Constantes.BATERIA_BANDERA;
import static com.java.micarro.Constantes.CONSUMO;
import static com.java.micarro.Constantes.ELECTRICIDAD;
import static com.java.micarro.Constantes.ELECTRICIDAD_BANDERA;
import static com.java.micarro.Constantes.ESPACIO_VACIO;
import static com.java.micarro.Constantes.GASOLINA;
import static com.java.micarro.Constantes.GASOLINA_BANDERA;
import static com.java.micarro.Constantes.GRAFICO_CONSUMIBLES;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_ACTUAL;
import static com.java.micarro.Constantes.KILOMETRAJE_INGRESADO_DEBE_SER_MAYOR_AL_REGISTRADO;
import static com.java.micarro.Constantes.LLANTAS;
import static com.java.micarro.Constantes.LLANTAS_BANDERA;
import static com.java.micarro.Constantes.MARCA;
import static com.java.micarro.Constantes.MODELO;
import static com.java.micarro.Constantes.NO;
import static com.java.micarro.Constantes.PERSONA;
import static com.java.micarro.Constantes.PLACA;
import static com.java.micarro.Constantes.RECORRIDO;
import static com.java.micarro.Constantes.RECORRIDO_FROND;
import static com.java.micarro.Constantes.ROJO;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.Constantes.SI;
import static com.java.micarro.Constantes.ULTIMO_KILOMETRAJE;
import static com.java.micarro.Constantes.VERDE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Comun comun;
    private DatabaseReference databaseReference;

    private HomeViewModel homeViewModel;

    private String identificacion = "";
    private String banderaActualizarKilometraje = "";

    private TextView textViewPlaca;
    private TextView textViewMarca;
    private TextView textViewModelo;
    private TextView textViewKilometraje;
    private TextView textViewRecorrido;

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

        identificacion = obtenerValorSesion(IDENTIFICACION_SESION);

        inicializarVariables(root);
        cargarEntidadGlobalPersona(); // vuelve a consultar a la bdd
        cargarEtiquetasAuto(identificacion); // consulta a la bdd

        banderaActualizarKilometraje = obtenerValorSesion(ACTUALIZAR_KILOMETRAJE);
        habilitarActualizarKilometraje();

        return root;
    }

    /**
     * Método usado para habilitar o desabilitar la actualización del kilometraje.
     */
    private void habilitarActualizarKilometraje() {
        if (banderaActualizarKilometraje.equals(SI)) {
            actualizarKilometrajeFrond(true);
            buttonActualizarKilometraje.setOnClickListener(this);
        }

        if (banderaActualizarKilometraje.equals(NO)) {
            textViewRecorrido.setText("Recorrido: " + obtenerValorSesion(RECORRIDO));

            //gráfico desde el kilometraje de sesion
            dibujarGraficoHorizontal();
        }
    }

    /**
     * Método usado para cargar una cadena de sesión.
     *
     * @param valorSesion nombre de la variable de sesión que se quiere recuperar.
     * @return valor de la variable a recuperar de la sesión.
     */
    private String obtenerValorSesion(String valorSesion) {
        String salida = ESPACIO_VACIO;
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(valorSesion, ESPACIO_VACIO);
        return salida;
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
        barDataSet.setColors(cargarColor(kilometrajeActual, ACEITE_BANDERA), cargarColor(kilometrajeActual, GASOLINA_BANDERA), cargarColor(kilometrajeActual, LLANTAS_BANDERA), cargarColor(kilometrajeActual, BATERIA_BANDERA), cargarColor(kilometrajeActual, ELECTRICIDAD_BANDERA));

        BarData barData = new BarData(barDataSet);
        horizontalBarChart.setData(barData);
        horizontalBarChart.setData(data);
    }

    /**
     * Método usado para cargar kilometraje.
     *
     * @return
     */
    private int obtenerKilometraje() {
        int salida;

        if (banderaActualizarKilometraje.equals(NO)) {
            salida = Integer.parseInt(obtenerValorSesion(KILOMETRAJE_ACTUAL));
        } else {
            Auto auto = persona.getAuto().get(0);
            salida = obtenerKilometrajeBaseDatos();
        }

        return salida;
    }

    /**
     * Método usado para obener el kilometraje desade la base de datos.
     *
     * @return
     */
    private int obtenerKilometrajeBaseDatos() {
        Auto auto = persona.getAuto().get(0);
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
            case ACEITE_BANDERA:
                salida = obtenerSalidaAceite(kilometraje);
                break;
            case GASOLINA_BANDERA:
                salida = obtenerSalidaGasolina(kilometraje);
                break;
            case LLANTAS_BANDERA:
                salida = obtenerSalidaLlantas(kilometraje);
                break;
            case BATERIA_BANDERA:
                salida = obtenerSalidaBateria(kilometraje);
                break;
            case ELECTRICIDAD_BANDERA:
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
            salida = VERDE;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = AMARILLO;
        }
        if (validacion <= 99) {
            salida = ROJO;
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
            salida = VERDE;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = AMARILLO;
        }
        if (validacion <= 99) {
            salida = ROJO;
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
            salida = VERDE;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = AMARILLO;
        }
        if (validacion <= 99) {
            salida = ROJO;
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
            salida = VERDE;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = AMARILLO;
        }
        if (validacion <= 99) {
            salida = ROJO;
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
            salida = VERDE;
        }
        if (validacion <= 2999 && validacion >= 100) {
            salida = AMARILLO;
        }
        if (validacion <= 99) {
            salida = ROJO;
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
        String[] arregloEtiquetas = {ELECTRICIDAD, BATERIA, LLANTAS, GASOLINA, ACEITE};
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
        int kilometrajeCajaTexto = Integer.parseInt(kilometrajeIngresado);
        Auto auto = persona.getAuto().get(0);
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());


        if (comun.esNumero(kilometrajeIngresado)) {

            if (validarKilometrajeMayorRegistrado(kilometrajeActual, kilometrajeCajaTexto)) {

                actualizarKilometraje(kilometrajeIngresado);
                dibujarGraficoHorizontal();
            }
        }
    }

    /**
     * Método usado para cargar variable global persona.
     */
    private void cargarEntidadGlobalPersona() {
        final String identificacion = obtenerValorSesion(IDENTIFICACION_SESION);

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
        editTextKilometraje.setText(ESPACIO_VACIO);
    }

    /**
     * Método usado para inicializar variables.
     */
    private void inicializarVariables(View root) {
        comun = new Comun();

        editTextKilometraje = root.findViewById(R.id.editText_auto_principal_Kilometraje_Ingresado);
        persona = new Persona();
        buttonActualizarKilometraje = (Button) root.findViewById(R.id.button_auto_principal_actualizar_kilomtraje);
        horizontalBarChart = (HorizontalBarChart) root.findViewById(R.id.graficaHorizontal);

        textViewPlaca = root.findViewById(R.id.textView_auto_principal_placa);
        textViewMarca = root.findViewById(R.id.textView_auto_principal_marca);
        textViewModelo = root.findViewById(R.id.textView_auto_principal_modelo);
        textViewKilometraje = root.findViewById(R.id.textView_auto_principal_kilometraje);
        textViewRecorrido = root.findViewById(R.id.textView_auto_principal_recorrido);

        databaseReference = comun.ObtenerDataBaseReference(getActivity());
    }

    /**
     * Método usado para cargar entidad persona, del usuaro logueado.
     *
     * @param identificacionSesion identificador del cliente.
     * @return persona en sesión.
     */
    private void cargarEtiquetasAuto(String identificacionSesion) {

        final String identificacion = identificacionSesion;

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona per = objDataSnapshot.getValue(Persona.class);

                    if (identificacion.equals(per.getUid())) {

                        textViewPlaca.setText(PLACA + per.getAuto().get(0).getPlaca());
                        textViewMarca.setText(MARCA + per.getAuto().get(0).getMarca());
                        textViewModelo.setText(MODELO + per.getAuto().get(0).getModelo());
                        textViewKilometraje.setText(ULTIMO_KILOMETRAJE + per.getAuto().get(0).getKilometraje());
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
        Auto auto = persona.getAuto().get(0);
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());


            String recorrido = String.valueOf(kilometrajeCajaTexto - kilometrajeActual);

            // mostrar el kilometraje que se a recorrido
            textViewRecorrido.setText(RECORRIDO_FROND + recorrido);

            auto.setKilometraje(String.valueOf(kilometrajeCajaTexto));

            // mantenimiento kodigo
            Mantenimiento m = new Mantenimiento();
            m.setFechaKilometraje("fechaDesdeBack");
            m.setGastos("gastosDesdeBack");
            m.setObservaciones("observacionDesdeBack");
            m.setTipoMantenimiento("tipoMantenimientoDesdeBack");
            persona.setMantenimiento(m);
            // mantenimiento kodigo

            databaseReference.child(PERSONA).child(persona.getUid()).setValue(persona);
            Toast.makeText(getActivity().getApplicationContext(), ACTUALIZADO, Toast.LENGTH_SHORT).show();

            grabarKilometrajeActualSesion(kilometrajeCajaTexto, recorrido);
            actualizarKilometrajeFrond(false);
            limpiarCajas();

    }

    private boolean validarKilometrajeMayorRegistrado(int kilometrajeActual, int kilometrajeCajaTexto) {
        boolean resultado = false;

        if (kilometrajeCajaTexto > kilometrajeActual) {
            resultado = true;
        } else {
            Toast.makeText(getActivity().getApplicationContext(), KILOMETRAJE_INGRESADO_DEBE_SER_MAYOR_AL_REGISTRADO, Toast.LENGTH_SHORT).show();
        }

        return resultado;
    }

    /**
     * Método usado para grabar en sesión el kilometraje
     *
     * @param kilometrajeActualizado
     */
    private void grabarKilometrajeActualSesion(int kilometrajeActualizado, String recorrido) {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KILOMETRAJE_ACTUAL, String.valueOf(kilometrajeActualizado));
        editor.putString(ACTUALIZAR_KILOMETRAJE, NO);
        editor.putString(RECORRIDO, recorrido);
        editor.commit();
    }

    /**
     * Método usado para activar/desactivar botón actualizar y entrada de texto.
     *
     * @param actualizar boolena que indica si se activa o desactiva la entrada.
     */
    private void actualizarKilometrajeFrond(boolean actualizar) {
        buttonActualizarKilometraje.setEnabled(actualizar);
        editTextKilometraje.setEnabled(actualizar);
    }
}