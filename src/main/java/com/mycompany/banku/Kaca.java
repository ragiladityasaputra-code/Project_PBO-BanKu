package com.mycompany.banku;

public class Kaca extends Sampah {
    public Kaca() { super("Kaca", 500); }

    @Override
    public double hitungNilai(double beratKg) {
        double nilai = beratKg * getHargaPerKg();
        nilai -= 200; // potongan biaya penanganan pecahan
        return nilai < 0 ? 0 : nilai;
    }
}
