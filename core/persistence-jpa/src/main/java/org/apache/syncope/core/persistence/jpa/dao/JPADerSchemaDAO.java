/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.jpa.dao;

import java.util.List;
import javax.persistence.TypedQuery;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.core.persistence.api.dao.DerAttrDAO;
import org.apache.syncope.core.persistence.api.dao.DerSchemaDAO;
import org.apache.syncope.core.persistence.api.dao.ExternalResourceDAO;
import org.apache.syncope.core.persistence.api.entity.AnyUtils;
import org.apache.syncope.core.persistence.api.entity.AnyUtilsFactory;
import org.apache.syncope.core.persistence.api.entity.DerAttr;
import org.apache.syncope.core.persistence.api.entity.DerSchema;
import org.apache.syncope.core.persistence.jpa.entity.JPAAnyUtilsFactory;
import org.apache.syncope.core.persistence.jpa.entity.JPADerSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JPADerSchemaDAO extends AbstractDAO<DerSchema, String> implements DerSchemaDAO {

    @Autowired
    private DerAttrDAO derAttrDAO;

    @Autowired
    private ExternalResourceDAO resourceDAO;

    @Override
    public DerSchema find(final String key) {
        return entityManager.find(JPADerSchema.class, key);
    }

    @Override
    public List<DerSchema> findAll() {
        TypedQuery<DerSchema> query = entityManager.createQuery(
                "SELECT e FROM " + JPADerSchema.class.getSimpleName() + " e", DerSchema.class);
        return query.getResultList();
    }

    @Override
    public <T extends DerAttr<?>> List<T> findAttrs(final DerSchema schema, final Class<T> reference) {
        final StringBuilder queryString = new StringBuilder("SELECT e FROM ").
                append(((JPADerAttrDAO) derAttrDAO).getJPAEntityReference(reference).getSimpleName()).
                append(" e WHERE e.schema=:schema");

        TypedQuery<T> query = entityManager.createQuery(queryString.toString(), reference);
        query.setParameter("schema", schema);

        return query.getResultList();
    }

    @Override
    public DerSchema save(final DerSchema derSchema) {
        return entityManager.merge(derSchema);
    }

    @Override
    public void delete(final String key) {
        final DerSchema schema = find(key);
        if (schema == null) {
            return;
        }

        AnyUtilsFactory anyUtilsFactory = new JPAAnyUtilsFactory();
        for (AnyTypeKind anyTypeKind : AnyTypeKind.values()) {
            AnyUtils anyUtils = anyUtilsFactory.getInstance(anyTypeKind);

            for (DerAttr<?> attr : findAttrs(schema, anyUtils.derAttrClass())) {
                derAttrDAO.delete(attr.getKey(), anyUtils.derAttrClass());
            }

            resourceDAO.deleteMapping(key, anyUtils.derIntMappingType());
        }

        entityManager.remove(schema);
    }
}
