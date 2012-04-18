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
package org.netbeans.modules.html.parser;

import java.util.*;
import org.netbeans.modules.html.editor.lib.api.ProblemDescription;
import org.netbeans.modules.html.editor.lib.api.elements.*;
import org.netbeans.modules.web.common.api.LexerUtils;
import org.openide.util.CharSequences;

/**
 *
 * @author marekfukala
 */
public class ElementsFactory {

    private CharSequence source;

    ElementsFactory(CharSequence source) {
        this.source = source;
    }

    ModifiableCloseTag createCloseTag(int startOffset, int endOffset, byte nameLen) {
        return new CommonCloseTag(source, startOffset, endOffset, nameLen);
    }

    ModifiableOpenTag createOpenTag(int startOffset, int endOffset, byte nameLen) {
        return new CommonOpenTag(source, startOffset, endOffset, nameLen);
    }

    ModifiableOpenTag createVirtualOpenTag(CharSequence name) {
        return new VirtualOpenTag(name);
    }

    ModifiableOpenTag createEmptyOpenTag(int startOffset, int endOffset, byte nameLen) {
        return new EmptyOpenTag(source, startOffset, endOffset, nameLen);
    }

    Root createRoot() {
        return new Root(source, null);
    }

    CommonAttribute createAttribute(int nameOffset, int valueOffset, short nameLen, int valueLen) {
        return new CommonAttribute(source, nameOffset, valueOffset, nameLen, valueLen);
    }

    CommonAttribute createAttribute(int nameOffset, short nameLen) {
        return new CommonAttribute(source, nameOffset, nameLen);
    }

    static interface ModifiableElement extends Element {

        public void detachFromParent();
        
        public void setEndOffset(int endOffset);
        
        public void setParent(Node parent);
    }
    
    static interface ModifiableCloseTag extends ModifiableElement, CloseTag {
        
        public void setMatchingOpenTag(OpenTag openTag);
        
    }

    static interface ModifiableOpenTag extends ModifiableElement, OpenTag {

        public void addChild(Element element);
        
        public void insertChildBefore(Element toInsert, Element element);

        public void addChildren(Collection<Element> element);

        public void removeChildren(Collection<Element> children);

        public void removeChild(Element element);

        public void setSemanticEndOffset(int endOffset);
        
        public void setMatchingCloseTag(CloseTag closeTag);

        public void setAttribute(Attribute attribute);
    }

    static abstract class TagElement implements Named, ModifiableElement {

        //32-64
        private CharSequence source;
        //32
        //element start
        private int startOffset;
        //16
        //element end - element start. Should fit into 2^16
        private short endOffsetToStartOffsetDiff;
        //8
        //lenght of the name of the element
        private byte nameLen;
        //32-64
        private Node parent;

        //sum (32bit JVM): 120bits=15bytes
        public TagElement(CharSequence source, int startOffset, int endOffset, byte nameLen) {
            assert nameLen >= 0;
            
            this.source = source;
            this.startOffset = startOffset;
            setEndOffset(endOffset);
            setNameLen(nameLen);
        }

        @Override
        public void setEndOffset(int endOffset) {
            this.endOffsetToStartOffsetDiff = (short) (endOffset - startOffset);
        }

        protected void setNameLen(byte nameLen) {
            assert nameLen >= 0;
            this.nameLen = nameLen;
        }

        //1 for open tag (< between startOffset and the name), 2 for end tag (</ ...)
        protected abstract int nameOffsetToStartOffsetDiff();

        private int nameOffset() {
            return startOffset + nameOffsetToStartOffsetDiff();
        }

        @Override
        public CharSequence name() {
            return source.subSequence(nameOffset(), nameOffset() + nameLen);
        }

        @Override
        public CharSequence namespacePrefix() {
            int colonIndex = CharSequences.indexOf(name(), ":");
            return colonIndex == -1 ? null : name().subSequence(0, colonIndex);
        }

        @Override
        public CharSequence unqualifiedName() {
            int colonIndex = CharSequences.indexOf(name(), ":");
            return colonIndex == -1 ? name() : name().subSequence(colonIndex + 1, name().length());
        }

        @Override
        public int from() {
            return startOffset;
        }

