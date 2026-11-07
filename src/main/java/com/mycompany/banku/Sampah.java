package com.mycompany.banku;

public abstract class Sampah {
    private String jenis;
    private double hargaPerKg;

    public Sampah(String jenis, double hargaPerKg) {
        this.jenis = jenis;
        this.hargaPerKg = hargaPerKg;
    }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public double getHargaPerKg() { return hargaPerKg; }
    public void setHargaPerKg(double hargaPerKg) {
        if (hargaPerKg >= 0) {
            this.hargaPerKg = hargaPerKg;
        }
    }

    // tiap turunan punya cara hitung sendiri (polymorphism)
    public abstract double hitungNilai(double beratKg);
}
