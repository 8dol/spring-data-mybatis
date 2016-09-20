package com.edol.data.type;

import com.edol.data.exception.DBEnumConvertRuntimeException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by mind on 7/17/15.
 */
public class DBEnumTypeHandler extends BaseTypeHandler<DBEnum> {
    private Class<DBEnum> type;

    public DBEnumTypeHandler() {
    }

    public DBEnumTypeHandler(Class<DBEnum> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public DBEnum getNullableResult(ResultSet rs, String name) throws SQLException {
        return convert(rs.getInt(name));
    }

    @Override
    public DBEnum getNullableResult(ResultSet rs, int i) throws SQLException {
        return convert(rs.getInt(i));
    }

    @Override
    public DBEnum getNullableResult(CallableStatement cs, int i)
            throws SQLException {
        return convert(cs.getInt(i));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DBEnum enumObj,
                                    JdbcType type) throws SQLException {
        ps.setInt(i, enumObj.getIntValue());
    }

    private DBEnum convert(int status) {
        DBEnum[] objs = type.getEnumConstants();
        for (DBEnum em : objs) {
            if (em.getIntValue() == status) {
                return em;
            }
        }
        if (status == 0) {
            return null;
        }
        throw new DBEnumConvertRuntimeException(new StringBuffer(type.getSimpleName()).append(": The value ").append(status).append(" is not mapper ENUM").toString());
    }
}