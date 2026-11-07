package com.mycompany.banku;

public abstract class Pengguna {
    private String username;
    private String nama;
    private String password; // private, diakses lewat getter setter

    public Pengguna(String username, String nama, String password) {
        this.username = username;
        this.nama = nama;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password != null && password.length() >= 4) {
            this.password = password;
        }
    }

    public boolean cekPassword(String input) {
        return this.password.equals(input);
    }

    public abstract String getPeran();
}
