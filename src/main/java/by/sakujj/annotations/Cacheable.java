package by.sakujj.annotations;

import by.sakujj.cache.Cache;
import by.sakujj.dao.DAO;
import by.sakujj.proxy.DynamicProxyCreator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;

/**
 * <p> Used to annotate overridden methods of interfaces, which extend DAO&lt;?,?>. </p>
 * <p>Annotated DAO implementations may be passed to
 * {@link DynamicProxyCreator#newDAOCachingProxy(DAO, Cache)} to get an interface proxy
 * instance, which uses a cache.</p>
 *
 * <p>Examples of methods to annotate: </p>
 * <ul>
 * <li>{@code findById(Object id, ...)},</li>
 * <li>{@code deleteById(Object id, ...)},</li>
 * <li>{@code save(Entity<?,?> entityToSave, ...)},</li>
 * <li>{@code update(Entity<?,?> entityToUpdate, ...)}.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {

    /**
     * Specifies the type of DAO method.
     */
    DAOMethod daoMethod();

    enum DAOMethod {
        FIND_BY_ID, UPDATE, SAVE, DELETE_BY_ID
    }
}
