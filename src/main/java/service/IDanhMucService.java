package service;

import model.DanhMuc;
import java.util.List;
public interface IDanhMucService {
    List<DanhMuc> getAll(); List<DanhMuc> getAllByPage(int page); int getTotalPages();
    String add(DanhMuc dm); String update(DanhMuc dm); String delete(String maDanhMuc);
}
