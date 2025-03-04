/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webquery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;


/**
 * Enumerated.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080212 nsano initial version <br>
 */
@Deprecated
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Enumerated {

    /** */
    EnumType value();

    /**
     * TODO アノテーションがメソッド指定の場合
     */
    class Util {

        /** */
        public static boolean isEnumetated(Field field) {
            return field.getAnnotation(Enumerated.class) != null;
        }

        /**
         *
         * @param field @{@link Parameter} annotated field.
         * @param fieldValue enum value
         * @throws NullPointerException when field is not annotated by {@link Enumerated}
         */
        public static <E extends Enum<E>> String getFieldValueAsString(Field field, E fieldValue) {
            Enumerated enumerated = field.getAnnotation(Enumerated.class);
            return switch (enumerated.value()) {
                case ORDINAL -> fieldValue == null ? "null" : String.valueOf(fieldValue.ordinal());
                default -> fieldValue == null ? "null" : fieldValue.name();
            };
        }
    }
}
