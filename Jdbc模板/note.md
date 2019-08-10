[TOC]

# JdbcTemplate
环境搭建：需要 DataSource 支持，导入 jar
* jdbc
* tx：有关事务
* mysql 驱动
* dbcp（两个）：连接池

## jdbcTemplate.execute() 源码分析
```java
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
    
        //指定参数非空
        Assert.notNull(action, "Callback object must not be null");
        
        //从数据源得到一条连接
        Connection con = DataSourceUtils.getConnection(this.getDataSource());
        Statement stmt = null;

        Object var7;
        try {
            Connection conToUse = con;
            if (this.nativeJdbcExtractor != null && this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativeStatements()) {
                conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
            }

            stmt = conToUse.createStatement();
            this.applyStatementSettings(stmt);
            Statement stmtToUse = stmt;
            if (this.nativeJdbcExtractor != null) {
                stmtToUse = this.nativeJdbcExtractor.getNativeStatement(stmt);
            }

            //接口方法回调
            T result = action.doInStatement(stmtToUse);
            this.handleWarnings(stmt);
            var7 = result;
        } catch (SQLException var11) {
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            
            //释放资源（放在 Spring 中则由事务管理器处理）
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            con = null;
            throw this.getExceptionTranslator().translate("StatementCallback", getSql(action), var11);
        } finally {
            JdbcUtils.closeStatement(stmt);
            
            //释放资源
            DataSourceUtils.releaseConnection(con, this.getDataSource());
        }

        return var7;
    }
```