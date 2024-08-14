/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import vavi.beans.BeanUtil;
import vavi.beans.Binder;
import vavi.util.Debug;


/**
 * BaseParser.
 *
 * @param <I> input type
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2020/03/20 nsano initial version <br>
 */
public abstract class BaseParser<I, T, N> implements Parser<I, T> {

    /** If you want to modify the selector by the field condition, implement a method in a sub class. */
    protected String fixSelector(Field field, String selector) {
        return selector;
    }

    /** Creates a document from the input. */
    protected abstract N getDocument(I input);

    /** Selects all nodes specified by the selector. */
    protected abstract Iterable<N> selectAll(String selector, N document);

    /** Gets text of the node. */
    protected abstract String asText(Object node);

    /** Selects a node specified by the selector. */
    protected abstract N select(String selector, N document);

    /** Creates document from the node. */
    protected abstract N getSubDocument(N node);

    /** {@link WebScraper#encoding()} */
    protected String encoding;

    /** {@link WebScraper#isCollection()} */
    protected boolean isCollection;

    /** {@link WebScraper#isDebug()} */
    protected boolean isDebug;

    /** */
    protected boolean isTwoPass;

    /**
     * If you specify {@link WebScraper#value()} do 1 step scraping else do 2 step scraping.
     * <li> 1 step scraping:
     * {@link Target#value()} で指定した selector で取得する方法。
     * <li> 2 step scraping:
     * {@link WebScraper#value()} で指定した selector で取得できる部分 selected から
     * {@link Target#value()} で指定した subSelector で取得する方法。
     */
    @Override
    public List<T> parse(Class<T> type, InputHandler<I> inputHandler, String ... args) {
        isDebug = WebScraper.Util.isDebug(type);
        encoding = WebScraper.Util.getEncoding(type);
if (WebScraper.Util.isDebug(type)) {
 Debug.println(Level.FINER, "encoding: " + encoding);
}
        isCollection = WebScraper.Util.isCollection(type);
        try {
            List<T> results = new ArrayList<>();
            if (WebScraper.Util.getValue(type).isEmpty()) {
                processOneStep(type, i -> {
                    if (i < results.size()) {
                        return results.get(i);
                    } else {
                        try {
                            T bean = type.newInstance();
                            results.add(bean);
                            return bean;
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }, null, inputHandler, args);
            } else {
                processTwoStep(type, results::add, inputHandler, args);
            }
            return results;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * If you specify {@link WebScraper#value()} do 1 step scraping else do 2 step scraping.
     * <li> 1 step scraping:
     * {@link Target#value()} で指定した selector で取得する方法。
     * <li> 2 step scraping:
     * {@link WebScraper#value()} で指定した selector で取得できる部分 selected から
     * {@link Target#value()} で指定した subSelector で取得する方法。
     * <p>
     * you need to specify at the first xpath element of the {@link Target#value()}
     * as same as the last xpath element in {@link WebScraper#value()}.
     * </p>
     */
    @Override
    public void foreach(Class<T> type, Consumer<T> eachHandler, InputHandler<I> inputHandler, String ... args) {
        isDebug = WebScraper.Util.isDebug(type);
        encoding = WebScraper.Util.getEncoding(type);
if (WebScraper.Util.isDebug(type)) {
 Debug.println(Level.FINER, "encoding: " + encoding);
}
        isCollection = WebScraper.Util.isCollection(type);
        try {
            if (WebScraper.Util.getValue(type).isEmpty()) {
                List<T> results = new ArrayList<>();
                processOneStep(type, i -> {
                    if (i < results.size()) {
                        return results.get(i);
                    } else {
                        try {
                            T bean = type.newInstance();
                            results.add(bean);
                            return bean;
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }, eachHandler, inputHandler, args);
            } else {
                processTwoStep(type, eachHandler, inputHandler, args);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 1 step scraping.
     * TODO not match for foreach
     */
    private void processOneStep(Class<T> type, Function<Integer, T> beans, Consumer<T> eachHandler, InputHandler<I> inputHandler, String ... args) throws Exception {

        isTwoPass = false;

        N document = getDocument(inputHandler.getInput(args));

        Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
        for (Field field : targetFields) {

            String selector = Target.Util.getValue(field);
            Binder binder = Target.Util.getBinder(field);
            Class<?> option = Target.Util.getOption(field);
            boolean optional = Target.Util.isOptional(field);

            if (isCollection) {

                Iterable<N> nodes;
                try {
                    nodes = selectAll(fixSelector(field, selector), document);
                } catch (Exception e) {
                    if (!optional) {
                        throw e;
                    }
                    continue;
                }

                int i = 0;
                for (N node : nodes) {
                    // because loops for each fields, instantiation should be done once
                    T bean = beans.apply(i++);

                    try {
                        String text = asText(node);
                        BeanUtil.setFieldValue(field, bean, text);
                    } catch (Exception e) {
                        if (!optional) {
                            throw e;
                        }
                        continue;
                    }

                    if (eachHandler != null) { // TODO field loop and selected are inverse
                        eachHandler.accept(bean);
                    }
                }
            } else {

                // because loops for each fields, instantiation should be done once
                T bean = beans.apply(0);

                try {
                    String text = asText(select(fixSelector(field, selector), document));
                    binder.bind(bean, field, field.getType(), text, option);
                } catch (Exception e) {
                    if (!optional) {
                        throw e;
                    }
                    continue;
                }

                if (eachHandler != null) { // TODO field loop and selected are inverse
                    eachHandler.accept(bean);
                }
            }
        }
    }

    /** 2 step scraping. */
    private void processTwoStep(Class<T> type, Consumer<T> eachHandler, InputHandler<I> inputHandler, String ... args) throws Exception {

        isTwoPass = true;

        String selector = WebScraper.Util.getValue(type);

        N document = getDocument(inputHandler.getInput(args));

        for (N node : selectAll(selector, document)) {
            T bean = type.newInstance();

            N subDocument = getSubDocument(node);

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {
                String subSelector = Target.Util.getValue(field);
                Binder binder = Target.Util.getBinder(field);
                Class<?> option = Target.Util.getOption(field);
                boolean optional = Target.Util.isOptional(field);
                try {
if (isDebug) {
 Debug.println(Level.FINER, fixSelector(field, subSelector) + ", " + subDocument);
}
                    String text = asText(select(fixSelector(field, subSelector), subDocument));
                    binder.bind(bean, field, field.getType(), text, option);
                } catch (Exception e) {
                    if (!optional) {
if (isDebug) {
 Debug.println(Level.FINE, e + "\n" + subDocument);
}
                    }
                }
            }

            eachHandler.accept(bean);
        }
    }
}
