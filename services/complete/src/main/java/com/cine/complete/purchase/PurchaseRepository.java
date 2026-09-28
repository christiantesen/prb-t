package com.cine.complete.purchase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class PurchaseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PurchaseRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<CandyProduct> listCandy() {
        return jdbcTemplate.query("CALL sp_list_candy()", (rs, row) -> new CandyProduct(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price")));
    }

    public String complete(String email, String fullName, String documentType, String documentNumber,
                           String operationDate, String transactionId, BigDecimal total, List<Map<String, Object>> items) {
        String itemsJson;
        try {
            itemsJson = objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo armar el detalle de la compra", exception);
        }

        return jdbcTemplate.execute((ConnectionCallback<String>) connection -> {
            try (CallableStatement statement = connection.prepareCall("{call sp_complete_purchase(?,?,?,?,?,?,?,?,?)}")) {
                statement.setString(1, email);
                statement.setString(2, fullName);
                statement.setString(3, documentType);
                statement.setString(4, documentNumber);
                statement.setString(5, operationDate);
                statement.setString(6, transactionId);
                statement.setBigDecimal(7, total);
                statement.setString(8, itemsJson);
                statement.registerOutParameter(9, Types.CHAR);
                statement.execute();
                return statement.getString(9);
            }
        });
    }
}
