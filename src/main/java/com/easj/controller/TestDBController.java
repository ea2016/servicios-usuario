package com.easj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/dbtest")
public class TestDBController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<String> testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return ResponseEntity.ok("Conexión OK a PostgreSQL");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al conectar: " + e.getMessage());
        }
    }
}
