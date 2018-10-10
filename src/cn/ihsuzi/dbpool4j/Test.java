package cn.ihsuzi.dbpool4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Test
{

	public static void main(String[] args) throws SQLException
	{
		// get ConnectionPool
		ConnectionPool pool = ConnectionPool.getConnectionPool();
		// set Pool MaxSize
		pool.setMaxSize(10);
		// set Pool's connection alive time
		pool.setTimeout(10000);
		// set driverName
		pool.setDriver_name("driver_name");
		// set DB connection url
		pool.setDb_conUrl("db_conUrl");
		// set DB username
		pool.setDb_name("db_name");
		// set DB password
		pool.setDb_password("db_password");
		
		// get conncetion using pool
		Connection con = pool.getConnection();
		Statement statement = con.createStatement();
		
		/*
		 * do something use Statement
		 */
		
		statement.close();
		
		// put connection to pool
		pool.release(con);
		
		// close Connection,cant use this when using 'pool.release(con)'
		pool.close(con);
		
	}

}
