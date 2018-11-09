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
package org.apache.syncope.core.persistence.jpa.entity.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.syncope.core.persistence.api.entity.PlainAttr;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.group.GPlainAttr;
import org.apache.syncope.core.persistence.api.entity.group.GPlainAttrUniqueValue;
import org.apache.syncope.core.persistence.jpa.entity.AbstractPlainAttrValue;

@JsonIgnoreProperties({ "valueAsString", "value" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PGGPlainAttrUniqueValue extends AbstractPlainAttrValue implements GPlainAttrUniqueValue {

    private static final long serialVersionUID = -4326417972859745823L;

    @JsonIgnore
    private PGGPlainAttr attr;

    @Override
    public GPlainAttr getAttr() {
        return attr;
    }

    @Override
    public void setAttr(final PlainAttr<?> attr) {
        checkType(attr, PGGPlainAttr.class);
        this.attr = (PGGPlainAttr) attr;
    }

    @JsonIgnore
    @Override
    public PlainSchema getSchema() {
        return getAttr() == null ? null : getAttr().getSchema();
    }

    @Override
    public void setSchema(final PlainSchema schema) {
        // nothing to do
    }
}
