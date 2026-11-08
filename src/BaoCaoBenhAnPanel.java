import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class BaoCaoBenhAnPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    
    public BaoCaoBenhAnPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("BÁO CÁO THỐNG KÊ LỊCH SỬ KHÁM BỆNH ĐỘNG VẬT", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(211, 84, 0));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        model = new DefaultTableModel(new String[]{"Mã ĐV", "Loại", "Tổng Số Lần Khám", "Ngày Khám Gần Nhất", "Bác Sĩ Khám Gần Nhất"}, 0);
        table = new JTable(model);
        
        JButton btnXuatFile = new JButton("Xuất File CSV");
        btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXuatFile.setBackground(new Color(41, 128, 185));
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
        String sql = "SELECT dv.MaDongVat, dv.Loai, COUNT(hs.LanKham) as TongSoLan, " +
                     "MAX(hs.NgayKham) as NgayMoiNhat, " +
                     "(SELECT nv.HoVaTen FROM HoSoBenhAn hs2 JOIN NhanVien nv ON hs2.MaNV = nv.MaNV WHERE hs2.MaDongVat = dv.MaDongVat ORDER BY hs2.LanKham DESC LIMIT 1) as BacSi " +
                     "FROM DongVat dv LEFT JOIN HoSoBenhAn hs ON dv.MaDongVat = hs.MaDongVat " +
                     "GROUP BY dv.MaDongVat, dv.Loai";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaDongVat"), 
                    rs.getString("Loai"), 
                    rs.getInt("TongSoLan"), 
                    rs.getDate("NgayMoiNhat") != null ? rs.getDate("NgayMoiNhat").toString() : "Chưa khám",
                    rs.getString("BacSi") != null ? rs.getString("BacSi") : "Không có"
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void xuatFileCSV() {
        try (FileWriter fw = new FileWriter("BaoCaoBenhAnDongVat.csv")) {
            fw.write("MaDongVat,Loai,TongSoLanKham,NgayKhamGanNhat,BacSiKhamGanNhat\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    fw.write(model.getValueAt(i, j).toString() + (j == model.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Xuất file BaoCaoBenhAnDongVat.csv thành công!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi ghi file: " + ex.getMessage());
        }
    }
}
