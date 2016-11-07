package br.com.mv.lerarquivoxml.repositorio.pool;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Classe responsavel por simular um pool de conexao com o banco.
 * 
 * @author diego.almeida
 * @since 19/10/2016
 *
 */
public class DBConnectionPool {

	static {
		freeDbConnections = new ArrayList<Connection>();
		try {
			DBConnectionPool.loadDbProperties();
			DBConnectionPool.loadDbDriver();
		} catch (ClassNotFoundException e) {
			System.out.println("DB DRIVER NAO ENCONTRADO!");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("ERRO DE CONEXAO COM O BANCO!");
			System.exit(2);
		}
	}

	/**
	 * The db properties (driver, url, login, and password)
	 */
	private static Properties dbProperties;

	/**
	 * The free connection queue
	 */
	private static List<Connection> freeDbConnections;

	/**
	 * Returns a free db connection accessing to the free db connection queue.
	 * If the queue is empty a new db connection will be created.
	 *
	 * @return A db connection
	 * @throws SQLException
	 */
	public static synchronized Connection getConnection() throws SQLException {
		
		Connection connection;

		if (!freeDbConnections.isEmpty()) {
			// Extract a connection from the free db connection queue
			connection = freeDbConnections.get(0);
			DBConnectionPool.freeDbConnections.remove(0);

			try {
				// If the connection is not valid, a new connection will be analyzed
				if (connection.isClosed()) {
					connection = DBConnectionPool.getConnection();
				}
			} catch (SQLException e) {
				connection = DBConnectionPool.getConnection();
			}
		} else {
			connection = DBConnectionPool.createDBConnection();
		}

		return connection;
	}

	/**
	 * Releases the connection represented by <code>pReleasedConnection</code>
	 * parameter
	 *
	 * @param pReleasedConnection The db connection to release
	 */
	public static synchronized void releaseConnection(Connection pReleasedConnection) {

		// Add the connection to the free db connection queue
		DBConnectionPool.freeDbConnections.add(pReleasedConnection);
	}

	/**
	 * Creates a new db connection
	 *
	 * @return A db connection
	 * @throws SQLException
	 */
	private static Connection createDBConnection() throws SQLException {
		
		Connection newConnection = null;
		newConnection = DriverManager.getConnection(DBConnectionPool.dbProperties.getProperty("database.url"), DBConnectionPool.dbProperties.getProperty("database.username"), DBConnectionPool.dbProperties.getProperty("database.password"));
		newConnection.setAutoCommit(true);
		
		return newConnection;
	}

	private static void loadDbDriver() throws ClassNotFoundException {
		
		Class.forName(DBConnectionPool.dbProperties.getProperty("database.driver"));
	}

	/**
	 * Loads the db properties
	 *
	 * @throws IOException
	 */
	private static void loadDbProperties() throws IOException {
		
		URL root = DBConnectionPool.class.getProtectionDomain().getCodeSource().getLocation();
		// System.out.println("ROOT = " + root);
		URL fileProperties = new URL(root, "config.properties");
		
		DBConnectionPool.dbProperties = new Properties();
		DBConnectionPool.dbProperties.load(fileProperties.openStream());
	}

}
