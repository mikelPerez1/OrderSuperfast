package com.OrderSuperfast;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class horaAdapter extends RecyclerView.Adapter<horaAdapter.ViewHolder> {
    private ArrayList<Pair<String, String>> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    private String t = "";
    // final horaAdapter.OnItemClickListener listener;


    int k = 1;

    public interface OnItemClickListener {
        void onItemClick(Pair<String, String> item);
    }


    public horaAdapter(ArrayList<Pair<String, String>> itemList, DescriptionActivity context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        //this.listener=listener;
    }

    public void delete() {
        while (mData.size() > 0) {
            mData.remove(0);
        }

        k = 0;

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }


    @Override
    public horaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_horas, null);
        return new horaAdapter.ViewHolder(view);
    }


    //
    ///

    @Override
    public void onBindViewHolder(final horaAdapter.ViewHolder holder, final int position) {
        boolean esprimero = position == 0;

        holder.bindData(mData.get(position), esprimero);
    }

    public void setItems(ArrayList<Pair<String, String>> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView horas;

        ViewHolder(View itemView) {
            super(itemView);
            horas = itemView.findViewById(R.id.tiempoAnadido);
        }


        void bindData(final Pair<String, String> item, boolean esprimero) {
            if (item != null) {
                String hora1 = item.first;
                String tiempo = item.second;


                String horaFinal = hora(hora1, tiempo);
                if (k < mData.size()) {

                    if (!t.equals("")) {
                        String[] s = t.split(":");
                        String[] h = hora1.split(":");
                        int h1 = Integer.valueOf(s[0]);
                        int h2 = Integer.valueOf(h[0]);

                        if (h1 == 0) {
                            h1 += 24;
                        }
                        if (h2 == 0) {
                            h2 += 24;
                        }

                        int llevada = h2 - h1;


                        int r = Integer.valueOf(s[1]) - (Integer.valueOf(h[1]) + (60 * llevada));
                        if (r > 0) {
                            r = Integer.valueOf(s[1]) - (Integer.valueOf(h[1]) + 60);
                        } else {
                            r = Math.abs(r);

                        }


                        horas.setText(context.getResources().getString(R.string.tiempoTranscurrido) + " " + r + " mins" + "\n" + context.getResources().getString(R.string.tiempoAñadido) + " " + tiempo);
                    } else {
                        horas.setText(context.getResources().getString(R.string.tiempoAñadido) + " " + tiempo);
                    }
                } else if (k >= mData.size()) {
                    if (!t.equals("")) {
                        String[] s = t.split(":");
                        String[] h = hora1.split(":");

                        int h1 = Integer.valueOf(s[0]);
                        int h2 = Integer.valueOf(h[0]);

                        if (h1 == 0) {
                            h1 += 24;
                        }
                        if (h2 == 0) {
                            h2 += 24;
                        }

                        int llevada = h2 - h1;


                        int r = Integer.valueOf(s[1]) - (Integer.valueOf(h[1]) + (60 * llevada));
                        if (r > 0) {
                            r = Integer.valueOf(s[1]) - (Integer.valueOf(h[1]) + 60);
                        } else {
                            r = Math.abs(r);
                        }


                        horas.setText(context.getResources().getString(R.string.tiempoTranscurrido) + " " + r + " mins" + "\n" + context.getResources().getString(R.string.tiempoAñadido) + " " + tiempo + "\n" + context.getResources().getString(R.string.horaEstimada) + " " + horaFinal + "h");
                    } else {

                        horas.setText(context.getResources().getString(R.string.tiempoAñadido) + " " + tiempo + "\n" + context.getResources().getString(R.string.horaEstimada) + " " + horaFinal + "h");
                    }
                }
                k++;
                if (esprimero) {
                    t = item.first;
                }
            }

        }

        private String hora(String hora1, String tiempo) {
            String[] horas = hora1.split(":");
            String[] tiempos = tiempo.split(" ");
            int tiempoInt = Integer.valueOf(tiempos[0]);
            int horasInt = Integer.valueOf(horas[0]);
            int minutosInt = Integer.valueOf(horas[1]);

            minutosInt += tiempoInt;
            if (minutosInt >= 60) {
                minutosInt -= 60;
                horasInt += 1;
            }
            String resultado;
            if (minutosInt < 10) {
                resultado = horasInt + ":0" + minutosInt;
            } else {
                resultado = horasInt + ":" + minutosInt;

            }

            return resultado;
        }

    }

}