        @Override
        public int to() {
            return startOffset + endOffsetToStartOffsetDiff;
        }

        @Override
        public CharSequence image() {
            return source.subSequence(from(), to());
        }

        @Override
        public CharSequence id() {
            return name();
        }

        @Override
        public Collection<ProblemDescription> problems() {
            return Collections.emptyList();
        }

        @Override
        public void setParent(Node parent) {
            this.parent = parent;
        }

        @Override
        public Node parent() {
            return parent;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(name()).append("(").append(type().name()).append(")").append("; ").append(from()).append("-").append(to()).toString();
        }

        @Override
        public void detachFromParent() {
//            ModifiableOpenTag mot = (ModifiableOpenTag) parent();
//            mot.removeChild(this);
            setParent(null);
        }
    }

    static class EmptyOpenTag extends TagElement implements OpenTag, ModifiableOpenTag {

        private Collection<Attribute> attrs;

        public EmptyOpenTag(CharSequence source, int startOffset, int endOffset, byte nameLen) {
            super(source, startOffset, endOffset, nameLen);
        }

        void addAttribute(Attribute attr) {
            if(attrs == null) {
                attrs = new ArrayList<Attribute>();
            }
            attrs.add(attr);
        }

        void addAttributes(Collection<Attribute> attributes) {
            if(attrs == null) {
                attrs = new ArrayList<Attribute>();
            }
            attrs.addAll(attributes);
        }

        @Override
        public Collection<Attribute> attributes() {
            return attrs == null ? Collections.<Attribute>emptyList() : attrs;
        }

        @Override
        public Collection<Attribute> attributes(AttributeFilter filter) {
            Collection<Attribute> filtered = new ArrayList<Attribute>(1);
            for (Attribute a : attributes()) {
                if (filter.accepts(a)) {
                    filtered.add(a);
                }
            }
            return filtered;
        }

        @Override
        public Attribute getAttribute(String name) {
            //typically very low number of attrs so the linear search doesn't hurt
            for (Attribute a : attributes()) {
                if (LexerUtils.equals(name, a.name(), true, false)) {
                    return a;
                }
            }
            return null;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public CloseTag matchingCloseTag() {
            return null;
        }

        @Override
        public int semanticEnd() {
            return to();
        }

        @Override
        public Collection<Element> children() {
            return Collections.emptyList();
        }

        @Override
        public Collection<Element> children(ElementType type) {
            return Collections.emptyList();
        }

        @Override
        public Collection<Element> children(ElementFilter filter) {
            return Collections.emptyList();
        }

        @Override
        public <T extends Element> Collection<T> children(Class<T> type) {
            return Collections.emptyList();
        }

        @Override
        public ElementType type() {
            return ElementType.OPEN_TAG;
        }

        @Override
        protected int nameOffsetToStartOffsetDiff() {
            return 1; //"<".length();
        }

        @Override
        public void addChild(Element element) {
            //no-op
//            throw new IllegalStateException();
        }

        @Override
        public void removeChild(Element element) {
            //no-op
//            throw new IllegalStateException();
        }

        @Override
        public void setSemanticEndOffset(int endOffset) {
            //no-op
        }

        @Override
        public void setAttribute(Attribute attribute) {
            addAttribute(attribute);
        }

        @Override
        public void removeChildren(Collection<Element> children) {
            //no-op
//            throw new IllegalStateException();
        }

        @Override
        public void addChildren(Collection<Element> element) {
            //no-op
//            throw new IllegalStateException();
        }

        @Override
        public void insertChildBefore(Element toInsert, Element element) {
            //no-op
//            throw new IllegalStateException();
        }

        @Override
        public void setMatchingCloseTag(CloseTag closeTag) {
            //no-op
//            throw new IllegalStateException();
        }
    }

    static class CommonOpenTag extends EmptyOpenTag {

        private List<Element> children;
        private CloseTag matchingEndTag;
        private int logicalEndOffset;

