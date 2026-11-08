import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class BaoCaoTonChuongPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    
    public BaoCaoTonChuongPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("BÁO CÁO TÌNH HÌNH TỒN CHUỒNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        model = new DefaultTableModel(new String[]{"Mã Chuồng", "Tên Chuồng", "Sức Chứa", "Đang Nuôi", "Còn Trống"}, 0);
        table = new JTable(model);
        
        JButton btnXuatFile = new JButton("Xuất File CSV");
        btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXuatFile.setBackground(new Color(39, 174, 96));
        btnXuatFile.setForeground(Color.BLACK);
        btnXuatFile.setFocusPainted(false);
        btnXuatFile.setPreferredSize(new Dimension(140, 35));
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.add(btnXuatFile);
        
        btnXuatFile.addActionListener(e -> xuatFileCSV());
        
        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
        
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
