package com.java.micarro.ui.home;

import static android.content.ContentValues.TAG;
import static com.java.micarro.Constantes.ACEITE;
import static com.java.micarro.Constantes.ACEITE_BANDERA;
import static com.java.micarro.Constantes.ACEPTAR;
import static com.java.micarro.Constantes.ACTUALIZADO;
import static com.java.micarro.Constantes.ACTUALIZAR_KILOMETRAJE;
import static com.java.micarro.Constantes.AMARILLO;
import static com.java.micarro.Constantes.APELLIDO_SESION;
import static com.java.micarro.Constantes.BATERIA;
import static com.java.micarro.Constantes.BATERIA_BANDERA;
import static com.java.micarro.Constantes.CHANNEL_ID;
import static com.java.micarro.Constantes.CONSUMO;
import static com.java.micarro.Constantes.CONTADOR_ACEITE;
import static com.java.micarro.Constantes.CONTADOR_BATERIA;
import static com.java.micarro.Constantes.CONTADOR_ELECTRICIDAD;
import static com.java.micarro.Constantes.CONTADOR_GASOLINA;
import static com.java.micarro.Constantes.CONTADOR_LLANTAS;
import static com.java.micarro.Constantes.CORREO_SESION;
import static com.java.micarro.Constantes.ELECTRICIDAD;
import static com.java.micarro.Constantes.ELECTRICIDAD_BANDERA;
import static com.java.micarro.Constantes.ESPACIO_BLACO;
import static com.java.micarro.Constantes.ESPACIO_VACIO;
import static com.java.micarro.Constantes.GASOLINA;
import static com.java.micarro.Constantes.GASOLINA_BANDERA;
import static com.java.micarro.Constantes.GRAFICO_CONSUMIBLES;
import static com.java.micarro.Constantes.IDENTIFICACION_SESION;
import static com.java.micarro.Constantes.INGRESE_CONSUMO;
import static com.java.micarro.Constantes.KILOMETRAJE_ACEITE_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_BATERIA_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_ELECTRICIDAD_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_EN_CERO;
import static com.java.micarro.Constantes.KILOMETRAJE_GASOLINA_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_INGRESADO_DEBE_SER_MAYOR_AL_REGISTRADO;
import static com.java.micarro.Constantes.KILOMETRAJE_INICIAL_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_LLANTAS_SESION;
import static com.java.micarro.Constantes.KILOMETRAJE_SESION;
import static com.java.micarro.Constantes.LLANTAS;
import static com.java.micarro.Constantes.LLANTAS_BANDERA;
import static com.java.micarro.Constantes.MANTENIMIENTO_NECESARIO;
import static com.java.micarro.Constantes.MARCA;
import static com.java.micarro.Constantes.MARCA_SESION;
import static com.java.micarro.Constantes.MODELO;
import static com.java.micarro.Constantes.MODELO_SESION;
import static com.java.micarro.Constantes.NO;
import static com.java.micarro.Constantes.NOMBRE_SESION;
import static com.java.micarro.Constantes.NOTIFICACION;
import static com.java.micarro.Constantes.NOTIFICACION_ID;
import static com.java.micarro.Constantes.PERSONA;
import static com.java.micarro.Constantes.PLACA;
import static com.java.micarro.Constantes.PLACA_SESION;
import static com.java.micarro.Constantes.REALIZO_MANTENIMIENTO_RESPECTIVO;
import static com.java.micarro.Constantes.RECORRIDO;
import static com.java.micarro.Constantes.RECORRIDO_FROND;
import static com.java.micarro.Constantes.REVISAR_CONSUMIBLES;
import static com.java.micarro.Constantes.ROJO;
import static com.java.micarro.Constantes.SHARED_LOGIN_DATA;
import static com.java.micarro.Constantes.SI;
import static com.java.micarro.Constantes.TELEFONO_SESION;
import static com.java.micarro.Constantes.ULTIMO_KILOMETRAJE;
import static com.java.micarro.Constantes.USTED_YA_REALIZO_EL_CAMBIO_SI_NO_EL_COSTO_DEL_CAMBIO_FUE;
import static com.java.micarro.Constantes.VERDE;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.java.micarro.NotificacionActivity;
import com.java.micarro.R;
import com.java.micarro.model.Auto;
import com.java.micarro.model.Mantenimiento;
import com.java.micarro.model.Persona;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

    private int contadorAceite = 0;
    private int contadorGasolina = 0;
    private int contadorLlantas = 0;
    private int contadorElectricidad = 0;
    private int contadorBateria = 0;

    private PendingIntent pendingIntent;

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

        inicializarVariables(root);
        identificacion = comun.obtenerValorSesion(getActivity(), IDENTIFICACION_SESION);
        cargarEntidadGlobalPersona(); // vuelve a consultar a la bdd
        cargarEtiquetasAuto(identificacion); // consulta a la bdd

        banderaActualizarKilometraje = comun.obtenerValorSesion(getActivity(), ACTUALIZAR_KILOMETRAJE);
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
            textViewRecorrido.setText("Recorrido: " + comun.obtenerValorSesion(getActivity(), RECORRIDO));

            //gráfico desde el kilometraje de sesion
            dibujarGraficoHorizontal();
        }
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

        int colorAceite = cargarColor(kilometrajeActual, ACEITE_BANDERA);
        int colorGasolina = cargarColor(kilometrajeActual, GASOLINA_BANDERA);
        int colorLlantas = cargarColor(kilometrajeActual, LLANTAS_BANDERA);
        int colorBateria = cargarColor(kilometrajeActual, BATERIA_BANDERA);
        int colorElectricidad = cargarColor(kilometrajeActual, ELECTRICIDAD_BANDERA);
        barDataSet.setColors(colorAceite, colorGasolina, colorLlantas, colorBateria, colorElectricidad);

        BarData barData = new BarData(barDataSet);
        horizontalBarChart.setData(barData);
        horizontalBarChart.setData(data);

        if (banderaActualizarKilometraje.equals(SI)) {
            if (colorAceite == ROJO || colorGasolina == ROJO || colorLlantas == ROJO || colorBateria == ROJO || colorElectricidad == ROJO) {

                int banderaKilometraje = 0;

                if (colorAceite == ROJO) {
                    banderaKilometraje = 5000;
                }
                if (colorLlantas == ROJO) {
                    banderaKilometraje = 10000;
                }
                if (colorBateria == ROJO) {
                    banderaKilometraje = 15000;
                }

                crearNotificacion(banderaKilometraje, String.valueOf(kilometrajeActual));
            }

            banderaActualizarKilometraje = NO;
        }

    }

    /**
     * Método usado para cargar kilometraje.
     *
     * @return
     */
    private int obtenerKilometraje() {
        int salida;

        if (banderaActualizarKilometraje.equals(NO)) {
            //salida = Integer.parseInt(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_SESION));
            salida = Integer.parseInt(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_SESION));

            //kodi
            persona = new Persona();
            persona.setUid(comun.obtenerValorSesion(getActivity(), IDENTIFICACION_SESION));
            persona.setNombre(comun.obtenerValorSesion(getActivity(), NOMBRE_SESION));
            persona.setApellido(comun.obtenerValorSesion(getActivity(), APELLIDO_SESION));
            persona.setTelefono(comun.obtenerValorSesion(getActivity(), TELEFONO_SESION));
            persona.setCorreo(comun.obtenerValorSesion(getActivity(), CORREO_SESION));

            List<Auto> autos = new ArrayList<>();

            Auto a = new Auto();
            a.setPlaca(comun.obtenerValorSesion(getActivity(), PLACA_SESION));
            a.setMarca(comun.obtenerValorSesion(getActivity(), MARCA_SESION));
            a.setModelo(comun.obtenerValorSesion(getActivity(), MODELO_SESION));
            a.setKilometrajeInicial(comun.obtenerValorSesion(getActivity(),KILOMETRAJE_INICIAL_SESION));
            a.setKilometraje(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_SESION));
            a.setKilometrajeAceite(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_ACEITE_SESION));
            a.setKilometrajeBateria(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_BATERIA_SESION));
            a.setKilometrajeElectricidad(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_ELECTRICIDAD_SESION));
            a.setKilometrajeGasolina(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_GASOLINA_SESION));
            a.setKilometrajeLlantas(comun.obtenerValorSesion(getActivity(), KILOMETRAJE_LLANTAS_SESION));

            autos.add(a);

            persona.setAuto(autos);
            //kodi

        } else {
            Auto auto = persona.getAuto().get(0);
            salida = obtenerKilometrajeBaseDatos();
        }

        return salida;
    }

    /**
     * Método usado para obener el kilometraje desde la base de datos.
     *
     * @return
     */
    private int obtenerKilometrajeBaseDatos() {
        Auto auto = persona.getAuto().get(0);
        int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        return kilometrajeActual;
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

        int contador = 0;
        if (contadorAceite > 0) {
            contador = contadorAceite;
        } else {
            Auto auto = persona.getAuto().get(0);
            contador = Integer.parseInt(auto.getKilometrajeAceite());
        }

        if (contador > 0) {
            validacion = validacion + (5000 * contador);
        }

        if (validacion >= 3000) {
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

        int contador = 0;
        if (contadorGasolina > 0) {
            contador = contadorGasolina;
        } else {
            Auto auto = persona.getAuto().get(0);
            contador = Integer.parseInt(auto.getKilometrajeGasolina());
        }

        if (contador > 0) {
            validacion = validacion + (5000 * contador);
        }

        if (validacion >= 3000) {
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

        int contador = 0;
        if (contadorLlantas > 0) {
            contador = contadorLlantas;
        } else {
            Auto auto = persona.getAuto().get(0);
            contador = Integer.parseInt(auto.getKilometrajeLlantas());
        }

        if (contador > 0) {
            validacion = validacion + (10000 * contador);
        }

        if (validacion >= 3000) {
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

        int contador = 0;
        if (contadorBateria > 0) {
            contador = contadorBateria;
        } else {
            Auto auto = persona.getAuto().get(0);
            contador = Integer.parseInt(auto.getKilometrajeBateria());
        }

        if (contador > 0) {
            validacion = validacion + (15000 * contador);
        }

        if (validacion >= 3000) {
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

        int contador = 0;
        if (contadorElectricidad > 0) {
            contador = contadorElectricidad;
        } else {
            Auto auto = persona.getAuto().get(0);
            contador = Integer.parseInt(auto.getKilometrajeElectricidad());
        }

        if (contador > 0) {
            validacion = validacion + (10000 * contador);
        }

        if (validacion >= 3000) {
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
     * Método usado para crear de notificación si el consumible necesita cambio.
     *
     * @param banderaKilometraje
     */
    private void crearNotificacion(int banderaKilometraje, String kilometrajeCajaTexto) {
        String stringBanderaKilometraje = String.valueOf(banderaKilometraje);
        crearVentanaEmergenteConsumibles(stringBanderaKilometraje, kilometrajeCajaTexto);
        crearNotificacionBarraSuperior(banderaKilometraje);
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

        int valorAceiteY = 5000 - kilometrajeActual;
        if (valorAceiteY <= 99) {
            valorAceiteY = valorAceiteY + 5000;
        }
        BarEntry barAceite = new BarEntry(4, valorAceiteY);
        barEntries.add(barAceite);


        int valorGasolinaY = 5000 - kilometrajeActual;
        if (valorGasolinaY < 99) {
            valorGasolinaY = valorGasolinaY + 5000;
        }
        BarEntry barGasolina = new BarEntry(3, valorGasolinaY);
        barEntries.add(barGasolina);


        int valorLlantasY = 10000 - kilometrajeActual;
        if (valorLlantasY < 99) {
            valorLlantasY = valorLlantasY + 10000;
        }
        BarEntry barLlantas = new BarEntry(2, valorLlantasY);
        barEntries.add(barLlantas);


        int valorBateriaY = 15000 - kilometrajeActual;
        if (valorBateriaY < 99) {
            valorBateriaY = valorBateriaY + 15000;
        }
        BarEntry barBateria = new BarEntry(1, valorBateriaY);
        barEntries.add(barBateria);


        int valorElectricidadY = 10000 - kilometrajeActual;
        if (valorElectricidadY < 99) {
            valorElectricidadY = valorElectricidadY + 10000;
        }
        BarEntry barElectricidad = new BarEntry(0, valorElectricidadY);
        barEntries.add(barElectricidad);
    }

    @Override
    public void onClick(View v) {

        String kilometrajeIngresado = editTextKilometraje.getText().toString();
        int kilometrajeCajaTexto = Integer.parseInt(kilometrajeIngresado);

        Auto auto = persona.getAuto().get(0);
        //int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        int kilometrajeActual = Integer.parseInt(auto.getKilometrajeInicial());

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
        final String identificacion = comun.obtenerValorSesion(getActivity(), IDENTIFICACION_SESION);

        databaseReference.child(PERSONA).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HomeFragment.this.persona = new Persona();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Persona p = objDataSnapshot.getValue(Persona.class);

                    if (identificacion.equals(p.getUid())) {
                        HomeFragment.this.persona = p;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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
                        //textViewKilometraje.setText(ULTIMO_KILOMETRAJE + per.getAuto().get(0).getKilometraje());
                        textViewKilometraje.setText(ULTIMO_KILOMETRAJE + per.getAuto().get(0).getKilometrajeInicial());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
    private void grabarKilometrajeActualSesion(int kilometrajeActualizado, String recorrido, Auto auto) {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KILOMETRAJE_SESION, auto.getKilometraje());

        editor.putString(ACTUALIZAR_KILOMETRAJE, NO);
        //editor.putString(RECORRIDO, recorrido);
        editor.putString(RECORRIDO, auto.getKilometraje());
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

    /**
     * Método usado para orquestar métodos necesarios para notificar en pantalla.
     */
    private void crearNotificacionBarraSuperior(int banderaKilometraje) {
        setPendingIntent();
        crearNotificaionChannel();
        crearNotificaion(banderaKilometraje);
    }

    /**
     * Método usado para direjirse en la notificación.
     */
    private void setPendingIntent() {

        Intent intent = new Intent(getActivity().getApplicationContext(), NotificacionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity().getApplicationContext());
        stackBuilder.addParentStack(NotificacionActivity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, pendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Método usado para crear el canal de notificación, necesario por la versión actual de android.
     */
    private void crearNotificaionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NOTIFICACION;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Método usado para crear notificación con los datos para ver en pantalla.
     */
    private void crearNotificaion(int banderaKilometraje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_directions_car_25);
        builder.setContentTitle(MANTENIMIENTO_NECESARIO + banderaKilometraje);
        builder.setContentText(USTED_YA_REALIZO_EL_CAMBIO_SI_NO_EL_COSTO_DEL_CAMBIO_FUE);
        builder.setColor(Color.RED);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000); // luz en el teléfono al notificar.
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity().getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }

    /**
     * Método usado para crear ventana emergente con alerta de realizar el mantenimiento.
     */
    private void crearVentanaEmergenteConsumibles(String banderaKilometraje, String kilometrajeCajaTexto) {

        final String bandera = banderaKilometraje;
        final String kilometrajeActual = kilometrajeCajaTexto;

        AlertDialog.Builder ventana = new AlertDialog.Builder(getActivity());
        ventana.setMessage(REALIZO_MANTENIMIENTO_RESPECTIVO)
                .setCancelable(false)
                .setPositiveButton(SI, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        crearVentanaEmergenteConsumos(dialog, bandera, kilometrajeActual);
                    }
                })
                .setNegativeButton(NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        ventana.setTitle(REVISAR_CONSUMIBLES);
        ventana.show();
    }

    /**
     * Método usado para crear ventana emergente para ingresar el consumo.
     *
     * @param dialog
     */
    private void crearVentanaEmergenteConsumos(DialogInterface dialog, String banderaKilometraje, String kilometrajeCajaTexto) {
        final EditText editTextConsumo = new EditText(getActivity());
        final String banderaIngresado = banderaKilometraje;
        final String kilometrajeIngresado = kilometrajeCajaTexto;

        editTextConsumo.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder ventanaConsumo = new AlertDialog.Builder(getActivity());
        ventanaConsumo.setView(editTextConsumo);
        ventanaConsumo.setTitle(CONSUMO);
        ventanaConsumo.setMessage(INGRESE_CONSUMO)
                .setCancelable(false)
                .setPositiveButton(ACEPTAR, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogConsumo, int which) {
                        String valorConsumo = editTextConsumo.getText().toString().trim();
                        encerarKilometraje(kilometrajeIngresado, valorConsumo, banderaIngresado);
                        dialogConsumo.cancel();

                        Toast.makeText(getActivity().getApplicationContext(), KILOMETRAJE_EN_CERO, Toast.LENGTH_LONG).show();
                    }
                });

        dialog.cancel();
        ventanaConsumo.show();
    }

    /**
     * Método usado para reiniciar el kilometraje del vehículo.
     */
    private void encerarKilometraje(String kilometrajeCajaTexto, String consumo, String banderaKilometraje) {
        actualizarKilometrajeConsumibles(kilometrajeCajaTexto, consumo, banderaKilometraje);

        Auto auto = persona.getAuto().get(0);
        int contadorA = Integer.parseInt(auto.getKilometrajeAceite());
        if (contadorA > 0) {
            dibujarGraficoHorizontal();
        }

        int contadorG = Integer.parseInt(auto.getKilometrajeGasolina());
        if (contadorG > 0) {
            dibujarGraficoHorizontal();
        }

        int contadorL = Integer.parseInt(auto.getKilometrajeLlantas());
        if (contadorL > 0) {
            dibujarGraficoHorizontal();
        }

        int contadorE = Integer.parseInt(auto.getKilometrajeElectricidad());
        if (contadorE > 0) {
            dibujarGraficoHorizontal();
        }

        int contadorB = Integer.parseInt(auto.getKilometrajeBateria());
        if (contadorB > 0) {
            dibujarGraficoHorizontal();
        }
    }

    /**
     * Método usado para actualizar kilometraje
     *
     * @param kilometrajeIngresado kilometraje a sumar al kilometraje actual.
     */
    private void actualizarKilometraje(String kilometrajeIngresado) {
        Auto auto = persona.getAuto().get(0);
        int kilometrajeCajaTexto = Integer.parseInt(kilometrajeIngresado);
        //int kilometrajeActual = Integer.parseInt(auto.getKilometraje());
        int kilometrajeActual = Integer.parseInt(auto.getKilometrajeInicial());

        String recorrido = obtenerKilometrajeRecorrido(kilometrajeCajaTexto, kilometrajeActual);

        mostrarRecorridoPantalla(recorrido, auto);


        grabarKilometrajeBaseDatos(kilometrajeIngresado, auto, recorrido);
        grabarKilometrajeActualSesion(kilometrajeCajaTexto, recorrido, auto);
        actualizarKilometrajeFrond(false);
        limpiarCajas();
    }

    /**
     * Método usado para grabar el kilometraje en la base de datos firebase.
     *
     * @param kilometrajeIngresado
     * @param auto
     */
    private void grabarKilometrajeBaseDatos(String kilometrajeIngresado, Auto auto, String recorrido) {
        setearKilometraje(kilometrajeIngresado, auto, recorrido);
        databaseReference.child(PERSONA).child(persona.getUid()).setValue(persona);
        Toast.makeText(getActivity().getApplicationContext(), ACTUALIZADO, Toast.LENGTH_SHORT).show();
    }

    /**
     * Método usado para setear el kilometraje que se ingreso en la caja de texto.
     *
     * @param kilometrajeIngresado
     * @param auto
     */
    private void setearKilometraje(String kilometrajeIngresado, Auto auto, String recorrido) {
        //auto.setKilometraje(kilometrajeIngresado);
        auto.setKilometrajeInicial(kilometrajeIngresado);
        int a = Integer.parseInt(auto.getKilometraje()) + Integer.parseInt(recorrido);
        auto.setKilometraje(String.valueOf(a));
    }

    /**
     * Método usado para mostrar el kilometraje recorrido.
     *
     * @param recorrido
     */
    private void mostrarRecorridoPantalla(String recorrido, Auto auto) {
        int a = Integer.parseInt(auto.getKilometraje()) + Integer.parseInt(recorrido);
        //auto.setKilometraje(String.valueOf(a));

        textViewRecorrido.setText(RECORRIDO_FROND + String.valueOf(a));
    }

    /**
     * Método usado para obtener el kilometraje recorrido.
     *
     * @param kilometrajeCajaTexto
     * @param kilometrajeActual
     * @return
     */
    private String obtenerKilometrajeRecorrido(int kilometrajeCajaTexto, int kilometrajeActual) {
        return String.valueOf(kilometrajeCajaTexto - kilometrajeActual);
    }

    /**
     * Método usado para actualizar kilometraje
     *
     * @param kilometrajeIngresado kilometraje a sumar al kilometraje actual.
     */
    private void actualizarKilometrajeConsumibles(String kilometrajeIngresado, String consumo, String banderaKilometraje) {

        Auto auto = persona.getAuto().get(0);
        int kilometrajeCajaTexto = Integer.parseInt(kilometrajeIngresado);

        cargarContadoresConsumibles(auto, banderaKilometraje);
        grabarMantenimiento(consumo, banderaKilometraje);

        databaseReference.child(PERSONA).child(persona.getUid()).setValue(persona);
        Toast.makeText(getActivity().getApplicationContext(), ACTUALIZADO, Toast.LENGTH_SHORT).show();

        grabarKilometrajeActualSesion(kilometrajeCajaTexto, kilometrajeIngresado, auto);
    }

    private void cargarContadoresConsumibles(Auto auto, String banderaKilometraje) {

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        switch (Integer.parseInt(banderaKilometraje)) {
            case 5000:
                int contadorA = 0;
                int contadorG = 0;

                contadorA = Integer.parseInt(auto.getKilometrajeAceite());
                contadorA = contadorA + 1;
                auto.setKilometrajeAceite(String.valueOf(contadorA));

                contadorG = Integer.parseInt(auto.getKilometrajeGasolina());
                contadorG = contadorG + 1;
                auto.setKilometrajeGasolina(String.valueOf(contadorG));


                editor.putString(KILOMETRAJE_ACEITE_SESION, String.valueOf(contadorA));
                editor.putString(KILOMETRAJE_GASOLINA_SESION, String.valueOf(contadorG));
                editor.commit();

                contadorAceite = contadorA;
                contadorGasolina = contadorG;
                break;

            case 10000:
                int contadorL = 0;
                int contadorE = 0;

                contadorL = Integer.parseInt(auto.getKilometrajeLlantas());
                contadorL = contadorL + 1;
                auto.setKilometrajeLlantas(String.valueOf(contadorL));


                contadorE = Integer.parseInt(auto.getKilometrajeElectricidad());
                contadorE = contadorE + 1;
                auto.setKilometrajeElectricidad(String.valueOf(contadorE));



                editor.putString(KILOMETRAJE_LLANTAS_SESION, String.valueOf(contadorL));
                editor.putString(KILOMETRAJE_ELECTRICIDAD_SESION, String.valueOf(contadorE));
                editor.commit();

                contadorLlantas = contadorL;
                contadorElectricidad = contadorE;
                break;

            case 15000:
                int contadorB = 0;
                contadorB = Integer.parseInt(auto.getKilometrajeBateria());
                contadorB = contadorB + 1;
                auto.setKilometrajeBateria(String.valueOf(contadorB));

                editor.putString(KILOMETRAJE_BATERIA_SESION, String.valueOf(contadorB));

                editor.commit();

                contadorBateria = contadorB;
                break;
        }
    }

    private void grabarContadorSesion(String cadena, int contador) {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(cadena, String.valueOf(contador));
        editor.commit();
    }

    /**
     * Método usado para grabar el mantenimiento realizado.
     *
     * @param consumo
     */
    private void grabarMantenimiento(String consumo, String banderaKilometrake) {
        List<Mantenimiento> mantenimientos = new ArrayList<Mantenimiento>();

        Mantenimiento m = new Mantenimiento();
        m.setFechaKilometraje(recuperarFechaSistema());
        m.setGastos(consumo + " $");
        m.setObservaciones("Mantenimiento OK");
        m.setTipoMantenimiento(banderaKilometrake + " km.");

        if (persona.getMantenimiento() != null) {
            mantenimientos = persona.getMantenimiento(); //recuperó los mantenimientos de la bdd
        }

        mantenimientos.add(m); // se agregan el mantenimiento actual

        persona.setMantenimiento(mantenimientos);
    }

    /**
     * Método usado para recuperar fecha y hora del sistema.
     *
     * @return
     */
    private String recuperarFechaSistema() {
        Calendar fecha = new GregorianCalendar();

        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);

        return dia + "/" + (mes + 1) + "/" + año + ESPACIO_BLACO + hora + ":" + minuto + ":" + segundo;
    }
}