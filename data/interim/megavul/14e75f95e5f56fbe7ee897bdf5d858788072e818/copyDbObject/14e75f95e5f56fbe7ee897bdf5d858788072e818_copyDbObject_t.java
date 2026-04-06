class copyDbObject {
public static void copyDbObject(
		String dbObject, 
		boolean useSuffix, 
		Connection connSource, 
		Connection connTarget, 
		List<String> valuePatterns,
		List<String> valueReplacements,
		PrintStream out
	) throws SQLException {
		String currentStatement = null;
		Database_2 db = new Database_2();
		try {
			Database_2[] plugins = Utils.getDatabasePlugIns();
			db = plugins[0];
		} catch (Exception e) {
			out.println("Can not activate database plugin: " + e.getMessage());
		}
		try {
			// Delete all rows from target
			PreparedStatement s = connTarget.prepareStatement(currentStatement = "DELETE FROM " + dbObject + (useSuffix ? "_" : ""));
			s.executeUpdate();
			s.close();
			// Read all rows from source
			s = connSource.prepareStatement(currentStatement = "SELECT * FROM " + dbObject + (useSuffix ? "_" : ""));
			s.setFetchSize(100);
			ResultSet rs = s.executeQuery();
			if(rs != null) {
				ResultSetMetaData rsm = rs.getMetaData();
				FastResultSet frs = new FastResultSet(rs);
				int nRows = 0;
				while (frs.next()) {
					// Read row from source and prepare INSERT statement
					String statement = "INSERT INTO " + dbObject + (useSuffix ? "_" : "") + " ";
					List<Object> statementParameters = new ArrayList<Object>();
					List<String> processTargetColumnNames = new ArrayList<String>();
					for (int j = 0; j < rsm.getColumnCount(); j++) {
						String columnName = rsm.getColumnName(j + 1);
						if(frs.getObject(columnName) != null) {
							String mappedColumnName = CopyDb.mapColumnName(connTarget, dbObject, columnName);
							if(mappedColumnName != null) {
								statement += (statementParameters.size() == 0 ? " (" : ", ") + mappedColumnName;
								processTargetColumnNames.add(mappedColumnName);
								if(frs.getObject(columnName) instanceof java.sql.Clob) {
									try {
										statementParameters.add(CopyDb.getStringFromClob((java.sql.Clob) frs.getObject(columnName)));
									} catch (Exception e) {
										out.println("Reading Clob failed. Reason: " + e.getMessage());
										out.println("statement=" + statement);
										out.println("parameters=" + statementParameters);
									}
								} else if(frs.getObject(columnName) instanceof java.sql.Blob) {
									try {
										statementParameters.add(CopyDb.getBytesFromBlob((java.sql.Blob) frs.getObject(columnName)));
									} catch (Exception e) {
										out.println("Reading Blob failed. Reason: " + e.getMessage());
										out.println("statement=" + statement);
										out.println("parameters=" + statementParameters);
									}
								} else {
									statementParameters.add(
										CopyDb.mapColumnValue(
											connSource,
											dbObject,
											columnName,
											frs.getObject(columnName),
											valuePatterns,
											valueReplacements
										)
									);
								}
							}
						}
					}
					statement += ") VALUES (";
					for (int j = 0; j < statementParameters.size(); j++) {
						statement += j == 0 ? "?" : ", ?";
					}
					statement += ")";
					// Add row to target
					try {
						PreparedStatement t = connTarget.prepareStatement(currentStatement = statement);
						for (int j = 0; j < statementParameters.size(); j++) {
							Object parameter = statementParameters.get(j);
							if("oracle.sql.TIMESTAMP".equals(parameter.getClass().getName())) {
								Method timestampValueMethod = parameter.getClass().getMethod("timestampValue", new Class[] {});
								parameter = timestampValueMethod.invoke(parameter, new Object[] {});
							} else if("microsoft.sql.DateTimeOffset".equals(parameter.getClass().getName())) {
								Method timestampValueMethod = parameter.getClass().getMethod("getTimestamp", new Class[] {});
								parameter = timestampValueMethod.invoke(parameter, new Object[] {});
							}
							if(parameter instanceof java.sql.Timestamp) {
								t.setTimestamp(j + 1, (java.sql.Timestamp) parameter);
							} else if(parameter instanceof java.sql.Date) {
								t.setDate(j + 1, (java.sql.Date) parameter);
							} else if(parameter instanceof Double) {
								t.setBigDecimal(j + 1, new BigDecimal((Double)parameter));
							} else if(parameter instanceof Float) {
								t.setBigDecimal(j + 1, new BigDecimal((Float)parameter));
							} else {
								db.setPreparedStatementValue(connTarget, t, j + 1, parameter);
							}
						}
						t.executeUpdate();
						t.close();
					} catch (Exception e) {
						new ServiceException(e).log();
						out.println("Insert failed. Reason: " + e.getMessage());
						out.println("statement=" + statement);
						out.println("parameters=" + statementParameters);
					}
					nRows++;
					if(nRows % 1000 == 0) {
						out.println(nRows + " rows copied");
					}
				}
				rs.close();
			} else {
				out.println("Did not copy table (result set is null). Statement: " + currentStatement);
			}
			s.close();
		} catch (Exception e) {
			new ServiceException(e).log();
			out.println("Can not copy table (see log for more info). Statement: " + currentStatement);
		}
	}
}
