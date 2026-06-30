package repository;

import model.Topping;
import java.util.List;

public interface IToppingRepository {
    List<Topping> getAll();
    List<Topping> getAll(int offset, int limit);
    int getTotalCount();
    boolean add(Topping tp);
    boolean update(Topping tp);
    boolean updateTrangThai(String maTopping, int trangThai);
    boolean delete(String maTopping);
    List<Topping> search(String keyword);
    Topping findById(String maTopping);
}