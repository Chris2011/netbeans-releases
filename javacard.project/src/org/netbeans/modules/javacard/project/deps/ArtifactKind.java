
package org.netbeans.modules.javacard.project.deps;

import org.openide.util.NbBundle;

/**
 * Kinds of artifacts supported by a given DependencyKind
 *
 * @author Tim Boudreau
 */
public enum ArtifactKind {
    ORIGIN,
    SOURCES_PATH,
    EXP_FILE,
    SIG_FILE,
    ;

    //Constants which are used as suffixes for property names in project.properties
    //e.g. libs.ant.jar.origin=../../apache-ant/lib/ant.jar
    private static final String ORIGIN_ATTR = "origin"; //NOI18N
    private static final String EXP_FILE_ATTR = "expfile"; //NOI18N
    private static final String SOURCES_ATTR = "sources"; //NOI18N
    private static final String SIGFILE_ATTR = "signature"; //NOI18N

    public String XMLName() {
        switch (this) {
            case ORIGIN : return ORIGIN_ATTR;
            case EXP_FILE : return EXP_FILE_ATTR;
            case SOURCES_PATH : return SOURCES_ATTR;
            case SIG_FILE : return SIGFILE_ATTR;
            default : throw new AssertionError();
        }
    }

    public boolean mayBeMultipleFiles() {
        return this == SOURCES_PATH;
    }

    public boolean mayBeNull() {
        return this == SOURCES_PATH;
    }

    @Override
    public String toString() {
        return NbBundle.getMessage(ArtifactKind.class, name());
    }
}