        public CommonOpenTag(CharSequence source, int startOffset, int endOffset, byte nameLen) {
            super(source, startOffset, endOffset, nameLen);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void setMatchingCloseTag(CloseTag endTag) {
            this.matchingEndTag = endTag;
        }

        @Override
        public CloseTag matchingCloseTag() {
            return matchingEndTag;
        }

        @Override
        public void addChild(Element child) {
            if (children == null) {
                children = new ArrayList<Element>(1);
            }
            children.add(child);
            ((ModifiableElement)child).setParent(this);
        }

        @Override
        public void addChildren(Collection<Element> elements) {
            for(Element e : new LinkedList<Element>(elements)) {
                addChild(e);
            }
        }

        @Override
        public void removeChild(Element element) {
            if (children == null) {
                return;
            }
            children.remove(element);
            if (children.isEmpty()) {
                children = null;
            }
            
            ((ModifiableElement)element).setParent(null);
        }

        @Override
        public void removeChildren(Collection<Element> toRemove) {
            if(children == null) {
                return ;
            }
            Iterator<Element> childrenIterator = toRemove.iterator();
            while(childrenIterator.hasNext()) {
                Element child = childrenIterator.next();
                ((ModifiableElement)child).setParent(null);
                childrenIterator.remove();
            }
            if(children().isEmpty()) {
                children = null;
            }
        }

        @Override
        public void insertChildBefore(Element toInsert, Element element) {
            int index = children.indexOf(element);
            if(index == -1) {
                return ;
            }
            children.add(index, toInsert);
            ((ModifiableElement)toInsert).setParent(this);
        }

        @Override
        public Collection<Element> children() {
            return children == null ? Collections.<Element>emptyList() : children;
        }

        @Override
        public Collection<Element> children(ElementType type) {
            Collection<Element> filtered = new ArrayList<Element>();
            for (Element e : children()) {
                if (e.type() == type) {
                    filtered.add(e);
                }
            }
            return filtered;
        }

        @Override
        public Collection<Element> children(ElementFilter filter) {
            Collection<Element> filtered = new ArrayList<Element>();
            for (Element e : children()) {
                if (filter.accepts(e)) {
                    filtered.add(e);
                }
            }
            return filtered;
        }

        @Override
        public <T extends Element> Collection<T> children(Class<T> type) {
            Collection<T> filtered = new ArrayList<T>();
            for (Element child : children()) {
                if (type.isAssignableFrom(child.getClass())) {
                    filtered.add(type.cast(child));
                }
            }
            return filtered;
        }

        @Override
        public void setSemanticEndOffset(int endOffset) {
            this.logicalEndOffset = endOffset;
        }

        @Override
        public int semanticEnd() {
            return logicalEndOffset;
        }
    }

    static class CommonCloseTag extends TagElement implements ModifiableCloseTag {

        private OpenTag matchingOpenTag;

        public CommonCloseTag(CharSequence source, int startOffset, int endOffset, byte nameLen) {
            super(source, startOffset, endOffset, nameLen);
        }

        @Override
        public ElementType type() {
            return ElementType.CLOSE_TAG;
        }

        @Override
        public OpenTag matchingOpenTag() {
            return matchingOpenTag;
        }

        @Override
        public void setMatchingOpenTag(OpenTag openTag) {
            this.matchingOpenTag = openTag;
        }

        @Override
        protected int nameOffsetToStartOffsetDiff() {
            return 2; //"</".length();
        }
    }

    static class VirtualOpenTag implements OpenTag, ModifiableOpenTag {

        private Collection<Attribute> attrs;
        private CharSequence name;
        private List<Element> children;
        private CloseTag matchingCloseTag;
        private int logicalEndOffset;
        private Node parent;

        public VirtualOpenTag(CharSequence name) {
            this.name = name;
        }

        void addAttribute(Attribute attr) {
            if(attrs == null) {
                attrs = new ArrayList<Attribute>(1);
            }
            attrs.add(attr);
        }

        void addAttributes(Collection<Attribute> attributes) {
            if(attrs == null) {
                attrs = new ArrayList<Attribute>(1);
            }
            attrs.addAll(attributes);
        }

        @Override
        public Collection<Attribute> attributes() {
            return attrs == null ? Collections.<Attribute>emptyList() : attrs;
        }

