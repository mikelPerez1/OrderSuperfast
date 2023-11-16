package com.OrderSuperfast.Modelo.Adaptadores;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoAbstracto;
import com.OrderSuperfast.R;
import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayList;
import java.util.List;

public class pAdapter extends FastScrollRecyclerView.Adapter<pAdapter.ViewHolder> implements FastScroller.SectionIndexer {
    private ArrayList<ProductoAbstracto> midata;
    private final LayoutInflater mInflaterProducto;
    private final Context context;
    final pAdapter.OnItemClickListener listener;
    private final pAdapter.OnSwitchClickListener listenerSwitch;
    private final ArrayList<Producto> lista;
    private final List<String> listaIDsProductos;
    private final List<String> listaIDsElementos;
    private ArrayList<Pair<Integer, Pair<String, Integer>>> cambios;
    private int n;
    private final int l;
    private final List<Pair<Integer, Character>> letrasProductos;
    private final List<Pair<Integer, Character>> letrasOpciones;
    private int inset = 0;
    private Display display;

    public pAdapter(ArrayList<ProductoAbstracto> itemList, Context context, pAdapter.OnItemClickListener listener, pAdapter.OnSwitchClickListener pListener) {
        this.mInflaterProducto = LayoutInflater.from(context);
        this.context = context;
        this.midata = itemList;
        this.listener = listener;
        this.listenerSwitch=pListener;
        this.lista = new ArrayList<>();
        this.n = 0;
        this.l = 0;
        this.listaIDsProductos = new ArrayList<>();
        this.listaIDsElementos = new ArrayList<>();
        this.letrasProductos = new ArrayList<>();
        this.letrasOpciones = new ArrayList<>();

    }

    @Override
    public CharSequence getSectionText(int position) {

        if (midata.get(position).getClaseTipo().equals("producto") || midata.get(position).getClaseTipo().equals("opcion")) {
            if (midata.get(position).getClaseTipo().equals("opcion")) {
                return "OPC";
            } else {
                Producto p=(Producto) midata.get(position);
                return p.getPrimeraLetra();
            }
        } else {
            if (position == 0 && midata.size() > 1) {
                return getSectionText(position + 1);
            } else {
                if (position > 1) {
                    return getSectionText(position - 1);
                } else {
                    return null;
                }
            }
        }


    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        FastScrollRecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        FastScrollRecyclerView r = (FastScrollRecyclerView) recyclerView;

        LinearLayoutManager llm = (LinearLayoutManager) manager;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                getSectionText(llm.findFirstVisibleItemPosition());


                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(ProductoAbstracto item);
    }

    public interface OnSwitchClickListener{
        void onSwitchClick(ProductoAbstracto item);
    }

    public void resetCambios() {
        if (n == 0) {
            for (int i = 0; lista.size() > i; i++) {
                lista.remove(i);

            }
            n = 1;
        }
    }

    @Override
    public int getItemCount() {
        return midata.size();
    }

    public List<String> getCambiosProductos() {
        return listaIDsProductos;
    }

    public List<String> getCambiosElementos() {
        return listaIDsElementos;
    }


