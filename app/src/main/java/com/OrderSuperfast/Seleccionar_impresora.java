package com.OrderSuperfast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class Seleccionar_impresora extends AppCompatActivity {
    private ArrayList<Impresora> listaImpresoras = new ArrayList<>();
    private RecyclerView recyclerListaImpresoras;
    private int inset,angulo=0;
    private Display display;
    private Impresora itemSeleccionado;
    private ConstraintLayout botonPrueba, botonConfirmar,layoutVolver;
    private SharedPreferences sharedImpresoras;
    private SharedPreferences.Editor editor;
    private NetworkTask2 networkTask;
    private AdapterListaImpresoras adapter;
    private boolean stop=false;
    private Handler handlerCargando = new Handler();
    private ImageView imageCargando;


    @Override
    protected void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_impresora);

        sharedImpresoras=getSharedPreferences("impresoras",Context.MODE_PRIVATE);
        editor=sharedImpresoras.edit();

      //  listaImpresoras.addAll(((Global) this.getApplication()).getListaImpresoras());

        imageCargando=findViewById(R.id.imageViewCargando);

        recyclerListaImpresoras = findViewById(R.id.recyclerListaImpresoras);
        recyclerListaImpresoras.setHasFixedSize(true);
        recyclerListaImpresoras.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdapterListaImpresoras(listaImpresoras, this, new AdapterListaImpresoras.OnItemClickListener() {
            @Override
            public void onItemClick(Impresora item) {
                itemSeleccionado = item;
            }
        });

        recyclerListaImpresoras.setAdapter(adapter);

        layoutVolver=findViewById(R.id.layoutVolverConfImpresoras);
        layoutVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stop=true;
                stopHandlerCargando();
                onBackPressed();
            }
        });

        ponerInsets();
        inicializarBotones();

       // networkTask = new NetworkTask2();
        //networkTask.setAdapter(adapter);
        //networkTask.execute(this);

        buscarImpresoras();

    }


    private void ponerInsets() {
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        System.out.println("ROTACION " + display.getRotation());
        ConstraintLayout layoutPrincipal = findViewById(R.id.layoutPrincipal);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutPrincipal.setPadding(0, inset, 0, 0);

            } else {
                System.out.println("ROTACION 2 entra");
                if (display.getRotation() == Surface.ROTATION_90) {
                    layoutPrincipal.setPadding(inset, 0, 0, 0);


                } else {
                    System.out.println("ROTACION " + display.getRotation());
                    layoutPrincipal.setPadding(0, 0, inset, 0);


                }

            }
        }
    }

    private void inicializarBotones() {
        botonPrueba = findViewById(R.id.botonProbarImp);
        botonConfirmar = findViewById(R.id.botonConfirmarImp);

        botonPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarPeticionPruebaImpresora();

            }
        });

        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //peticion que envia la ip de la impresora seleccionada al servidor
                //de momento lo guarda en el dispositivo físico

                if (itemSeleccionado != null) {

                    String listaImpresorasString = sharedImpresoras.getString("lista", "");
                    if (!listaImpresorasString.equals("")) {

                        try {
                            JSONArray jsonArray = new JSONArray(listaImpresorasString);
                            JSONObject elemento = new JSONObject();
                            elemento.put("ip",itemSeleccionado.getIp() );
                            elemento.put("puerto",itemSeleccionado.getPuerto());
                            elemento.put("nombre","");

                            jsonArray.put(elemento);

                            editor.putString("lista",jsonArray.toString());
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else{
                        try {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject elemento = new JSONObject();
                            elemento.put("ip",itemSeleccionado.getIp() );
                            elemento.put("puerto",itemSeleccionado.getPuerto());
                            elemento.put("nombre","");

                            jsonArray.put(elemento);

                            editor.putString("lista",jsonArray.toString());
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    finish();
                }else{
                    Toast.makeText(Seleccionar_impresora.this, "Seleccione una impresora", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void enviarPeticionPruebaImpresora() {
        if (itemSeleccionado != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Realiza la operación de red aquí
                    // ...
                    try {
                        // Crear una instancia de URL


                        Socket socket = new Socket(itemSeleccionado.getIp(), itemSeleccionado.getPuerto());
                        System.out.println("socket hecho");
                        socket.setSoTimeout(4000);

                        OutputStream outputStream = socket.getOutputStream();

                        //socket.setSoTimeout(1000);

                        // Construir la solicitud IPP
                        byte[] command = new byte[]{27, 64, 27, 33, 0, 'H', 'o', 'l', 'a', ' ', 'm', 'u', 'n', 'd', 'o', 10,10,10,0x1B,0x69};
                      //  byte[] command = new byte[]{16,4,1};
                        // Enviar la solicitud IPP a la impresora
                        outputStream.write(command);
                        outputStream.flush();

                        System.out.println("parte de leer respuesta");

                        // Leer la respuesta de la impresora



                        System.out.println("parte de cerrar");

                        outputStream.close();
                        socket.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                        // Manejar errores de conexión o comunicación con la impresora
                    }

                    // Actualiza la interfaz de usuario si es necesario
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Actualiza la interfaz de usuario después de la operación de red
                            // ...
                        }
                    });

                }
            });


            thread.start();
        } else {
            Toast.makeText(this, "No se ha seleccionado dispositivo", Toast.LENGTH_SHORT).show();
        }


    }


    private void buscarImpresoras(){

        setHandlerCargando();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String ipAddress = getIPAddress(); // Obtiene la dirección IP del dispositivo
                    System.out.println("ips dispositivo"+ipAddress);

                    String subnetMask = getSubnetMask(); // Obtiene la máscara de subred del dispositivo
                    subnetMask="255.255.255.0";
                    System.out.println("ips "+subnetMask);

                    InetAddress inetAddress = InetAddress.getByName(ipAddress);
                    byte[] ipBytes = inetAddress.getAddress();
                    byte[] subnetBytes = InetAddress.getByName(subnetMask).getAddress();

                    for (int i = 1; i < ipBytes.length; i++) {
                        ipBytes[i] = (byte) (ipBytes[i] & subnetBytes[i]); // Aplica la máscara de subred a la dirección IP
                    }



                    // Realiza el escaneo de las direcciones IP en la red
                    int j=0;
                    for (int i = 2; i <= 255; i++) {
                        if(stop){
                            break;
                        }
                        ipBytes[3] = (byte) i;
                        InetAddress address = InetAddress.getByAddress(ipBytes);
                        System.out.println("ips "+address.getHostAddress());
                        //  printDocument(address.getHostAddress().getBytes(StandardCharsets.UTF_8));

                        //detectPrinter(address.getHostAddress(), community, oidDeviceName, oidDeviceModel);

                        if (address.isReachable(100)) {
                            System.out.println("ips si "+address.getHostAddress());
                            boolean conectado=ipConectada(address.getHostAddress(),9100);
                            if(conectado){
                                Impresora imp=new Impresora(address.getHostAddress(),9100);
                                listaImpresoras.add(imp);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            //peticionObtenerDatosDispositivo(address.getHostAddress());
                            //ipEsImpresora(address.getHostAddress());
                        }else{
                            System.out.println("ips no "+address.getHostAddress());

                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopHandlerCargando();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }

    private boolean ipConectada(String ip,int puerto){
        boolean estaConectado=false;
        try {
            // Establecer la conexión con la impresor
            System.out.println("socket");

            Socket socket = new Socket(ip, puerto);
            System.out.println("socket hecho");
            estaConectado=socket.isConnected();
            socket.setSoTimeout(1000);
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
            // Manejar errores de conexión o comunicación con la impresora
        }
        return estaConectado;
    }

    private  String getIPAddress() {
        // Obtén la dirección IP del dispositivo
        // Implementa la lógica para obtener la dirección IP del dispositivo en Android
        // Puedes usar la clase WifiManager o ConnectivityManager para obtener la dirección IP actual
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ipAddressString = String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
        return ipAddressString;

    }

    private String getSubnetMask() {
        // Obtén la máscara de subred del dispositivo
        // Implementa la lógica para obtener la máscara de subred del dispositivo en Android
        // Puedes usar la clase WifiManager o ConnectivityManager para obtener la máscara de subred actual
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int subnetMask = wifiManager.getDhcpInfo().netmask;
        System.out.println("subnet int "+subnetMask);

        String subnetMaskString = String.format("%d.%d.%d.%d",
                (subnetMask & 0xff),
                (subnetMask >> 8 & 0xff),
                (subnetMask >> 16 & 0xff),
                (subnetMask >> 24 & 0xff));
        System.out.println("subnet String "+subnetMaskString);
        return subnetMaskString;

    }

    private void setHandlerCargando(){
        imageCargando.setVisibility(View.VISIBLE);

        handlerCargando.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageCargando.setRotation(angulo);
                angulo++;
                handlerCargando.postDelayed(this,10);
            }
        },50);
    }

    private void stopHandlerCargando(){
        handlerCargando.removeCallbacksAndMessages(null);
        angulo=0;
        imageCargando.setVisibility(View.INVISIBLE);
    }




}