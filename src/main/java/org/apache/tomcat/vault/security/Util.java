/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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

package org.apache.tomcat.vault.security;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;

/**
 * Util.
 *
 * @author Scott.Stark@jboss.org
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class Util {

    private static final StringManager strm = StringManager.getManager(Util.class);
    private static final Log log = LogFactory.getLog(Util.class);
    private static final StringManager msm = StringManager.getManager("org.apache.tomcat.vault.security.resources");

    private static PasswordCache externalPasswordCache;

    /**
     * Execute a password load command to obtain the char[] contents of a
     * password.
     *
     * @param passwordCmd - A command to execute to obtain the plaintext
     *                    password. The format is one of:
     *                    '{EXT}...' where the '...' is the exact command
     *                    '{EXTC[:expiration_in_millis]}...' where the '...' is the exact command
     *                    line that will be passed to the Runtime.exec(String) method to execute a
     *                    platform command. The first line of the command output is used as the
     *                    password.
     *                    EXTC variant will cache the passwords for expiration_in_millis milliseconds.
     *                    Default cache expiration is 0 = infinity.
     *                    '{CMD}...' or '{CMDC[:expiration_in_millis]}...' for a general command to execute. The general
     *                    command is a string delimited by ',' where the first part is the actual
     *                    command and further parts represents its parameters. The comma can be
     *                    backslashed in order to keep it as a part of the parameter.
     * @return the password characters
     * @throws Exception
     */
    public static char[] loadPassword(String passwordCmd)
            throws Exception {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(Util.class.getName() + ".loadPassword"));
        }
        char[] password = null;
        String passwordCmdType = null;

        // Look for a {...} prefix indicating a password command
        if (passwordCmd.charAt(0) == '{') {
            StringTokenizer tokenizer = new StringTokenizer(passwordCmd, "{}");
            passwordCmdType = tokenizer.nextToken();
            passwordCmd = tokenizer.nextToken();
        } else {
            // Its just the password string
            password = passwordCmd.toCharArray();
        }

        if (password == null) {
            // Load the password
            if (passwordCmdType.startsWith("EXTC") || passwordCmdType.startsWith("CMDC")) {
                long timeOut = 0;
                if (passwordCmdType.indexOf(':') > -1) {
                    try {
                        String[] token = passwordCmdType.split(":");
                        timeOut = Long.parseLong(token[1]);
                    } catch (Throwable e) {
                        // ignore
                        log.error(strm.getString("util.parsingTimeoutNumber"));
                    }
                }
                if (externalPasswordCache == null) {
                    externalPasswordCache = ExternalPasswordCache
                            .getExternalPasswordCacheInstance();
                }
                if (externalPasswordCache.contains(passwordCmd, timeOut)) {
                    password = externalPasswordCache.getPassword(passwordCmd);
                } else {
                    password = switchCommandExecution(passwordCmdType, passwordCmd);
                    if (password != null) {
                        externalPasswordCache.storePassword(passwordCmd, password);
                    }
                }
            } else if (passwordCmdType.startsWith("EXT") || passwordCmdType.startsWith("CMD")) {
                // non-cached variant
                password = switchCommandExecution(passwordCmdType, passwordCmd);
            } else {
                throw new IllegalArgumentException(msm.getString("invalidPasswordCommandType", passwordCmdType));
            }
        }
        return password;
    }

    private static char[] switchCommandExecution(String passwordCmdType, String passwordCmd)
            throws Exception {
        if (passwordCmdType.startsWith("EXT"))
            return execPasswordCmd(passwordCmd);
        else if (passwordCmdType.startsWith("CMD"))
            return execPBBasedPasswordCommand(passwordCmd);
        else
            throw new IllegalArgumentException(msm.getString("invalidPasswordCommandType", passwordCmdType));
    }

    /**
     * Execute a Runtime command to load a password.
     *
     * @param passwordCmd
     * @return
     * @throws Exception
     */
    private static char[] execPasswordCmd(String passwordCmd)
            throws Exception {
        log.trace(strm.getString("util.beginExecPasswordCmd", passwordCmd));
        String password = execCmd(passwordCmd);
        return password.toCharArray();
    }

    private static String execCmd(String cmd) throws Exception {
        SecurityManager sm = System.getSecurityManager();
        String line;
        if (sm != null) {
            line = RuntimeActions.PRIVILEGED.execCmd(cmd);
        } else {
            line = RuntimeActions.NON_PRIVILEGED.execCmd(cmd);
        }
        return line;
    }

    /**
     * Execute a Runtime command to load a password.
     * It uses ProcessBuilder to execute the command.
     *
     * @param passwordCmd
     * @return the loaded password
     * @throws Exception
     */
    private static char[] execPBBasedPasswordCommand(String passwordCmd) throws Exception {
        log.trace(strm.getString("util.beginExecPasswordCmd", passwordCmd));
        SecurityManager sm = System.getSecurityManager();
        String password;
        if (sm != null) {
            password = RuntimeActions.PB_BASED_PRIVILEGED.execCmd(passwordCmd);
        } else {
            password = RuntimeActions.PB_BASED_NON_PRIVILEGED.execCmd(passwordCmd);
        }
        return password.toCharArray();
    }


    interface RuntimeActions {
        RuntimeActions PRIVILEGED = new RuntimeActions() {
            public String execCmd(final String cmd)
                    throws Exception {
                try {
                    String line = AccessController.doPrivileged(
                            new PrivilegedExceptionAction<String>() {
                                public String run() throws Exception {
                                    return NON_PRIVILEGED.execCmd(cmd);
                                }
                            }
                    );
                    return line;
                } catch (PrivilegedActionException e) {
                    throw e.getException();
                }
            }
        };
        RuntimeActions NON_PRIVILEGED = new RuntimeActions() {
            public String execCmd(final String cmd)
                    throws Exception {
                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec(cmd);
                InputStream stdin = null;
                String line;
                BufferedReader reader = null;
                try {
                    stdin = p.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stdin));
                    line = reader.readLine();
                } finally {
                    if (reader != null)
                        reader.close();
                    if (stdin != null)
                        stdin.close();
                }

                int exitCode = p.waitFor();
                log.trace(strm.getString("util.endExecPasswordCmd", exitCode));
                return line;
            }
        };
        RuntimeActions PB_BASED_PRIVILEGED = new RuntimeActions() {
            public String execCmd(final String command)
                    throws Exception {
                try {
                    String password = AccessController.doPrivileged(
                            new PrivilegedExceptionAction<String>() {
                                public String run() throws Exception {
                                    return PB_BASED_NON_PRIVILEGED.execCmd(command);
                                }
                            }
                    );
                    return password;
                } catch (PrivilegedActionException e) {
                    throw e.getException();
                }
            }
        };
        RuntimeActions PB_BASED_NON_PRIVILEGED = new RuntimeActions() {
            public String execCmd(final String command) throws Exception {
                final String[] parsedCommand = parseCommand(command);
                final ProcessBuilder builder = new ProcessBuilder(parsedCommand);
                final Process process = builder.start();
                final String line;
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    line = reader.readLine();
                } finally {
                    if (reader != null)
                        reader.close();
                }

                int exitCode = process.waitFor();
                log.trace(strm.getString("util.endExecPasswordCmd", exitCode));
                return line;
            }

            protected String[] parseCommand(String command) {
                // comma can be backslashed
                final String[] parsedCommand = command.split("(?<!\\\\),");
                for (int k = 0; k < parsedCommand.length; k++) {
                    if (parsedCommand[k].indexOf('\\') != -1)
                        parsedCommand[k] = parsedCommand[k].replaceAll("\\\\,", ",");
                }
                return parsedCommand;
            }
        };

        String execCmd(String cmd) throws Exception;
    }

    /**
     * Checks whether password can be loaded by {@link #loadPassword(String)}.
     *
     * @param passwordCmd a potential password command
     * @return true if password can be loaded by {@link #loadPassword(String)}, false otherwise.
     */
    public static boolean isPasswordCommand(String passwordCmd) {
        return (passwordCmd != null)
                && (passwordCmd.startsWith("{EXT}")
                || passwordCmd.startsWith("{EXTC")  // it has to be without closing brace to cover :<time in millis>
                || passwordCmd.startsWith("{CMD}")
                || passwordCmd.startsWith("{CMDC"));  // it has to be without closing brace to cover :<time in millis>
    }

    /**
     * Checks whether password can be loaded by {@link #loadPassword(String)}.
     *
     * @param passwordCmd a potential password command
     * @return true if password can be loaded by {@link #loadPassword(String)}, false otherwise.
     */
    public static boolean isPasswordCommand(char[] passwordCmd) {
        return (passwordCmd != null) && isPasswordCommand(new String(passwordCmd));
    }

}