        @Override
        public Collection<Attribute> attributes(AttributeFilter filter) {
            Collection<Attribute> filtered = new ArrayList<Attribute>(1);
            for (Attribute a : attributes()) {
                if (filter.accepts(a)) {
                    filtered.add(a);
                }
            }
            return filtered;
        }

        @Override
        public Attribute getAttribute(String name) {
            //typically very low number of attrs so the linear search doesn't hurt
            for (Attribute a : attributes()) {
                if (LexerUtils.equals(name, a.name(), true, false)) {
                    return a;
                }
            }
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public CloseTag matchingCloseTag() {
            return matchingCloseTag;
        }

        @Override
        public int semanticEnd() {
            return logicalEndOffset;
        }

        @Override
        public CharSequence name() {
            return name;
        }

        @Override
        public CharSequence namespacePrefix() {
            int colonIndex = CharSequences.indexOf(name(), ":");
            return colonIndex == -1 ? null : name().subSequence(0, colonIndex);
        }

        @Override
        public CharSequence unqualifiedName() {
            int colonIndex = CharSequences.indexOf(name(), ":");
            return colonIndex == -1 ? name() : name().subSequence(colonIndex + 1, name().length());
        }

        @Override
        public int from() {
            return -1;
        }

        @Override
        public int to() {
            return -1;
        }

        @Override
        public ElementType type() {
            return ElementType.OPEN_TAG;
        }

        @Override
        public CharSequence image() {
            return null;
        }

        @Override
        public CharSequence id() {
            return name();
        }

        @Override
        public Collection<ProblemDescription> problems() {
            return Collections.emptyList();
        }

        @Override
        public Node parent() {
            return parent;
        }

        @Override
        public Collection<Element> children() {
            return children == null ? Collections.<Element>emptyList() : children;
        }

        @Override
        public Collection<Element> children(ElementType type) {
            Collection<Element> filtered = new ArrayList<Element>();
            for (Element e : children()) {
                if (e.type() == type) {
                    filtered.add(e);
                }
            }
            return filtered;
        }

        @Override
        public Collection<Element> children(ElementFilter filter) {
            Collection<Element> filtered = new ArrayList<Element>();
            for (Element e : children()) {
                if (filter.accepts(e)) {
                    filtered.add(e);
                }
            }
            return filtered;
        }

        @Override
        public <T extends Element> Collection<T> children(Class<T> type) {
            Collection<T> filtered = new ArrayList<T>();
            for (Element child : children()) {
                if (type.isAssignableFrom(child.getClass())) {
                    filtered.add(type.cast(child));
                }
            }
            return filtered;
        }
        
        @Override
        public void insertChildBefore(Element toInsert, Element element) {
            if(children == null) {
                children = new ArrayList<Element>();
            }
            int index = children.indexOf(element);
            if(index == -1) {
                return ;
            }
            children.add(index, toInsert);
            ((ModifiableElement)toInsert).setParent(this);
        }

        @Override
        public void setParent(Node parent) {
            this.parent = parent;
        }

       @Override
        public void addChild(Element child) {
            if (children == null) {
                children = new ArrayList<Element>(1);
            }
            children.add(child);
            ((ModifiableElement)child).setParent(this);
        }

        @Override
        public void addChildren(Collection<Element> elements) {
            for(Element e : new LinkedList<Element>(elements)) {
                addChild(e);
            }
        }

        @Override
        public void removeChild(Element element) {
            if (children == null) {
                return;
            }
            children.remove(element);
            if (children.isEmpty()) {
                children = null;
            }
            
            ((ModifiableElement)element).setParent(null);
        }

        @Override
        public void removeChildren(Collection<Element> toRemove) {
            if(children == null) {
                return ;
            }
            Iterator<Element> childrenIterator = toRemove.iterator();
            while(childrenIterator.hasNext()) {
                Element child = childrenIterator.next();
                ((ModifiableElement)child).setParent(null);
                childrenIterator.remove();
            }
            if(children().isEmpty()) {
                children = null;
            }
        }

        @Override
        public void setEndOffset(int endOffset) {
            //no-op
        }

        @Override
        public void detachFromParent() {
            ModifiableOpenTag mot = (ModifiableOpenTag) parent();
            mot.removeChild(this);
            setParent(null);
        }

        @Override
        public void setSemanticEndOffset(int endOffset) {
            this.logicalEndOffset = endOffset;
        }

        @Override
        public void setAttribute(Attribute attribute) {
            addAttribute(attribute);
        }

        @Override
        public void setMatchingCloseTag(CloseTag closeTag) {
            this.matchingCloseTag = closeTag;
        }
        
        @Override
        public String toString() {
            return new StringBuilder().append(name()).append("(").append(type().name()).append(")").append("; (virtual)").toString();
        }

    }

