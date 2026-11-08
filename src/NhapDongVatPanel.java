import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class NhapDongVatPanel extends JPanel {
    private JTextField txtMaDV, txtCanNang, txtGhiChu, txtCanNangNhap, txtGiaNhap;
    private JComboBox<String> cbLoai, cbGioiTinh, cbTinhTrang, cbChuong, cbPhieuNhap;
    private JButton btnLuu, btnLamMoi;

    public NhapDongVatPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Tiêu đề form
        JLabel lblTitle = new JLabel("QUẢN LÝ NHẬP THÔNG TIN ĐỘNG VẬT", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(39, 174, 96));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form nhập liệu chính (chia 2 cột)
        JPanel pnlForm = new JPanel(new GridLayout(5, 4, 15, 12));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 225, 233), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        // Khởi tạo các trường nhập với font chữ to, dễ nhìn (Cỡ 14)
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        txtMaDV = new JTextField(); txtMaDV.setFont(fontInput);
        cbLoai = new JComboBox<>(new String[]{"Bò", "Lợn", "Gà", "Dê", "Cừu"}); cbLoai.setFont(fontInput);
        cbGioiTinh = new JComboBox<>(new String[]{"Đực", "Cái", "Trống", "Mái"}); cbGioiTinh.setFont(fontInput);
        txtCanNang = new JTextField(); txtCanNang.setFont(fontInput);

        cbTinhTrang = new JComboBox<>(new String[]{"Khỏe mạnh", "Bình thường", "Đang theo dõi sức khỏe"}); cbTinhTrang.setFont(fontInput);
        txtCanNangNhap = new JTextField(); txtCanNangNhap.setFont(fontInput);
        txtGiaNhap = new JTextField(); txtGiaNhap.setFont(fontInput);
        txtGhiChu = new JTextField(); txtGhiChu.setFont(fontInput);

        cbChuong = new JComboBox<>(); cbChuong.setFont(fontInput);
        cbPhieuNhap = new JComboBox<>(); cbPhieuNhap.setFont(fontInput);

        // Đưa các control vào lưới
        pnlForm.add(createLabel("Mã Động Vật (*):", fontLabel)); pnlForm.add(txtMaDV);
        pnlForm.add(createLabel("Loại (*):", fontLabel)); pnlForm.add(cbLoai);

        pnlForm.add(createLabel("Giới Tính:", fontLabel)); pnlForm.add(cbGioiTinh);
        pnlForm.add(createLabel("Cân Nặng Hiện Tại (kg) (*):", fontLabel)); pnlForm.add(txtCanNang);

        pnlForm.add(createLabel("Tình Trạng:", fontLabel)); pnlForm.add(cbTinhTrang);
        pnlForm.add(createLabel("Cân Nặng Lúc Nhập (kg) (*):", fontLabel)); pnlForm.add(txtCanNangNhap);

        pnlForm.add(createLabel("Giá Nhập (VNĐ) (*):", fontLabel)); pnlForm.add(txtGiaNhap);
        pnlForm.add(createLabel("Mã Chuồng (*):", fontLabel)); pnlForm.add(cbChuong);

        pnlForm.add(createLabel("Mã Phiếu Nhập (*):", fontLabel)); pnlForm.add(cbPhieuNhap);
        pnlForm.add(createLabel("Ghi Chú:", fontLabel)); pnlForm.add(txtGhiChu);

        add(pnlForm, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pnlButtons.setBackground(new Color(245, 247, 250));

        btnLuu = new JButton("Lưu Động Vật");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(41, 128, 185));
        btnLuu.setForeground(Color.BLACK);
        btnLuu.setFocusPainted(false);
        btnLuu.setPreferredSize(new Dimension(140, 38));

        btnLamMoi = new JButton("Làm Mới");
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLamMoi.setBackground(new Color(127, 140, 141));
        btnLamMoi.setForeground(Color.BLACK);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setPreferredSize(new Dimension(110, 38));

        pnlButtons.add(btnLuu);
        pnlButtons.add(btnLamMoi);
        add(pnlButtons, BorderLayout.SOUTH);

        // Load dữ liệu cho ComboBox Chuồng và Phiếu Nhập từ CSDL
        loadComboBoxData();

        // Sự kiện nút lưu
        btnLuu.addActionListener(e -> xuyluLuu());

        // Sự kiện nút làm mới
        btnLamMoi.addActionListener(e -> xoaForm());
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    private void loadComboBoxData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load Chuồng
            cbChuong.removeAllItems();
            Statement st1 = conn.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT MaChuong, TenChuong FROM Chuong");
            while (rs1.next()) {
                cbChuong.addItem(rs1.getString("MaChuong") + " - " + rs1.getString("TenChuong"));
            }

            // Load Phiếu Nhập
            cbPhieuNhap.removeAllItems();
            Statement st2 = conn.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT MaPhieuNhap, NgayNhap FROM PhieuNhap");
            while (rs2.next()) {
                cbPhieuNhap.addItem(rs2.getString("MaPhieuNhap") + " (" + rs2.getDate("NgayNhap") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void xuyluLuu() {
        if (txtMaDV.getText().trim().isEmpty() || txtCanNang.getText().trim().isEmpty() ||
                txtCanNangNhap.getText().trim().isEmpty() || txtGiaNhap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các thông tin bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double canNang = Double.parseDouble(txtCanNang.getText().trim());
            double canNangLucNhap = Double.parseDouble(txtCanNangNhap.getText().trim());
            double giaNhap = Double.parseDouble(txtGiaNhap.getText().trim());

            if (canNang <= 0 || canNangLucNhap <= 0 || giaNhap < 0) {
                JOptionPane.showMessageDialog(this, "Cân nặng phải > 0 và giá nhập phải >= 0!", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy mã chuồng và mã phiếu nhập từ ComboBox (chọn phần trước dấu " - ")
            String selectedChuong = (String) cbChuong.getSelectedItem();
            String maChuong = selectedChuong != null ? selectedChuong.split(" - ")[0] : "";

            String selectedPhieu = (String) cbPhieuNhap.getSelectedItem();
            String maPhieuNhap = selectedPhieu != null ? selectedPhieu.split(" ")[0] : "";

            String sql = "INSERT INTO DongVat(MaDongVat, Loai, GioiTinh, CanNang, TinhTrang, GhiChu, CanNangLucNhap, GiaNhap, MaChuong, MaPhieuNhap, MaPhieuXuat) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, txtMaDV.getText().trim());
                pst.setString(2, (String) cbLoai.getSelectedItem());
                pst.setString(3, (String) cbGioiTinh.getSelectedItem());
                pst.setDouble(4, canNang);
                pst.setString(5, (String) cbTinhTrang.getSelectedItem());
                pst.setString(6, txtGhiChu.getText().trim());
                pst.setDouble(7, canNangLucNhap);
                pst.setDouble(8, giaNhap);
                pst.setString(9, maChuong);
                pst.setString(10, maPhieuNhap);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Thêm động vật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                xoaForm();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cân nặng và giá nhập phải là định dạng số hợp lệ!", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaForm() {
        txtMaDV.setText("");
        txtCanNang.setText("");
        txtCanNangNhap.setText("");
        txtGiaNhap.setText("");
        txtGhiChu.setText("");
        txtMaDV.requestFocus();
    }
}