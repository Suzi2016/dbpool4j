package cn.ihsuzi.dbpool4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool
{
	private static int maxSize = 10;
	private static int currentSize = 0;
	private static ArrayList<DBCon> pool = new ArrayList<>();
	private static int timeout = 10000;
	private static Thread listenerThread;
	
	private static String driver_name = "";
	private static String db_name = "";
	private static String db_password = "";
	private static String db_conUrl = "";

	private static final String CLASSNAME = "ConnectionPool:";
	
	private static ConnectionPool connectionPool = new ConnectionPool();
	
	private ConnectionPool() {}
	
	public static ConnectionPool getConnectionPool()
	{
		return ConnectionPool.connectionPool;
	}

	public String getDriver_name()
	{
		return driver_name;
	}

	public void setDriver_name(String driver_name)
	{
		ConnectionPool.driver_name = driver_name;
	}

	public String getDb_name()
	{
		return db_name;
	}

	public void setDb_name(String db_name)
	{
		ConnectionPool.db_name = db_name;
	}

	public String getDb_password()
	{
		return db_password;
	}

	public void setDb_password(String db_password)
	{
		ConnectionPool.db_password = db_password;
	}

	public String getDb_conUrl()
	{
		return db_conUrl;
	}

	public void setDb_conUrl(String db_conUrl)
	{
		ConnectionPool.db_conUrl = db_conUrl;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setMaxSize(int maxSize)
	{
		ConnectionPool.maxSize = maxSize;
	}

	public void setTimeout(int timeout)
	{
		ConnectionPool.timeout = timeout;
	}

	private synchronized static int getCurrentSize()
	{
		return currentSize;
	}

	private static synchronized void setCurrentSizeDel()
	{
		if (ConnectionPool.currentSize > 0)
		{
			ConnectionPool.currentSize--;
		}
	}

	private static synchronized void setCurrentSizeAdd()
	{
		if (ConnectionPool.currentSize < maxSize)
		{
			ConnectionPool.currentSize++;
		}
	}

	public Connection getConnection()
	{
		while (getCurrentSize() >= maxSize)
		{
			try
			{
				Thread.sleep(30);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
		setCurrentSizeAdd();

		// first get connection from pool
		synchronized (CLASSNAME)
		{
			if (pool != null && pool.size() > 0)
			{
				System.out.println("get Connection from pool");
				Connection con = pool.get(0).getCon();
				pool.remove(0);
				return con;
			}
		}

		Connection con;
		try
		{
			Class.forName(driver_name);
			con = DriverManager.getConnection(db_conUrl, db_name, db_password);

			return con;
		} catch (Exception e)
		{
			System.out.println(CLASSNAME + "DB connected is failed");
			e.printStackTrace();
			return null;
		}
	}

	public void close(Connection con)
	{
		try
		{
			if (con != null && !con.isClosed())
			{
				con.close();
				setCurrentSizeDel();
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void release(Connection con)
	{
		System.out.println(CLASSNAME + "put a connection to pool");
		pool.add(new DBCon(con));
		setCurrentSizeDel();
		if (listenerThread == null || !listenerThread.isAlive())
		{
			listener();
		}
	}

	private static void listener()
	{
		System.out.println(CLASSNAME + ":the listener is start");
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				while (pool.size() > 0)
				{
					synchronized (pool)
					{

						for (int i = 0; i < pool.size(); i++)
						{
							DBCon con = pool.get(i);
							if (con.getSleeptime() >= timeout)
							{
								System.out.println(CLASSNAME + "remove a connection from pool");
								con.colse();
								pool.remove(con);
								i--;
								setCurrentSizeDel();
							} else
							{
								con.setSleeptime(con.getSleeptime() + 1000);
							}
						}

						try
						{
							Thread.sleep(1000);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}

					}
				}
			}
		};

		listenerThread = new Thread(r);
		listenerThread.start();

	}

}
