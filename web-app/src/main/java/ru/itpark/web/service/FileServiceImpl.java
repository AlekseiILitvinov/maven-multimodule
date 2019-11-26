package ru.itpark.web.service;

import lombok.val;
import ru.itpark.web.exception.FileAccessException;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class FileServiceImpl implements FileService {
    private final String uploadPath;

    public FileServiceImpl(String uploadPath) {
        try {
            this.uploadPath = uploadPath;
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public void readFile(String id, OutputStream os) {
        try {
            val path = Paths.get(uploadPath).resolve(id);
            Files.copy(path, os);
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public String writeFile(Part part) {
        try {
            val id = UUID.randomUUID().toString();
            part.write(Paths.get(uploadPath).resolve(id).toString());
            return id;
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }
}
