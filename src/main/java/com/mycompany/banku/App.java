package com.mycompany.banku;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Pengguna> daftarPengguna = new ArrayList<>();
    static Sampah[] katalog = { new Plastik(), new Kertas(), new Logam(), new Kaca() };

    // barang kebutuhan pokok yang bisa ditukar
    static String[] namaBarang = { "Beras 1 kg", "Minyak goreng 1 liter", "Gula 1 kg" };
    static double[] hargaBarang = { 14000, 18000, 17000 };

    static final String KODE_PETUGAS = "BANKU2026";

    public static void main(String[] args) {
        // akun petugas bawaan biar bisa langsung dicoba
        daftarPengguna.add(new Petugas("admin", "Petugas Utama", "admin123", "PTG-001"));

        boolean jalan = true;
        while (jalan) {
            garis();
            System.out.println("   BANKU - Bank Sampah Digital");
            garis();
            System.out.println("1. Login");
            System.out.println("2. Registrasi");
            System.out.println("3. Informasi edukasi");
            System.out.println("0. Keluar");
            int pilih = bacaInt("Pilih menu: ");

            switch (pilih) {
                case 1: login(); break;
                case 2: registrasi(); break;
                case 3: tampilEdukasi(); break;
                case 0:
                    jalan = false;
                    System.out.println("Terima kasih, jangan lupa pilah sampahmu.");
                    break;
                default: System.out.println("Menu tidak ada."); 
            }
        }
    }

    // ---------- LOGIN & REGISTRASI ----------
    static Pengguna cariPengguna(String username) {
        for (Pengguna p : daftarPengguna) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return p;
            }
        }
        return null;
    }

    static void registrasi() {
        System.out.println("\n--- Registrasi ---");
        System.out.println("1. Nasabah");
        System.out.println("2. Petugas (butuh kode)");
        int jenis = bacaInt("Daftar sebagai: ");
        if (jenis != 1 && jenis != 2) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        String username = bacaTeks("Username: ");
        if (username.isEmpty() || cariPengguna(username) != null) {
            System.out.println("Username kosong atau sudah dipakai.");
            return;
        }
        String nama = bacaTeks("Nama lengkap: ");
        String pass = bacaTeks("Password (min 4 karakter): ");
        if (pass.length() < 4) {
            System.out.println("Password terlalu pendek.");
            return;
        }

        if (jenis == 1) {
            daftarPengguna.add(new Nasabah(username, nama, pass));
            System.out.println("Registrasi nasabah berhasil.");
        } else {
            String kode = bacaTeks("Kode petugas: ");
            if (!kode.equals(KODE_PETUGAS)) {
                System.out.println("Kode salah, pendaftaran dibatalkan.");
                return;
            }
            String id = "PTG-" + String.format("%03d", jumlahPetugas() + 1);
            daftarPengguna.add(new Petugas(username, nama, pass, id));
            System.out.println("Registrasi petugas berhasil dengan ID " + id + ".");
        }
    }

    static int jumlahPetugas() {
        int n = 0;
        for (Pengguna p : daftarPengguna) {
            if (p instanceof Petugas) n++;
        }
        return n;
    }

    static void login() {
        System.out.println("\n--- Login ---");
        String username = bacaTeks("Username: ");
        String pass = bacaTeks("Password: ");

        Pengguna p = cariPengguna(username);
        if (p == null || !p.cekPassword(pass)) {
            System.out.println("Username atau password salah.");
            return;
        }

        System.out.println("Halo, " + p.getNama() + " (" + p.getPeran() + ")");
        if (p instanceof Petugas) {
            menuPetugas((Petugas) p);
        } else {
            menuNasabah((Nasabah) p);
        }
    }

    // ---------- MENU PETUGAS ----------
    static void menuPetugas(Petugas petugas) {
        boolean aktif = true;
        while (aktif) {
            garis();
            System.out.println("MENU PETUGAS [" + petugas.getIdPetugas() + "]");
            System.out.println("1. Catat setoran sampah");
            System.out.println("2. Proses penukaran saldo nasabah");
            System.out.println("3. Lihat daftar nasabah");
            System.out.println("4. Laporan rekapitulasi");
            System.out.println("5. Lihat daftar harga");
            System.out.println("0. Logout");
            int pilih = bacaInt("Pilih menu: ");

            switch (pilih) {
                case 1: catatSetoran(); break;
                case 2: prosesPenukaran(); break;
                case 3: daftarNasabah(); break;
                case 4: laporanRekap(); break;
                case 5: tampilHarga(); break;
                case 0: aktif = false; break;
                default: System.out.println("Menu tidak ada.");
            }
        }
    }

    static Nasabah pilihNasabah() {
        String user = bacaTeks("Username nasabah: ");
        Pengguna p = cariPengguna(user);
        if (p instanceof Nasabah) {
            return (Nasabah) p;
        }
        System.out.println("Nasabah tidak ditemukan.");
        return null;
    }

    static void catatSetoran() {
        System.out.println("\n--- Catat Setoran ---");
        Nasabah n = pilihNasabah();
        if (n == null) return;

        tampilHarga();
        int idx = bacaInt("Pilih jenis sampah (nomor): ") - 1;
        if (idx < 0 || idx >= katalog.length) {
            System.out.println("Jenis sampah tidak valid.");
            return;
        }
        double berat = bacaDouble("Berat (kg): ");
        if (berat <= 0) {
            System.out.println("Berat harus lebih dari nol.");
            return;
        }

        Sampah s = katalog[idx];
        double nilai = s.hitungNilai(berat); // method polymorphic
        n.tambahSaldo(nilai);
        n.catat(new Transaksi(LocalDate.now(), "SETOR", s.getJenis(), berat, nilai));

        System.out.printf("Setoran %.2f kg %s dicatat. Nilai: %s%n",
                berat, s.getJenis(), rupiah(nilai));
        System.out.println("Saldo " + n.getNama() + " sekarang: " + rupiah(n.getSaldo()));
    }

    static void prosesPenukaran() {
        System.out.println("\n--- Penukaran Saldo ---");
        Nasabah n = pilihNasabah();
        if (n == null) return;
        lakukanPenukaran(n);
    }

    static void daftarNasabah() {
        System.out.println("\n--- Daftar Nasabah ---");
        int no = 0;
        for (Pengguna p : daftarPengguna) {
            if (p instanceof Nasabah) {
                Nasabah n = (Nasabah) p;
                no++;
                System.out.printf("%d. %s (%s) | saldo %s%n",
                        no, n.getNama(), n.getUsername(), rupiah(n.getSaldo()));
            }
        }
        if (no == 0) System.out.println("Belum ada nasabah.");
    }

    static void laporanRekap() {
        System.out.println("\n--- Laporan Rekapitulasi ---");
        System.out.println("1. Semua periode");
        System.out.println("2. Per periode (rentang tanggal)");
        int mode = bacaInt("Pilih: ");

        LocalDate awal = LocalDate.MIN;
        LocalDate akhir = LocalDate.MAX;
        if (mode == 2) {
            awal = bacaTanggal("Dari tanggal (yyyy-mm-dd): ");
            akhir = bacaTanggal("Sampai tanggal (yyyy-mm-dd): ");
            if (awal == null || akhir == null) {
                System.out.println("Format tanggal salah.");
                return;
            }
        }

        double[] totalBerat = new double[katalog.length];
        double[] totalNilai = new double[katalog.length];
        for (Pengguna p : daftarPengguna) {
            if (!(p instanceof Nasabah)) continue;
            for (Transaksi t : ((Nasabah) p).getRiwayat()) {
                if (!t.getTipe().equals("SETOR")) continue;
                if (t.getTanggal().isBefore(awal) || t.getTanggal().isAfter(akhir)) continue;
                for (int i = 0; i < katalog.length; i++) {
                    if (katalog[i].getJenis().equals(t.getKeterangan())) {
                        totalBerat[i] += t.getBeratKg();
                        totalNilai[i] += t.getNominal();
                    }
                }
            }
        }

        double sumBerat = 0, sumNilai = 0;
        System.out.printf("%-10s %12s %16s%n", "Jenis", "Berat (kg)", "Nilai");
        for (int i = 0; i < katalog.length; i++) {
            System.out.printf("%-10s %12.2f %16s%n",
                    katalog[i].getJenis(), totalBerat[i], rupiah(totalNilai[i]));
            sumBerat += totalBerat[i];
            sumNilai += totalNilai[i];
        }
        System.out.printf("%-10s %12.2f %16s%n", "TOTAL", sumBerat, rupiah(sumNilai));
    }

    // ---------- MENU NASABAH ----------
    static void menuNasabah(Nasabah n) {
        boolean aktif = true;
        while (aktif) {
            garis();
            System.out.println("MENU NASABAH - " + n.getNama());
            System.out.println("1. Lihat saldo");
            System.out.println("2. Tukar saldo");
            System.out.println("3. Riwayat transaksi");
            System.out.println("4. Daftar harga sampah");
            System.out.println("5. Informasi edukasi");
            System.out.println("6. Ganti password");
            System.out.println("0. Logout");
            int pilih = bacaInt("Pilih menu: ");

            switch (pilih) {
                case 1:
                    System.out.println("Saldo kamu: " + rupiah(n.getSaldo()));
                    break;
                case 2: lakukanPenukaran(n); break;
                case 3: tampilRiwayat(n); break;
                case 4: tampilHarga(); break;
                case 5: tampilEdukasi(); break;
                case 6:
                    String lama = bacaTeks("Password lama: ");
                    if (!n.cekPassword(lama)) {
                        System.out.println("Password lama salah.");
                        break;
                    }
                    String baru = bacaTeks("Password baru (min 4): ");
                    if (baru.length() < 4) {
                        System.out.println("Terlalu pendek, batal.");
                    } else {
                        n.setPassword(baru);
                        System.out.println("Password berhasil diganti.");
                    }
                    break;
                case 0: aktif = false; break;
                default: System.out.println("Menu tidak ada.");
            }
        }
    }

    static void lakukanPenukaran(Nasabah n) {
        System.out.println("Saldo " + n.getNama() + ": " + rupiah(n.getSaldo()));
        System.out.println("1. Tarik tunai");
        System.out.println("2. Tukar barang kebutuhan pokok");
        int jenis = bacaInt("Pilih: ");

        if (jenis == 1) {
            double jumlah = bacaDouble("Jumlah penarikan: ");
            if (jumlah <= 0) {
                System.out.println("Jumlah tidak valid.");
            } else if (n.kurangiSaldo(jumlah)) {
                n.catat(new Transaksi(LocalDate.now(), "TARIK", "Tarik tunai", 0, jumlah));
                System.out.println("Penarikan " + rupiah(jumlah) + " berhasil.");
            } else {
                System.out.println("Saldo tidak cukup.");
            }
        } else if (jenis == 2) {
            for (int i = 0; i < namaBarang.length; i++) {
                System.out.printf("%d. %s (%s)%n", i + 1, namaBarang[i], rupiah(hargaBarang[i]));
            }
            int idx = bacaInt("Pilih barang: ") - 1;
            if (idx < 0 || idx >= namaBarang.length) {
                System.out.println("Barang tidak valid.");
                return;
            }
            if (n.kurangiSaldo(hargaBarang[idx])) {
                n.catat(new Transaksi(LocalDate.now(), "TUKAR", namaBarang[idx], 0, hargaBarang[idx]));
                System.out.println("Berhasil menukar " + namaBarang[idx] + ".");
            } else {
                System.out.println("Saldo belum cukup, setor lagi ya.");
            }
        } else {
            System.out.println("Pilihan tidak valid.");
        }
    }

    static void tampilRiwayat(Nasabah n) {
        System.out.println("\n--- Riwayat Transaksi ---");
        if (n.getRiwayat().isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        for (Transaksi t : n.getRiwayat()) {
            String berat = t.getBeratKg() > 0 ? String.format(" (%.2f kg)", t.getBeratKg()) : "";
            String tanda = t.getTipe().equals("SETOR") ? "+" : "-";
            System.out.printf("%s | %-5s | %s%s | %s%s%n",
                    t.getTanggal(), t.getTipe(), t.getKeterangan(), berat,
                    tanda, rupiah(t.getNominal()));
        }
    }

    // ---------- TAMPILAN UMUM ----------
    static void tampilHarga() {
        System.out.println("\nDaftar jenis sampah:");
        for (int i = 0; i < katalog.length; i++) {
            System.out.printf("%d. %-8s %s/kg%n", i + 1,
                    katalog[i].getJenis(), rupiah(katalog[i].getHargaPerKg()));
        }
    }

    static void tampilEdukasi() {
        System.out.println("\n--- Edukasi Memilah Sampah ---");
        System.out.println("Plastik : bilas dulu, buang tutup dan label kalau bisa.");
        System.out.println("Kertas  : jaga tetap kering, kertas basah atau berminyak tidak laku.");
        System.out.println("Logam   : kaleng dipipihkan biar hemat tempat.");
        System.out.println("Kaca    : bungkus pecahan, tukang sampah juga punya tangan.");
        System.out.println("Sisa makanan jangan dicampur, itu urusan kompos.");
    }

    static void garis() {
        System.out.println("\n==============================");
    }

    static String rupiah(double nilai) {
        return String.format("Rp%,.0f", nilai).replace(',', '.');
    }

    // ---------- HELPER INPUT SCANNER ----------
    static String bacaTeks(String pesan) {
        System.out.print(pesan);
        return sc.nextLine().trim();
    }

    static int bacaInt(String pesan) {
        while (true) {
            System.out.print(pesan);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka bulat saja.");
            }
        }
    }

    static double bacaDouble(String pesan) {
        while (true) {
            System.out.print(pesan);
            String s = sc.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang benar.");
            }
        }
    }

    static LocalDate bacaTanggal(String pesan) {
        System.out.print(pesan);
        try {
            return LocalDate.parse(sc.nextLine().trim());
        } catch (Exception e) {
            return null;
        }
    }
}
