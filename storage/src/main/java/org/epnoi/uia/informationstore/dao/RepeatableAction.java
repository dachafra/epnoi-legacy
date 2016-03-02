package org.epnoi.uia.informationstore.dao;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by cbadenes on 02/03/16.
 */
@FunctionalInterface
public interface RepeatableAction<T> {

    public abstract T run() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}