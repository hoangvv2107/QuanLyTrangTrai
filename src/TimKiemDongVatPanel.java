import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TimKiemDongVatPanel extends JPanel {
    private JTextField txtMaGhiChu, txtCanNangTu, txtCanNangDen;
    private JComboBox<String> cbLoai, cbGioiTinh, cbChuong, cbTrangThai;
    private JTable tbKetQua, tbLichSuKham;
    private DefaultTableModel modelKetQua, modelLichSu;
    private JButton btnTimKiem, btnXoaBoLoc;

    public TimKiemDongVatPanel() {
        setLayout(new BorderLayout(10, 10));
        
        // --- 1. PANEL TIÊU CHÍ TÌM KIẾM ---
        JPanel pnlTieuChi = new JPanel(new GridBagLayout());
        pnlTieuChi.setBorder(new TitledBorder("Tiêu chí tìm kiếm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Dòng 1
        gbc.gridx = 0; gbc.gridy = 0; pnlTieuChi.add(new JLabel("Mã / Ghi chú:"), gbc);
        gbc.gridx = 1; pnlTieuChi.add(txtMaGhiChu = new JTextField(15), gbc);
        gbc.gridx = 2; pnlTieuChi.add(new JLabel("Loại:"), gbc);
        gbc.gridx = 3; pnlTieuChi.add(cbLoai = new JComboBox<>(new String[]{"-- Tất cả --", "Bò", "Lợn", "Gà"}), gbc);
        gbc.gridx = 4; pnlTieuChi.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 5; pnlTieuChi.add(cbGioiTinh = new JComboBox<>(new String[]{"-- Tất cả --", "Đực", "Cái", "Trống", "Mái"}), gbc);

        // Dòng 2
        gbc.gridx = 0; gbc.gridy = 1; pnlTieuChi.add(new JLabel("Chuồng:"), gbc);
        gbc.gridx = 1; pnlTieuChi.add(cbChuong = new JComboBox<>(new String[]{"-- Tất cả --"}), gbc);
        gbc.gridx = 2; pnlTieuChi.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; pnlTieuChi.add(cbTrangThai = new JComboBox<>(new String[]{"-- Tất cả --", "Đang nuôi", "Đã xuất bán"}), gbc);
        gbc.gridx = 4; pnlTieuChi.add(new JLabel("Cân nặng từ - đến (kg):"), gbc);
        JPanel pnlCanNang = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        pnlCanNang.add(txtCanNangTu = new JTextField(4));
        pnlCanNang.add(new JLabel(" -> "));
        pnlCanNang.add(txtCanNangDen = new JTextField(4));
        gbc.gridx = 5; pnlTieuChi.add(pnlCanNang, gbc);

        // Nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnTimKiem = new JButton("Tìm kiếm"));
        pnlButtons.add(btnXoaBoLoc = new JButton("Xóa bộ lọc"));
        gbc.gridx = 5; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        pnlTieuChi.add(pnlButtons, gbc);

        // --- 2. PANEL KẾT QUẢ TÌM KIẾM ---
        String[] colsKetQua = {"Mã ĐV", "Loại", "Giới tính", "Cân nặng (kg)", "Tình trạng", "Chuồng", "Phiếu nhập", "Nhà cung cấp", "Giá nhập", "Trạng thái", "Khách mua"};
        modelKetQua = new DefaultTableModel(colsKetQua, 0);
        tbKetQua = new JTable(modelKetQua);
        JScrollPane scrollKetQua = new JScrollPane(tbKetQua);
        scrollKetQua.setBorder(new TitledBorder("Kết quả tìm kiếm"));
        scrollKetQua.setPreferredSize(new Dimension(800, 250));

        // --- 3. PANEL LỊCH SỬ KHÁM BỆNH ---
        String[] colsLichSu = {"Lần khám", "Ngày khám", "Chẩn đoán", "Đơn thuốc", "Bác sĩ thú y"};
        modelLichSu = new DefaultTableModel(colsLichSu, 0);
        tbLichSuKham = new JTable(modelLichSu);
        JScrollPane scrollLichSu = new JScrollPane(tbLichSuKham);
        scrollLichSu.setBorder(new TitledBorder("Lịch sử khám bệnh của động vật đang chọn"));
        scrollLichSu.setPreferredSize(new Dimension(800, 150));

        // Layout tổng
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollKetQua, scrollLichSu);
        splitPane.setResizeWeight(0.7);

        add(pnlTieuChi, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        btnTimKiem.addActionListener(e -> loadDataDongVat());
        tbKetQua.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tbKetQua.getSelectedRow() != -1) {
                String maDV = (String) tbKetQua.getValueAt(tbKetQua.getSelectedRow(), 0);
                loadChiTietBenhAn(maDV);
            }
        });
    }

    private void loadDataDongVat() {
        modelKetQua.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT dv.MaDongVat, dv.Loai, dv.GioiTinh, dv.CanNang, dv.TinhTrang, " +
                            "c.TenChuong, dv.MaPhieuNhap, ncc.TenNCC, dv.GiaNhap, " +
                            "IF(dv.MaPhieuXuat IS NULL, 'Đang nuôi', 'Đã xuất') as TrangThai " +
                            "FROM DongVat dv " +
                            "JOIN Chuong c ON dv.MaChuong = c.MaChuong " +
                            "JOIN PhieuNhap pn ON dv.MaPhieuNhap = pn.MaPhieuNhap " +
                            "JOIN NhaCungCap ncc ON pn.MaNCC = ncc.MaNCC " +
                            "WHERE 1=1"
            );

            java.util.List<Object> params = new java.util.ArrayList<>();

            // 1. Lọc theo Mã / Ghi chú
            String keyword = txtMaGhiChu.getText().trim();
            if (!keyword.isEmpty()) {
                sql.append(" AND (dv.MaDongVat LIKE ? OR dv.GhiChu LIKE ?)");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }

            // 2. Lọc theo Loại
            String loai = (String) cbLoai.getSelectedItem();
            if (loai != null && !loai.equals("-- Tất cả --")) {
                sql.append(" AND dv.Loai = ?");
                params.add(loai);
            }

            // 3. Lọc theo Giới tính
            String gioiTinh = (String) cbGioiTinh.getSelectedItem();
            if (gioiTinh != null && !gioiTinh.equals("-- Tất cả --")) {
                sql.append(" AND dv.GioiTinh = ?");
                params.add(gioiTinh);
            }

            // 4. Lọc theo Trạng thái
            String trangThai = (String) cbTrangThai.getSelectedItem();
            if (trangThai != null && !trangThai.equals("-- Tất cả --")) {
                if (trangThai.equals("Đang nuôi")) {
                    sql.append(" AND dv.MaPhieuXuat IS NULL");
                } else if (trangThai.equals("Đã xuất bán")) {
                    sql.append(" AND dv.MaPhieuXuat IS NOT NULL");
                }
            }

            // 5. Lọc theo khoảng Cân nặng từ - đến
            String cnTu = txtCanNangTu.getText().trim();
            if (!cnTu.isEmpty()) {
                try {
                    double dTu = Double.parseDouble(cnTu);
                    sql.append(" AND dv.CanNang >= ?");
                    params.add(dTu);
                } catch (NumberFormatException ignored) {}
            }

            String cnDen = txtCanNangDen.getText().trim();
            if (!cnDen.isEmpty()) {
                try {
                    double dDen = Double.parseDouble(cnDen);
                    sql.append(" AND dv.CanNang <= ?");
                    params.add(dDen);
                } catch (NumberFormatException ignored) {}
            }

            PreparedStatement pst = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                modelKetQua.addRow(new Object[]{
                        rs.getString("MaDongVat"), rs.getString("Loai"), rs.getString("GioiTinh"),
                        rs.getDouble("CanNang"), rs.getString("TinhTrang"), rs.getString("TenChuong"),
                        rs.getString("MaPhieuNhap"), rs.getString("TenNCC"), rs.getDouble("GiaNhap"),
                        rs.getString("TrangThai"), ""
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn: " + ex.getMessage());
        }
    }

    private void loadChiTietBenhAn(String maDongVat) {
        modelLichSu.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT hs.LanKham, hs.NgayKham, hs.ChuanDoan, hs.DonThuoc, nv.HoVaTen " +
                         "FROM HoSoBenhAn hs " +
                         "JOIN NhanVien nv ON hs.MaNV = nv.MaNV " +
                         "WHERE hs.MaDongVat = ? ORDER BY hs.LanKham DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, maDongVat);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                modelLichSu.addRow(new Object[]{
                    rs.getInt("LanKham"), rs.getDate("NgayKham"), rs.getString("ChuanDoan"),
                    rs.getString("DonThuoc"), rs.getString("HoVaTen")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}