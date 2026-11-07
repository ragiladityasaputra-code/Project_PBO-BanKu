package com.mycompany.banku;

public class Kertas extends Sampah {
    public Kertas() { super("Kertas", 1500); }

    @Override
    public double hitungNilai(double beratKg) {
        return beratKg * getHargaPerKg(); // lurus saja, tanpa bonus
    }
}
