package model;

import java.util.Date;

public class NhatKyHoatDong {
    private long maLog;
    private String hanhDong;
    private String tableTacDong;
    private String recordTacDong;
    private String dataCu;
    private String dataMoi;
    private Date thoiGian;

    private NhanVien nhanVien; // Người thực hiện thay đổi

    public NhatKyHoatDong() {}

    public long getMaLog() { return maLog; }
    public void setMaLog(long maLog) { this.maLog = maLog; }

    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    public String getTableTacDong() { return tableTacDong; }
    public void setTableTacDong(String tableTacDong) { this.tableTacDong = tableTacDong; }

    public String getRecordTacDong() { return recordTacDong; }
    public void setRecordTacDong(String recordTacDong) { this.recordTacDong = recordTacDong; }

    public String getDataCu() { return dataCu; }
    public void setDataCu(String dataCu) { this.dataCu = dataCu; }

    public String getDataMoi() { return dataMoi; }
    public void setDataMoi(String dataMoi) { this.dataMoi = dataMoi; }

    public Date getThoiGian() { return thoiGian; }
    public void setThoiGian(Date thoiGian) { this.thoiGian = thoiGian; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
}