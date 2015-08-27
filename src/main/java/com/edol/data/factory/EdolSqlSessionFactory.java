package com.edol.data.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;

import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by mind on 8/27/15.
 */
@Slf4j
public class EdolSqlSessionFactory extends SqlSessionFactoryBean {

    private Map<Class, Class> interfaceTypeHandlerMap = new HashMap<>();

    private List<String> scanPackages = Collections.emptyList();

    @Override
    protected void configOtherTypeHandler(Configuration config) {
        TypeHandlerRegistry typeHandlerRegistry = config.getTypeHandlerRegistry();

        scanPackages.forEach(pack -> {
            interfaceTypeHandlerMap.forEach((k, v) -> {
                ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
                resolverUtil.find(new ResolverUtil.IsA(k), pack);

                log.debug("Find {}, for {}, in {}.", k, v, pack);

                resolverUtil.getClasses().forEach(c -> {
                    log.debug("Register {}.", c);

                    if (!c.isAnonymousClass() && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                        typeHandlerRegistry.register(c, v);
                    }
                });
            });
        });
    }

    public void setScanPackages(String packages) {
        if (packages != null && packages.trim().length() > 0) {
            scanPackages = Arrays.asList(packages.split(";")).stream().map(x -> x.trim()).filter(y -> y.length() > 0).collect(toList());
        }
    }

    public void addInterfaceTypeHandler(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        interfaceTypeHandlerMap.put(javaTypeClass, typeHandlerClass);
    }
}
