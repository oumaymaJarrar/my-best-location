package com.example.mybestlocation;

public class Position {
    int idPosition;
    String pseudo,number,longitude,latitude;

    public Position(int idPosition, String pseudo, String longitude, String latitude) {
        this.idPosition = idPosition;
        this.pseudo = pseudo;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Position( String pseudo, String number, String longitude, String latitude) {
        this.idPosition = idPosition;
        this.pseudo = pseudo;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public Position( int idPosition,String pseudo, String number, String longitude, String latitude) {
        this.idPosition = idPosition;
        this.pseudo = pseudo;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Position(String longitude, String latitude, String pseudo) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.pseudo = pseudo;
    }


    @Override
    public String toString() {
        return "Position{" +
                "idPosition=" + idPosition +
                ", pseudo='" + pseudo + '\'' +
                ", numero='" + number + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String numero) {
        this.number = numero;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
