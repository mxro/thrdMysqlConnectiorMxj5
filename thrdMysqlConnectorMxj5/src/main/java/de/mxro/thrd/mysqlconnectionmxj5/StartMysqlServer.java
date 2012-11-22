package de.mxro.thrd.mysqlconnectionmxj5;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import com.mysql.management.MysqldResource;
import com.mysql.management.MysqldResourceI;
import com.mysql.management.util.QueryUtil;

public class StartMysqlServer {

	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

	/**
	 * Starting an embedded mysql server.
	 * 
	 * Example from
	 * http://dev.mysql.com/doc/connector-mxj/en/connector-mxj-configuration
	 * -java-object.html
	 * 
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		final File ourAppDir = new File("C:\\data\\mysql");

		final File databaseDir = new File(ourAppDir, "mysql-mxj");

		final int port = Integer.parseInt(System.getProperty("c-mxj_test_port",
				"3336"));

		final String userName = "alice";
		final String password = "q93uti0opwhkd";
		final MysqldResource mysqldResource = startDatabase(databaseDir, port,
				userName, password);
		Class.forName(DRIVER);
		Connection conn = null;
		try {
			final String dbName = "our_test_app";
			final String url = "jdbc:mysql://localhost:" + port + "/" + dbName //
					+ "?" + "createDatabaseIfNotExist=true"//
			;
			conn = DriverManager.getConnection(url, userName, password);
			final String sql = "SELECT VERSION()";
			final String queryForString = new QueryUtil(conn)
					.queryForString(sql);
			System.out.println("------------------------");
			System.out.println(sql);
			System.out.println("------------------------");
			System.out.println(queryForString);
			System.out.println("------------------------");
			System.out.flush();
			Thread.sleep(100); // wait for System.out to finish flush
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
			try {
				mysqldResource.shutdown();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static MysqldResource startDatabase(final File databaseDir,
			final int port, final String userName, final String password) {
		final MysqldResource mysqldResource = new MysqldResource(databaseDir);
		final Map database_options = new HashMap();
		database_options.put(MysqldResourceI.PORT, Integer.toString(port));
		database_options.put(MysqldResourceI.INITIALIZE_USER, "true");
		database_options.put(MysqldResourceI.INITIALIZE_USER_NAME, userName);
		database_options.put(MysqldResourceI.INITIALIZE_PASSWORD, password);
		mysqldResource.start("test-mysqld-thread", database_options);
		if (!mysqldResource.isRunning()) {
			throw new RuntimeException("MySQL did not start.");
		}
		System.out.println("MySQL is running.");
		return mysqldResource;
	}

}
