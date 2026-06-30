package repository;

import util.ConfigUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private static final String URL = ConfigUtil.getProperty("db.url");
    private static final String USER = ConfigUtil.getProperty("db.user");
    private static final String PASS = ConfigUtil.getProperty("db.password");

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println(" LỖI: Thiếu thư viện mssql-jdbc trong pom.xml!");
        } catch (SQLException e) {
            System.err.println(" LỖI KẾT NỐI DATABASE: " + e.getMessage());
        }
        return con;
    }

    public static void main(String[] args) {
        System.out.println("⏳ Đang thử kết nối tới SQL Server...");
        Connection con = getConnection();
        if (con != null) {
            System.out.println(" CHÚC MỪNG! KẾT NỐI THÀNH CÔNG TỚI DATABASE!");
        } else {
            System.out.println(" KẾT NỐI THẤT BẠI. HÃY KIỂM TRA LẠI FILE application.properties NHÉ!");
        }
    }
}