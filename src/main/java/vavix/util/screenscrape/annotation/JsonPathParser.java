/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonContext;

import vavi.beans.BeanUtil;
import vavi.util.Debug;


/**
 * JsonPathParser.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2018/02/22 nsano initial version <br>
 */
public class JsonPathParser<T> implements Parser<InputStream, T> {

    /**
     * <li> TODO now 1 step JsonPath only
     *
     * ignore {@link WebScraper#encoding()}
     */
    public List<T> parse(Class<T> type, InputHandler<InputStream> handler, String ... args) {
        try {
//            String encoding = WebScraper.Util.getEncoding(type);
//System.err.println("encoding: " + encoding);

            boolean isCollection = WebScraper.Util.isCollection(type);

            InputStream is = handler.getInput(args);
            Object document = JsonPath.parse(is);
System.err.println(JsonContext.class.cast(document).jsonString());

            List<T> results = new ArrayList<>();

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {

                String jsonPath = Target.Util.getValue(field);
                if (jsonPath.isEmpty()) {
                    jsonPath = "$." + (isCollection ? "." : "") + field.getName();
                }
System.err.println("jsonPath: " + jsonPath);

                if (isCollection) {

                    List<Object> nodeList = JsonPath.read(document, jsonPath);
System.err.println("nodeList: " + nodeList);
                    for (int i = 0; i < nodeList.size(); i++) {
                        // because loops for each fields, instantiation should be done once
                        T bean = null;
                        try {
                            bean = results.get(i);
                        } catch (IndexOutOfBoundsException e) {
                            bean = type.newInstance();
                            results.add(bean);
                        }

                        String text = nodeList.get(i).toString().trim();
//System.err.println(field.getName() + ": " + text);
                        BeanUtil.setFieldValue(field, bean, text);
                    }
                } else {

                    // because loops for each fields, instantiation should be done once
                    T bean = null;
                    try {
                        bean = results.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        bean = type.newInstance();
                        results.add(bean);
                    }

                    String text = ((String) JsonPath.read(document, jsonPath)).trim();
                    BeanUtil.setFieldValue(field, bean, text);
                }
            }

            return results;

        } catch (IllegalAccessException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * <h4>2 step JsonPath</h4>
     * ignore {@link WebScraper#encoding()}
     * <p>
     *  {@link WebScraper#value()} で指定した JsonPath で取得できる部分 Json から
     *  {@link Target#value()} で指定した JsonPath で取得する方法。
     * </p>
     * <p>
     * you need to specify at the first element of the {@link Target#value()} as same as the last element in {@link WebScraper#value()}.
     * </p>
     * <li> TODO now 2 step JsonPath only
     * <li> TODO {@link WebScraper#value()} が存在すれば 2 step とか
     */
    public void foreach(Class<T> type, EachHandler<T> eachHandler, InputHandler<InputStream> inputHandler, String ... args) {
        try {
//            String encoding = WebScraper.Util.getEncoding(type);
//System.err.println("encoding: " + encoding);

            String jsonPath = WebScraper.Util.getValue(type);

            InputStream is = inputHandler.getInput(args);
            Object document = JsonPath.parse(is);

            List<Object> nodeList = JsonPath.read(document, jsonPath);
//System.err.println("nodeList: " + nodeList.getLength());
if (nodeList.size() == 0) {
 Debug.println("no node list: " + jsonPath);
}

            for (int i = 0; i < nodeList.size(); i++) {
                T bean = type.newInstance();

                Object node = nodeList.get(i);
//System.err.println(node.toString(encoding));
                Object subDocument = JsonPath.parse(node.toString());

                Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                for (Field field : targetFields) {
                    String subJsonPpath = Target.Util.getValue(field);
                    String text = (String) JsonPath.read(subDocument, subJsonPpath);
                    BeanUtil.setFieldValue(field, bean, text.trim());
                }

                eachHandler.exec(bean);
            }

        } catch (IllegalAccessException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
