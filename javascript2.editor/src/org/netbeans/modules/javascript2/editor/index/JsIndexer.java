/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.index;

import org.netbeans.modules.javascript2.model.api.IndexedElement;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.javascript2.lexer.api.JsTokenId;
import org.netbeans.modules.javascript2.model.api.JsElement;
import org.netbeans.modules.javascript2.model.api.JsFunction;
import org.netbeans.modules.javascript2.model.api.JsObject;
import org.netbeans.modules.javascript2.model.api.Model;
import org.netbeans.modules.javascript2.types.api.TypeUsage;
import org.netbeans.modules.javascript2.model.api.ModelUtils;
import org.netbeans.modules.javascript2.editor.parser.JsParserResult;
import org.netbeans.modules.javascript2.editor.parser.SanitizingParser;
import org.netbeans.modules.javascript2.model.api.Index;
import static org.netbeans.modules.javascript2.model.api.IndexedElement.ANONYMOUS_POSFIX;
import static org.netbeans.modules.javascript2.model.api.IndexedElement.OBJECT_POSFIX;
import static org.netbeans.modules.javascript2.model.api.IndexedElement.PARAMETER_POSTFIX;
import org.netbeans.modules.javascript2.model.api.JsArray;
import org.netbeans.modules.javascript2.model.api.JsReference;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.util.ChangeSupport;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Petr Pisl
 */
public class JsIndexer extends EmbeddingIndexer {

    private static final Logger LOG = Logger.getLogger(JsIndexer.class.getName());

    private static ChangeSupport changeSupport = new ChangeSupport(JsIndexer.class);

    @Override
    protected void index(Indexable indexable, Result result, Context context) {
        LOG.log(Level.FINE, "Indexing: {0}, fullPath: {1}", new Object[]{indexable.getRelativePath(), result.getSnapshot().getSource().getFileObject().getPath()});

        if (!(result instanceof JsParserResult)) {
            return;
        }

        if (!context.checkForEditorModifications()) {
            // FIXME
            //JsIndex.changeInIndex();
        }
        JsParserResult parserResult = (JsParserResult) result;
        Model model = Model.getModel(parserResult, true);

        IndexingSupport support;
        try {
            support = IndexingSupport.getInstance(context);
        } catch (IOException ioe) {
            LOG.log(Level.WARNING, null, ioe);
            return;
        }

        // we need to remove old documents (document per object, not file)
        support.removeDocuments(indexable);

        JsObject globalObject = model.getGlobalObject();
        for (JsObject object : globalObject.getProperties().values()) {
            if (object.getParent() != null) {
                storeObject(object, object.getName(), support, indexable);
            }
        }
        
        IndexDocument document = support.createDocument(indexable);
        for (JsObject object : globalObject.getProperties().values()) {
            if (object.getParent() != null) {
                storeUsages(object, object.getName(), document);
            }
        }
        support.addDocument(document);
    }

