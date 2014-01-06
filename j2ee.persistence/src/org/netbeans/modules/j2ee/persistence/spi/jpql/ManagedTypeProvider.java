/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.j2ee.persistence.spi.jpql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.util.Elements;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeRepository;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.EntityMappings;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.EntityMappingsMetadata;

/**
 *
 * @author sp153251
 */
public class ManagedTypeProvider implements IManagedTypeProvider {

    private final Project project;
    private Map<String, IManagedType> managedTypes;
    private Map<String, IEntity> entities;
    private Map<String, IMappedSuperclass> mSuperclasses;
    private Map<String, IEmbeddable> embeddables;
    private ITypeRepository typeRepository;
    private final EntityMappings mappings;
    private boolean valid = true;//used to conrol long tasks, if not valid long tasks should be either terminated or goes short way
    private final Elements elements;

    public ManagedTypeProvider(Project project, EntityMappingsMetadata metaData, Elements elements) {
        this.project = project;
        this.mappings = metaData.getRoot();
        this.elements = elements;
    }
    
    public ManagedTypeProvider(Project project, EntityMappings mappings, Elements elements) {
        this.project = project;
        this.mappings = mappings;
        this.elements = elements;
    }
    
//    @Override
//    public Iterable<IEntity> abstractSchemaTypes() {
//        initializeManagedTypes();
//        Collection<IEntity> abstractSchemaTypes;
//        ManagedTypeVisitor visitor = new ManagedTypeVisitor();
//        for (IManagedType managedType : managedTypes.values()) {
//            managedType.accept(visitor);
//        }
//        abstractSchemaTypes = visitor.getEntities();
//        return Collections.unmodifiableCollection(abstractSchemaTypes);
//    }

    @Override
    public IManagedType getManagedType(IType itype) {
        initializeManagedTypes();
        for (IManagedType mt : managedTypes.values()) {
            if (isValid() && mt.getType() != null && mt.getType().equals(itype)) {//have sense to return only properly resolved types
                return mt;
            }
        }
        return null;
    }

    @Override
    public IManagedType getManagedType(String name) {
        initializeManagedTypes();
        return managedTypes.get(name);
    }

    @Override
    public ITypeRepository getTypeRepository() {
        if (typeRepository == null) {
            typeRepository = new TypeRepository(project, this, elements);
        }
        return typeRepository;
    }

    @Override
    public Iterable<IManagedType> managedTypes() {
        initializeManagedTypes();
        return Collections.unmodifiableCollection(managedTypes.values());
    }
    
    public boolean isValid() {
        return valid;
    }
    
    /**
     * make model invalid and it shoul case processing to stop, minimize etc.
     * results with SPI may not be consider valid if provider isn't valid
     */
    public void invalidate() {
        valid = false;
        //TODO: may have sense to clean stored data
        if(typeRepository != null) {
            ((TypeRepository)typeRepository).invalidate();
            typeRepository = null;
        }
    }

    @Override
    public Iterable<IEntity> entities() {
        initializeManagedTypes();
        return entities.values();
    }

    @Override
    public IEmbeddable getEmbeddable(IType itype) {
        initializeManagedTypes();
        return getEmbeddable(itype.getName());
    }

    @Override
    public IEmbeddable getEmbeddable(String tName) {
        initializeManagedTypes();
        if(tName == null) {
            return null;
        }
        int lst = tName.lastIndexOf('.');
        String supposeName = lst>-1 ? tName.substring(lst+1) : tName;
        IEmbeddable ret = embeddables.get(supposeName);
        if(ret == null) {
            //name may not match short class name
            for(IEmbeddable ent:embeddables.values()) {
                if(tName.equals(ent.getType().getName())) {
                    ret = ent;
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public IEntity getEntity(IType itype) {
        initializeManagedTypes();
        return getEntity(itype.getName());
    }

    @Override
    public IEntity getEntity(String tName) {
        initializeManagedTypes();
        if(tName == null) {
            return null;
        }
        int lst = tName.lastIndexOf('.');
        String supposeName = lst>-1 ? tName.substring(lst+1) : tName;
        IEntity ret = entities.get(supposeName);
        if(ret == null) {
            //name may not match short class name
            for(IEntity ent:entities.values()) {
                if(tName.equals(ent.getType().getName())) {
                    ret = ent;
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public IEntity getEntityNamed(String name) {
        initializeManagedTypes();
        return entities.get(name);
    }

    @Override
    public IMappedSuperclass getMappedSuperclass(IType itype) {
        initializeManagedTypes();
        return getMappedSuperclass(itype.getName());
    }

    @Override
    public IMappedSuperclass getMappedSuperclass(String tName) {
        initializeManagedTypes();
        if(tName == null) {
            return null;
        }
        int lst = tName.lastIndexOf('.');
        String supposeName = lst>-1 ? tName.substring(lst+1) : tName;
        IMappedSuperclass ret = mSuperclasses.get(supposeName);
        if(ret == null) {
            //name may not match short class name
            for(IMappedSuperclass ent:mSuperclasses.values()) {
                if(tName.equals(ent.getType().getName())) {
                    ret = ent;
                    break;
                }
            }
        }
        return ret;
    }

    private void initializeManagedTypes() {
        if (managedTypes == null) {
            entities = new HashMap<String, IEntity>();
            embeddables = new HashMap<String, IEmbeddable>();
            mSuperclasses = new HashMap<String, IMappedSuperclass>();
            managedTypes = new HashMap<String, IManagedType>();
            for (org.netbeans.modules.j2ee.persistence.api.metadata.orm.Entity persistentType : mappings.getEntity()) {

                if (persistentType != null) {
                    String name = persistentType.getName();

                    if (managedTypes.containsKey(name)) {
                        continue;
                    }
                    IEntity ent = new Entity(persistentType, this);
                    managedTypes.put(name, ent);
                    entities.put(name, ent);
                }
            }
            for (org.netbeans.modules.j2ee.persistence.api.metadata.orm.Embeddable persistentType : mappings.getEmbeddable()) {

                if (persistentType != null) {
                    String name = persistentType.getClass2();

                    if (managedTypes.containsKey(name)) {
                        continue;
                    }
                    IEmbeddable emb = new Embeddable(persistentType, this);
                    managedTypes.put(name, emb);
                    embeddables.put(name, emb);
                }
            }
            for (org.netbeans.modules.j2ee.persistence.api.metadata.orm.MappedSuperclass persistentType : mappings.getMappedSuperclass()) {

                if (persistentType != null) {
                    String name = persistentType.getClass2();

                    if (managedTypes.containsKey(name)) {
                        continue;
                    }
                    IMappedSuperclass msc = new MappedSuperclass(persistentType, this);
                    managedTypes.put(name, msc);
                    mSuperclasses.put(name, msc);
                }
            }
        }
    }
}
