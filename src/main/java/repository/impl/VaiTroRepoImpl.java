package repository.impl;

import model.VaiTro;
import repository.DBConnect;
import repository.IVaiTroRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VaiTroRepoImpl implements IVaiTroRepository {
    @Override
    public List<VaiTro> getAll() {
        List<VaiTro> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM VAI_TRO");
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                VaiTro vt = new VaiTro();
                vt.setMaVaiTro(rs.getInt("ma_vt"));
                vt.setTenVaiTro(rs.getString("ten_vt"));
                vt.setMoTa(rs.getString("mo_ta"));
                list.add(vt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
