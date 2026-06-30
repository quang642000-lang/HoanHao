package repository.impl;

import model.NhatKyHoatDong;
import model.NhanVien;
import repository.DBConnect;
import repository.INhatKyHoatDongRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhatKyHoatDongRepoImpl implements INhatKyHoatDongRepository {
    @Override
    public boolean insertLog(NhatKyHoatDong log) {
        String sql = "INSERT INTO NHAT_KY_HOAT_DONG (ma_nv, hanh_dong, table_tac_dong, record_tac_dong, data_cu, data_moi) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, log.getNhanVien().getMaNV());
            ps.setString(2, log.getHanhDong());
            ps.setString(3, log.getTableTacDong());
            ps.setString(4, log.getRecordTacDong());
            ps.setString(5, log.getDataCu());
            ps.setString(6, log.getDataMoi());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<NhatKyHoatDong> getAllLogs(int offset, int limit) {
        List<NhatKyHoatDong> list = new ArrayList<>();
        String sql = "SELECT nk.*, nv.ho_ten FROM NHAT_KY_HOAT_DONG nk JOIN NHAN_VIEN nv ON nk.ma_nv = nv.ma_nv ORDER BY nk.ma_log DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset); ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhatKyHoatDong nk = new NhatKyHoatDong();
                    nk.setMaLog(rs.getLong("ma_log"));
                    nk.setHanhDong(rs.getString("hanh_dong"));
                    nk.setTableTacDong(rs.getString("table_tac_dong"));
                    nk.setRecordTacDong(rs.getString("record_tac_dong"));
                    nk.setDataCu(rs.getString("data_cu"));
                    nk.setDataMoi(rs.getString("data_moi"));
                    nk.setThoiGian(rs.getTimestamp("thoi_gian"));
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nv"));
                    nv.setHoTen(rs.getString("ho_ten"));
                    nk.setNhanVien(nv);
                    list.add(nk);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getTotalCount() {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM NHAT_KY_HOAT_DONG"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }
}
