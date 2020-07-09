/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomcat.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.io.IOException;

import javax.servlet.ServletException;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/*
 * RequestDumperValve valve.
 * Read header and dump them in catalina.out
 */

public class MyValve
    extends ValveBase {

    private String vaultparam = "default";
    public String getVaultparam() {
        return vaultparam;
    }
    public void setVaultparam(String vaultparam) {
        this.vaultparam = vaultparam;    
    }

    public void invoke(Request request, Response response) throws IOException, ServletException {
        System.out.println("in the valve from the context.xml in webapp: Vaultparam: " + vaultparam);
    	getNext().invoke(request, response);
    }
}
