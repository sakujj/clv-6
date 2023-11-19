package by.sakujj.proxy;

import by.sakujj.annotations.Cacheable;
import by.sakujj.cache.Cache;
import by.sakujj.model.Entity;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

@RequiredArgsConstructor
public class DAOCachingInvocationHandler<T> implements InvocationHandler {
    private final T target;
    private final Class<?> targetClass;
    private final Cache cache;

    /**
     * Overridden invoke method that enables use of cache on the returned proxy
     * methods, that are annotated with {@link Cacheable}.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Method targetMethod = getOverriddenMethod(method);
            Optional<Cacheable> optionalAnnotation = getCacheableAnnotation(targetMethod);
            if (optionalAnnotation.isEmpty()) {
                return method.invoke(target, args);
            }

            Cacheable annotation = optionalAnnotation.get();
            return switch (annotation.daoMethod()) {
                case FIND_BY_ID -> proxyFindById(method, args);
                case UPDATE -> proxyUpdateById(method, args);
                case DELETE_BY_ID -> proxyDeleteById(method, args);
                case SAVE -> proxySave(method, args);
            };
        } catch (Throwable t) {
            throw t.getCause();
        }
    }


    private Object proxyFindById(Method method, Object[] args) throws Throwable {
        Object id = args[0];
        var optionalEntity = cache.getById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity;
        }

        Optional<? extends Entity<?, ?>> rez = (Optional<? extends Entity<?, ?>>) method.invoke(target, args);
        rez.ifPresent(cache::addOrUpdate);

        return rez;
    }

    private Object proxyDeleteById(Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);

        Object id = args[0];
        var optionalEntity = cache.getById(id);
        optionalEntity
                .ifPresent(entity -> cache.removeById(entity.getId()));

        return result;
    }

    private Object proxySave(Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);

        Object objToSave = args[0];
        cache.addOrUpdate((Entity<?, ?>) objToSave);

        return result;
    }

    private Object proxyUpdateById(Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);

        Object objToUpdate = args[0];
        cache.addOrUpdate((Entity<?, ?>) objToUpdate);

        return result;
    }


    private Optional<Cacheable> getCacheableAnnotation(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Cacheable.class));
    }

    private Method getOverriddenMethod(Method method) throws NoSuchMethodException {
        return targetClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
    }
}
