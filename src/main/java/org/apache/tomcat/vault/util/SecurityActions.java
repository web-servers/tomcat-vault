/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.apache.tomcat.vault.util;

import java.net.URL;

/**
 * Security Actions
 *
 * @author Anil.Saldhana@redhat.com
 * @since Dec 9, 2008
 */
class SecurityActions {

    /**
     * Load a class using the given class's classloader, fallback to thread context classloader
     *
     * @param theClass the class to use for classloader lookup
     * @param fqn the fully qualified name of the class to load
     * @return the loaded class, or null if not found
     */
    static Class<?> loadClass(final Class<?> theClass, final String fqn) {
        ClassLoader classLoader = theClass.getClassLoader();

        Class<?> clazz = loadClass(classLoader, fqn);
        if (clazz == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            clazz = loadClass(classLoader, fqn);
        }
        return clazz;
    }

    /**
     * Load a class using the given classloader
     *
     * @param cl the classloader to use
     * @param fqn the fully qualified name of the class to load
     * @return the loaded class, or null if not found
     */
    static Class<?> loadClass(final ClassLoader cl, final String fqn) {
        try {
            return cl.loadClass(fqn);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Set the system property
     *
     * @param key the property key
     * @param value the property value
     */
    static void setSystemProperty(final String key, final String value) {
        System.setProperty(key, value);
    }

    /**
     * Get the system property
     *
     * @param key the property key
     * @param defaultValue the default value if property is not found
     * @return the property value or default value
     */
    static String getSystemProperty(final String key, final String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    /**
     * Load a resource based on the passed class classloader.
     * Failing which try with the Thread Context ClassLoader
     *
     * @param clazz the class to use for classloader lookup
     * @param resourceName the resource name to load
     * @return the URL of the resource, or null if not found
     */
    static URL loadResource(final Class<?> clazz, final String resourceName) {
        ClassLoader clazzLoader = clazz.getClassLoader();
        URL url = clazzLoader.getResource(resourceName);

        if (url == null) {
            clazzLoader = Thread.currentThread().getContextClassLoader();
            url = clazzLoader.getResource(resourceName);
        }

        return url;
    }
}