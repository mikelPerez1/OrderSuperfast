package com.OrderSuperfast;


import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.OrderSuperfast.Modelo.Adaptadores.AdapterListaImpresoras;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class NetworkTask2 extends AsyncTask<Activity, Void, List<String>> {
    private Seleccionar_impresora context;
    private AdapterListaImpresoras adaptador;

    private static final String PRINTER_IP_ADDRESS = "192.168.0.87";
    private static final int IPP_PORT = 9100;
    private String urlString = "http://192.168.0.87:9100";
    private boolean stop=false;

    public static void detectPrinter(String ipAddress, String community, OID oidDeviceName, OID oidDeviceModel) {
        try {
            // Crear el objeto de transporte y establecer la dirección IP y puerto
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Crear el objeto SNMP y establecer la versión
            Snmp snmp = new Snmp(transport);

            // Crear el objetivo de comunidad SNMP
            Address targetAddress = GenericAddress.parse("udp:" + ipAddress + "/161");
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setAddress(targetAddress);
            target.setVersion(SnmpConstants.version2c);

            // Crear el PDU para el comando SNMP GET
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(oidDeviceName));
            pdu.add(new VariableBinding(oidDeviceModel));
            pdu.setType(PDU.GET);

            // Enviar el PDU y recibir la respuesta
            ResponseEvent event = snmp.send(pdu, target);
            PDU response = event.getResponse();

            // Analizar la respuesta
            if (response != null) {
                String deviceName = response.getVariableBindings().get(0).getVariable().toString();
                String deviceModel = response.getVariableBindings().get(1).getVariable().toString();

                System.out.println("Nombre del dispositivo: " + deviceName);
                System.out.println("Modelo del dispositivo: " + deviceModel);
            } else {
                System.out.println("No se recibió respuesta del dispositivo.");
            }

            // Cerrar la conexión SNMP
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void obtenerIpsRapido(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        System.out.println("IP: " + inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            // Maneja los errores de socket
        }
    }


    public void printDocument(byte[] documentData) {

        try {
            // Crear una instancia de URL
            System.out.println("httpurl");
            URL url = new URL(urlString);


            // Abrir la conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            System.out.println("conexion");

            // Establecer el método de solicitud (GET, POST, etc.)
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            byte[] command = new byte[]{16,4,1};

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(command);
            outputStream.flush();
            outputStream.close();

            // Obtener la respuesta del servidor
            // int responseCode = connection.getResponseCode();
            System.out.println("httpurl respuesta");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("respuesta bien");
                // La solicitud se ha enviado correctamente
                // Puedes leer la respuesta del servidor si es necesario
            } else {
                System.out.println("respuesta mal");

                // Ocurrió un error al enviar la solicitud
                // Puedes realizar las acciones apropiadas según el código de respuesta
            }

            // Imprimir la respuesta del servidor
            //System.out.println("Response Code: " + responseCode);
            // System.out.println("Response Body: " + response.toString());

            // Cerrar la conexión
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar errores de conexión o lectura de datos
        }


 /*

        try {
            // Establecer la conexión con la impresor
            System.out.println("socket");

            Socket socket = new Socket(PRINTER_IP_ADDRESS, IPP_PORT);
            System.out.println("socket hecho");
            System.out.println("Connected to: " + PRINTER_IP_ADDRESS + ":" + IPP_PORT);
            socket.setSoTimeout(4000);

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            //socket.setSoTimeout(1000);

            // Construir la solicitud IPP
            byte[] ippRequest = buildIppPrintJobRequest(documentData);
            //byte[] command = new byte[]{27, 64, 27, 33, 0, 'H', 'o', 'l', 'a', ' ', 'm', 'u', 'n', 'd', 'o', 10,10,10,0x1B,0x69};
            byte[] command = new byte[]{16,4,1};
            // Enviar la solicitud IPP a la impresora
            outputStream.write(command);
            outputStream.flush();

            System.out.println("parte de leer respuesta");

            // Leer la respuesta de la impresora
            String s =readResponse(inputStream);


            // Procesar la respuesta de la impresora
            if(s!=null && s.length()>0) {
              //  processIppResponse(response);
                System.out.println("respuesta no nula");
            }
            System.out.println("parte de cerrar");
            // Cerrar los flujos y la conexión
            if(inputStream.available()>0){
                System.out.println("hay algo");
            }
            outputStream.close();
            inputStream.close();
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
            // Manejar errores de conexión o comunicación con la impresora
        }


  */

    }

    private byte[] buildIppPrintJobRequest(byte[] documentData) {
        // Construir la estructura IPP según las especificaciones
        // Aquí debes implementar la lógica para construir la solicitud IPP
        // Incluir atributos de operación, URL de impresión, datos del documento, etc.
        // Codificar la estructura en un arreglo de bytes y retornarlo
        return new byte[0];
    }

    private String readResponse(InputStream inputStream) throws IOException {
        // Leer y almacenar la respuesta de la impresora en un arreglo de bytes

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if(inputStream.available()>0) {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String response = new String(buffer, 0, bytesRead);
            System.out.println("respuesta " + response);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            String resp = outputStream.toString();
            System.out.println("resp "+resp);
            return response;
        }else{
            return "";
        }
    }

    private void processIppResponse(byte[] response) {
        String respuesta = response.toString();
        System.out.println("Respuesta "+respuesta);
        // Procesar y analizar la respuesta IPP de la impresora
        // Extraer información relevante como el estado de impresión, mensajes de error, etc.
        // Puedes utilizar bibliotecas o implementar tu propia lógica de análisis según las especificaciones IPP
    }

    public void setAdapter(AdapterListaImpresoras adapt){
        adaptador=adapt;
    }

    @Override
    protected List<String> doInBackground(Activity... params) {
        // Realiza aquí tus operaciones de red, como consultas HTTP, etc.
        // No realices ninguna operación que actualice la interfaz de usuario directamente desde aquí.
        context  =  (Seleccionar_impresora) params[0];
        ((Global) context.getApplication()).removeListaImpresoras();


        List<String> a = getUsedIPAddresses();

        if(!stop) {
            ArrayList<Impresora> listaImpresoras = ((Global) context.getApplication()).getListaImpresoras();
            for (int i = 0; i < listaImpresoras.size(); i++) {
                Impresora imp = listaImpresoras.get(i);
                System.out.println("ip impresora " + imp.getIp() + " y puerto " + imp.getPuerto());
            }

            //Intent i = new Intent(context, Seleccionar_impresora.class);
           // context.startActivity(i);
        }else{
            while(a.size()>0) {
                a.remove(0);
            }


        }
        return a;
    }

    private boolean ipConectada(String ip,int puerto){
        boolean estaConectado=false;
        try {
            // Establecer la conexión con la impresor
            System.out.println("socket");

            Socket socket = new Socket(ip, puerto);
            System.out.println("socket hecho");
            System.out.println("Connected to: " + PRINTER_IP_ADDRESS + ":" + IPP_PORT);
            estaConectado=socket.isConnected();
            socket.setSoTimeout(1000);
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
            // Manejar errores de conexión o comunicación con la impresora
        }
        return estaConectado;
    }

    public  List<String> getUsedIPAddresses() {
        List<String> usedIPAddresses = new ArrayList<>();

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
                        ((Global) context.getApplication()).anadirImpresora(imp);
                    }
                    //peticionObtenerDatosDispositivo(address.getHostAddress());
                    //ipEsImpresora(address.getHostAddress());
                    usedIPAddresses.add(address.getHostAddress());
                }else{
                    System.out.println("ips no "+address.getHostAddress());

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return usedIPAddresses;

    }

    private String getSubnetMask() {
        // Obtén la máscara de subred del dispositivo
        // Implementa la lógica para obtener la máscara de subred del dispositivo en Android
        // Puedes usar la clase WifiManager o ConnectivityManager para obtener la máscara de subred actual
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    private  String getIPAddress() {
        // Obtén la dirección IP del dispositivo
        // Implementa la lógica para obtener la dirección IP del dispositivo en Android
        // Puedes usar la clase WifiManager o ConnectivityManager para obtener la dirección IP actual
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ipAddressString = String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
        return ipAddressString;

    }

    private void ipEsImpresora(String ip){


        try {
            InetAddress inetAddress = InetAddress.getByName(ip);

            if (inetAddress.isReachable(1000)) {
                String hostName = inetAddress.getHostName();
                String canonicalHostName = inetAddress.getCanonicalHostName();

                System.out.println(hostName);
                String url= "<!DOCTYPE HTML><html><head></head><body><p>hola</p></body></html>";
                //  context.printDocument(ip,url);
                //printDocument(ip,url);
                // Verifica si el nombre de host o el nombre canónico contienen palabras clave asociadas con impresoras.
                if (hostName.contains("printer") || canonicalHostName.contains("printer")) {
                    Log.d("IP Verification", "La dirección IP pertenece a una impresora.");
                } else {
                    Log.d("IP Verification", "La dirección IP no parece ser una impresora.");
                }
            } else {
                Log.d("IP Verification", "La dirección IP no es alcanzable.");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void printDocument(String ipAddress,String url) {
        // Crea un WebView y configúralo para cargar el contenido a imprimir

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView(context.getApplicationContext());
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                        String jobName = "MyPrintJob";
                        PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

                        PrintAttributes.Builder builder = new PrintAttributes.Builder();
                        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
                        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());
                    }
                });

                webView.loadUrl(url);
            }
        });
    }


    @Override
    protected void onPostExecute(List<String> resultado) {
        // Aquí puedes realizar cualquier operación que actualice la interfaz de usuario
        // con los resultados obtenidos de la operación de red.




    }


    public void terminarProceso(){
        stop=true;
        this.cancel(true);

    }
}
