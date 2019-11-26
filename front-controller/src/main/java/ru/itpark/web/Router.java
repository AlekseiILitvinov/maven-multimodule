package ru.itpark.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface Router {
    default void init() {};
    void route(HttpServletRequest request, HttpServletResponse response);
}
