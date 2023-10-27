package com.OrderSuperfast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocalDataBase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "BDLocal.db";
    public static final String campoId = "ID";
    public static final String COLUMNA_ID = "numMesa";
    public static final String COLUMNA_fecha = "fecha";
    public static final String nombreTabla = "Pedidos";
    public static final String COLUMNA_minutos = "tiempo";
    private static final String SQL_CREAR = "create table "
            + nombreTabla + "(" + campoId + " integer primary key AUTOINCREMENT NOT NULL,"
            + COLUMNA_ID
            + " integer  not null, " + COLUMNA_fecha
            + " text not null, " + COLUMNA_minutos
            + " text );";

    private static final String nombreTablaNotificacion = "Notificacion";
    private static final String numPedido = "numPedido";
    private static final String fecha = "fecha";
    private static final String eliminado = "eliminado";

    private static final String createTable2 = "create table " + nombreTablaNotificacion + "(" + numPedido + " text primary key NOT NULL,"
            + fecha + " text not null,"
            + eliminado + " integer default 0 ); ";


    public LocalDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR);
        db.execSQL(createTable2);
        Log.d("crear", "creado");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }


    public void agregarNotificacion(String num, String fecha1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(numPedido, num);
        values.put(fecha, fecha1);
        db.insert(nombreTablaNotificacion, null, values);
        db.close();

    }

    public boolean existeNotificacion(String num) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(nombreTablaNotificacion,
                        null,
                        " numPedido = ?",
                        new String[]{num},
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {


            cursor.close();
            db.close();

            return true;

        } else {
            cursor.close();
            db.close();

            return false;
        }


    }

    public boolean notificacionParaEliminar(String numPedido) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(nombreTablaNotificacion,
                        null,
                        " numPedido = ?",
                        new String[]{numPedido},
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                if (cursor.getInt(2) == 1) {
                    cursor.close();
                    db.close();
                    return true;
                } else {
                    cursor.close();
                    db.close();

                    return false;
                }

            }


        } else {
            cursor.close();
            db.close();

            return true;
        }
        cursor.close();
        db.close();
        return true;
    }

    public void updateEliminarNo() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        System.out.println("entra en updateEliminarNo");
        values.put(eliminado, 0);


        int i = db.update(nombreTablaNotificacion,
                values,
                null,
                null);

        System.out.println("entra en updateEliminarNo " + i);

        db.close();

    }

    public void updateEliminar(String num) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(eliminado, 1);


        int i = db.update(nombreTablaNotificacion,
                values,
                " numPedido = ?",
                new String[]{num});
        db.close();

    }

    public void actualizarNotificacion(String num, String fecha1, int modo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(fecha, fecha1);
        values.put(eliminado, modo);


        int i = db.update(nombreTablaNotificacion,
                values,
                " numPedido = ?",
                new String[]{num});
        db.close();
    }

    public boolean eliminarNotificacion(String num) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(nombreTablaNotificacion,
                    " numPedido = ?",
                    new String[]{num});
            db.close();
            Log.d("eliminado", "si");
            return true;

        } catch (Exception ex) {
            Log.d("eliminado", "no");
            return false;
        }
    }


    public JSONArray obtenerNotificaciones() throws JSONException {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {numPedido, fecha, eliminado};
        ArrayList<Pair<String, String>> datos = new ArrayList<>();
        JSONArray datos2 = new JSONArray();
        Cursor cursor =
                db.query(nombreTablaNotificacion,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                //String a = cursor.getString(cursor.getColumnIndex(COLUMNA_fecha));
                Pair<String, String> d = new Pair<>(cursor.getString(0), cursor.getString(1));
                Log.d("s", "El nombre es " + d.second);

                datos.add(d);


                JSONObject data = new JSONObject();
                data.put("numPedido", cursor.getString(0));
                data.put("fecha", cursor.getString(1));
                data.put("eliminado", cursor.getInt(2));
                datos2.put(data);

            }
            cursor.close();

            return datos2;

        }

        db.close();
        return null;
    }


    public void agregar(int numMesa, String fecha, String minutos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMNA_ID, numMesa);
        values.put(COLUMNA_fecha, fecha);
        values.put(COLUMNA_minutos, minutos);

        db.insert(nombreTabla, null, values);
        Log.d("pedido", "agregado");
        db.close();
    }

    public boolean existe(int id, String fechaInicio, String tiempo) {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_fecha, COLUMNA_minutos};
        ArrayList<Pair<String, String>> datos = new ArrayList<>();
        Cursor cursor =
                db.query(nombreTabla,
                        null,
                        " numMesa = ? & fecha = ? & tiempo = ?",
                        new String[]{String.valueOf(id), fechaInicio, tiempo},
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            db.close();

            return true;

        } else {
            cursor.close();
            db.close();

            return false;
        }


    }

    public ArrayList<Pair<String, String>> obtener(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_fecha, COLUMNA_minutos};
        ArrayList<Pair<String, String>> datos = new ArrayList<>();
        Cursor cursor =
                db.query(nombreTabla,
                        projection,
                        " numMesa = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                //String a = cursor.getString(cursor.getColumnIndex(COLUMNA_fecha));
                Pair<String, String> d = new Pair<>(cursor.getString(0), cursor.getString(1));
                Log.d("s", "El nombre es " + d.second);

                datos.add(d);

            }
            cursor.close();

            return datos;

        }

        db.close();
        return null;
    }

    public void deleteLast(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {campoId, COLUMNA_fecha, COLUMNA_minutos};
        Cursor cursor =
                db.query(nombreTabla,
                        projection,
                        " numMesa = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {
            int idDelete = 0;
            while (cursor.moveToNext()) {
                //String a = cursor.getString(cursor.getColumnIndex(COLUMNA_fecha));
                idDelete = cursor.getInt(0);

            }
            if (idDelete != 0) {
                db.delete(nombreTabla,
                        " ID = ?",
                        new String[]{String.valueOf(idDelete)});
            }
            cursor.close();


        }

        db.close();

    }

    public String obtenerUltimo(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {campoId};
        Cursor cursor =
                db.query(nombreTabla,
                        projection,
                        null,
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount() > 0) {
            String num = "";
            while (cursor.moveToNext()) {


            }
            cursor.close();

            return num;

        }

        db.close();
        return null;
    }

    public void actualizar(int id, String minutos) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (!minutos.equals("")) {
            values.put(COLUMNA_minutos, minutos);
        }

        int i = db.update(nombreTabla,
                values,
                " numMesa = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean eliminar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(nombreTabla,
                    " numMesa = ?",
                    new String[]{String.valueOf(id)});
            db.close();
            Log.d("eliminado", "si");
            return true;

        } catch (Exception ex) {
            Log.d("eliminado", "no");
            return false;
        }
    }


}