    protected static IndexDocument createDocument(JsObject object, String fqn, IndexingSupport support, Indexable indexable) {
        IndexDocument elementDocument = support.createDocument(indexable);
        elementDocument.addPair(Index.FIELD_BASE_NAME, object.getName(), true, true);
        elementDocument.addPair(Index.FIELD_BASE_NAME_INSENSITIVE, object.getName().toLowerCase(), true, false);
        elementDocument.addPair(Index.FIELD_FQ_NAME,  fqn + (object.isAnonymous() ? ANONYMOUS_POSFIX 
                : object.getJSKind() == JsElement.Kind.PARAMETER ? PARAMETER_POSTFIX : OBJECT_POSFIX), true, true);
//        boolean isGlobal = object.getParent() != null ? ModelUtils.isGlobal(object.getParent()) : ModelUtils.isGlobal(object);
//        elementDocument.addPair(JsIndex.FIELD_IS_GLOBAL, (isGlobal ? "1" : "0"), true, true);
        elementDocument.addPair(Index.FIELD_OFFSET, Integer.toString(object.getOffset()), true, true);            
        elementDocument.addPair(Index.FIELD_FLAG, Integer.toString(IndexedElement.Flag.getFlag(object)), false, true);
//        StringBuilder sb = new StringBuilder();
//        for (JsObject property : object.getProperties().values()) {
//            if (!property.getModifiers().contains(Modifier.PRIVATE)) {
//                sb.append(codeProperty(property)).append("#@#");
//            }
//        }
//        elementDocument.addPair(JsIndex.FIELD_PROPERTY, sb.toString(), false, true);
        StringBuilder sb = new StringBuilder();
        for (TypeUsage type : object.getAssignments()) {
            sb.append(type.getType());
            sb.append(":"); //NOI18N
            sb.append(type.getOffset());
            sb.append(":"); //NOI18N
            sb.append(type.isResolved() ? "1" : "0");  //NOI18N
            sb.append("|");
        }
        elementDocument.addPair(Index.FIELD_ASSIGNMENTS, sb.toString(), false, true);
        
        if (object.getJSKind().isFunction()) {
            sb = new StringBuilder();
            for(TypeUsage type : ((JsFunction)object).getReturnTypes()) {
                sb.append(type.getType());
                sb.append(","); //NOI18N
                sb.append(type.getOffset());
                sb.append(","); //NOI18N
                sb.append(type.isResolved() ? "1" : "0");  //NOI18N
                sb.append("|");
            }
            elementDocument.addPair(Index.FIELD_RETURN_TYPES, sb.toString(), false, true);
            elementDocument.addPair(Index.FIELD_PARAMETERS, codeParameters(((JsFunction)object).getParameters()), false, true);
        }
        
        if (object instanceof JsArray) {
            sb = new StringBuilder();
            for(TypeUsage type : ((JsArray)object).getTypesInArray()) {
                sb.append(type.getType());
                sb.append(","); //NOI18N
                sb.append(type.getOffset());
                sb.append(","); //NOI18N
                sb.append(type.isResolved() ? "1" : "0");  //NOI18N
                sb.append("|");
            }
            elementDocument.addPair(Index.FIELD_ARRAY_TYPES, sb.toString(), false, true);
        }

        
        return elementDocument;
    }
    
    protected static IndexDocument createDocumentForReference(JsReference object, String fqn, IndexingSupport support, Indexable indexable) {
        IndexDocument elementDocument = support.createDocument(indexable);
        elementDocument.addPair(Index.FIELD_BASE_NAME, object.getName(), true, true);
        elementDocument.addPair(Index.FIELD_BASE_NAME_INSENSITIVE, object.getName(), true, false);
        elementDocument.addPair(Index.FIELD_FQ_NAME,  fqn + (object.isAnonymous() ? ANONYMOUS_POSFIX 
                : object.getJSKind() == JsElement.Kind.PARAMETER ? PARAMETER_POSTFIX : OBJECT_POSFIX), true, true);
        elementDocument.addPair(Index.FIELD_OFFSET, Integer.toString(object.getOffset()), true, true);            
        elementDocument.addPair(Index.FIELD_FLAG, Integer.toString(IndexedElement.Flag.getFlag(object)), false, true);

        StringBuilder sb = new StringBuilder();
        sb.append(object.getOriginal().getFullyQualifiedName());
        sb.append(":"); //NOI18N
        sb.append(object.getOffset());
        sb.append(":"); //NOI18N
        sb.append("1");  //NOI18N
        elementDocument.addPair(Index.FIELD_ASSIGNMENTS, sb.toString(), false, true);
        
        if (object.getJSKind().isFunction()) {
            sb = new StringBuilder();
            for(TypeUsage type : ((JsFunction)object).getReturnTypes()) {
                sb.append(type.getType());
                sb.append(","); //NOI18N
                sb.append(type.getOffset());
                sb.append(","); //NOI18N
                sb.append(type.isResolved() ? "1" : "0");  //NOI18N
                sb.append("|");
            }
            elementDocument.addPair(Index.FIELD_RETURN_TYPES, sb.toString(), false, true);
            elementDocument.addPair(Index.FIELD_PARAMETERS, codeParameters(((JsFunction)object).getParameters()), false, true);
        }
        
        if (object instanceof JsArray) {
            sb = new StringBuilder();
            for(TypeUsage type : ((JsArray)object).getTypesInArray()) {
                sb.append(type.getType());
                sb.append(","); //NOI18N
                sb.append(type.getOffset());
                sb.append(","); //NOI18N
                sb.append(type.isResolved() ? "1" : "0");  //NOI18N
                sb.append("|");
            }
            elementDocument.addPair(Index.FIELD_ARRAY_TYPES, sb.toString(), false, true);
        }
        return elementDocument;
    }

