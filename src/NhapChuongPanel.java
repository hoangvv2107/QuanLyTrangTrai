import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class NhapChuongPanel extends JPanel {
    private JTextField txtMaChuong, txtTenChuong, txtSucChua, txtViTri;
    private JButton btnLuu, btnLamMoi;

    public NhapChuongPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Tiêu đề form
        JLabel lblTitle = new JLabel("QUẢN LÝ THÔNG TIN CHUỒNG TRẠI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form nhập liệu chính
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 225, 233), 1),
            new EmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        // Dòng 1: Mã Chuồng
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblMa = new JLabel("Mã Chuồng (*):");
        lblMa.setFont(fontLabel);
        pnlForm.add(lblMa, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtMaChuong = new JTextField(20);
        txtMaChuong.setFont(fontInput);
        txtMaChuong.setPreferredSize(new Dimension(250, 32));
        pnlForm.add(txtMaChuong, gbc);

        // Dòng 2: Tên Chuồng
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblTen = new JLabel("Tên Chuồng (*):");
        lblTen.setFont(fontLabel);
        pnlForm.add(lblTen, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTenChuong = new JTextField(20);
        txtTenChuong.setFont(fontInput);
        txtTenChuong.setPreferredSize(new Dimension(250, 32));
        pnlForm.add(txtTenChuong, gbc);

        // Dòng 3: Sức chứa
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblSucChua = new JLabel("Sức Chứa (> 0) (*):");
        lblSucChua.setFont(fontLabel);
        pnlForm.add(lblSucChua, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtSucChua = new JTextField(20);
        txtSucChua.setFont(fontInput);
        txtSucChua.setPreferredSize(new Dimension(250, 32));
        pnlForm.add(txtSucChua, gbc);

        // Dòng 4: Vị trí
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblViTri = new JLabel("Vị Trí:");
        lblViTri.setFont(fontLabel);
        pnlForm.add(lblViTri, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtViTri = new JTextField(20);
        txtViTri.setFont(fontInput);
        txtViTri.setPreferredSize(new Dimension(250, 32));
        pnlForm.add(txtViTri, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // Panel nút bấm phía dưới
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        pnlButtons.setBackground(new Color(245, 247, 250));

        btnLuu = new JButton("Lưu Dữ Liệu");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(39, 174, 96));
        btnLuu.setForeground(Color.BLACK);
        btnLuu.setFocusPainted(false);
        btnLuu.setPreferredSize(new Dimension(130, 38));

        btnLamMoi = new JButton("Làm Mới");
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLamMoi.setBackground(new Color(127, 140, 141));
        btnLamMoi.setForeground(Color.BLACK);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setPreferredSize(new Dimension(110, 38));

        pnlButtons.add(btnLuu);
        pnlButtons.add(btnLamMoi);
        add(pnlButtons, BorderLayout.SOUTH);

        // Sự kiện nút Lưu
        btnLuu.addActionListener(e -> {
            if (txtMaChuong.getText().trim().isEmpty() || txtTenChuong.getText().trim().isEmpty() || txtSucChua.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các thông tin bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int sucChua = Integer.parseInt(txtSucChua.getText().trim());
                if (sucChua <= 0) throw new NumberFormatException();
                
                luuVaoDB(txtMaChuong.getText().trim(), txtTenChuong.getText().trim(), sucChua, txtViTri.getText().trim());
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên lớn hơn 0!", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sự kiện nút Làm mới
        btnLamMoi.addActionListener(e -> {
            txtMaChuong.setText("");
            txtTenChuong.setText("");
            txtSucChua.setText("");
            txtViTri.setText("");
            txtMaChuong.requestFocus();
        });
    }

    private void luuVaoDB(String ma, String ten, int sucChua, String viTri) {
        String sql = "INSERT INTO Chuong(MaChuong, TenChuong, SucChua, ViTri) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ma);
            pst.setString(2, ten);
            pst.setInt(3, sucChua);
            pst.setString(4, viTri);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm mới chuồng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Clear form sau khi thêm thành công
            btnLamMoi.doClick();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
