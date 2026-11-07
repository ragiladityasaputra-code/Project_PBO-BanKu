package com.mycompany.banku;

import java.util.ArrayList;

public class Nasabah extends Pengguna {
    private double saldo;
    private ArrayList<Transaksi> riwayat = new ArrayList<>();

    public Nasabah(String username, String nama, String password) {
        super(username, nama, password);
        this.saldo = 0;
    }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) {
        if (saldo >= 0) {
            this.saldo = saldo;
        }
    }

    public ArrayList<Transaksi> getRiwayat() { return riwayat; }

    public void tambahSaldo(double jumlah) { setSaldo(saldo + jumlah); }

    // return false kalau saldo tidak cukup
    public boolean kurangiSaldo(double jumlah) {
        if (jumlah > saldo) {
            return false;
        }
        setSaldo(saldo - jumlah);
        return true;
    }

    public void catat(Transaksi t) { riwayat.add(t); }

    @Override
    public String getPeran() { return "Nasabah"; }
}
