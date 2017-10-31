package com.netcracker.paladin.infrastructure.repositories;

import com.netcracker.paladin.infrastructure.repositories.exceptions.NoSavedConfigPropertiesException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created on 30.10.14.
 */
public class ConfigRepositoryImpl implements ConfigRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ConfigRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void saveProperties(Properties properties){
        delete();
        insert(properties);
    }

    public Properties loadProperties() throws NoSavedConfigPropertiesException {
        Map<String, Object> namedParameters = new HashMap<String, Object>();

        String sql = "SELECT * FROM CONFIG";

        List<Properties> result = namedParameterJdbcTemplate.query(
                sql,
                new ConfigMapper());

        switch (result.size()){
            case 0:
                throw new NoSavedConfigPropertiesException();
            case 1:
                return result.get(0);
            default:
                throw new Error("Multiple property rows");
        }
    }

    private void insert(Properties properties){
        String sql = "INSERT INTO CONFIG " +
                    "(USER_MY, HOST, PORT, PASSWORD_MY, STARTTLS, AUTH) " +
                    "VALUES (:user_my, :host, :port, :password_my, :starttls, :auth)";

        Map namedParameters = new HashMap();
        namedParameters.put("user_my", properties.getProperty("mail.user"));
        namedParameters.put("host", properties.getProperty("mail.smtp.host"));
        namedParameters.put("port", properties.getProperty("mail.smtp.port"));
        namedParameters.put("password_my", properties.getProperty("mail.password"));
        namedParameters.put("starttls", properties.getProperty("mail.smtp.starttls.enable"));
        namedParameters.put("auth", properties.getProperty("mail.smtp.auth"));

        namedParameterJdbcTemplate.update(sql, namedParameters);
    }

    private void delete(){
        String SQL = "DELETE FROM CONFIG";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameterJdbcTemplate.update(SQL, namedParameters);
    }

    private static final class ConfigMapper implements RowMapper<Properties> {

        public Properties mapRow(ResultSet rs, int rowNum) throws SQLException {
            Properties loadedProperties = new Properties();

            loadedProperties.setProperty("mail.smtp.host", rs.getString("HOST"));
            loadedProperties.setProperty("mail.smtp.port", rs.getString("PORT"));
            loadedProperties.setProperty("mail.user", rs.getString("USER_MY"));
            loadedProperties.setProperty("mail.password", rs.getString("PASSWORD_MY"));
            loadedProperties.setProperty("mail.smtp.starttls.enable", rs.getString("STARTTLS"));
            loadedProperties.setProperty("mail.smtp.auth", rs.getString("AUTH"));

            return loadedProperties;
        }
    }
}
