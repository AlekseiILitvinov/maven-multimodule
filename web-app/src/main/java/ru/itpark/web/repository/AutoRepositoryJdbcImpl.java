package ru.itpark.web.repository;


import ru.itpark.util.JdbcTemplate;
import ru.itpark.util.RowMapper;
import ru.itpark.web.exception.DataAccessException;
import ru.itpark.web.model.AutoModel;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AutoRepositoryJdbcImpl implements AutoRepository {
    private final DataSource ds;
    private final JdbcTemplate template;
    private final RowMapper<AutoModel> mapper = rs -> new AutoModel(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("imageUrl")
    );

    public AutoRepositoryJdbcImpl(DataSource ds, JdbcTemplate template) {
        this.ds = ds;
        this.template = template;

        try {
            template.update(ds, "CREATE TABLE IF NOT EXISTS autos (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, imageUrl TEXT);");
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<AutoModel> getAll() {
        try {
            return template.queryForList(ds, "SELECT id, name, imageUrl FROM autos;", mapper);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Optional<AutoModel> getById(int id) {
        try {
            return template.queryForObject(ds, "SELECT id, name, imageUrl FROM autos;", mapper);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void save(AutoModel model) {
        try {
            if (model.getId() == 0) {
                int id = template.<Integer>updateForId(ds, "INSERT INTO autos(name, imageUrl) VALUES (?, ?);", stmt -> {
                    stmt.setString(1, model.getName());
                    stmt.setString(2, model.getImageUrl());
                    return stmt;
                });
                model.setId(id);
            } else {
                template.update(ds, "UPDATE autos SET name = ?, imageUrl = ? WHERE id = ?;", stmt -> {
                    stmt.setString(1, model.getName());
                    stmt.setString(2, model.getImageUrl());
                    stmt.setInt(3, model.getId());
                    return stmt;
                });
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void removeById(int id) {
        try {
            template.update(ds, "DELETE FROM autos WHERE id = ?;", stmt -> {
                stmt.setInt(1, id);
                return stmt;
            });
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
