package com.OrderSuperfast;

public class Producto extends ProductoAbstracto{


    private final String clase;


    private int num;

    private final boolean esProducto;
    private Integer idPadre;
    private final String primeraLetra;

    public Producto(String name, String clas, String id, boolean visible, boolean esProducto,String claseTipo) {
        super(id,name,clas,visible,claseTipo);
        this.clase = clas;
        this.num = 0;
        this.esProducto = esProducto;
        this.idPadre = 0;
        this.primeraLetra = String.valueOf(name.charAt(0));
    }

    public String getPrimeraLetra() {
        return this.primeraLetra;
    }

    public int getIdPadre() {
        return this.idPadre;
    }

    public void setIdPadre(int id) {
        this.idPadre = id;
    }

    public boolean getEsProducto() {
        return this.esProducto;
    }

    public String getClase() {
        return this.clase;
    }


    public int getNum() {
        return this.num;
    }



    public void setNum(int i) {
        this.num = i;
    }


}
