package com.edol.data.factory;

import com.edol.testdomain.TTEnum;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by mind on 8/27/15.
 */
public class EdolSqlSessionFactoryTest {

    @Test
    public void testRegister() throws Exception {
        EdolSqlSessionFactory ssf = new EdolSqlSessionFactory();

        ssf.addScanInterfacePackages("com.edol.testdomain");

        Configuration configuration = new Configuration();
        ssf.configBeforeXmlBuilderParse(configuration);

        assertTrue(configuration.getTypeHandlerRegistry().hasTypeHandler(TTEnum.class));
    }
}