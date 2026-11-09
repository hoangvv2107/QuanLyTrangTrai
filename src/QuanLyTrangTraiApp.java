import javax.swing.*;

public class QuanLyTrangTraiApp extends JFrame {
    public QuanLyTrangTraiApp() {
        setTitle("Hệ thống Quản lý Trang trại");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Mục 5: Hai màn hình nhập liệu tiêu biểu
        tabbedPane.addTab("Nhập Chuồng (Mục 5.1)", new NhapChuongPanel());
        NhapDongVatPanel nhapDongVatPanel = new NhapDongVatPanel();
        tabbedPane.addTab("Nhập Động Vật (Mục 5.2)", nhapDongVatPanel);

        // Mục 6: Màn hình tìm kiếm
        tabbedPane.addTab("Tìm kiếm Động vật (Mục 6)", new TimKiemDongVatPanel());

        // Mục 7: Hai màn hình báo cáo
        BaoCaoTonChuongPanel baoCaoTonChuongPanel = new BaoCaoTonChuongPanel();
        tabbedPane.addTab("BC Tồn Chuồng (Mục 7.1)", baoCaoTonChuongPanel);
        tabbedPane.addTab("BC Bệnh Án ĐV (Mục 7.2)", new BaoCaoBenhAnPanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == nhapDongVatPanel) {
                nhapDongVatPanel.lamMoiDanhSachChuong();
            }
            if (tabbedPane.getSelectedComponent() == baoCaoTonChuongPanel) {
                baoCaoTonChuongPanel.lamMoiBaoCao();
            }
        });

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) {}
            new QuanLyTrangTraiApp().setVisible(true);
        });
    }
}