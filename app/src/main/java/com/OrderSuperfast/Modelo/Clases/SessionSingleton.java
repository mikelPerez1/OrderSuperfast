package com.OrderSuperfast.Modelo.Clases;


/**
 * clase que contiene los datos de la sesión actual
 *
 */
public class SessionSingleton {
    private static SessionSingleton instance;

    // Atributos de la sesión
    private String restaurantId;
    private String zoneId;
    private String deviceId;
    private String restaurantName;
    private String zoneName;
    private String deviceName;
    private String restaurantImage;

    // Constructor privado para evitar la creación directa de instancias
    private SessionSingleton() {
        // Inicialización opcional de los atributos de la sesión
        restaurantId = "";
        zoneId = "";
        deviceId = "";
        restaurantName = "";
        zoneName = "";
        deviceName = "";
        restaurantImage = "";
    }

    // Método para obtener la instancia única de la clase
    public static synchronized SessionSingleton getInstance() {
        if (instance == null) {
            instance = new SessionSingleton();
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    // Métodos para obtener y establecer el ID del restaurante
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    // Métodos para obtener y establecer los demás atributos de la sesión
    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRestaurantName(){return this.restaurantName;}

    public void setRestaurantName(String restaurantName){this.restaurantName = restaurantName;}

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRestaurantImage(){return this.restaurantImage;}

    public void setRestaurantImage(String restaurantImage){this.restaurantImage = restaurantImage;}
}
