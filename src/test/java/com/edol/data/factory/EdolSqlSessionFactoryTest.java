package com.edol.data.factory;

import com.edol.data.type.DBEnum;
import com.edol.data.type.DBEnumTypeHandler;
import com.edol.testdomain.TTEnum;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mind on 8/27/15.
 */
public class EdolSqlSessionFactoryTest {

    @Test
    public void testRegister() throws Exception {
        EdolSqlSessionFactory ssf = new EdolSqlSessionFactory();

        ssf.setScanPackages("com.edol.testdomain");
        ssf.addInterfaceTypeHandler(DBEnum.class, DBEnumTypeHandler.class);

        Configuration configuration = new Configuration();
        ssf.configOtherTypeHandler(configuration);

        assertTrue(configuration.getTypeHandlerRegistry().hasTypeHandler(TTEnum.class));
    }
}