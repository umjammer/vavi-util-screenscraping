/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import vavi.net.www.protocol.URLStreamHandlerUtil;


/**
 * WebScraper.
 * <p>
 * input is specified by {@link #url()} or {@link #input()}.
 * if you specified {@link #url()}, {@link DefaultInputHandler} is used implicitly.
 * </p>
 * <p>
 * reputation is specified by {@link #isCollection()}. reputation is default.
 * if you set {@link #isCollection()} false, return values index will be available 0 only.
 * </p>
 * <p>
 * if {@link #input()} is default value, strings {args_index} in {@link WebScraper#url()} will be replaced by args by it's order.
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/30 nsano initial version <br>
 */
@java.lang.annotation.Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebScraper {

    /**
     * for {@link DefaultInputHandler}
     * <p>
     * strings "{args_index}" will be replaced by `args` by its order.
     * </p>
     * @see DefaultInputHandler#dealUrlAndArgs(String, String...)
     */
    String url() default "";

    /**
     * handler for web input.
     *
     * CAUTION!!!
     * <p>
     * default handler's return value (Reader)
     * depends on "file.encoding" system. property
     * </p>
     */
    Class<? extends InputHandler<?>> input() default DefaultInputHandler.class;

    /** parser for input */
    Class<? extends Parser> parser() default XPathParser.class;

    /** for 2 step XPath */
    String value() default "";

    /** repeatable data or not */
    boolean isCollection() default true;

    /** input encoding. default is <code>System.getProperty("file.encoding")</code> */
    String encoding() default "";

    /** debug mode or not */
    boolean isDebug() default false;

    /** */
    class Util {

        private Util() {
        }

        /** */
        public static InputHandler<?> getInputHandler(Class<?> type) {
            try {
                WebScraper webScraper = type.getAnnotation(WebScraper.class);
                return webScraper.input().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        /** */
        @SuppressWarnings("unchecked")
        public static <T> Parser<?, T> getParser(Class<T> type) {
            try {
                WebScraper webScraper = type.getAnnotation(WebScraper.class);
                return webScraper.parser().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        /** */
        public static boolean isCollection(Class<?> type) {
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }
            return webScraper.isCollection();
        }

        /** for 2 step XPath */
        public static String getValue(Class<?> type) {
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }
            return webScraper.value();
        }

        /** */
        public static String getEncoding(Class<?> type) {
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }
            String encoding = webScraper.encoding();
            if (encoding.isEmpty()) {
                return System.getProperty("file.encoding");
            } else {
                return encoding;
            }
        }

        /** */
        public static String getUrl(Class<?> type) {
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }
            return webScraper.url();
        }

        /** */
        public static boolean isDebug(Class<?> type) {
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }
            return webScraper.isDebug();
        }

        /**
         * @return {@link Target} annotated fields
         */
        public static Set<Field> getTargetFields(Class<?> type) {
            //
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }

            //
            Set<Field> targetFields = new HashSet<>();

            for (Field field : type.getDeclaredFields()) {
                Target target = field.getAnnotation(Target.class);
                if (target != null) {
                    targetFields.add(field);
                }
            }

            return targetFields;
        }

        /* for "classpath" schema */
        static {
            URLStreamHandlerUtil.loadService();
        }

        /**
         * Scrapes data.
         * entry point for user.
         * <p>
         * if {@link WebScraper#input()} is default, strings `{args_index}` in {@link WebScraper#url()} will be replaced by `args` by its order.
         * </p>
         *
         * @param type type annotated by {@link WebScraper}
         * @param args parameters for input handler
         * @return List of type objects.
         * @throws IllegalArgumentException when {@link WebScraper#input()} is default and url is null.
         * @see DefaultInputHandler#dealUrlAndArgs(String, String...)
         */
        public static <I, T> List<T> scrape(Class<T> type, String ... args) throws IOException {
            //
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }

            @SuppressWarnings("unchecked")
            Parser<I, T> parser = (Parser<I, T>) getParser(type);
            @SuppressWarnings("unchecked")
            InputHandler<I> inputHandler = (InputHandler<I>) getInputHandler(type);

            // if inputHandler is default, and if url is specified,
            // `url` will be set for a InputHandler#getInput()'s argument automatically.
            String url = WebScraper.Util.getUrl(type);
            args = inputHandler.dealUrlAndArgs(url, args);

            return parser.parse(type, inputHandler, args);
        }

        /**
         * Scrapes data.
         * entry point for user.
         * <p>
         * if {@link WebScraper#input()} is default, strings `{args_index}` in {@link WebScraper#url()} will be replaced by `args` by its order.
         * </p>
         *
         * @param type type annotated by {@link WebScraper}
         * @param args parameters for input handler
         * @throws IllegalArgumentException when {@link WebScraper#input()} is default and url is null.
         * @see DefaultInputHandler#dealUrlAndArgs(String, String...)
         */
        public static <I, T> void foreach(Class<T> type, Consumer<T> eachHandler, String ... args) throws IOException {
            //
            WebScraper webScraper = type.getAnnotation(WebScraper.class);
            if (webScraper == null) {
                throw new IllegalArgumentException("type is not annotated with @WebScraper");
            }

            @SuppressWarnings("unchecked")
            Parser<I, T> parser = (Parser<I, T>) getParser(type);
            @SuppressWarnings("unchecked")
            InputHandler<I> inputHandler = (InputHandler<I>) getInputHandler(type);

            // if inputHandler is default, and if url is specified,
            // `url` will be set for a InputHandler#getInput()'s argument automatically.
            String url = WebScraper.Util.getUrl(type);
            args = inputHandler.dealUrlAndArgs(url, args);

            parser.foreach(type, eachHandler, inputHandler, args);
        }
    }
}
