/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import vavi.beans.Binder;
import vavi.util.properties.annotation.Property;


/**
 * Target.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/30 nsano initial version <br>
 */
@java.lang.annotation.Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Target {

    /** depends on {@link Parser} */
    String value() default "";

    /** depends on {@link Parser} */
    boolean optional() default false;

    /**
     * TODO currently this method effects on {@link JsonPathParser} only.
     */
    Class<? extends Binder> binder() default GsonBinder.class;

    /**
     * TODO currently this method effects on {@link JsonPathParser} only.
     */
    Class<?> option() default Object.class;

    /**
     * TODO アノテーションがメソッド指定の場合
     */
    class Util {

        private Util() {
        }

        /**
         * @param field {@link Target} annotated
         */
        public static String getValue(Field field) {
            Target target = field.getAnnotation(Target.class);
            if (target == null) {
                throw new IllegalArgumentException("bean is not annotated with @Target");
            }
            return target.value();
        }

        /**
         * @param field {@link Target} annotated
         */
        public static boolean isOptional(Field field) {
            Target target = field.getAnnotation(Target.class);
            if (target == null) {
                throw new IllegalArgumentException("bean is not annotated with @Target");
            }
            return target.optional();
        }

        /**
         * @param field @{@link Property} annotated field.
         */
        public static <T> Binder getBinder(Field field) {
            Target target = field.getAnnotation(Target.class);
            if (target == null) {
                throw new IllegalArgumentException("bean is not annotated with @Target");
            }

            try {
                Binder binder = target.binder().getDeclaredConstructor().newInstance();
                return binder;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * @param field @{@link Property} annotated field.
         */
        public static Class<?> getOption(Field field) {
            Target target = field.getAnnotation(Target.class);
            if (target == null) {
                throw new IllegalArgumentException("bean is not annotated with @Target");
            }

            try {
                Class<?> typeToken= target.option();
                if (Object.class.equals(typeToken)) {
                    return null;
                } else {
                    return typeToken;
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

/* */
