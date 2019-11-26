package ru.itpark.web.router;

import lombok.val;
import ru.itpark.util.JdbcTemplate;
import ru.itpark.web.Router;
import ru.itpark.web.exception.InitializationException;
import ru.itpark.web.exception.NotFoundException;
import ru.itpark.web.model.AutoModel;
import ru.itpark.web.repository.AutoRepositoryJdbcImpl;
import ru.itpark.web.service.AutoService;
import ru.itpark.web.service.AutoServiceImpl;
import ru.itpark.web.service.FileServiceImpl;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

public class AppRouter implements Router {
    // Container
    // Map<Class, Object> <- object.getClass()
    // Reflections -> Type
    // .newInstance
    // JNDI
    // Factory Class -> method.invoke() <- Component
    private AutoService autoService;
    private FileServiceImpl fileService;

    // без контейнера - сами тут вручную настраиваем и всё инициализируем
    public void init() {
        try {
            val context = new InitialContext();
            val dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/db");
            val uploadPath = System.getenv("UPLOAD_PATH");
            fileService = new FileServiceImpl(uploadPath);
            autoService = new AutoServiceImpl(new AutoRepositoryJdbcImpl(dataSource, new JdbcTemplate()), fileService);
        } catch (NamingException e) {
            throw new InitializationException(e);
        }
    }

    @Override
    public void route(HttpServletRequest request, HttpServletResponse response) {
        try {
            val rootUrl = request.getContextPath().isEmpty() ? "/" : request.getContextPath();
            val url = request.getRequestURI().substring(request.getContextPath().length());
            request.setCharacterEncoding("UTF-8");

            if (url.equals("/")) {
                if (request.getMethod().equals("GET")) {
                    val items = autoService.getAll();
                    request.setAttribute("items", items);
                    request.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(request, response);
                    return;
                }

                if (request.getMethod().equals("POST")) {

                    val name = request.getParameter("name");
                    val part = request.getPart("image");
                    autoService.save(new AutoModel(0, name, null), part);
                    response.sendRedirect(rootUrl);
                    return;
                }
                throw new NotFoundException();
            }

            // Sample: /details/{id}
            // TODO: обычно парсинг делают через регулярные выражения, но тут простой вариант
            if (url.startsWith("/details/")) {
                if (request.getMethod().equals("GET")) {
                    val id = Integer.parseInt(url.substring("/details/".length()));
                    val item = autoService.getById(id);
                    request.setAttribute("item", item);
                    request.getRequestDispatcher("/WEB-INF/details.jsp").forward(request, response);
                    return;
                }

                throw new NotFoundException();
            }

            if (url.startsWith("/remove/")) {
                if (request.getMethod().equals("POST")) {
                    val id = Integer.parseInt(url.substring("/remove/".length()));
                    autoService.removeById(id);
                    response.sendRedirect(rootUrl);
                    return;
                }

                throw new NotFoundException();
            }

            if (url.startsWith("/images/")) {
                if (request.getMethod().equals("GET")) {
                    val id = url.substring("/images/".length());
                    fileService.readFile(id, response.getOutputStream());
                    return;
                }

                throw new NotFoundException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                request.getRequestDispatcher("/WEB-INF/404.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
