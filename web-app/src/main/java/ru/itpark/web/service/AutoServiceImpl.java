package ru.itpark.web.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.itpark.web.exception.NotFoundException;
import ru.itpark.web.model.AutoModel;
import ru.itpark.web.repository.AutoRepository;

import javax.servlet.http.Part;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AutoServiceImpl implements AutoService {
    private final AutoRepository repository;
    private final FileService fileService;

    @Override
    public List<AutoModel> getAll() {
        return repository.getAll();
    }

    @Override
    public AutoModel getById(int id) {
        return repository.getById(id).orElseThrow(() -> new NotFoundException(String.format("Object with id %d not found", id)));
    }

    @Override
    public void save(AutoModel model, Part part) {
        if (part != null) {
            val image = fileService.writeFile(part);
            model.setImageUrl(image);
        }
        repository.save(model);
    }

    @Override
    public void removeById(int id) {
        Optional<AutoModel> optional = repository.getById(id);
        if (optional.isPresent()) {
            final String imageUrl = optional.get().getImageUrl();
            if (imageUrl != null) {
                fileService.eraseFile(imageUrl);
            }
            repository.removeById(id);
        }
    }
}
