package ru.itpark.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FrontController extends HttpServlet {
    private Router router;

    @Override
    public void init() throws ServletException {
        final List<Router> routes = StreamSupport.stream(ServiceLoader.load(Router.class).spliterator(), false)
                .collect(Collectors.toList());

        if (routes.isEmpty()) {
            throw new RuntimeException("no router found");
        }

        if (routes.size() != 1) {
            throw new RuntimeException("multiple routers found");
        }

        router = routes.get(0);
        router.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("service");
        router.route(req, resp);
    }
}