    @Override
    public pAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflaterProducto.inflate(R.layout.list_producto, null);
        return new pAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final pAdapter.ViewHolder holder, int position) {
        holder.bindData(midata.get(position), position);
    }


    public void setItems(List<ProductoAbstracto> items) {
        midata = (ArrayList<ProductoAbstracto>) items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage, iconImage2;
        TextView pedido, idOculto, categoria, idPadre, txtVisibility, textLetra;
        Switch switch1;
        View linea;


        ViewHolder(View itemView) {
            super(itemView);

            pedido = itemView.findViewById(R.id.NpedidoTxt);
            switch1 = itemView.findViewById(R.id.switch2);
            idOculto = itemView.findViewById(R.id.tId);
            iconImage = itemView.findViewById(R.id.checkbox1);
            iconImage2 = itemView.findViewById(R.id.radio1);
            categoria = itemView.findViewById(R.id.TxtCambios);
            idPadre = itemView.findViewById(R.id.tIdPadre);
            txtVisibility = itemView.findViewById(R.id.txtVisibility);
            linea = itemView.findViewById(R.id.view2);

            //Listener del Switch
            /*
            switch1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    View v = (View) view.getParent();
                    TextView t = v.findViewById(R.id.NpedidoTxt);
                    TextView id = v.findViewById(R.id.tId);
                    TextView idPadre = v.findViewById(R.id.tIdPadre);
                    String idInt = (String) id.getText();
                    Integer idPadreInt = null;
                    TextView tBool = v.findViewById(R.id.textBool);

                    idPadreInt = Integer.valueOf((String) idPadre.getText()).intValue();
                    String tipo = (String) categoria.getText();
                    if (t != null) {

                        int i = 0;
                        boolean esta = false;
                        System.out.println(tBool.getText().toString());
                        if (tBool.getText().toString().equals("true")) {
                            while (i < listaIDsProductos.size() && !esta) {
                                if (listaIDsProductos.get(i).equals(idInt)) {
                                    esta = true;
                                }
                                i++;
                            }
                            if (esta) {

                                listaIDsProductos.remove(i - 1);
                            } else {
                                listaIDsProductos.add(idInt);

                            }


                        } else {
                            i = 0;
                            esta = false;
                            while (i < listaIDsElementos.size() && !esta) {
                                if (listaIDsElementos.get(i).equals(idInt)) {
                                    esta = true;
                                }
                                i++;
                            }
                            if (esta) {

                                listaIDsElementos.remove(i - 1);
                            } else {
                                listaIDsElementos.add(idInt);

                            }
                        }
                    }
                }
            });


             */

        }

        void bindData(final ProductoAbstracto item, int position) {


            txtVisibility.setText("");
            txtVisibility.setVisibility(View.INVISIBLE);
            linea.setVisibility(View.VISIBLE);
            ///////////////
            for (int l = 0; l < lista.size(); l++) {
                if (lista.get(l).getId() == item.getId() && lista.get(l).getClaseTipo().equals(item.getClaseTipo())) {
                    if (lista.get(l).getVisible()) {
                        item.setVisible(true);
                    } else {
                        item.setVisible(false);
                    }
                }
            }
            iconImage.setVisibility(View.INVISIBLE);
            iconImage2.setVisibility(View.INVISIBLE);
            idOculto.setText(String.valueOf(item.getId()));
            categoria.setText(item.getClaseTipo());
            System.out.println(item.getNombre());

            switch1.setChecked(item.getVisible());


            SharedPreferences prefInset = context.getSharedPreferences("inset", Context.MODE_PRIVATE);
            inset = prefInset.getInt("inset", 0);
            System.out.println("insetLog " + inset);
            display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();


            if (item.getTipo().equals("carta")) {

                switch1.setVisibility(View.INVISIBLE);
                pedido.setTextSize(28);
                pedido.setPadding(6, 50, 0, 20);
                pedido.setText(Html.fromHtml("<b>" + item.getNombre() + "</b>"));
                txtVisibility.setText("");
                txtVisibility.setVisibility(View.INVISIBLE);
                linea.setVisibility(View.INVISIBLE);
                prefInset = context.getSharedPreferences("inset", Context.MODE_PRIVATE);
                inset = prefInset.getInt("inset", 0);
                System.out.println("insetLog " + inset);
                if (inset > 0) {
                    if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        pedido.setPadding(0, inset, 0, 0);
                    } else {
                        if (display.getRotation() == Surface.ROTATION_90) {
                            pedido.setPadding(inset, 0, 0, 0);
                        } else {
                            pedido.setPadding(0, 0, 0, 0);

                        }

                    }
                }


            } else if (item.getClaseTipo().equals("producto")) {

                Character letra1 = item.getNombre().charAt(0);
                int n = letraProductoEsta(letra1);
                if (n == -1) {
                    letrasProductos.add(new Pair<Integer, Character>(position, letra1));

                    System.out.println("txtVis +" + txtVisibility.getText());
                    txtVisibility.setText(Html.fromHtml("<b>" + letra1 + "</b>"));
                    txtVisibility.setVisibility(View.VISIBLE);

                } else {
                    if (position == letrasProductos.get(n).first) {
                        txtVisibility.setText(letrasProductos.get(n).second.toString());
                        txtVisibility.setVisibility(View.VISIBLE);
                    } else {
                        txtVisibility.setText("");
                        txtVisibility.setVisibility(View.INVISIBLE);
                    }
                }

                pedido.setTypeface(Typeface.DEFAULT);
                pedido.setTextSize(20);
                pedido.setText(item.getNombre());
                switch1.setVisibility(View.VISIBLE);

               // idPadre.setText(String.valueOf(item.getIdPadre()));

                if (inset > 0) {
                    if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        pedido.setPadding(100, 0, 0, 0);
                        txtVisibility.setPadding(0, 0, 0, 0);

                    } else {
                        if (display.getRotation() == Surface.ROTATION_90) {
                            pedido.setPadding(inset + 100, 0, 0, 0);
                            txtVisibility.setPadding(inset, 0, 0, 0);
                        } else {
                            pedido.setPadding(100, 0, 0, 0);
                            txtVisibility.setPadding(0, 0, 0, 0);
                        }


                    }
                } else {
                    pedido.setPadding(100, 0, 0, 0);
                    txtVisibility.setPadding(0, 0, 0, 0);
                }


            } else if (item.getClaseTipo().equals("subcategoria")) {
                pedido.setPadding(100, 0, 0, 0);
                pedido.setTextSize(20);
                pedido.setText(Html.fromHtml("<b>" + item.getNombre() + "</b>"));

                switch1.setVisibility(View.INVISIBLE);
                txtVisibility.setText("");
                txtVisibility.setVisibility(View.INVISIBLE);

            } else if (item.getClaseTipo().equals("opcion")) {
                pedido.setPadding(100, 0, 0, 0);
                pedido.setTextSize(20);
                pedido.setTypeface(Typeface.DEFAULT_BOLD);
                linea.setVisibility(View.INVISIBLE);

                pedido.setText("• " + item.getNombre());
                switch1.setVisibility(View.INVISIBLE);
                txtVisibility.setText("");
                txtVisibility.setVisibility(View.INVISIBLE);

                if (inset > 0) {
                    if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        pedido.setPadding(100, 0, 0, 0);
                        txtVisibility.setPadding(0, 0, 0, 0);

                    } else {
                        if (display.getRotation() == Surface.ROTATION_90) {
                            pedido.setPadding(inset + 100, 0, 0, 0);
                            txtVisibility.setPadding(inset, 0, 0, 0);
                        } else {
                            pedido.setPadding(100, 0, 0, 0);
                            txtVisibility.setPadding(0, 0, 0, 0);
                        }


                    }
                } else {
                    pedido.setPadding(100, 0, 0, 0);
                    txtVisibility.setPadding(0, 0, 0, 0);

                }

                Character letra1 = item.getNombre().charAt(0);
                int n = letraOpcionEsta(letra1);
                if (n == -1) {
                    letrasOpciones.add(new Pair<Integer, Character>(position, letra1));

                    System.out.println("txtVis +" + txtVisibility.getText());
                    txtVisibility.setText(Html.fromHtml("<b>" + letra1 + "</b>"));
                    txtVisibility.setVisibility(View.INVISIBLE);

                } else {
                    if (position == letrasOpciones.get(n).first) {
                        txtVisibility.setText(letrasOpciones.get(n).second.toString());
                        txtVisibility.setVisibility(View.INVISIBLE);
                    } else {
                        txtVisibility.setText("");
                        txtVisibility.setVisibility(View.INVISIBLE);
                    }
                }


            } else if (item.getClaseTipo().equals("elemento")) {
                int pad = 200;
                pedido.setPadding(pad, 0, 0, 0);
                pedido.setTextSize(18);
                pedido.setTypeface(Typeface.DEFAULT);

                pedido.setText(item.getNombre());
                switch1.setVisibility(View.VISIBLE);
                //idPadre.setText(String.valueOf(item.getIdPadre()));

                if (item.getTipo().equals("check")) {
                    iconImage.setVisibility(View.VISIBLE);
                    iconImage.setPadding(pad - 50, 0, 0, 0);
                } else if (item.getTipo().equals("radio")) {
                    iconImage2.setVisibility(View.VISIBLE);
                    iconImage2.setPadding(pad - 70, 0, 0, 0);
                }


            }

            TextView tBool = itemView.findViewById(R.id.textBool);
            if (item.getClaseTipo().equals("producto")) {
                tBool.setText("true");
            } else {
                tBool.setText("false");

            }

            //////////////

            switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerSwitch.onSwitchClick(item);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }


    }

    private int letraProductoEsta(Character letra) {
        int i = 0;
        boolean esta = false;
        int num = -1;
        while (letrasProductos.size() > i) {
            if (letrasProductos.get(i).second.equals(letra)) {
                esta = true;
                num = i;
            }
            i++;
        }
        return num;
    }

    private int letraOpcionEsta(Character letra) {
        int i = 0;
        boolean esta = false;
        int num = -1;
        while (letrasOpciones.size() > i) {
            if (letrasOpciones.get(i).second.equals(letra)) {
                esta = true;
                num = i;
            }
            i++;
        }
        return num;
    }

}

