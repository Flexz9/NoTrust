package flexzcraft.notrust.repository;

import com.mojang.logging.LogUtils;
import flexzcraft.notrust.entity.Log;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogRepository {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String DATABASE_URL = "jdbc:sqlite:notrust.db";
	Connection connection = null;

	public LogRepository() {
		connect();
	}

	public void connect() {
		if (connection != null) return;

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(DATABASE_URL);
		} catch (SQLException e) {
			e.printStackTrace();
			connection = null;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		createTableIfNotExists();
	}

	private void createTableIfNotExists() {
		String createTableSQL = "CREATE TABLE IF NOT EXISTS logs ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "playername TEXT,"
				+ "x REAL,"
				+ "y REAL,"
				+ "z REAL,"
				+ "dimension TEXT,"
				+ "timestamp TIMESTAMP,"
				+ "action TEXT,"
				+ "text TEXT"
				+ ")";

		try (Statement statement = connection.createStatement()) {
			statement.execute(createTableSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void save(Log log) {
		connect();
		if (connection != null) {
			String sql = "INSERT INTO logs (playername, x, y, z, dimension, timestamp, action, text) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, log.getPlayername());
				pstmt.setDouble(2, log.getX());
				pstmt.setDouble(3, log.getY());
				pstmt.setDouble(4, log.getZ());
				pstmt.setString(5, log.getDimension());
				pstmt.setTimestamp(6, log.getTimestamp());
				pstmt.setString(7, log.getAction());
				pstmt.setString(8, log.getText());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.error(log.toString());
		}
	}

	public void save(List<Log> listOfLogs) throws SQLException {
		if (listOfLogs.isEmpty())
			return;

		connect();
		String sql = "INSERT INTO logs (playername, x, y, z, dimension, timestamp, action, text) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = connection.prepareStatement(sql);

		for (Log log: listOfLogs) {
			pstmt.setString(1, log.getPlayername());
			pstmt.setDouble(2, log.getX());
			pstmt.setDouble(3, log.getY());
			pstmt.setDouble(4, log.getZ());
			pstmt.setString(5, log.getDimension());
			pstmt.setTimestamp(6, log.getTimestamp());
			pstmt.setString(7, log.getAction());
			pstmt.setString(8, log.getText());
			pstmt.addBatch();
		}

		pstmt.executeBatch();
	}

	public List<Log> selectLogsByPosition(double x, double y, double z, String dimension, int limit) {
		List<Log> logs = new ArrayList<>();
		String sql = "SELECT * FROM logs WHERE x = ? AND y = ? AND z = ? AND dimension = ? ORDER BY timestamp DESC LIMIT ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, x);
			pstmt.setDouble(2, y);
			pstmt.setDouble(3, z);
			pstmt.setString(4, dimension);
			pstmt.setInt(5, limit);

			ResultSet rs = pstmt.executeQuery();
			queryToLogList(rs, logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return logs;
	}

	public List<Log> selectLogsByPosition(int x1, int y1, int z1, int x2, int y2, int z2, String dimension, int limit) {
		List<Log> logs = new ArrayList<>();
		String sql = "SELECT * FROM logs WHERE x >= ? AND y >= ? AND z >= ? AND x <= ? AND y <= ? AND z <= ? AND dimension = ? ORDER BY timestamp DESC LIMIT ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, x1);
			pstmt.setDouble(2, y1);
			pstmt.setDouble(3, z1);
			pstmt.setDouble(4, x2);
			pstmt.setDouble(5, y2);
			pstmt.setDouble(6, z2);
			pstmt.setString(7, dimension);
			pstmt.setInt(8, limit);

			ResultSet rs = pstmt.executeQuery();
			queryToLogList(rs, logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return logs;
	}

	public List<Log> selectLogsByPositionAndPlayer(double x, double y, double z, String dimension, String playername, int limit) {
		List<Log> logs = new ArrayList<>();
		String sql = "SELECT * FROM logs WHERE x = ? AND y = ? AND z = ? AND dimension = ? and playername = ? ORDER BY timestamp DESC LIMIT ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, x);
			pstmt.setDouble(2, y);
			pstmt.setDouble(3, z);
			pstmt.setString(4, dimension);
			pstmt.setString(5, playername);
			pstmt.setInt(6, limit);

			ResultSet rs = pstmt.executeQuery();
			queryToLogList(rs, logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return logs;
	}

	public List<Log> selectLogsByPositionAndPlayer(int x1, int y1, int z1, int x2, int y2, int z2, String dimension, String playername, int limit) {
		List<Log> logs = new ArrayList<>();
		String sql = "SELECT * FROM logs WHERE x >= ? AND y >= ? AND z >= ? AND x <= ? AND y <= ? AND z <= ? AND dimension = ? and playername = ? ORDER BY timestamp DESC LIMIT ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, x1);
			pstmt.setDouble(2, y1);
			pstmt.setDouble(3, z1);
			pstmt.setDouble(4, x2);
			pstmt.setDouble(5, y2);
			pstmt.setDouble(6, z2);
			pstmt.setString(7, dimension);
			pstmt.setString(8, playername);
			pstmt.setInt(9, limit);

			ResultSet rs = pstmt.executeQuery();
			queryToLogList(rs, logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return logs;
	}

	public List<Log> selectLogsByPositionAndAction(double x, double y, double z, String dimension, String action, int limit) {
		List<Log> logs = new ArrayList<>();
		String sql = "SELECT * FROM logs WHERE x = ? AND y = ? AND z = ? AND dimension = ? and action = ? ORDER BY timestamp DESC LIMIT ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, x);
			pstmt.setDouble(2, y);
			pstmt.setDouble(3, z);
			pstmt.setString(4, dimension);
			pstmt.setString(5, action);
			pstmt.setInt(6, limit);

			ResultSet rs = pstmt.executeQuery();
			queryToLogList(rs, logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return logs;
	}

	public List<Log> selectLogsByPositionAndPlayernameAndAction(double x, double y, double z, String dimension, String playername, String action, int limit) {
		List<Log> logs = new ArrayList<>();
		String sql = "SELECT * FROM logs WHERE x = ? AND y = ? AND z = ? AND dimension = ? and playername = ? and action = ? ORDER BY timestamp DESC LIMIT ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, x);
			pstmt.setDouble(2, y);
			pstmt.setDouble(3, z);
			pstmt.setString(4, dimension);
			pstmt.setString(5, playername);
			pstmt.setString(6, action);
			pstmt.setInt(7, limit);

			ResultSet rs = pstmt.executeQuery();
			queryToLogList(rs, logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return logs;
	}

	private void queryToLogList(ResultSet rs, List<Log> logs) throws SQLException {
		while (rs.next()) {
			Log log = new Log(
					rs.getString("playername"),
					rs.getDouble("x"),
					rs.getDouble("y"),
					rs.getDouble("z"),
					rs.getString("dimension"),
					rs.getTimestamp("timestamp"),
					rs.getString("action"),
					rs.getString("text")
			);
			logs.add(log);
		}
	}

	public void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
		}
	}
}
