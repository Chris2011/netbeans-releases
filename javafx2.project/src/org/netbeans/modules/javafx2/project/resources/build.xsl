<?xml version="1.0" encoding="UTF-8"?>
<!--
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2012 Oracle and/or its affiliates. All rights reserved.

Oracle and Java are registered trademarks of Oracle and/or its affiliates.
Other names may be trademarks of their respective owners.

The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
particular file as subject to the "Classpath" exception as provided
by Oracle in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2012 Sun Microsystems, Inc.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:j2seproject="http://www.netbeans.org/ns/j2se-project/3"
                xmlns:fx="javafx:com.sun.javafx.tools.ant"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project j2seproject">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">

        <!-- Annoyingly, the JAXP impl in JRE 1.4.2 seems to randomly reorder attrs. -->
        <!-- (I.e. the DOM tree gets them in an unspecified order?) -->
        <!-- As a workaround, use xsl:attribute for all but the first attr. -->
        <!-- This seems to produce them in the order you want. -->
        <!-- Tedious, but appears to do the job. -->
        <!-- Important for build.xml, which is very visible; not so much for build-impl.xml. -->

        <xsl:comment> You may freely edit this file. See commented blocks below for </xsl:comment>
        <xsl:comment> some examples of how to customize the build. </xsl:comment>
        <xsl:comment> (If you delete it and reopen the project it will be recreated.) </xsl:comment>
        <xsl:comment> By default, only the Clean and Build commands use this build script. </xsl:comment>
        
        <xsl:variable name="name" select="/project:project/project:configuration/j2seproject:data/j2seproject:name"/>
        <!-- Synch with build-impl.xsl: -->
        <!-- XXX really should translate all chars that are *not* safe (cf. PropertyUtils.getUsablePropertyName): -->
        <xsl:variable name="codename" select="translate($name, ' ', '_')"/>
        <project name="{$codename}">
            <xsl:attribute name="default">default</xsl:attribute>
            <xsl:attribute name="basedir">.</xsl:attribute>
            <description>Builds, tests, and runs the project <xsl:value-of select="$name"/>.</description>
            <import file="nbproject/build-impl.xml"/>

            <xsl:comment><![CDATA[

    There exist several targets which are by default empty and which can be 
    used for execution of your tasks. These targets are usually executed 
    before and after some main targets. Those of them relevant for JavaFX project are: 

      -pre-init:                 called before initialization of project properties
      -post-init:                called after initialization of project properties
      -pre-compile:              called before javac compilation
      -post-compile:             called after javac compilation
      -pre-compile-test:         called before javac compilation of JUnit tests
      -post-compile-test:        called after javac compilation of JUnit tests
      -pre-jfx-jar:              called before FX SDK specific <fx:jar> task
      -post-jfx-jar:             called after FX SDK specific <fx:jar> task
      -pre-jfx-deploy:           called before FX SDK specific <fx:deploy> task
      -post-jfx-deploy:          called after FX SDK specific <fx:deploy> task
      -post-clean:               called after cleaning build products

    (Targets beginning with '-' are not intended to be called on their own.)

    Example of inserting a HTML postprocessor after javaFX SDK deployment:

        <target name="-post-jfx-deploy">
            <basename property="jfx.deployment.base" file="${jfx.deployment.jar}" suffix=".jar"/>
            <property name="jfx.deployment.html" location="${jfx.deployment.dir}${file.separator}${jfx.deployment.base}.html"/>
            <custompostprocess>
                <fileset dir="${jfx.deployment.html}"/>
            </custompostprocess>
        </target>

    Example of calling an Ant task from JavaFX SDK. Note that access to JavaFX SDK Ant tasks must be
    initialized; to ensure this is done add the dependence on -check-jfx-sdk-version target:

        <target name="-post-jfx-jar" depends="-check-jfx-sdk-version">
            <echo message="Calling jar task from JavaFX SDK"/>
            <fx:jar ...>
                ...
            </fx:jar>
        </target>

    For more details about JavaFX SDK Ant tasks go to
    http://docs.oracle.com/javafx/2/deployment/jfxpub-deployment.htm

    For list of available properties check the files
    nbproject/build-impl.xml and nbproject/jfx-impl.xml.

    ]]></xsl:comment>

        </project>

    </xsl:template>
    
</xsl:stylesheet> 
