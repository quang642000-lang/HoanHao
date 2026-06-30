package service;

import model.NhatKyHoatDong;
import java.util.List;
public interface INhatKyHoatDongService {
    void logAction(String maNV, String hanhDong, String table, String recordId, String oldData, String newData);
    List<NhatKyHoatDong> getAllLogsByPage(int page);
    int getTotalPages();
}
