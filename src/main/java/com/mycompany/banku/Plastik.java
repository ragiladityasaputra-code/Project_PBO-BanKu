package com.mycompany.banku;

public class Plastik extends Sampah {
    public Plastik() { super("Plastik", 3000); }

    @Override
    public double hitungNilai(double beratKg) {
        double nilai = beratKg * getHargaPerKg();
        if (beratKg >= 5) {
            nilai += nilai * 0.10; // setoran banyak, dapat bonus 10 persen
        }
        return nilai;
    }
}
