package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.OrderSuperfast.Vista.Global;
import com.OrderSuperfast.Vista.logActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ControladorLog extends ControladorGeneral{

    private logActivity myContext;

    /**
     * Crea un Controlador de Log asociado con la actividad de registro (logActivity).
     *
     * @param mContext El contexto de la actividad de registro (logActivity) al que se asocia este Controlador de Log.
     */
    public ControladorLog(logActivity mContext){
        this.myContext = mContext;
        removeOlderLines();
    }


    /**
     * Elimina líneas antiguas de un archivo de registro que contienen mensajes con fechas anteriores a seis meses.
     * Las líneas del archivo de registro que contienen mensajes con fechas anteriores a seis meses son excluidas
     * del archivo, manteniendo solo los registros recientes.
     */
    private void removeOlderLines() {
        System.out.println("date old enough 3");
        try {
            System.out.println("date old enough 2 " + myContext.getPackageName());

            SharedPreferences sharedIds = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);

            String idRest = sharedIds.getString("saveIdRest", "");


            String filePath = "/data/data/" + myContext.getPackageName() + "/files/" + "logChanges" + idRest + ".txt"; // Replace with your file path
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

            Calendar sixMonthsAgo = Calendar.getInstance();
            sixMonthsAgo.add(Calendar.MONTH, -6);
            File inputFile = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            StringBuilder newContent = new StringBuilder();

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Split the line into date and message parts
                String[] parts = currentLine.split(" ", 2);
                System.out.println("date old enough 1 " + parts[0] + " " + parts[1]);


                String dateStr = parts[0];
                String message = parts[1];

                try {
                    System.out.println("date log " + dateStr.toString());

                    Date logDate = dateFormat2.parse(dateStr);

                    if (logDate.after(sixMonthsAgo.getTime())) {
                        System.out.println("date old enough");
                        newContent.append(currentLine).append(System.getProperty("line.separator"));
                    }
                } catch (Exception e) {
                    // Handle date parsing errors if any
                    e.printStackTrace();
                }

            }

            reader.close();

            System.out.println("newContent " + newContent.toString());
            FileWriter writer = new FileWriter(inputFile, false);
            writer.write(newContent.toString());
            writer.close();

        } catch (Exception e) {

        }

    }


    /**
     * Lee información de un archivo y procesa los datos para su presentación en una vista específica.
     *
     * @param context El contexto asociado a la operación de lectura del archivo.
     * @return Una cadena formateada con los datos del archivo para mostrar en una vista.
     */
    private String readFromFile(Context context) {

        String ret = "";

        SharedPreferences sharedPreferences = myContext.getSharedPreferences("logPedido", Context.MODE_PRIVATE);

        String pedido = sharedPreferences.getString("pedido", "");


        try {
            SharedPreferences sharedIds = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
            String idRest = sharedIds.getString("saveIdRest", "");

            InputStream inputStream = context.openFileInput("logChanges" + idRest + ".txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                String fechaAnterior = "";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (pedido.equals("") || receiveString.contains(" " + pedido + " ")) {
                        System.out.println("log2 " + receiveString);
                        String[] arrayString = receiveString.split(" ");
                        String fechaNow = arrayString[0];

                        if (!fechaAnterior.equals("")) {
                            try {
                                Date now = format.parse(fechaNow);
                                Date before = format.parse(fechaAnterior);

                                if (now.after(before)) {
                                    String fechaPoner = format.format(now).replace("-", "/");

                                    stringBuilder.append("\n\n<br><br>   ").append("<br><b>" + fechaPoner + "</b>");
                                    myContext.addTextview(fechaPoner,false);


                                }


                                arrayString[0] = "";

                                System.out.println("log1 ");
                                arrayString[1] = "<b> •&nbsp;&nbsp;" + arrayString[1] + "</b>";
                                String str = TextUtils.join(" ", arrayString);
                                stringBuilder.append("\n\n<br><br> ").append(str);
                                myContext.addTextview(str,true);



                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            fechaAnterior = fechaNow;
                            String fechaPoner = fechaAnterior.replace("-", "/");
                            stringBuilder.append("\n\n<br><br>  ").append("<b>" + fechaPoner + "</b>");
                            myContext.addTextview(fechaPoner,false);
                            arrayString[0] = "";
                            arrayString[1] = "<b> •&nbsp;&nbsp;" + arrayString[1] + "</b>";
                            String str = TextUtils.join(" ", arrayString);
                            stringBuilder.append("\n\n<br><br> ").append(str);
                            myContext.addTextview(str,true);


                        }
                        fechaAnterior = fechaNow;
                        // stringBuilder.append("\n\n  ").append(receiveString);
                    }
                }
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("\n\n<br><br> No log found");
                }
                stringBuilder.append("\n\n<br><br>  ");

                inputStream.close();
                ret = stringBuilder.toString();


            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e);
        }
        System.out.println("logString " + ret);
        return ret;
    }





}
