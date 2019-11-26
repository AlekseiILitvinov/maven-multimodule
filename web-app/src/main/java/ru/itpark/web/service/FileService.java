package ru.itpark.web.service;

import javax.servlet.http.Part;
import java.io.OutputStream;

public interface FileService {
    void readFile(String id, OutputStream os);

    String writeFile(Part part);
}
