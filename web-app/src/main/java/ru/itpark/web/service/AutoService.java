package ru.itpark.web.service;

import ru.itpark.web.model.AutoModel;

import javax.servlet.http.Part;
import java.util.List;

public interface AutoService {
    List<AutoModel> getAll();

    AutoModel getById(int id);

    void save(AutoModel model, Part part);

    void removeById(int id);
}
