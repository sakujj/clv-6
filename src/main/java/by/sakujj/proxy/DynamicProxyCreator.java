package by.sakujj.proxy;

import by.sakujj.annotations.Cacheable;
import by.sakujj.cache.Cache;
import by.sakujj.dao.DAO;


import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Grouped static factory methods to create dynamic proxies.
 */
public class DynamicProxyCreator {
    /**
     * Used to create a caching proxy for the passed dao @param.
     * Creation process is dictated by
     * {@link Cacheable} annotations applied to dao @param.
     *
     * @param dao   {@link DAO} or it's subinterface instance to proxy
     * @param cache {@link by.sakujj.cache.Cache} instance to use in proxy
     * @return dynamically created interface-based proxy, that uses passed cache @param
     */
    public static <T extends DAO<?, ?>> Object newDAOCachingProxy(T dao, Cache cache) {
        Class<?> daoClass = dao.getClass();

        return Proxy.newProxyInstance(
                daoClass.getClassLoader(),
                Arrays.stream(daoClass.getInterfaces())
                        .filter(DAO.class::isAssignableFrom)
                        .toArray(Class[]::new),
                new DAOCachingInvocationHandler<>(dao, daoClass, cache)
        );
    }


}
