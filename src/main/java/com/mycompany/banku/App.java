/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.banku;

import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author ragil
 */
public class App extends Application {

    private static final String EDUKASI =
        "Plastik : bilas dulu, buang tutup dan label kalau bisa.\n"
      + "Kertas  : jaga tetap kering, kertas basah atau berminyak tidak laku.\n"
      + "Logam   : kaleng dipipihkan biar hemat tempat.\n"
      + "Kaca    : bungkus pecahan, tukang sampah juga punya tangan.\n"
      + "Sisa makanan jangan dicampur, itu urusan kompos.";

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("BanKu - Bank Sampah Digital");
        tampilLogin();
        stage.show();
    }

    // ================= HELPER UMUM =================

    private void tampil(Parent root, double lebar, double tinggi) {
        stage.setScene(new Scene(root, lebar, tinggi));
        stage.centerOnScreen();
    }

    private void alert(Alert.AlertType tipe, String judul, String pesan) {
        Alert a = new Alert(tipe);
        a.setTitle("BanKu");
        a.setHeaderText(judul);
        a.setContentText(pesan);
        a.showAndWait();
    }

    // null kalau teks bukan angka (try-catch)
    private Double parseAngka(String teks) {
        try {
            return Double.parseDouble(teks.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private GridPane buatForm() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(15));
        return g;
    }

    private <T> TableColumn<T, String> kolom(String judul, Function<T, String> isi, double lebar) {
        TableColumn<T, String> c = new TableColumn<>(judul);
        c.setCellValueFactory(d -> new SimpleStringProperty(isi.apply(d.getValue())));
        c.setPrefWidth(lebar);
        return c;
    }

    private String teksHarga() {
        StringBuilder sb = new StringBuilder("DAFTAR HARGA SAMPAH\n\n");
        for (Sampah s : DataStore.katalog) {
            sb.append(String.format("%-8s %s per kg%n", s.getJenis(), DataStore.rupiah(s.getHargaPerKg())));
        }
        sb.append("\nBONUS DAN POTONGAN\n");
        sb.append("Plastik : bonus 10 persen untuk setoran 5 kg atau lebih\n");
        sb.append("Logam   : bonus 5 persen untuk setoran 3 kg atau lebih\n");
        sb.append("Kaca    : potongan penanganan Rp200 per setoran\n");
        sb.append("\nBARANG YANG BISA DITUKAR\n");
        for (int i = 0; i < DataStore.namaBarang.length; i++) {
            sb.append(String.format("%-24s %s%n", DataStore.namaBarang[i], DataStore.rupiah(DataStore.hargaBarang[i])));
        }
        sb.append("\nEDUKASI MEMILAH SAMPAH\n\n").append(EDUKASI);
        return sb.toString();
    }

    private TextArea buatPanelInfo() {
        TextArea ta = new TextArea(teksHarga());
        ta.setEditable(false);
        ta.setStyle("-fx-font-family: 'Consolas', monospace;");
        return ta;
    }

    // ================= LOGIN =================

    private void tampilLogin() {
        Label judul = new Label("BanKu");
        judul.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        Label sub = new Label("Bank Sampah Digital");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");

        GridPane form = buatForm();
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Username"), 0, 0);
        form.add(tfUser, 1, 0);
        form.add(new Label("Password"), 0, 1);
        form.add(pfPass, 1, 1);

        Button btnLogin = new Button("Login");
        btnLogin.setDefaultButton(true);
        Button btnDaftar = new Button("Daftar Akun");
        Button btnEdu = new Button("Edukasi");

        btnLogin.setOnAction(e -> {
            String user = tfUser.getText().trim();
            String pass = pfPass.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Data belum lengkap", "Username dan password wajib diisi.");
                return;
            }
            Pengguna p = DataStore.cariPengguna(user);
            if (p == null || !p.cekPassword(pass)) {
                alert(Alert.AlertType.ERROR, "Login gagal", "Username atau password salah.");
                return;
            }
            if (p instanceof Petugas) {
                tampilPetugas((Petugas) p);
            } else {
                tampilNasabah((Nasabah) p);
            }
        });
        btnDaftar.setOnAction(e -> tampilRegistrasi());
        btnEdu.setOnAction(e -> alert(Alert.AlertType.INFORMATION, "Edukasi Memilah Sampah", EDUKASI));

        HBox tombol = new HBox(10, btnLogin, btnDaftar, btnEdu);
        tombol.setAlignment(Pos.CENTER);

        Label info = new Label("Akun petugas bawaan: admin / admin123");
        info.setStyle("-fx-text-fill: gray;");

        VBox root = new VBox(15, judul, sub, form, tombol, info);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        tampil(root, 460, 400);
    }

    // ================= REGISTRASI =================

    private void tampilRegistrasi() {
        Label judul = new Label("Registrasi Akun");
        judul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField tfUser = new TextField();
        TextField tfNama = new TextField();
        PasswordField pfPass = new PasswordField();
        ComboBox<String> cbPeran = new ComboBox<>(FXCollections.observableArrayList("Nasabah", "Petugas"));
        cbPeran.setValue("Nasabah");
        PasswordField pfKode = new PasswordField();
        pfKode.setPromptText("Khusus petugas");
        pfKode.setDisable(true);
        cbPeran.setOnAction(e -> pfKode.setDisable(!"Petugas".equals(cbPeran.getValue())));

        GridPane form = buatForm();
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Daftar sebagai"), 0, 0);
        form.add(cbPeran, 1, 0);
        form.add(new Label("Username"), 0, 1);
        form.add(tfUser, 1, 1);
        form.add(new Label("Nama lengkap"), 0, 2);
        form.add(tfNama, 1, 2);
        form.add(new Label("Password (min 4)"), 0, 3);
        form.add(pfPass, 1, 3);
        form.add(new Label("Kode petugas"), 0, 4);
        form.add(pfKode, 1, 4);

        Button btnSimpan = new Button("Simpan");
        btnSimpan.setDefaultButton(true);
        Button btnKembali = new Button("Kembali");

        btnSimpan.setOnAction(e -> {
            String user = tfUser.getText().trim();
            String nama = tfNama.getText().trim();
            String pass = pfPass.getText();
            boolean petugas = "Petugas".equals(cbPeran.getValue());

            if (user.isEmpty() || nama.isEmpty() || pass.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Data belum lengkap", "Username, nama, dan password wajib diisi.");
                return;
            }
            if (pass.length() < 4) {
                alert(Alert.AlertType.WARNING, "Password terlalu pendek", "Password minimal 4 karakter.");
                return;
            }
            if (DataStore.cariPengguna(user) != null) {
                alert(Alert.AlertType.ERROR, "Username dipakai", "Pilih username lain.");
                return;
            }

            if (petugas) {
                if (!DataStore.KODE_PETUGAS.equals(pfKode.getText())) {
                    alert(Alert.AlertType.ERROR, "Kode salah", "Kode petugas tidak cocok, pendaftaran dibatalkan.");
                    return;
                }
                String id = "PTG-" + String.format("%03d", DataStore.jumlahPetugas() + 1);
                DataStore.daftarPengguna.add(new Petugas(user, nama, pass, id));
                alert(Alert.AlertType.INFORMATION, "Berhasil", "Petugas terdaftar dengan ID " + id + ".");
            } else {
                DataStore.daftarPengguna.add(new Nasabah(user, nama, pass));
                alert(Alert.AlertType.INFORMATION, "Berhasil", "Nasabah terdaftar. Silakan login.");
            }
            tampilLogin();
        });
        btnKembali.setOnAction(e -> tampilLogin());

        HBox tombol = new HBox(10, btnSimpan, btnKembali);
        tombol.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, judul, form, tombol);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        tampil(root, 480, 460);
    }

    // ================= DASHBOARD NASABAH =================

    private void tampilNasabah(Nasabah n) {
        Label lblHalo = new Label("Halo, " + n.getNama() + " (Nasabah)");
        lblHalo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label lblSaldo = new Label();
        lblSaldo.setStyle("-fx-font-size: 16px; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> tampilLogin());

        Region pengisi = new Region();
        HBox.setHgrow(pengisi, Priority.ALWAYS);
        HBox atas = new HBox(20, lblHalo, pengisi, lblSaldo, btnLogout);
        atas.setAlignment(Pos.CENTER_LEFT);
        atas.setPadding(new Insets(12));
        atas.setStyle("-fx-background-color: #e8f5e9;");

        TableView<Transaksi> tabel = buatTabelRiwayat();

        Runnable segarkan = () -> {
            lblSaldo.setText("Saldo: " + DataStore.rupiah(n.getSaldo()));
            tabel.setItems(FXCollections.observableArrayList(n.getRiwayat()));
        };
        segarkan.run();

        Tab tTukar = new Tab("Tukar Saldo", buatPanelTukar(n, segarkan));
        Tab tRiwayat = new Tab("Riwayat", tabel);
        Tab tInfo = new Tab("Harga dan Edukasi", buatPanelInfo());
        Tab tAkun = new Tab("Akun", buatPanelPassword(n));

        TabPane tabs = new TabPane(tTukar, tRiwayat, tInfo, tAkun);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane();
        root.setTop(atas);
        root.setCenter(tabs);
        tampil(root, 780, 520);
    }

    private TableView<Transaksi> buatTabelRiwayat() {
        TableView<Transaksi> tabel = new TableView<>();
        tabel.getColumns().add(this.<Transaksi>kolom("Tanggal", t -> t.getTanggal().toString(), 110));
        tabel.getColumns().add(this.<Transaksi>kolom("Tipe", Transaksi::getTipe, 80));
        tabel.getColumns().add(this.<Transaksi>kolom("Keterangan", Transaksi::getKeterangan, 200));
        tabel.getColumns().add(this.<Transaksi>kolom("Berat (kg)",
                t -> t.getBeratKg() > 0 ? String.format("%.2f", t.getBeratKg()) : "-", 100));
        tabel.getColumns().add(this.<Transaksi>kolom("Nominal", t -> DataStore.rupiah(t.getNominal()), 140));
        tabel.setPlaceholder(new Label("Belum ada transaksi."));
        return tabel;
    }

    private GridPane buatPanelPassword(Nasabah n) {
        PasswordField pfLama = new PasswordField();
        PasswordField pfBaru = new PasswordField();
        Button btn = new Button("Ganti Password");

        GridPane g = buatForm();
        g.add(new Label("Password lama"), 0, 0);
        g.add(pfLama, 1, 0);
        g.add(new Label("Password baru (min 4)"), 0, 1);
        g.add(pfBaru, 1, 1);
        g.add(btn, 1, 2);

        btn.setOnAction(e -> {
            if (pfLama.getText().isEmpty() || pfBaru.getText().isEmpty()) {
                alert(Alert.AlertType.WARNING, "Data belum lengkap", "Kedua kolom password wajib diisi.");
                return;
            }
            if (!n.cekPassword(pfLama.getText())) {
                alert(Alert.AlertType.ERROR, "Password salah", "Password lama tidak cocok.");
                return;
            }
            if (pfBaru.getText().length() < 4) {
                alert(Alert.AlertType.WARNING, "Terlalu pendek", "Password baru minimal 4 karakter.");
                return;
            }
            n.setPassword(pfBaru.getText());
            pfLama.clear();
            pfBaru.clear();
            alert(Alert.AlertType.INFORMATION, "Berhasil", "Password sudah diganti.");
        });
        return g;
    }

    /*
     * Panel penukaran dipakai dua role.
     * Kalau tetap bernilai null (petugas), muncul kolom username nasabah.
     */
    private GridPane buatPanelTukar(Nasabah tetap, Runnable sesudah) {
        TextField tfUser = new TextField();
        tfUser.setPromptText("Username nasabah");

        ComboBox<String> cbPilihan = new ComboBox<>();
        cbPilihan.getItems().add("Tarik tunai");
        for (int i = 0; i < DataStore.namaBarang.length; i++) {
            cbPilihan.getItems().add(DataStore.namaBarang[i] + " (" + DataStore.rupiah(DataStore.hargaBarang[i]) + ")");
        }
        cbPilihan.getSelectionModel().selectFirst();

        TextField tfJumlah = new TextField();
        tfJumlah.setPromptText("Jumlah penarikan (Rp)");
        cbPilihan.setOnAction(e ->
            tfJumlah.setDisable(cbPilihan.getSelectionModel().getSelectedIndex() != 0));

        Button btn = new Button("Proses");

        GridPane g = buatForm();
        int baris = 0;
        if (tetap == null) {
            g.add(new Label("Username nasabah"), 0, baris);
            g.add(tfUser, 1, baris++);
        }
        g.add(new Label("Pilihan"), 0, baris);
        g.add(cbPilihan, 1, baris++);
        g.add(new Label("Jumlah"), 0, baris);
        g.add(tfJumlah, 1, baris++);
        g.add(btn, 1, baris);

        btn.setOnAction(e -> {
            Nasabah n = tetap;
            if (n == null) {
                String u = tfUser.getText().trim();
                if (u.isEmpty()) {
                    alert(Alert.AlertType.WARNING, "Data belum lengkap", "Username nasabah wajib diisi.");
                    return;
                }
                n = DataStore.cariNasabah(u);
                if (n == null) {
                    alert(Alert.AlertType.ERROR, "Tidak ditemukan", "Nasabah dengan username itu tidak ada.");
                    return;
                }
            }

            int idx = cbPilihan.getSelectionModel().getSelectedIndex();
            if (idx == 0) {
                if (tfJumlah.getText().trim().isEmpty()) {
                    alert(Alert.AlertType.WARNING, "Data belum lengkap", "Jumlah penarikan wajib diisi.");
                    return;
                }
                Double jumlah = parseAngka(tfJumlah.getText());
                if (jumlah == null || jumlah <= 0) {
                    alert(Alert.AlertType.ERROR, "Format salah", "Jumlah harus berupa angka lebih dari nol.");
                    return;
                }
                if (DataStore.tarikTunai(n, jumlah)) {
                    alert(Alert.AlertType.INFORMATION, "Berhasil",
                          "Penarikan " + DataStore.rupiah(jumlah) + " berhasil.\nSisa saldo: " + DataStore.rupiah(n.getSaldo()));
                    tfJumlah.clear();
                } else {
                    alert(Alert.AlertType.ERROR, "Saldo tidak cukup", "Saldo saat ini " + DataStore.rupiah(n.getSaldo()) + ".");
                }
            } else {
                int barang = idx - 1;
                if (DataStore.tukarBarang(n, barang)) {
                    alert(Alert.AlertType.INFORMATION, "Berhasil",
                          "Berhasil menukar " + DataStore.namaBarang[barang] + ".\nSisa saldo: " + DataStore.rupiah(n.getSaldo()));
                } else {
                    alert(Alert.AlertType.ERROR, "Saldo belum cukup", "Setor sampah dulu untuk menambah saldo.");
                }
            }
            sesudah.run();
        });
        return g;
    }

    // ================= DASHBOARD PETUGAS =================

    private void tampilPetugas(Petugas p) {
        Label lblHalo = new Label("Halo, " + p.getNama() + " (Petugas " + p.getIdPetugas() + ")");
        lblHalo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> tampilLogin());

        Region pengisi = new Region();
        HBox.setHgrow(pengisi, Priority.ALWAYS);
        HBox atas = new HBox(20, lblHalo, pengisi, btnLogout);
        atas.setAlignment(Pos.CENTER_LEFT);
        atas.setPadding(new Insets(12));
        atas.setStyle("-fx-background-color: #e3f2fd;");

        TableView<Nasabah> tabelNasabah = buatTabelNasabah();
        Runnable segarkan = () -> tabelNasabah.setItems(FXCollections.observableArrayList(DataStore.semuaNasabah()));
        segarkan.run();

        Tab tSetor = new Tab("Catat Setoran", buatPanelSetor(segarkan));
        Tab tTukar = new Tab("Penukaran Saldo", buatPanelTukar(null, segarkan));
        Tab tNasabah = new Tab("Daftar Nasabah", tabelNasabah);
        Tab tRekap = new Tab("Rekapitulasi", buatPanelRekap());
        Tab tInfo = new Tab("Harga dan Edukasi", buatPanelInfo());

        // daftar nasabah dimuat ulang tiap tab dibuka
        tNasabah.setOnSelectionChanged(e -> { if (tNasabah.isSelected()) segarkan.run(); });

        TabPane tabs = new TabPane(tSetor, tTukar, tNasabah, tRekap, tInfo);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane();
        root.setTop(atas);
        root.setCenter(tabs);
        tampil(root, 820, 540);
    }

    private TableView<Nasabah> buatTabelNasabah() {
        TableView<Nasabah> tabel = new TableView<>();
        tabel.getColumns().add(this.<Nasabah>kolom("Username", Nasabah::getUsername, 180));
        tabel.getColumns().add(this.<Nasabah>kolom("Nama", Nasabah::getNama, 240));
        tabel.getColumns().add(this.<Nasabah>kolom("Saldo", n -> DataStore.rupiah(n.getSaldo()), 160));
        tabel.setPlaceholder(new Label("Belum ada nasabah terdaftar."));
        return tabel;
    }

    private GridPane buatPanelSetor(Runnable sesudah) {
        TextField tfUser = new TextField();
        tfUser.setPromptText("Username nasabah");

        ComboBox<String> cbJenis = new ComboBox<>();
        for (Sampah s : DataStore.katalog) {
            cbJenis.getItems().add(s.getJenis() + " (" + DataStore.rupiah(s.getHargaPerKg()) + "/kg)");
        }
        cbJenis.getSelectionModel().selectFirst();

        TextField tfBerat = new TextField();
        tfBerat.setPromptText("Berat dalam kg, contoh 2.5");

        Label lblHasil = new Label();
        lblHasil.setStyle("-fx-text-fill: #2e7d32;");

        Button btn = new Button("Catat Setoran");

        GridPane g = buatForm();
        g.add(new Label("Username nasabah"), 0, 0);
        g.add(tfUser, 1, 0);
        g.add(new Label("Jenis sampah"), 0, 1);
        g.add(cbJenis, 1, 1);
        g.add(new Label("Berat (kg)"), 0, 2);
        g.add(tfBerat, 1, 2);
        g.add(btn, 1, 3);
        g.add(lblHasil, 1, 4);

        btn.setOnAction(e -> {
            String u = tfUser.getText().trim();
            if (u.isEmpty() || tfBerat.getText().trim().isEmpty()) {
                alert(Alert.AlertType.WARNING, "Data belum lengkap", "Username nasabah dan berat wajib diisi.");
                return;
            }
            Nasabah n = DataStore.cariNasabah(u);
            if (n == null) {
                alert(Alert.AlertType.ERROR, "Tidak ditemukan", "Nasabah dengan username itu tidak ada.");
                return;
            }
            Double berat = parseAngka(tfBerat.getText());
            if (berat == null || berat <= 0) {
                alert(Alert.AlertType.ERROR, "Format salah", "Berat harus berupa angka lebih dari nol.");
                return;
            }

            Sampah s = DataStore.katalog[cbJenis.getSelectionModel().getSelectedIndex()];
            double nilai = DataStore.setor(n, s, berat);

            lblHasil.setText(String.format("Tercatat: %.2f kg %s = %s", berat, s.getJenis(), DataStore.rupiah(nilai)));
            alert(Alert.AlertType.INFORMATION, "Setoran tercatat",
                  String.format("%.2f kg %s dari %s%nNilai: %s%nSaldo sekarang: %s",
                          berat, s.getJenis(), n.getNama(), DataStore.rupiah(nilai), DataStore.rupiah(n.getSaldo())));
            tfBerat.clear();
            sesudah.run();
        });
        return g;
    }

    private VBox buatPanelRekap() {
        DatePicker dpAwal = new DatePicker();
        DatePicker dpAkhir = new DatePicker();
        Button btn = new Button("Tampilkan");
        Button btnSemua = new Button("Semua Periode");

        TableView<String[]> tabel = new TableView<>();
        tabel.getColumns().add(this.<String[]>kolom("Jenis", r -> r[0], 180));
        tabel.getColumns().add(this.<String[]>kolom("Total Berat (kg)", r -> r[1], 180));
        tabel.getColumns().add(this.<String[]>kolom("Total Nilai", r -> r[2], 200));
        tabel.setItems(FXCollections.observableArrayList(DataStore.rekap(null, null)));

        btn.setOnAction(e -> {
            LocalDate awal = dpAwal.getValue();
            LocalDate akhir = dpAkhir.getValue();
            if (awal == null || akhir == null) {
                alert(Alert.AlertType.WARNING, "Tanggal belum lengkap", "Pilih tanggal awal dan akhir, atau tekan Semua Periode.");
                return;
            }
            if (awal.isAfter(akhir)) {
                alert(Alert.AlertType.ERROR, "Rentang salah", "Tanggal awal tidak boleh setelah tanggal akhir.");
                return;
            }
            tabel.setItems(FXCollections.observableArrayList(DataStore.rekap(awal, akhir)));
        });
        btnSemua.setOnAction(e -> {
            dpAwal.setValue(null);
            dpAkhir.setValue(null);
            tabel.setItems(FXCollections.observableArrayList(DataStore.rekap(null, null)));
        });

        HBox filter = new HBox(10, new Label("Dari"), dpAwal, new Label("Sampai"), dpAkhir, btn, btnSemua);
        filter.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(12, filter, tabel);
        box.setPadding(new Insets(15));
        VBox.setVgrow(tabel, Priority.ALWAYS);
        return box;
    }
}

