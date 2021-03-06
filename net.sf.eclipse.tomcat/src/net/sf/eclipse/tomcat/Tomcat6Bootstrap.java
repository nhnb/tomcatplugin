/* The MIT License
 * (c) Copyright Sysdeo SA 2001-2002
 * (c) Copyright Eclipse Tomcat Plugin 2014-2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sf.eclipse.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;


/**
 * Bootstrap specifics for Tomcat 6
 * See %TOMCAT5_HOME%/bin/catalina.bat
 */
public class Tomcat6Bootstrap extends TomcatBootstrap {

    @Override
    public String[] getClasspath() {
        ArrayList<String> classpath = new ArrayList<String>();
        classpath.add(getTomcatDir() + File.separator + "bin" + File.separator + "bootstrap.jar");

        // Add tools.jar JDK file to classpath
        String toolsJarLocation = VMLauncherUtility.getVMInstall().getInstallLocation() + File.separator + "lib" + File.separator + "tools.jar";
        if(new File(toolsJarLocation).exists()) {
            classpath.add(toolsJarLocation);
        }
        return (classpath.toArray(new String[0]));
    }

    @Override
    public String getMainClass() {
        return "org.apache.catalina.startup.Bootstrap";
    }

    @Override
    public String getStartCommand() {
        return "start";
    }

    @Override
    public String getStopCommand() {
        return "stop";
    }

    @Override
    public String[] getPrgArgs(String command) {
        String[] prgArgs;
        if (TomcatLauncherPlugin.getDefault().getConfigMode().equals(TomcatLauncherPlugin.SERVERXML_MODE)) {
            prgArgs = new String[3];
            prgArgs[0] = "-config";
            prgArgs[1] = "\"" + TomcatLauncherPlugin.getDefault().getConfigFile() + "\"";
            prgArgs[2] = command;
        } else {
            prgArgs = new String[1];
            prgArgs[0] = command;
        }
        return prgArgs;
    }


    @Override
    public String[] getVmArgs() {
        ArrayList<String> vmArgs = new ArrayList<String>();
        vmArgs.add("-Dcatalina.home=\"" + getTomcatDir() + "\"");

        String endorsedDir = getTomcatDir() + File.separator + "endorsed";
        vmArgs.add("-Djava.endorsed.dirs=\"" + endorsedDir + "\"");

        String catalinaBase = getTomcatBase();
        if(catalinaBase.length() == 0) {
            catalinaBase = getTomcatDir();
        }

        vmArgs.add("-Dcatalina.base=\"" + catalinaBase + "\"");
        vmArgs.add("-Djava.io.tmpdir=\"" + catalinaBase + File.separator + "temp\"");

        if(TomcatLauncherPlugin.getDefault().isSecurityManagerEnabled()) {
            vmArgs.add("-Djava.security.manager");
            String securityPolicyFile = catalinaBase + File.separator + "conf" + File.separator + "catalina.policy";
            vmArgs.add("-Djava.security.policy=\"" + securityPolicyFile + "\"");
        }

        return (vmArgs.toArray(new String[0]));
    }


    @Override
    public String getXMLTagAfterContextDefinition() {
        return "</Host>";
    }

    @Override
    public IPath getJasperJarPath() {
        return new Path("lib").append("jasper.jar");
    }

    @Override
    public IPath getServletJarPath() {
        return new Path("lib").append("servlet-api.jar");
    }

    @Override
    public IPath getJSPJarPath() {
        return new Path("lib").append("jsp-api.jar");
    }

    public IPath getElJarPath() {
        return new Path("lib").append("el-api.jar");
    }

    public IPath getAnnotationsJarPath() {
        return new Path("lib").append("annotations-api.jar");
    }

    @Override
    public Collection<IClasspathEntry> getTomcatJars() {
        IPath tomcatHomePath = TomcatLauncherPlugin.getDefault().getTomcatIPath();
        ArrayList<IClasspathEntry> jars = (ArrayList<IClasspathEntry>) super.getTomcatJars();
        jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getElJarPath()), null, null));
        jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getAnnotationsJarPath()), null, null));
        return jars;
    }

    @Override
    public String getLabel() {
        return "Tomcat 6.x";
    }

    @Override
    public String getContextWorkDir(String workFolder) {
        StringBuffer workDir = new StringBuffer("workDir=");
        workDir.append('"');
        workDir.append(workFolder);
        workDir.append('"');
        return workDir.toString();
    }

}

