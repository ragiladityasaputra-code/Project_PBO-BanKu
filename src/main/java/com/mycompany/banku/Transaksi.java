package com.mycompany.banku;

import java.time.LocalDate;

public class Transaksi {
    private LocalDate tanggal;
    private String tipe;        // SETOR, TUKAR, atau TARIK
    private String keterangan;
    private double beratKg;
    private double nominal;

    public Transaksi(LocalDate tanggal, String tipe, String keterangan,
                     double beratKg, double nominal) {
        this.tanggal = tanggal;
        this.tipe = tipe;
        this.keterangan = keterangan;
        this.beratKg = beratKg;
        this.nominal = nominal;
    }

    public LocalDate getTanggal() { return tanggal; }
    public String getTipe() { return tipe; }
    public String getKeterangan() { return keterangan; }
    public double getBeratKg() { return beratKg; }
    public double getNominal() { return nominal; }
}
