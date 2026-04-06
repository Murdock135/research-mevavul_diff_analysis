class tryDoUpgrade {
private void tryDoUpgrade(String basePath, String jdbcUrl, String user, String password, String driverClass) {
        String currentVersion = ZrLogUtil.getCurrentSqlVersion(jdbcUrl, user, password, driverClass);
        List<Map.Entry<Integer, List<String>>> sqlList = ZrLogUtil.getExecSqlList(currentVersion, basePath);
        if (!sqlList.isEmpty()) {
            try {
                for (Map.Entry<Integer, List<String>> entry : sqlList) {
                    Connection connection = ZrLogUtil.getConnection(jdbcUrl, user, password, driverClass);
                    if (connection != null) {
                        //执行需要更新的sql脚本
                        Statement statement = connection.createStatement();
                        try {
                            for (String sql : entry.getValue()) {
                                statement.execute(sql);
                            }
                        } catch (Exception e) {
                            LOGGER.error("execution sql ", e);
                            //有异常终止升级
                            return;
                        } finally {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (SQLException e) {
                                    LOGGER.error(e);
                                }
                            }
                        }
                        //执行需要转换的数据
                        try {
                            UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com.zrlog.web.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                            try {
                                upgradeVersionHandler.doUpgrade(connection);
                            } catch (Exception e) {
                                LOGGER.error("", e);
                                return;
                            }
                        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                            LOGGER.warn("Try exec upgrade method error, " + e.getMessage());
                        } finally {
                            connection.close();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
            haveSqlUpdated = true;
        }
    }
}
