package com.mycompany.banku;

public class Petugas extends Pengguna {
    private String idPetugas;

    public Petugas(String username, String nama, String password, String idPetugas) {
        super(username, nama, password);
        this.idPetugas = idPetugas;
    }

    public String getIdPetugas() { return idPetugas; }
    public void setIdPetugas(String idPetugas) { this.idPetugas = idPetugas; }

    @Override
    public String getPeran() { return "Petugas"; }
}
