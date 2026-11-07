package com.mycompany.banku;

public class Logam extends Sampah {
    public Logam() { super("Logam", 5000); }

    @Override
    public double hitungNilai(double beratKg) {
        double nilai = beratKg * getHargaPerKg();
        if (beratKg >= 3) {
            nilai += nilai * 0.05; // logam berat dikasih bonus kecil
        }
        return nilai;
    }
}
