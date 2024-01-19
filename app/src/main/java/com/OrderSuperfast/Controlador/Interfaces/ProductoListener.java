package com.OrderSuperfast.Controlador.Interfaces;

import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;

public interface ProductoListener {
    void onCantidadChanged(ProductoPedido producto);
}
