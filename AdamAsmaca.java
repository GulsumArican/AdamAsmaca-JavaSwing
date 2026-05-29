import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AdamAsmaca {
    static String txtKlasoru = "C:\\P2Oyun\\TXTDosyalar\\";
    static String resimKlasoru = "C:\\P2Oyun\\Resimler\\";
    
    static String gecerliSifre = "";
    static int hataliSifreSayaci = 0;
    
    static String secilenKelime = "";
    static char[] yildizliKelime;
    static int yanlisTahminSayisi = 0; 
    static int oyunSuresi = 0;
    static Timer kronometre;
    
    static JLabel lblResim;
    static JPanel pnlHarfler;
    static JLabel lblSure;
    static JTextField txtHarfTahmini;
    static JTextField txtKelimeTahmini;
    
    static DefaultTableModel skorModeli;
    static DefaultTableModel logModeli;

    public static void main(String[] args) {
        sistemGirisKontrolu();
    }

    public static void sistemGirisKontrolu() {
        File sifreDosyasi = new File(txtKlasoru + "sifre.txt");
        
        try 
        {
          if (!sifreDosyasi.exists() || sifreDosyasi.length() == 0) {
            String yeniSifre = JOptionPane.showInputDialog(null, "Hoşgeldiniz. Kendinize bir şifre belirleyin:");
            if (yeniSifre != null && !yeniSifre.trim().isEmpty()) {
                FileWriter yazici = new FileWriter(sifreDosyasi);
                yazici.write(yeniSifre);
                yazici.close();
                gecerliSifre = yeniSifre;
                logKaydet("Yeni şifre oluşturuldu.");
                arayuzuBaslat();
                } else {
                    System.exit(0);
                }
            } else {
                BufferedReader okuyucu = new BufferedReader(new FileReader(sifreDosyasi));
                gecerliSifre = okuyucu.readLine();
                okuyucu.close();
                
                while (hataliSifreSayaci < 3) {
                    String girilen = JOptionPane.showInputDialog(null, "Lütfen şifrenizi girin:");
                    if (girilen == null) {
                        System.exit(0);
                    }
                    
                    if (girilen.equals(gecerliSifre)) {
                        logKaydet("Giriş başarılı");
                        arayuzuBaslat(); 
                        return; 
                    } else {
                        hataliSifreSayaci++;
                        logKaydet("Şifre hatalı");
                        JOptionPane.showMessageDialog(null, "Yanlış şifre! Kalan hakkınız: " + (3 - hataliSifreSayaci));
                    }
                }
                JOptionPane.showMessageDialog(null, "3 kez hatalı giriş yaptınız. Sistem kapatılıyor.");
                System.exit(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "C:\\P2Oyun dosyalarına ulaşılamadı. Klasörleri kontrol edin.");
        }
    }
    public static void logKaydet(String mesaj) {
        try {
            FileWriter yazici = new FileWriter(txtKlasoru + "log.txt", true);
            SimpleDateFormat tarihFormati = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String suan = tarihFormati.format(new Date());
            
            yazici.write(suan + " - " + mesaj + "\n");
            yazici.close();
        } catch (Exception e) {
            System.out.println("Log yazma hatası");
        }
    }
    public static void arayuzuBaslat() {
        JFrame anaPencere = new JFrame("Adam Asmaca - SDÜ");
        anaPencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        anaPencere.setSize(800, 650);
        anaPencere.setLocationRelativeTo(null); 

        JMenuBar menuCubugu = new JMenuBar();
        JMenu menuSecenekler = new JMenu("Oyun Seçenekleri");
        JMenuItem menuBasla = new JMenuItem("Oyuna Başla");
        JMenuItem menuYeniden = new JMenuItem("Oyunu Yeniden Başlat");
        
        menuSecenekler.add(menuBasla);
        menuSecenekler.add(menuYeniden);
        menuCubugu.add(menuSecenekler);
        anaPencere.setJMenuBar(menuCubugu);
        
        JTabbedPane sekmeler = new JTabbedPane();

        JPanel pnlOyun = new JPanel(new BorderLayout());
        
        JPanel pnlUst = new JPanel();
        lblSure = new JLabel("Süre: 0 saniye");
        lblSure.setFont(new Font("Calibri", Font.BOLD, 16));
        lblSure.setForeground(Color.RED);
        pnlUst.add(lblSure);
        pnlOyun.add(pnlUst, BorderLayout.NORTH);

        lblResim = new JLabel();
        lblResim.setHorizontalAlignment(JLabel.CENTER);
        pnlOyun.add(lblResim, BorderLayout.CENTER);

        JPanel pnlAlt = new JPanel(new GridLayout(3, 1));
        
        pnlHarfler = new JPanel(); 
        pnlAlt.add(pnlHarfler);
        
        JPanel pnlHarfTahmin = new JPanel();
        pnlHarfTahmin.add(new JLabel("Harf Tahmini:"));
        txtHarfTahmini = new JTextField(5);
        JButton btnHarfDene = new JButton("Dene");
        pnlHarfTahmin.add(txtHarfTahmini);
        pnlHarfTahmin.add(btnHarfDene);
        
        JPanel pnlKelimeTahmin = new JPanel();
        pnlKelimeTahmin.add(new JLabel("Kelime Tahmini:"));
        txtKelimeTahmini = new JTextField(15);
        JButton btnKelimeDene = new JButton("Dene");
        pnlKelimeTahmin.add(txtKelimeTahmini);
        pnlKelimeTahmin.add(btnKelimeDene);
        
        pnlAlt.add(pnlHarfTahmin);
        pnlAlt.add(pnlKelimeTahmin);
        
        pnlOyun.add(pnlAlt, BorderLayout.SOUTH);
        sekmeler.addTab("Oyun Oynama", pnlOyun);

        JPanel pnlSkorlar = new JPanel(new BorderLayout());
        String[] skorKolonlari = {"Tarih ve Saat", "Süre", "Sonuç"};
        skorModeli = new DefaultTableModel(skorKolonlari, 0);
        JTable skorTablosu = new JTable(skorModeli); 
        pnlSkorlar.add(new JScrollPane(skorTablosu), BorderLayout.CENTER);
        
        JButton btnSkorTemizle = new JButton("Eski Skorları Temizle");
        pnlSkorlar.add(btnSkorTemizle, BorderLayout.SOUTH);
        sekmeler.addTab("Eski Skorları Görüntüleme", pnlSkorlar);

        JPanel pnlLoglar = new JPanel(new BorderLayout());
        String[] logKolonlari = {"İşlem Zamanı ve Durum"};
        logModeli = new DefaultTableModel(logKolonlari, 0);
        JTable logTablosu = new JTable(logModeli);
        pnlLoglar.add(new JScrollPane(logTablosu), BorderLayout.CENTER);
        
        JButton btnLogTemizle = new JButton("Log Kayıtlarını Temizle");
        pnlLoglar.add(btnLogTemizle, BorderLayout.SOUTH);
        sekmeler.addTab("Logları Görüntüleme", pnlLoglar);

        anaPencere.add(sekmeler);
        anaPencere.setVisible(true);

        btnHarfDene.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                harfKontrolEt();
            }
        });

        btnKelimeDene.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                kelimeKontrolEt();
            }
        });

        ActionListener menuBaslatEvent = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                yeniOyunHazirla();
            }
        };
        menuBasla.addActionListener(menuBaslatEvent);
        menuYeniden.addActionListener(menuBaslatEvent);

        btnSkorTemizle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dosyaTemizle("oyunlar.txt", skorModeli);
            }
        });

        btnLogTemizle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dosyaTemizle("log.txt", logModeli);
            }
        });

        sekmeler.addChangeListener(e -> tablolariVeriIleDoldur());

        yeniOyunHazirla();
        tablolariVeriIleDoldur();
    }

    public static void yeniOyunHazirla() {
        yanlisTahminSayisi = 0;
        oyunSuresi = 0;
        lblSure.setText("Süre: 0 saniye");
        resimCiz(); 
        
        ArrayList<String> kelimeListesi = new ArrayList<>();
        try {
            BufferedReader okuyucu = new BufferedReader(new FileReader(txtKlasoru + "kelimeler.txt"));
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.trim().length() >= 6) { 
                    kelimeListesi.add(satir.trim().toUpperCase());
                }
            }
            okuyucu.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "kelimeler.txt dosyası bulunamadı!");
            return;
        }
        
        if (kelimeListesi.size() == 0) {
            JOptionPane.showMessageDialog(null, "kelimeler.txt dosyası boş veya 6 harften uzun kelime yok!");
            return;
        }
        
        Random rnd = new Random();
        int rastgeleIndex = rnd.nextInt(kelimeListesi.size());
        secilenKelime = kelimeListesi.get(rastgeleIndex);
        
        yildizliKelime = new char[secilenKelime.length()];
        for (int i = 0; i < yildizliKelime.length; i++) {
            yildizliKelime[i] = '*';
        }
        yildizlariEkrandaGoster();
        
        if (kronometre != null) {
            kronometre.stop();
        }
        kronometre = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                oyunSuresi++;
                lblSure.setText("Süre: " + oyunSuresi + " saniye");
            }
        });
        kronometre.start();
    }

    public static void yildizlariEkrandaGoster() {
        pnlHarfler.removeAll(); 
        
        for (int i = 0; i < yildizliKelime.length; i++) {
            JLabel lblHarf = new JLabel(yildizliKelime[i] + " ");
            lblHarf.setFont(new Font("Consolas", Font.BOLD, 28));
            pnlHarfler.add(lblHarf);
        }
        pnlHarfler.revalidate();
        pnlHarfler.repaint();
    }

    public static void resimCiz() {
        int resimNumarasi = (yanlisTahminSayisi == 0) ? 1 : yanlisTahminSayisi;
        
        File resimDosyasi = new File(resimKlasoru + resimNumarasi + ".jpg");
        if (resimDosyasi.exists()) {
            ImageIcon orjinalIkon = new ImageIcon(resimDosyasi.getAbsolutePath());
            Image boyutlandirilmis = orjinalIkon.getImage().getScaledInstance(250, 300, Image.SCALE_SMOOTH);
            lblResim.setIcon(new ImageIcon(boyutlandirilmis));
        } else {
            lblResim.setIcon(null);
            lblResim.setText("Resim Yok: " + resimNumarasi + ".jpg");
        }
    }

    public static void harfKontrolEt() {
        String tahmin = txtHarfTahmini.getText().toUpperCase();
        txtHarfTahmini.setText(""); 
        
        if (tahmin.length() != 1) {
            JOptionPane.showMessageDialog(null, "Lütfen sadece 1 harf girin.");
            return;
        }
        
        char harf = tahmin.charAt(0);
        boolean dogruBulduMu = false;
        
        for (int i = 0; i < secilenKelime.length(); i++) {
            if (secilenKelime.charAt(i) == harf) {
                yildizliKelime[i] = harf;
                dogruBulduMu = true;
            }
        }
        tahminSonucunuDegerlendir(dogruBulduMu);
    }

    public static void kelimeKontrolEt() {
        String tahmin = txtKelimeTahmini.getText().toUpperCase();
        txtKelimeTahmini.setText(""); 
        
        if (tahmin.isEmpty()) return;
        
        boolean dogruBulduMu = secilenKelime.equals(tahmin);
        
        if (dogruBulduMu) {
            yildizliKelime = secilenKelime.toCharArray();
        }
        tahminSonucunuDegerlendir(dogruBulduMu);
    }

    public static void tahminSonucunuDegerlendir(boolean dogruBulduMu) {
        yildizlariEkrandaGoster();
        
        if (dogruBulduMu == false) {
            yanlisTahminSayisi++;
            resimCiz();
        }
        String guncelKelime = String.valueOf(yildizliKelime);
        
        if (guncelKelime.equals(secilenKelime)) {
            oyunBitti("Tebrikler, Kazandınız.");
        } else if (yanlisTahminSayisi >= 11) {
            oyunBitti("Malesef Kaybettiniz! Kelime: " + secilenKelime);
        }
    }
    public static void oyunBitti(String mesaj) {
        kronometre.stop();
        JOptionPane.showMessageDialog(null, mesaj + "\nToplam Süre: " + oyunSuresi + " saniye");
        
        try {
            FileWriter yazici = new FileWriter(txtKlasoru + "oyunlar.txt", true);
            SimpleDateFormat tarihFormati = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String suAn = tarihFormati.format(new Date());
            
            String sonuc = mesaj.contains("Kazandınız") ? "KAZANDI" : "KAYBETTI";
            
            yazici.write(suAn + "," + oyunSuresi + " Saniye," + sonuc + "\n");
            yazici.close();
        } catch (Exception e) {
            System.out.println("Skor kaydedilemedi.");
        }
        
        tablolariVeriIleDoldur(); 
        yeniOyunHazirla(); 
    }
    
    public static void tablolariVeriIleDoldur() {
        skorModeli.setRowCount(0); 
        try {
            BufferedReader okuyucu = new BufferedReader(new FileReader(txtKlasoru + "oyunlar.txt"));
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                String[] veriler = satir.split(",");
                if (veriler.length == 3) {
                    skorModeli.addRow(veriler);
                } else {
                    skorModeli.addRow(new Object[]{satir, "", ""});
                }
            }
            okuyucu.close();
        } catch (Exception e) {}
        logModeli.setRowCount(0); 
        try {
            BufferedReader okuyucu = new BufferedReader(new FileReader(txtKlasoru + "log.txt"));
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                logModeli.addRow(new Object[]{satir});
            }
            okuyucu.close();
        } catch (Exception e) {}
    }
    
    public static void dosyaTemizle(String dosyaAdi, DefaultTableModel model) {
        String girilenSifre = JOptionPane.showInputDialog(null, "Temizlemek için şifrenizi girin:");
        
        if (girilenSifre != null && girilenSifre.equals(gecerliSifre)) {
            try {
                FileWriter yazici = new FileWriter(txtKlasoru + dosyaAdi);
                yazici.write(""); 
                yazici.close();
                
                model.setRowCount(0); 
                JOptionPane.showMessageDialog(null, "Dosya başarıyla temizlendi.");
                logKaydet(dosyaAdi + " dosyası temizlendi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Dosya silinirken hata oluştu!");
            }
        } else if (girilenSifre != null) {
            JOptionPane.showMessageDialog(null, "Hatalı şifre girdiniz. İşlem iptal edildi.");
        }
    }
}