package com.kaikeba.server.api.utils;

import java.sql.Connection;

/**
 * Created by pc on 15-1-28.
 */
public interface OptDB {
	public Connection getCon(String driver, String url, String username,
			String password);

	public void closeCon(Connection connection);

}
