import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class BaoCaoThongKePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    
    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout());
        
        JTextArea txtMoTa = new JTextArea("BÁO CÁO TÌNH HÌNH TỒN CHUỒNG\n" +
                "- Người tạo: Nhân viên Quản lý chuồng trại (HoVaTen)\n" +
                "- Người nhận: Giám đốc Trang trại\n" +
                "- Mục đích: Nắm bắt số lượng vật nuôi hiện hành để lên kế hoạch nhập xuất và mua thức ăn.");
        txtMoTa.setEditable(false);
        txtMoTa.setBackground(new Color(240, 240, 240));
        
        model = new DefaultTableModel(new String[]{"Mã Chuồng", "Tên Chuồng", "Sức Chứa", "Đang Nuôi", "Còn Trống"}, 0);
        table = new JTable(model);
        
        JButton btnXuatFile = new JButton("Xuất File CSV");
        btnXuatFile.addActionListener(e -> xuatFileCSV());
        
        add(txtMoTa, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnXuatFile, BorderLayout.SOUTH);
        
        loadBaoCao();
    }

    private void loadBaoCao() {
        String sql = "SELECT c.MaChuong, c.TenChuong, c.SucChua, " +
                     "COUNT(dv.MaDongVat) as DangNuoi " +
                     "FROM Chuong c LEFT JOIN DongVat dv ON c.MaChuong = dv.MaChuong AND dv.MaPhieuXuat IS NULL " +
                     "GROUP BY c.MaChuong, c.TenChuong, c.SucChua";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while(rs.next()) {
                int sucChua = rs.getInt("SucChua");
                int dangNuoi = rs.getInt("DangNuoi");
                model.addRow(new Object[]{
                    rs.getString("MaChuong"), rs.getString("TenChuong"), sucChua, dangNuoi, (sucChua - dangNuoi)
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void xuatFileCSV() {
        try (FileWriter fw = new FileWriter("BaoCaoTonChuong.csv")) {
            fw.write("MaChuong,TenChuong,SucChua,DangNuoi,ConTrong\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    fw.write(model.getValueAt(i, j).toString() + (j == model.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Xuất file BaoCaoTonChuong.csv thành công!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi ghi file: " + ex.getMessage());
        }
    }
}