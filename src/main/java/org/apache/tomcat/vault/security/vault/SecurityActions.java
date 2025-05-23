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

package org.apache.tomcat.vault.security.vault;

/**
 * Security Actions
 *
 * @author Anil.Saldhana@redhat.com
 * @since Aug 12, 2011
 */
class SecurityActions {

    /**
     * Load a class using the given class's classloader, fallback to thread context classloader
     *
     * @param clazz the class to use for classloader lookup
     * @param fqn the fully qualified name of the class to load
     * @return the loaded class, or null if not found
     */
    static Class<?> loadClass(final Class<?> clazz, final String fqn) {
        ClassLoader cl = clazz.getClassLoader();
        Class<?> loadedClass = null;

        // Try with the given class's classloader first
        try {
            loadedClass = cl.loadClass(fqn);
        } catch (ClassNotFoundException e) {
            // Ignore and try context classloader
        }

        // If not found, try with thread context classloader
        if (loadedClass == null) {
            try {
                loadedClass = Thread.currentThread().getContextClassLoader().loadClass(fqn);
            } catch (ClassNotFoundException e) {
                // Ignore - will return null
            }
        }

        return loadedClass;
    }

    /**
     * Load a class using the given classloader
     *
     * @param classLoader the classloader to use
     * @param fqn the fully qualified name of the class to load
     * @return the loaded class, or null if not found
     */
    static Class<?> loadClass(final ClassLoader classLoader, final String fqn) {
        try {
            return classLoader.loadClass(fqn);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}