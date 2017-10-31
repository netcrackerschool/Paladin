package com.netcracker.paladin.infrastructure.repositories;

import com.netcracker.paladin.domain.PublicKeyEntry;
import com.netcracker.paladin.infrastructure.repositories.exceptions.NoPublicKeyForEmailException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 27.10.14.
 */
public class PublicKeyEntryRepositoryImpl implements PublicKeyEntryRepository{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PublicKeyEntryRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void insert(PublicKeyEntry publicKeyEntry) {
        deleteByEmail(publicKeyEntry.getEmail());

        String sql = "INSERT INTO PUBLICKEYS " + "(EMAIL, PUBLICKEY) VALUES (:email, :publickey)";

        Map namedParameters = new HashMap();
        namedParameters.put("email", publicKeyEntry.getEmail());
        namedParameters.put("publickey", publicKeyEntry.getPublicKey());

        namedParameterJdbcTemplate.update(sql, namedParameters);
    }

    public PublicKeyEntry findByEmail(String email) {

        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("email", email);

        String sql = "SELECT * FROM PUBLICKEYS WHERE EMAIL = :email";

        List<PublicKeyEntry> result = namedParameterJdbcTemplate.query(
                sql,
                namedParameters,
                new PublicKeyEntryMapper());

        switch (result.size()){
            case 0:
                throw new NoPublicKeyForEmailException();
            case 1:
                return result.get(0);
            default:
                throw new IllegalStateException("Multiple public keys for one email!");
        }
    }

    @Override
    public void deleteByEmail(String email) {
        String SQL = "DELETE FROM PUBLICKEYS WHERE EMAIL = :email";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("email", email);
        namedParameterJdbcTemplate.update(SQL, namedParameters);
    }

    @Override
    public List<PublicKeyEntry> findAll() {

        Map<String, Object> namedParameters = new HashMap<String, Object>();

        String sql = "SELECT * FROM PUBLICKEYS";

        List<PublicKeyEntry> result = namedParameterJdbcTemplate.query(
                sql,
                namedParameters,
                new PublicKeyEntryMapper());

        return result;
    }

    private static final class PublicKeyEntryMapper implements RowMapper<PublicKeyEntry> {

        public PublicKeyEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            String email = rs.getString("EMAIL");
            byte[] publicKey = rs.getBytes("PUBLICKEY");
            PublicKeyEntry publicKeyEntry = new PublicKeyEntry(email, publicKey);
            return publicKeyEntry;
        }
    }
}
