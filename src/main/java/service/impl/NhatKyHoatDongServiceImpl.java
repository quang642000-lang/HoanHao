package service.impl;
import model.NhatKyHoatDong;
import model.NhanVien;
import repository.INhatKyHoatDongRepository;
import repository.impl.NhatKyHoatDongRepoImpl;
import service.INhatKyHoatDongService;
import java.util.List;

public class NhatKyHoatDongServiceImpl implements INhatKyHoatDongService {
    private INhatKyHoatDongRepository repo = new NhatKyHoatDongRepoImpl();
    private final int LIMIT = 15;

    @Override
    public void logAction(String maNV, String hanhDong, String table, String recordId, String oldData, String newData) {
        NhatKyHoatDong log = new NhatKyHoatDong();
        NhanVien nv = new NhanVien(); nv.setMaNV(maNV); log.setNhanVien(nv);
        log.setHanhDong(hanhDong); log.setTableTacDong(table);
        log.setRecordTacDong(recordId); log.setDataCu(oldData); log.setDataMoi(newData);
        repo.insertLog(log);
    }

    @Override public List<NhatKyHoatDong> getAllLogsByPage(int page) { return repo.getAllLogs((page - 1) * LIMIT, LIMIT); }
    @Override public int getTotalPages() { return (int) Math.ceil((double) repo.getTotalCount() / LIMIT); }
}