    private void storeObject(JsObject object, String fqn, IndexingSupport support, Indexable indexable) {
        if (!isInvisibleFunction(object)) {
            if (object.isDeclared() || ModelUtils.PROTOTYPE.equals(object.getName())) {
                // if it's delcared, then store in the index as new document.
                IndexDocument document = createDocument(object, fqn, support, indexable);
                support.addDocument(document);
            }
            if (!(object instanceof JsReference && ModelUtils.isDescendant(object, ((JsReference)object).getOriginal()))) {
                // look for all other properties. Even if the object doesn't have to be delcared in the file
                // there can be declared it's properties or methods
                for (JsObject property : object.getProperties().values()) {
                    if (!(property instanceof JsReference && !((JsReference)property).getOriginal().isAnonymous())) {
                        storeObject(property, fqn + '.' + property.getName(), support, indexable);
                    } else {
                        IndexDocument document = createDocumentForReference((JsReference)property, fqn + '.' + property.getName(), support, indexable);
////                      IndexDocument document = IndexedElement.createDocument(property, fqn + '.' + property.getName(), support, indexable);
                        support.addDocument(document);
                    }
                }
                if (object instanceof JsFunction) {
                    // store parameters
                    for (JsObject parameter : ((JsFunction)object).getParameters()) {
                        storeObject(parameter, fqn + '.' + parameter.getName(), support, indexable);
                    }
                }
            }
        }
    }
    
    private boolean isInvisibleFunction(JsObject object) {
        if (object.getJSKind().isFunction() && (object.isAnonymous() || object.getModifiers().contains(Modifier.PRIVATE))) {
            JsObject parent = object.getParent();
            if (parent != null && parent.getJSKind() == JsElement.Kind.FILE) {
                return false;
            }
            if (parent != null && parent instanceof JsFunction) {
                Collection<? extends TypeUsage> returnTypes = ((JsFunction) parent).getReturnTypes();
                String fqn = object.getFullyQualifiedName();
                for (TypeUsage returnType : returnTypes) {
                    if (returnType.getType().equals(fqn)) {
                        return false;
                    }
                }
            }
            Collection<? extends TypeUsage> returnTypes = ((JsFunction) object).getReturnTypes();
            if (returnTypes.size() == 1 && (returnTypes.iterator().next()).getType().equals("undefined")) {
                return true;
            }
        }
        return false;
    }

    private static String codeParameters(Collection<? extends JsObject> params) {
        StringBuilder result = new StringBuilder();
        for (Iterator<? extends JsObject> it = params.iterator(); it.hasNext();) {
            JsObject parametr = it.next();
            result.append(parametr.getName());
            result.append(":");
            for (Iterator<? extends TypeUsage> itType = parametr.getAssignmentForOffset(parametr.getOffset() + 1).iterator(); itType.hasNext();) {
                TypeUsage type = itType.next();
                result.append(type.getType());
                if (itType.hasNext()) {
                    result.append("|");
                }
            }
            if (it.hasNext()) {
                result.append(',');
            }
        }
        return result.toString();
    }

