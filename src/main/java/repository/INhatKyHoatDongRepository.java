package repository;

import model.NhatKyHoatDong;
import java.util.List;
public interface INhatKyHoatDongRepository {
    boolean insertLog(NhatKyHoatDong log);
    List<NhatKyHoatDong> getAllLogs(int offset, int limit);
    int getTotalCount();
}