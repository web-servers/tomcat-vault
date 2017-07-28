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

    public Properties load(){
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
