/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.banku;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author ragil
 */
public class DataStore {

    public static final ArrayList<Pengguna> daftarPengguna = new ArrayList<>();

    public static final Sampah[] katalog = {
        new Plastik(), new Kertas(), new Logam(), new Kaca()
    };

    public static final String[] namaBarang = {
        "Beras 1 kg", "Minyak goreng 1 liter", "Gula 1 kg"
    };
    public static final double[] hargaBarang = { 14000, 18000, 17000 };

    public static final String KODE_PETUGAS = "BANKU2026";

    static {
        // akun petugas bawaan biar bisa langsung dicoba
        daftarPengguna.add(new Petugas("admin", "Petugas Utama", "admin123", "PTG-001"));
    }

    private DataStore() { }

    public static Pengguna cariPengguna(String username) {
        for (Pengguna p : daftarPengguna) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return p;
            }
        }
        return null;
    }

    // null kalau username tidak ada atau bukan nasabah
    public static Nasabah cariNasabah(String username) {
        Pengguna p = cariPengguna(username);
        if (p instanceof Nasabah) {
            return (Nasabah) p;
        }
        return null;
    }

    public static int jumlahPetugas() {
        int n = 0;
        for (Pengguna p : daftarPengguna) {
            if (p instanceof Petugas) n++;
        }
        return n;
    }

    public static ArrayList<Nasabah> semuaNasabah() {
        ArrayList<Nasabah> hasil = new ArrayList<>();
        for (Pengguna p : daftarPengguna) {
            if (p instanceof Nasabah) hasil.add((Nasabah) p);
        }
        return hasil;
    }

    public static String rupiah(double nilai) {
        return String.format("Rp%,.0f", nilai).replace(',', '.');
    }

    // catat setoran, hasilnya nilai rupiah dari setoran itu
    public static double setor(Nasabah n, Sampah s, double beratKg) {
        double nilai = s.hitungNilai(beratKg); // polymorphism
        n.tambahSaldo(nilai);
        n.catat(new Transaksi(LocalDate.now(), "SETOR", s.getJenis(), beratKg, nilai));
        return nilai;
    }

    public static boolean tarikTunai(Nasabah n, double jumlah) {
        if (!n.kurangiSaldo(jumlah)) {
            return false;
        }
        n.catat(new Transaksi(LocalDate.now(), "TARIK", "Tarik tunai", 0, jumlah));
        return true;
    }

    public static boolean tukarBarang(Nasabah n, int idx) {
        if (!n.kurangiSaldo(hargaBarang[idx])) {
            return false;
        }
        n.catat(new Transaksi(LocalDate.now(), "TUKAR", namaBarang[idx], 0, hargaBarang[idx]));
        return true;
    }

    // rekap setoran per jenis, tanggal boleh null artinya tanpa batas
    public static ArrayList<String[]> rekap(LocalDate awal, LocalDate akhir) {
        double[] berat = new double[katalog.length];
        double[] nilai = new double[katalog.length];

        for (Nasabah n : semuaNasabah()) {
            for (Transaksi t : n.getRiwayat()) {
                if (!t.getTipe().equals("SETOR")) continue;
                if (awal != null && t.getTanggal().isBefore(awal)) continue;
                if (akhir != null && t.getTanggal().isAfter(akhir)) continue;
                for (int i = 0; i < katalog.length; i++) {
                    if (katalog[i].getJenis().equals(t.getKeterangan())) {
                        berat[i] += t.getBeratKg();
                        nilai[i] += t.getNominal();
                    }
                }
            }
        }

        ArrayList<String[]> baris = new ArrayList<>();
        double sumBerat = 0, sumNilai = 0;
        for (int i = 0; i < katalog.length; i++) {
            baris.add(new String[] {
                katalog[i].getJenis(),
                String.format("%.2f", berat[i]),
                rupiah(nilai[i])
            });
            sumBerat += berat[i];
            sumNilai += nilai[i];
        }
        baris.add(new String[] { "TOTAL", String.format("%.2f", sumBerat), rupiah(sumNilai) });
        return baris;
    }
}

