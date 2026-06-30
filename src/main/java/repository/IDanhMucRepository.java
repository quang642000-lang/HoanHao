package repository;

import model.DanhMuc;
import java.util.List;

public interface IDanhMucRepository {
    List<DanhMuc> getAll(); List<DanhMuc> getAll(int offset, int limit);
    int getTotalCount(); boolean add(DanhMuc dm); boolean update(DanhMuc dm); boolean delete(String maDM);
}