    private void storeUsages(JsObject object, String name, IndexDocument document) {
        StringBuilder sb = new StringBuilder();
        sb.append(object.getName());
        for (JsObject property : object.getProperties().values()) {
            if (storeUsage(property)) {
                sb.append(':');
                sb.append(property.getName()).append('#');
                if (property.getJSKind().isFunction()) {
                    sb.append('F');
                } else {
                    sb.append('P');
                }
            }
        }
        document.addPair(Index.FIELD_USAGE, sb.toString(), true, true);
        if (object instanceof JsFunction) {
            // store parameters
            for (JsObject parameter : ((JsFunction) object).getParameters()) {
                storeUsages(parameter, parameter.getName(), document);
            }
        }
        for (JsObject property : object.getProperties().values()) {
            if (storeUsage(property) && (!(property instanceof JsReference && !((JsReference)property).getOriginal().isAnonymous()))) {
                storeUsages(property, property.getName(), document);
            }
        }
    }
    
    private boolean storeUsage(JsObject object) {
        boolean result = true;
        if ("arguments".equals(object.getName()) || object.getJSKind() == JsElement.Kind.ANONYMOUS_OBJECT
                || object.getModifiers().contains(Modifier.PRIVATE)) {
            result = false;
        }
        return result;
    }
    
    public static final class Factory extends EmbeddingIndexerFactory {

        public static final String NAME = "js"; // NOI18N
        public static final int VERSION = 16;
        private static final int PRIORITY = 100;
        
        private static final ThreadLocal<Collection<Runnable>> postScanTasks = new ThreadLocal<Collection<Runnable>>();

        @Override
        public EmbeddingIndexer createIndexer(final Indexable indexable, final Snapshot snapshot) {
            if (isIndexable(indexable, snapshot)) {
                return new JsIndexer();
            } else {
                return null;
            }
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return VERSION;
        }

        private boolean isIndexable(Indexable indexable, Snapshot snapshot) {
            if ((snapshot.getText().length() > SanitizingParser.MAX_FILE_SIZE_TO_PARSE) && !SanitizingParser.PARSE_BIG_FILES) {
                return false;
            }
            return JsTokenId.JAVASCRIPT_MIME_TYPE.equals(snapshot.getMimeType());
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : deleted) {
                    is.removeDocuments(i);
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }

        @Override
        public void rootsRemoved(final Iterable<? extends URL> removedRoots) {
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : dirty) {
                    is.markDirtyDocuments(i);
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }

        @Override
        public boolean scanStarted(Context context) {
            postScanTasks.set(new LinkedList<Runnable>());
            return super.scanStarted(context);
        }

        @Override
        public void scanFinished(Context context) {
            try {
                for (Runnable task : postScanTasks.get()) {
                    task.run();
                }
            } finally {
                postScanTasks.remove();
                super.scanFinished(context);
            }
        }

        public static boolean isScannerThread() {
            return postScanTasks.get() != null;
        }

        public static void addPostScanTask(@NonNull final Runnable task) {
            Parameters.notNull("task", task);   //NOI18N
            final Collection<Runnable> tasks = postScanTasks.get();
            if (tasks == null) {
                throw new IllegalStateException("JsIndexer.postScanTask can be called only from scanner thread.");  //NOI18N
            }                        
            tasks.add(task);
        }

        @Override
        public int getPriority() {
            return PRIORITY;
        }
        
    } // End of Factory class
    
    @ServiceProvider(service = org.netbeans.modules.javascript2.model.spi.IndexChangeSupport.class)
    public static final class IndexChangeSupport implements org.netbeans.modules.javascript2.model.spi.IndexChangeSupport {

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        public void fireChange() {
            changeSupport.fireChange();
        }
    }
    
    @ServiceProvider(service = org.netbeans.modules.javascript2.editor.spi.PostScanProvider.class)
    public static final class PostScanProvider implements org.netbeans.modules.javascript2.editor.spi.PostScanProvider {

        @Override
        public void addPostScanTask(Runnable task) {
            Factory.addPostScanTask(task);
        }
    }
}
