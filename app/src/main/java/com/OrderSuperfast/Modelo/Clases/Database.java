package com.OrderSuperfast.Modelo.Clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Base de datos local que se utiliza para guardar los productos que se han tachado de los pedidos y que pedidos no se han visto todavia
 */
public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "OrderSuperfast.db";
    private static final int DATABASE_VERSION = 8;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla en la base de datos
        db.execSQL("CREATE TABLE pedidos (numero_pedido INTEGER,id_restaurante TEXT,es_nuevo BOOLEAN,PRIMARY KEY (numero_pedido,id_restaurante))");
        db.execSQL("CREATE TABLE productos (\n" +
                "    id_producto TEXT PRIMARY KEY,\n" +
                "    nombre TEXT\n" +
                ");\n");

        db.execSQL("CREATE TABLE detalles_pedido (\n" +
                "    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    numero_pedido INTEGER,\n" +
                "    id_producto INTEGER,\n" +
                "    posicion INTEGER,\n" +
                "    FOREIGN KEY (numero_pedido) REFERENCES pedidos_nuevos(numero_pedido),\n" +
                "    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)\n" +
                ");");
        db.execSQL("CREATE TABLE pedidos_nuevos (id_restaurante TEXT, numero_pedido INTEGER,nuevo BOOLEAN,PRIMARY KEY (id_restaurante,numero_pedido),FOREIGN KEY (id_restaurante) REFERENCES pedidos(id_restaurante))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Manejar actualizaciones de la base de datos si la estructura cambia
        db.execSQL("DROP TABLE IF EXISTS pedidos");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS detalles_pedido");
        db.execSQL("DROP TABLE IF EXISTS pedidos_nuevos");
        onCreate(db);
    }

    public void agregarPedido(int numeroPedido, String idRestaurante, boolean esNuevo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("numero_pedido", numeroPedido);
        values.put("id_restaurante", idRestaurante);
        values.put("es_nuevo", esNuevo);
        db.insert("pedidos", null, values);
        db.close();
    }


    public void borrarPedido(int numeroPedido, String idRestaurante) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pedidos", "numero_pedido = ? AND id_restaurante = ?", new String[]{String.valueOf(numeroPedido), idRestaurante});
        db.close();
    }

    public boolean existePedido(int numeroPedido, String idRestaurante) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos WHERE numero_pedido = ? AND id_restaurante = ?", new String[]{String.valueOf(numeroPedido), idRestaurante});
        boolean existe = (cursor.getCount() > 0);
        cursor.close();
        return existe;
    }

    public boolean pedidoEsNuevo(int numeroPedido, String idRestaurante) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos WHERE numero_pedido = ? AND id_restaurante = ? AND es_nuevo = true", new String[]{String.valueOf(numeroPedido), idRestaurante});
        boolean existe = (cursor.getCount() > 0);
        cursor.close();
        return existe;
    }

    public boolean agregarPedidoSiNoExiste(int numeroPedido, String idRestaurante) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("pedido agregar "+numeroPedido);
        // Verificar si ya existe el pedido
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos WHERE numero_pedido = ? AND id_restaurante = ?",
                new String[]{String.valueOf(numeroPedido), idRestaurante});

        boolean existe = cursor.getCount() > 0;
        cursor.close();

        if (!existe) {
            // El pedido no existe, entonces lo agregamos
            ContentValues values = new ContentValues();
            values.put("numero_pedido", numeroPedido);
            values.put("id_restaurante", idRestaurante);

            long result = db.insert("pedidos", null, values);
            return result != -1; // Retorna true si la inserción fue exitosa
        }

        // El pedido ya existe, no se agrega nuevamente
        return false;
    }


    public void setPedidoNuevo(String idRestaurante, int numPedido){
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si ya existe el pedido
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos WHERE id_restaurante = ? AND numero_pedido = ?",
                new String[]{idRestaurante, String.valueOf(numPedido)});

        boolean existePedido = cursor.getCount() > 0;
        cursor.close();

        // Actualizar o insertar según sea necesario
        ContentValues values = new ContentValues();
        values.put("id_restaurante", idRestaurante);
        values.put("numero_pedido", numPedido);
        values.put("nuevo", true);

        if (existePedido) {
            // El pedido ya existe, actualizar el estado a true
            db.update("pedidos", values, "id_restaurante = ? AND numero_pedido = ?",
                    new String[]{idRestaurante, String.valueOf(numPedido)});
        } else {
            // El pedido no existe, insertar un nuevo pedido con el estado a true
            db.insert("pedidos", null, values);
        }

        db.close();
    }

    public ArrayList<Integer> obtenerPedidosPorRestaurante(String idRestaurante) {
        ArrayList<Integer> listaPedidos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT numero_pedido FROM pedidos_nuevos WHERE id_restaurante = ?", new String[]{idRestaurante});


        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("numero_pedido");
            if (columnIndex != -1) {
                do {
                    int numeroPedido = cursor.getInt(columnIndex);
                    listaPedidos.add(numeroPedido);
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return listaPedidos;
    }


    public void agregarProductoAlPedidoSiNoExiste(int numeroPedido, String idProducto, int posicion) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si ya existe el enlace entre el producto y el pedido
        Cursor cursor = db.rawQuery("SELECT * FROM detalles_pedido WHERE numero_pedido = ? AND id_producto = ?",
                new String[]{String.valueOf(numeroPedido), String.valueOf(idProducto)});

        boolean existeEnlace = cursor.getCount() > 0;
        cursor.close();

        if (!existeEnlace) {
            // El enlace no existe, entonces lo agregamos
            ContentValues values = new ContentValues();
            values.put("numero_pedido", numeroPedido);
            values.put("id_producto", idProducto);
            values.put("posicion", posicion);
            db.insert("detalles_pedido", null, values);
        }

        db.close();
    }


    public void borrarProductoAlPedidoActualizando(int numeroPedido, String idProducto, int posicion) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar el enlace existente si existe
        db.delete("detalles_pedido", "numero_pedido = ? AND id_producto = ? AND posicion = ?",
                new String[]{String.valueOf(numeroPedido), idProducto,String.valueOf(posicion)});

        db.close();
    }

    // Otra función para eliminar un producto de un pedido
    public void eliminarProductoDelPedido(int numeroPedido, int idProducto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("detalles_pedido", "numero_pedido = ? AND id_producto = ?",
                new String[]{String.valueOf(numeroPedido), String.valueOf(idProducto)});
        db.close();
    }


    public boolean estaTachado(String idRest,int numPedido,int posicion){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM detalles_pedido d\n" +
                "INNER JOIN pedidos ped ON d.numero_pedido = ped.numero_pedido\n"+
                "WHERE ped.id_restaurante = ? AND d.numero_pedido = ? AND d.posicion = ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{idRest,String.valueOf(numPedido), String.valueOf(posicion)});

        // Verificar si se encontró algún resultado

        boolean existeEnlace = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }


        return existeEnlace;
    }


    // Función para obtener los productos enlazados a un pedido
    public ArrayList<Integer> obtenerProductosPorPedido(int numeroPedido) {
        ArrayList<Integer> listaProductos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener los productos enlazados a un pedido específico
        String query = "SELECT d.cantidad FROM detalles_pedido d\n" +
                "INNER JOIN productos p ON d.id_producto = p.id\n" +
                "WHERE d.numero_pedido = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(numeroPedido)});

        // Procesar el resultado de la consulta
        if (cursor != null && cursor.moveToFirst()) {

            int posicionProducto = cursor.getColumnIndex("posicion");

            do {
                int posicion = cursor.getInt(posicionProducto);
                // Puedes agregar más campos según la estructura de tu tabla productos

                // Crear un objeto Producto y agregarlo a la lista
                listaProductos.add(posicionProducto);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return listaProductos;
    }

    public boolean esNuevo(String idRest,int numeroPedido){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT 1 FROM pedidos_nuevos n INNER JOIN pedidos p ON n.id_restaurante = p.id_restaurante WHERE n.id_restaurante = ? AND n.numero_pedido = ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{idRest,String.valueOf(numeroPedido)});

        boolean esNuevo = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }


        return esNuevo;
    }




    public void borrarPedidoNuevo( String idRestaurante,int numeroPedido) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pedidos_nuevos", "numero_pedido = ? AND id_restaurante = ?", new String[]{String.valueOf(numeroPedido), idRestaurante});
        db.close();
    }


    public boolean estaNuevoPedido(String idRestaurante, int numeroPedido){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos_nuevos n INNER JOIN pedidos p ON n.id_restaurante = p.id_restaurante WHERE n.numero_pedido = ? AND n.id_restaurante = ?",
                new String[]{String.valueOf(numeroPedido), idRestaurante});

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        System.out.println("esta el pedido "+numeroPedido +" "+existe);
        return existe;
    }


    public void añadirNuevo(String idRestaurante, int numPedido) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si existe en la primera tabla
        boolean existeEnPrimeraTabla = existeElementoEnPrimeraTabla(idRestaurante, numPedido);

        // Verificar si existe en la segunda tabla
        boolean existeEnSegundaTabla = existeElementoEnSegundaTabla(numPedido,idRestaurante);

        if (!existeEnPrimeraTabla && !existeEnSegundaTabla) {
            // No existe en ninguna tabla, crear nuevo elemento en ambas
            ContentValues valoresPrimeraTabla = new ContentValues();
            valoresPrimeraTabla.put("id_restaurante", idRestaurante);
            valoresPrimeraTabla.put("numero_pedido", numPedido);
            db.insert("pedidos", null, valoresPrimeraTabla);

            ContentValues valoresSegundaTabla = new ContentValues();
            valoresSegundaTabla.put("id_restaurante", idRestaurante);
            valoresSegundaTabla.put("numero_pedido", numPedido);
            valoresSegundaTabla.put("nuevo", true);
            db.insert("pedidos_nuevos", null, valoresSegundaTabla);

        } else if (!existeEnPrimeraTabla) {
            // No existe en la primera tabla, pero existe en la segunda
            ContentValues valoresPrimeraTabla = new ContentValues();
            valoresPrimeraTabla.put("id_restaurante", idRestaurante);
            valoresPrimeraTabla.put("numero_pedido", numPedido);
            db.insert("pedidos", null, valoresPrimeraTabla);
        }else if (!existeEnSegundaTabla) {
            // No existe en la segunda tabla, pero existe en la primera
            ContentValues valoresSegundaTabla = new ContentValues();
            valoresSegundaTabla.put("id_restaurante", idRestaurante);
            valoresSegundaTabla.put("numero_pedido", numPedido);
            valoresSegundaTabla.put("nuevo", true);
            db.insert("pedidos_nuevos", null, valoresSegundaTabla);
        }

        // Cierra la conexión de la base de datos
        db.close();
    }

    // Métodos auxiliares para verificar existencia en cada tabla
    private boolean existeElementoEnPrimeraTabla(String idRestaurante, int numPedido) {
        SQLiteDatabase db = this.getReadableDatabase();
        String consulta = "SELECT * FROM pedidos WHERE id_restaurante = ? AND numero_pedido = ?";
        Cursor cursor = db.rawQuery(consulta, new String[]{String.valueOf(idRestaurante), String.valueOf(numPedido)});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    private boolean existeElementoEnSegundaTabla(int numPedido, String idRest) {
        SQLiteDatabase db = this.getReadableDatabase();
        String consulta = "SELECT * FROM pedidos_nuevos WHERE numero_pedido = ? AND id_restaurante = ?";
        Cursor cursor = db.rawQuery(consulta, new String[]{String.valueOf(numPedido),idRest});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

}
