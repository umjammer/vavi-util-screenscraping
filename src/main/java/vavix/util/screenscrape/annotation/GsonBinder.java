/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import vavi.beans.BeanUtil;
import vavi.beans.Binder;


/**
 * GsonBinder.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/19 umjammer initial version <br>
 */
public class GsonBinder<T> implements Binder {

    /** */
    private Gson gson = new GsonBuilder().create();

    /**
     * @param elseValue when the fieldClass is List.class, {@link TypeToken} is
     *            set. (TODO ad-hoc.)
     */
    @Override
    public void bind(Object destBean, Field field, Class<?> fieldClass, String value, Object elseValue) {
        if (fieldClass.equals(String.class)) {
            BeanUtil.setFieldValue(field, destBean, value);
        } else if (fieldClass.equals(List.class)) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Type listType = elseValue instanceof Class ?
                getTypeToken((Class) elseValue).getType() : new TypeToken<ArrayList<T>>() {}.getType();
//System.err.println("listType: " + listType.getTypeName());
            BeanUtil.setFieldValue(field, destBean, gson.fromJson(value, listType));
        } else {
            BeanUtil.setFieldValue(field, destBean, gson.fromJson(value, fieldClass));
        }
    }

    /** */
    private TypeToken<?> getTypeToken(Class<TypeToken<?>> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
