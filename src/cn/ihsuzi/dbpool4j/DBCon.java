package cn.ihsuzi.dbpool4j;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCon
{
	private Connection con;
	private int sleeptime = 0;
	
	private static final String CLASSNAME = "DBCon";
	
	public DBCon(Connection con)
	{
		super();
		this.con = con;
	}

	
	public void setSleeptime(int sleeptime)
	{
		this.sleeptime = sleeptime;
	}
	
	public int getSleeptime()
	{
		return sleeptime;
	}
	
	public void setCon(Connection con)
	{
		this.con = con;
	}
	
	public Connection getCon()
	{
		return con;
	}

	public void colse()
	{
		try
		{
			if (con!=null && con.isClosed())
			{
				con.close();
				System.out.println(CLASSNAME+"Connection is closed");
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}





