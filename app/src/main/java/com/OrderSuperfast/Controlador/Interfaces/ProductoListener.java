package com.OrderSuperfast.Controlador.Interfaces;

import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;

//interfaz que sirve para saber si se ha modificado la cantidad de un producto
//que estaba en el carrito en el apartado de pedido nuevo
public interface ProductoListener {
    void onCantidadChanged(ProductoPedido producto);
}
