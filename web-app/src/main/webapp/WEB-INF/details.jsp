<%@ page import="java.util.List" %>
<%@ page import="ru.itpark.web.model.AutoModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <%@ include file="bootstrap-css.jsp" %>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col">
            <% if (request.getAttribute("item") != null) { %>
            <h1>Details</h1>

            <div class="row">
                <% AutoModel item = (AutoModel) request.getAttribute("item"); %>
                <div class="col-sm-6 mt-3">
                    <div class="card">
                        <img src="<%= request.getContextPath() %>/images/<%= item.getImageUrl() %>" class="card-img-top"
                             alt="<%= item.getName() %>">
                        <div class="card-body">
                            <h5 class="card-title"><%= item.getName() %>
                            </h5>
                        </div>
                    </div>
                </div>
            </div>

            <form action="<%= request.getContextPath() %>/remove/<%= item.getId() %>" method="post" class="mt-3">
                <button type="submit" class="btn btn-primary mt-3">Remove</button>
            </form>
            <% } %>
        </div>
    </div>
</div>
<%@ include file="bootstrap-scripts.jsp" %>
</body>
</html>
