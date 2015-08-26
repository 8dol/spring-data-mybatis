package com.edol.data.interceptor;


import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by mind on 8/21/15.
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args =
        {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PageableAndSortInterceptor implements Interceptor {

    static int MAPPED_STATEMENT_INDEX = 0;
    static int PARAMETER_INDEX = 1;
    static int ROW_BOUNDS_INDEX = 2;
//    static int RESULT_HANDLER_INDEX = 3;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] queryArgs = invocation.getArgs();
        final Object parameter = queryArgs[PARAMETER_INDEX];

        Pageable pageable = findPageable(parameter);
        Sort sort = findSort(parameter);

        if (pageable == null && sort == null) {
            return invocation.proceed();
        }

        final MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
        final BoundSql boundSql = ms.getBoundSql(queryArgs[PARAMETER_INDEX]);

        String sql = "";
        if (pageable != null) {
            sql = getSortString(boundSql, pageable.getSort());
            sql = getLimitString(sql, pageable.getOffset(), pageable.getPageSize());
        } else {
            sql = getSortString(boundSql, sort);
        }

        if ("".equals(sql)) {
            return invocation.proceed();
        }

        queryArgs[ROW_BOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        queryArgs[MAPPED_STATEMENT_INDEX] = copyFromNewSql(ms, boundSql, sql);

        Object ret = invocation.proceed();

//        if (Page.class.isAssignableFrom(queryArgs[RESULT_HANDLER_INDEX].getClass())) {
//            Page<?> pi = new PageImpl<>((List) ret, pageable, 0);
//            return pi;
//        } else {
        return ret;
//        }
    }

    private String getSortString(BoundSql boundSql, Sort sort) {
        String sql = boundSql.getSql().trim().replaceAll(";$", "");

        if (sort == null) {
            return sql;
        }

        String sortSql = StreamSupport.stream(sort.spliterator(), false).map(
                x -> x.getProperty() + " " + x.getDirection()).collect(Collectors.joining(","));

        return String.format("%s order by %s", sql, sortSql);
    }

    private String getLimitString(String sql, int offset, int limit) {
        if (offset > 0) {
            return String.format("%s limit %d, %d", sql, offset, limit);
        } else {
            return String.format("%s limit %d", sql, limit);
        }
    }

    private Pageable findPageable(Object params) {
        if (params == null) {
            return null;
        }

        if (Pageable.class.isAssignableFrom(params.getClass())) {
            return (Pageable) params;
        } else if (params instanceof ParamMap) {
            ParamMap<Object> paramMap = (ParamMap<Object>) params;
            Optional<Object> first = paramMap.values().stream().filter(v -> v != null && Pageable.class.isAssignableFrom(v.getClass())).findFirst();
            if (first.isPresent()) {
                return (Pageable) first.get();
            }
        }

        return null;
    }

    private Sort findSort(Object params) {
        if (params == null) {
            return null;
        }

        if (Sort.class.isAssignableFrom(params.getClass())) {
            return (Sort) params;
        } else if (params instanceof ParamMap) {
            ParamMap<Object> paramMap = (ParamMap<Object>) params;
            Optional<Object> first = paramMap.values().stream().filter(v -> v != null && Sort.class.isAssignableFrom(v.getClass())).findFirst();
            if (first.isPresent()) {
                return (Sort) first.get();
            }
        }

        return null;
    }

    private MappedStatement copyFromNewSql(MappedStatement ms,
                                           BoundSql boundSql, String sql) {
        BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql);
        return copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
    }

    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql,
                                      String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    //see: MapperBuilderAssistant
    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuffer keyProperties = new StringBuffer();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object o) {
        if (Executor.class.isAssignableFrom(o.getClass())) {
            return Plugin.wrap(o, this);
        }

        return o;
    }

    @Override
    public void setProperties(Properties properties) {
    }
}

class BoundSqlSqlSource implements SqlSource {
    BoundSql boundSql;

    public BoundSqlSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}
