/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jmeter;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * This is a basic URL classloader for loading new resources
 * dynamically.
 *
 * It allows public access to the addURL() method.
 *
 * It also adds a convenience method to update the current thread classloader
 *
 */
public class DynamicClassLoader extends URLClassLoader {

    public DynamicClassLoader(URL[] urls) {
        super(urls);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent,
            URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    // Make the addURL method visible
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    /**
     *
     * @param urls - list of URLs to add to the thread's classloader
     */
    public static void updateLoader(URL [] urls) {
        DynamicClassLoader loader
            = (DynamicClassLoader) Thread.currentThread().getContextClassLoader();
        for(URL url : urls) {
            loader.addURL(url);
        }
    }
    
    private static final String CLASSPATH_SEPARATOR = File.pathSeparator;

    private static final String OS_NAME = System.getProperty("os.name");// $NON-NLS-1$

    private static final String OS_NAME_LC = OS_NAME.toLowerCase(java.util.Locale.ENGLISH);

    private static final String JAVA_CLASS_PATH = "java.class.path";// $NON-NLS-1$
    
    public void addPath(String path) throws MalformedURLException {
        File file = new File(path);
        // Ensure that directory URLs end in "/"
        if (file.isDirectory() && !path.endsWith("/")) {// $NON-NLS-1$
            file = new File(path + "/");// $NON-NLS-1$
        }
        addURL(file.toURI().toURL()); // See Java bug 4496398
        StringBuilder sb = new StringBuilder(System.getProperty(JAVA_CLASS_PATH));
        sb.append(CLASSPATH_SEPARATOR);
        sb.append(path);
        File[] jars = listJars(file);
        for (File jar : jars) {
            try {
                addURL(jar.toURI().toURL()); // See Java bug 4496398
                sb.append(CLASSPATH_SEPARATOR);
                sb.append(jar.getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        // ClassFinder needs this
        System.setProperty(JAVA_CLASS_PATH,sb.toString());
    }
    
    private static File[] listJars(File dir) {
        if (dir.isDirectory()) {
            return dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    if (name.endsWith(".jar")) {// $NON-NLS-1$
                        File jar = new File(f, name);
                        return jar.isFile() && jar.canRead();
                    }
                    return false;
                }
            });
        }
        return new File[0];
    }
}
