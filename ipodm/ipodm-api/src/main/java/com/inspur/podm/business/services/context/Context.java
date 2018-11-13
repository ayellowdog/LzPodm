/*
 * Copyright (c) 2015-2018 inspur Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inspur.podm.business.services.context;

import com.inspur.podm.business.services.redfish.odataid.ODataIdFromContextHelper;
import com.inspur.podm.common.types.Id;
import com.inspur.podm.common.types.redfish.NavigationProperty;
import com.inspur.podm.common.types.redfish.OdataIdProvider;

import java.util.Objects;

import static com.inspur.podm.common.utils.Contracts.requires;
import static com.inspur.podm.common.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Objects.hash;

/**
 * Represents path to Entity located in Entity Tree.
 */
public final class Context implements Comparable<Context>, NavigationProperty, OdataIdProvider {
    private final Id id;
    private final ContextType type;
    private final Context parent;

    private Context(Id id, ContextType type, Context parent) {
        requiresNonNull(id, "id");
        requiresNonNull(type, "type");

        this.id = id;
        this.type = type;
        this.parent = parent;
    }

    public static Context contextOf(Id entityId, ContextType type) {
        return new Context(entityId, type, null);
    }

    public Id getId() {
        return id;
    }

    public ContextType getType() {
        return type;
    }

    public Context getParent() {
        return parent;
    }

    public Context child(Id id, ContextType type) {
        requiresNonNull(id, "id");
        requiresNonNull(type, "type");
        requires(type.getPossibleParents().contains(this.type), "type cannot be child");

        return new Context(id, type, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Context)) {
            return false;
        }

        Context rhs = (Context) o;
        return Objects.equals(id, rhs.id)
            && Objects.equals(type, rhs.type)
            && Objects.equals(parent, rhs.parent);
    }

    @Override
    public int hashCode() {
        return hash(id, type);
    }

    @Override
    public String toString() {
        return parent == null
            ? format("%s(%s)", type, id)
            : format("%s-%s(%s)", parent, type, id);
    }

    public static boolean isAcceptableChildOf(ContextType childType, Context parent) {
        requiresNonNull(childType, "childType");

        if (parent == null) {
            return childType.getPossibleParents().isEmpty();
        }

        return childType.getPossibleParents().contains(parent.getType());
    }

    public static boolean hasParentOfTypeOnTopOf(Context context, ContextType possibleParentType) {
        requiresNonNull(context, "context");

        if (context.getParent() == null) {
            return possibleParentType == null;
        }

        if (context.getParent().getType() == possibleParentType) {
            return true;
        }

        return hasParentOfTypeOnTopOf(context.getParent(), possibleParentType);
    }

    @Override
    public ODataId asOdataId() {
        return ODataIdFromContextHelper.asOdataId(this);
    }

    @Override
    public int compareTo(Context other) {
        return comparing(Context::getType).thenComparing(Context::getId).compare(this, other);
    }
}
