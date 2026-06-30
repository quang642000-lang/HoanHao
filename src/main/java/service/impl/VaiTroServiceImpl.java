package service.impl;

import model.VaiTro;
import repository.IVaiTroRepository;
import repository.impl.VaiTroRepoImpl;
import service.IVaiTroService;
import java.util.List;

public class VaiTroServiceImpl implements IVaiTroService {
    private IVaiTroRepository repo = new VaiTroRepoImpl();
    @Override
    public List<VaiTro> getAll() { return repo.getAll(); }
}

