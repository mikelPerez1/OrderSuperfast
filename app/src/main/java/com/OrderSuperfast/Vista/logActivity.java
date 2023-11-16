package com.OrderSuperfast.Vista;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.R;

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
import java.util.Locale;

public class logActivity extends AppCompatActivity {

    TextView logTexto;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView imgNavBack, imgRest1, imgRest2, imgBack, imgBack2;
    private int inset = 0;
    private Display display;
    private CardView cardLog;
    private Resources resources;
    private ConstraintLayout barraVertical, barraHorizontal;
    private ScrollView scroll;
    private LinearLayout linearLog;
    private int dimen25;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);


        logTexto = findViewById(R.id.logText);


        sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ConstraintLayout l = findViewById(R.id.mainContainer);
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        System.out.println("insetLog " + inset);
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        initialize();
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);

        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //logTexto.setPadding(0, inset, 0, 0);
            } else {
                if (display.getRotation() == Surface.ROTATION_90) {
                    //  constraintNav.setPadding(0, 0, 0, 0);
                    //   logTexto.setPadding(inset, 0, 0, 0);

                } else {
                    System.out.println("ROTACION " + display.getRotation());

                    // constraintNav.setPadding(0, 0, inset, 0);
                    // logTexto.setPadding(0, 0, 0, 0);

                }

            }
        }

        removeOlderLines();
        String logResult = readFromFile(this);

        System.out.println("resultLog " + logResult);
        //logTexto.setText(Html.fromHtml(logResult));


        scroll.post(new Runnable() {
            @Override
            public void run() {
                int scrollHeight = scroll.getChildAt(0).getHeight();

// Desplaza el ScrollView hasta abajo sin animación
                scroll.scrollTo(0, scrollHeight);
            }
        });

    }

    private void initialize() {
        resources = getResources();
        imgRest1 = findViewById(R.id.imgRest1);
        imgRest2 = findViewById(R.id.imgRest2);
        imgBack = findViewById(R.id.imgBack);
        imgBack2 = findViewById(R.id.imgBack2);
        cardLog = findViewById(R.id.cardLog);
        barraVertical = findViewById(R.id.barraVertical);
        barraHorizontal = findViewById(R.id.barraHorizontal);
        scroll = findViewById(R.id.scroll);
        linearLog = findViewById(R.id.linearLog);
        dimen25 = (int) resources.getDimension(R.dimen.dimen25);

        changeOrientation();
        setCardMargins();
        setListeners();
    }


    private void setCardMargins() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardLog.getLayoutParams();
        int dim = (int) resources.getDimension(R.dimen.margen15dp);
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // params.setMargins(0,dim,dim,dim);
            // params.setMarginStart(0);
        } else {
            //  params.setMargins(0,dim,dim,0);
            //  params.setMarginStart(dim);
        }
        // cardLog.setLayoutParams(params);
    }

    private void setListeners() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void changeOrientation() {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            barraVertical.setVisibility(View.VISIBLE);
            barraHorizontal.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraVertical.getLayoutParams();
            params.setMargins(0, inset, 0, 0);
            barraVertical.setLayoutParams(params);

        } else {
            barraHorizontal.setVisibility(View.VISIBLE);
            barraVertical.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraHorizontal.getLayoutParams();
            params.setMarginStart(inset);
            barraHorizontal.setLayoutParams(params);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.remove("pedido");
        editor.commit();
    }


    private void removeOlderLines() {
        System.out.println("date old enough 3");
        try {
            System.out.println("date old enough 2 " + getPackageName());

            SharedPreferences sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);

            String idRest = ((Global) this.getApplication()).getIdRest();
            idRest = sharedIds.getString("saveIdRest", "");


            String filePath = "/data/data/" + getPackageName() + "/files/" + "logChanges" + idRest + ".txt"; // Replace with your file path
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

    private String readFromFile(Context context) {

        String ret = "";

        String pedido = sharedPreferences.getString("pedido", "");


        try {
            SharedPreferences sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);

            String idRest = ((Global) this.getApplication()).getIdRest();
            idRest = sharedIds.getString("saveIdRest", "");

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
                                    addTextview(fechaPoner,false);


                                }


                                arrayString[0] = "";

                                System.out.println("log1 ");
                                arrayString[1] = "<b> •&nbsp;&nbsp;" + arrayString[1] + "</b>";
                                String str = TextUtils.join(" ", arrayString);
                                stringBuilder.append("\n\n<br><br> ").append(str);
                                addTextview(str,true);



                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            fechaAnterior = fechaNow;
                            String fechaPoner = fechaAnterior.replace("-", "/");
                            stringBuilder.append("\n\n<br><br>  ").append("<b>" + fechaPoner + "</b>");
                            addTextview(fechaPoner,false);
                            arrayString[0] = "";
                            arrayString[1] = "<b> •&nbsp;&nbsp;" + arrayString[1] + "</b>";
                            String str = TextUtils.join(" ", arrayString);
                            stringBuilder.append("\n\n<br><br> ").append(str);
                            addTextview(str,true);


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


    private void addTextview(String texto,boolean margen) {
        TextView textView = new TextView(this);
        textView.setText(Html.fromHtml(texto));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height
        ));
        textView.setTextSize(16); // Set text size in sp
        textView.setTextColor(getResources().getColor(R.color.black, getTheme()));

        if(margen){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.setMarginStart(dimen25 + 40);
            params.setMargins(0,10,0,10);
            textView.setLayoutParams(params);
        }else{
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.setMarginStart(dimen25);
            textView.setTypeface(null,Typeface.BOLD);
            params.setMargins(0,80,0,10);

            textView.setLayoutParams(params);
        }
        linearLog.addView(textView);


    }

}