package com.proyecto.integrador.model;

import java.sql.*;

public class AdminService {

	// Crea tablas de usuarios si no existen y asegura un admin por defecto.
	public static void ensureDefaultAdmin(Connection conn, String adminUser, String adminPass) {
		if (conn == null) {
			System.out.println("AdminService: conexión nula, no se puede asegurar admin.");
			return;
		}
		try {
			// Crear tabla 'users' si no existe
			try (Statement s = conn.createStatement()) {
				String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
						"id INT AUTO_INCREMENT PRIMARY KEY, " +
						"username VARCHAR(100) UNIQUE NOT NULL, " +
						"password VARCHAR(255) NOT NULL, " +
						"role VARCHAR(50) DEFAULT 'USER'" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
				s.execute(createUsers);
			}

			// Crear tabla 'User' (legacy) si no existe
			try (Statement s = conn.createStatement()) {
				String createUser = "CREATE TABLE IF NOT EXISTS `User` (" +
						"id INT AUTO_INCREMENT PRIMARY KEY, " +
						"username VARCHAR(100) UNIQUE NOT NULL, " +
						"password VARCHAR(255) NOT NULL, " +
						"role VARCHAR(50) DEFAULT 'USER'" +
						") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
				s.execute(createUser);
			}

			// Insertar o actualizar admin en 'users'
			upsertAdmin(conn, "users", adminUser, adminPass);
			// Insertar o actualizar admin en 'User'
			upsertAdmin(conn, "User", adminUser, adminPass);

		} catch (SQLException e) {
			System.out.println("AdminService error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void upsertAdmin(Connection conn, String table, String username, String password) throws SQLException {
		// Intentar UPDATE, si no existe INSERT
		String update = "UPDATE " + table + " SET password = ?, role = 'ADMIN' WHERE username = ?";
		try (PreparedStatement ps = conn.prepareStatement(update)) {
			ps.setString(1, password);
			ps.setString(2, username);
			int affected = ps.executeUpdate();
			if (affected == 0) {
				String insert = "INSERT INTO " + table + " (username, password, role) VALUES (?, ?, 'ADMIN')";
				try (PreparedStatement pi = conn.prepareStatement(insert)) {
					pi.setString(1, username);
					pi.setString(2, password);
					pi.executeUpdate();
				}
			}
		}
	}
}
