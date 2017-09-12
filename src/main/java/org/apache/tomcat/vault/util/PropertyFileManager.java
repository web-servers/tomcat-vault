/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

import java.io.*;
import java.util.Properties;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Created by mbeck on 3/4/15.
 */
public class PropertyFileManager {
    private static final Log log = LogFactory.getLog(PropertyFileManager.class);

    private String fname;

    public PropertyFileManager(String fname) {
        this.fname = fname;
    }

    public void save(Properties prop) {
        OutputStream output = null;

        try {
            output = new FileOutputStream(this.fname);
            // save properties to project root folder
            prop.store(output, null);
        } catch (IOException io) {
            log.error(io.getMessage(), io);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public Properties load() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(this.fname);
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return prop;
    }
}
