import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Trangtrai?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root"; // Đổi nếu bạn dùng user khác
    private static final String PASSWORD = "21072006"; // Mật khẩu database của bạn

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}