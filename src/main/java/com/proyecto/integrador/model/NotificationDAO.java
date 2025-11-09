package com.proyecto.integrador.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
	private final Connection conn;

	public NotificationDAO(Connection conn) throws SQLException {
		this.conn = conn;
		ensureTable();
	}

	// Crea tabla si no existe
	public void ensureTable() throws SQLException {
		String ddl =
			"CREATE TABLE IF NOT EXISTS notifications (" +
			" id INT AUTO_INCREMENT PRIMARY KEY," +
			" sender VARCHAR(100) NOT NULL," +
			" message TEXT NOT NULL," +
			" created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
			" is_read TINYINT(1) NOT NULL DEFAULT 0" +
			")";
		try (Statement st = conn.createStatement()) {
			st.execute(ddl);
		}
	}

	// Inserta y devuelve la notificación creada
	public Notification insertNotification(String sender, String message) throws SQLException {
		String sql = "INSERT INTO notifications (sender, message, created_at, is_read) VALUES (?, ?, NOW(), 0)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, sender);
			ps.setString(2, message);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					// obtener created_at desde BD para consistencia
					return getById(id);
				}
			}
		}
		return null;
	}

	// Obtiene por id
	public Notification getById(int id) throws SQLException {
		String q = "SELECT id, sender, message, created_at, is_read FROM notifications WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(q)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs);
				}
			}
		}
		return null;
	}

	// Lista todas (ordenadas por fecha desc)
	public List<Notification> listAll() throws SQLException {
		List<Notification> list = new ArrayList<>();
		String q = "SELECT id, sender, message, created_at, is_read FROM notifications ORDER BY created_at DESC";
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
			while (rs.next()) list.add(mapRow(rs));
		}
		return list;
	}

	// Marca como leído (is_read = 1)
	public boolean markAsRead(int id) throws SQLException {
		String u = "UPDATE notifications SET is_read = 1 WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(u)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	// Elimina notificación
	public boolean delete(int id) throws SQLException {
		String d = "DELETE FROM notifications WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(d)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	private Notification mapRow(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String sender = rs.getString("sender");
		String message = rs.getString("message");
		Timestamp ts = rs.getTimestamp("created_at");
		boolean read = rs.getInt("is_read") == 1;
		LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
		return new Notification(id, sender, message, ldt, read);
	}
}
