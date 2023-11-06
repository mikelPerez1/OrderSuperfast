package com.OrderSuperfast;

import java.io.Serializable;

public class Importe implements Serializable {
    private String metodo_pago;
    private String total;
    private String impuesto;
    private String service_charge;
    private String propina = "";

    public Importe(String pMetodo,String pTotal,String pImpuesto,String pService,String pPropina){
        this.metodo_pago=pMetodo;
        this.total=pTotal;
        this.impuesto=pImpuesto;
        this.service_charge=pService;
        this.propina=pPropina;
    }

    //getters

    public String getMetodo_pago(){return this.metodo_pago;}

    public String getTotal(){return this.total;}

    public String getImpuesto(){return this.impuesto;}

    public String getService_charge(){return this.service_charge;}

    public String getPropina(){return this.propina;}
}
