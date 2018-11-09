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
package org.apache.syncope.core.persistence.api.dao;

import java.util.List;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyUtils;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;

public interface PGAnyDAO {

    <A extends Any<?>> List<A> findByDerAttrValue(
            String table, AnyUtils anyUtils, String schemaKey, String value, boolean ignoreCaseMatch);

    <A extends Any<?>> A findByPlainAttrUniqueValue(
            String table, AnyUtils anyUtils, String schemaKey, PlainAttrValue attrUniqueValue, boolean ignoreCaseMatch);

    <A extends Any<?>> List<A> findByPlainAttrValue(
            String table, AnyUtils anyUtils, String schemaKey, PlainAttrValue attrValue, boolean ignoreCaseMatch);

    <A extends Any<?>> void checkBeforeSave(String table, AnyUtils anyUtils, A any);
}
