package org.apache.tomcat.vault.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by mbeck on 3/4/15.
 */
public class PropertyFileManager {
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
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
