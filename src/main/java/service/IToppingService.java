package service;

import model.Topping;
import java.util.List;

public interface IToppingService {
    List<Topping> getAll();
    List<Topping> getAllByPage(int page);
    int getTotalPages();
    String add(Topping tp);
    String update(Topping tp);
    String updateTrangThai(String maTopping, int trangThai);
    String delete(String maTopping);
    List<Topping> search(String keyword);
}