    //it would be better design/memory usage not to reuse commonopentag, but since there's just
    //one instance some of the spare fields are acceptable
    static class Root extends CommonOpenTag implements FeaturedNode {

        private static final String ROOT = "root"; //NOI18N
        private String namespace;

        public Root(CharSequence source, String namespace) {
            super(source, 0, source.length(), (byte) 0);
            this.namespace = namespace;
        }

        @Override
        public CharSequence name() {
            return ROOT;
        }

        @Override
        public ElementType type() {
            return ElementType.ROOT;
        }

        @Override
        public Object getProperty(String propertyName) {
            if (propertyName.equalsIgnoreCase("namespace")) { //NOI18N
                return namespace;
            }

            return null;
        }
    }

    static class CommonAttribute implements Attribute {

        private CharSequence source;
        private int nameOffset;
        private short valueOffset2nameOffsetDiff;
        private short nameLen;
        private int valueLen;

        public CommonAttribute(CharSequence source, int nameOffset, short nameLen) {
            this.source = source;

            this.nameOffset = nameOffset;
            this.valueOffset2nameOffsetDiff = -1;

            this.nameLen = nameLen;
            this.valueLen = -1;
        }

        public CommonAttribute(CharSequence source, int nameOffset, int valueOffset, short nameLen, int valueLen) {
            this.source = source;

            this.nameOffset = nameOffset;
            this.valueOffset2nameOffsetDiff = (short) (valueOffset - nameOffset);

            this.nameLen = nameLen;
            this.valueLen = valueLen;
        }

        @Override
        public int nameOffset() {
            return nameOffset;
        }

        @Override
        public CharSequence name() {
            return source.subSequence(nameOffset, nameOffset + nameLen);
        }

        @Override
        public int valueOffset() {
            return valueOffset2nameOffsetDiff == -1 ? -1 : nameOffset + valueOffset2nameOffsetDiff;
        }

        @Override
        public CharSequence value() {
            return valueLen == -1 ? null : source.subSequence(valueOffset(), valueOffset() + valueLen);
        }

        @Override
        public boolean isValueQuoted() {
            if(value() == null) {
                return false;
            }
            if (valueLen < 2) {
                return false;
            } else {
                CharSequence value = value();
                return ((value.charAt(0) == '\'' || value.charAt(0) == '"')
                        && (value.charAt(value.length() - 1) == '\'' || value.charAt(value.length() - 1) == '"'));
            }
        }

        @Override
        public CharSequence unquotedValue() {
            if(value() == null) {
                return null;
            }
            return isValueQuoted() ? value().subSequence(1, value().length() - 1) : value();
        }

        @Override
        public CharSequence namespacePrefix() {
            int colonIndex = CharSequences.indexOf(name(), ":");
            return colonIndex == -1 ? null : name().subSequence(0, colonIndex);
        }

        @Override
        public CharSequence unqualifiedName() {
            int colonIndex = CharSequences.indexOf(name(), ":");
            return colonIndex == -1 ? name() : name().subSequence(colonIndex + 1, name().length());
        }

        @Override
        public int from() {
            return nameOffset();
        }

        @Override
        public int to() {
            return value() != null
                    ? valueOffset() + valueLen
                    : nameOffset() + nameLen;
        }

        @Override
        public ElementType type() {
            return ElementType.ATTRIBUTE;
        }

        @Override
        public CharSequence image() {
            return source.subSequence(from(), to());
        }

        @Override
        public CharSequence id() {
            return type().name();
        }

        @Override
        public Collection<ProblemDescription> problems() {
            return Collections.emptyList();
        }

        @Override
        public Node parent() {
            return null;
        }
    }
}
