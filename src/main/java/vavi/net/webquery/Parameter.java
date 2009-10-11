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

import vavi.beans.BeanUtil;


/**
 * Parameter. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070224 nsano initial version <br>
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

    /**
     * �K�{���ǂ���
     * @wadl compliant
     */
    boolean required() default false;

    /**
     * ���ۂ� rest parameter ��
     * "" �̎��� bean �̃t�B�[���h�������̂܂�
     * @wadl compliant
     */
    String name() default "";

    /**
     * TODO �A�m�e�[�V���������\�b�h�w��̏ꍇ 
     */
    class Util {
        /**
         * 
         * @param field
         * @param bean
         * @param parameter
         */
        public static String getParameterName(Field field, Object bean, Parameter parameter) {
            String name = parameter.name();
            if (name.isEmpty()) {
                return field.getName();
            } else {
                return name;
            }
        }

        /**
         * 
         * @param field
         * @param bean
         * @param parameter
         * @return nullable
         */
        @SuppressWarnings("unchecked")
        public static String getParameterValue(Field field, Object bean, Parameter parameter) {
            Class<?> fieldClass = field.getType();
            Object fieldValue = BeanUtil.getFieldValue(field, bean);
            if (Formatted.Util.isFormatted(field)) {
                return Formatted.Util.getFieldValueAsString(field, fieldValue); 
            } else {
                if (fieldClass.isEnum() && Enumerated.Util.isEnumetated(field)) {
                    return Enumerated.Util.getFieldValueAsString(field, Enum.class.cast(fieldValue));
                } else {
                    return fieldValue == null ? null : fieldValue.toString();
                }
            }
        }
    }
}

/* */
