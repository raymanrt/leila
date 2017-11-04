/*
 * Copyright 2017 Riccardo Tasso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.raymanrt.leila.formatter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.document.Document;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class DocumentFormatterWrapper implements Closeable {
    private Method formatMethod;
    private Method closeMethod;
    private boolean isCloseable;
    private Object obj;

    public DocumentFormatterWrapper(String[] pluginAndParams) {
        if(pluginAndParams == null || pluginAndParams.length == 0) {
            return;
        }

        final String classname = pluginAndParams[0];
        String[] params = ArrayUtils.remove(pluginAndParams,0);

        System.out.println(":: loading plugin: " + classname);
        System.out.println(":: params plugin: " + Arrays.asList(params));

        try {
            Class clazz = ClassLoader.getSystemClassLoader().loadClass(classname);
            this.formatMethod = findFormatMethod(clazz);

            if(formatMethod == null) {
                System.out.println(":: can't find a public formatMethod which has a Document parameter and a String return type");
            }

            this.obj = tryToBuildWithParams(clazz, params);

            this.isCloseable = isCloseable(clazz.getInterfaces());
            if(isCloseable) {
                this.closeMethod = findCloseMethod(clazz);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object tryToBuildWithParams(final Class clazz, final String[] params) {
        try {
            return clazz.getDeclaredConstructor(String[].class).newInstance((Object) params);
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            return clazz.getDeclaredConstructor(String.class).newInstance(params[0]);
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            if (params.length == 0) {
                return clazz.newInstance();
            }
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Method findCloseMethod(final Class clazz) {
        for(Method m : clazz.getMethods()) {
            if(
                    m.getReturnType().equals(Void.TYPE)
                            && m.getParameterCount() == 0
                            && Modifier.isPublic(m.getModifiers())
                            && m.getName().equals("close")
                    ) {
                return m;
            }
        }
        return null;
    }

    private Method findFormatMethod(final Class clazz) {

        for(Method m : clazz.getMethods()) {
            if(
                m.getReturnType().equals(String.class)
                && m.getParameterCount() == 1
                && m.getParameterTypes()[0].equals(Document.class)
                && Modifier.isPublic(m.getModifiers())
             ) {
                return m;
            }
        }
        return null;
    }

    private boolean isCloseable(final Class[] interfaces) {
        for(Class oneInterface : interfaces) {
            if(oneInterface.equals(Closeable.class)) return true;
        }
        return false;
    }

    public String format(final Document document) {
        try {
            if(formatMethod == null) {
                return SimpleDocumentFormatter.format(document);
            }
            return formatMethod.invoke(obj, document).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public void close() throws IOException {
        if(isCloseable) {
            try {
                closeMethod.invoke(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
