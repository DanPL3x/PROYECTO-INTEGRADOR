package com.proyecto.integrador.model;

import java.sql.*;

public class AdminAuth {

	public static boolean authenticate(Connection conn, String usuario, String pass) {
		if (conn == null) return false;

		// 1) Intentar usar la lógica existente en model.User si está disponible
		try {
			User u = new User(conn);
			if (u.userAutenticacion(usuario, pass)) return true;
		} catch (Throwable t) {
			// ignorar y continuar con fallback
		}

		// 2) Fallback: comprobar tablas 'users' y 'User'
		String[] tablas = {"users", "`User`"};
		for (String tabla : tablas) {
			String sql = "SELECT password FROM " + tabla + " WHERE username = ?";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, usuario);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String stored = rs.getString("password");
						if (stored == null) continue;
						// comparar en texto plano
						if (stored.equals(pass)) return true;
						// intentar MD5
						if (hashMD5(pass).equalsIgnoreCase(stored)) return true;
						// intentar SHA-256
						if (hashSHA256(pass).equalsIgnoreCase(stored)) return true;
					}
				}
			} catch (SQLException e) {
				// tabla no existe o error, seguir con siguiente
			}
		}
		return false;
	}

	private static String hashMD5(String s) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] d = md.digest(s.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : d) sb.append(String.format("%02x", b & 0xff));
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	private static String hashSHA256(String s) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
			byte[] d = md.digest(s.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : d) sb.append(String.format("%02x", b & 0xff));
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}
}
