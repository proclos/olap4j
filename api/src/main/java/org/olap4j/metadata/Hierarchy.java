/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.metadata;

import org.olap4j.OlapException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * An organization of the set of {@link Member}s in a {@link Dimension} and
 * their positions relative to one another.
 *
 * <p>A Hierarchy is a collection of {@link Level}s, each of which is a
 * category of similar {@link Member}s.</p>
 *
 * <p>A Dimension must have at least one Hierarchy, and may have more than one,
 * but most have exactly one Hierarchy.</p>
 *
 * @author jhyde
 * @since Aug 23, 2006
 */
public interface Hierarchy extends MetadataElement {
    /**
     * Returns the {@link Dimension} this <code>Hierarchy</code> belongs to.
     *
     * @return dimension this hierarchy belongs to
     */
    Dimension getDimension();

    /**
     * Returns a list of the {@link Level} objects in this
     * <code>Hierarchy</code>.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getLevels
     *
     * @return list of levels
     */
    NamedList<Level> getLevels();

    /**
     * Returns whether this <code>Hierarchy</code> has an 'all' member.
     *
     * @return whether this hierarchy has an 'all' member
     */
    boolean hasAll();

    /**
     * Returns the default {@link Member} of this <code>Hierarchy</code>.
     *
     * <p>If the hierarchy has an 'all' member, this member is often the
     * default.
     *
     * @return the default member of this hierarchy
     *
     * @see #getAllMember()
     * @see #getRootMembers()
     */
    Member getDefaultMember() throws OlapException;

    /**
     * Returns the member at the highest level of the rollup.
     *
     * <p>If the hierarchy has no 'all' member (see {@link #hasAll()}),
     * returns {@code null}.
     *
     * @return the member at the highest level of the rollup, or null
     *
     * @see #hasAll()
     * @see #getDefaultMember()
     * @see #getRootMembers()
     *
     * @since olap4j 2.0
     */
    Member getAllMember() throws OlapException;

    /**
     * Returns the root member or members of this <code>Hierarchy</code>.
     *
     * <p>If the dimension has an 'all' member, then this will be the sole
     * root member.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * <p>The result is similar to that returned by
     * <code>getLevels().get(0).getMembers()</code>; the contents will be the
     * same, but this method returns a {@link NamedList} rather than a
     * mere {@link java.util.List} because the members of the root level are
     * known to have unique names.
     *
     * @return root members of this hierarchy
     *
     * @throws OlapException on database error
     */
    NamedList<Member> getRootMembers() throws OlapException;

    /**
     * Returns whether the write-back to dimension column is enabled.
     *
     * @return whether the write-back to dimension column is enabled
     *
     * @since olap4j 2.0
     */
    boolean isReadWrite();

    /**
     * Returns the ordinal number of this {@code Hierarchy} across all
     * hierarchies of the {@link Cube}.
     *
     * @return ordinal within cube
     *
     * @since olap4j 2.0
     */
    int getOrdinal();

    /**
     * Returns the structure of this {@code Hierarchy}.
     *
     * @return hierarchy structure, never null
     *
     * @since olap4j 2.0
     */
    Structure getStructure();

    /**
     * Returns the structure type of this {@code Hierarchy}.
     *
     * @return hierarchy structure type, never null
     *
     * @since olap4j 2.0
     */
    StructureType getStructureType();

    /**
     * Returns the number of members in this {@code Hierarchy}. This value can
     * be an approximation of the real cardinality.
     *
     * @return number of members
     *
     * @since olap4j 2.0
     */
    int getCardinality();

    /**
     * Returns the path to be used when displaying this {@code Hierarchy} in a
     * user interface. Folder names are be separated by a semicolon (;). Nested
     * folders are indicated by a backslash (\).
     *
     * @return folder path
     *
     * @since olap4j 2.0
     */
    String getDisplayFolder();

    /**
     * Returns a hint to the client application on how to show this
     * {@code Hierarchy}.
     *
     * @return how to show this hierarchy; never null
     *
     * @since olap4j 2.0
     */
    InstanceSelection getInstanceSelection();

    /**
     * Returns the expected grouping behavior of clients for this
     * {@code Hierarchy}.
     *
     * @return expected grouping behavior
     *
     * @since olap4j 2.0
     */
    GroupingBehavior getGroupingBehavior();

    /**
     * Returns the source of this {@code Hierarchy}.
     *
     * <p>Note that a parent/child attribute hierarchy is both
     * {@link Origin#USER_DEFINED} and {@link Origin#SYSTEM_ENABLED}.
     *
     * @return source of this hierarchy; never null
     *
     * @since olap4j 2.0
     */
    Set<Origin> getOrigin();

    /**
     * Returns whether this hierarchy is parent-child.
     *
     * @return whether this hierarchy is parent-child
     *
     * @since olap4j 2.0
     */
    boolean isParentChild();

    /** Source of a hierarchy. */
    enum Origin implements XmlaConstant {
        /**
         * Identifies a user defined hierarchy.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_USER_DEFINED</code> (1).
         */
        USER_DEFINED(1, "Identifies a user defined hierarchy."),

        /**
         * Identifies an attribute hierarchy.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_SYSTEM_ENABLED</code> (2).
         */
        SYSTEM_ENABLED(2, "Identifies an attribute hierarchy"),

        /**
         * Identifies attributes with no attribute hierarchies.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_SYSTEM_INTERNAL</code> (4).
         */
        SYSTEM_INTERNAL(
            4, "Identifies attributes with no attribute hierarchies.");

        private final int xmlaOrdinal;
        private final String description;

        public static final Dictionary<Origin> DICTIONARY =
            DictionaryImpl.forClass(Origin.class);

        public static final Set<Origin> ONLY_USER_DEFINED =
            Collections.unmodifiableSet(EnumSet.of(USER_DEFINED));

        public static final Set<Origin> PARENT_CHILD =
            Collections.unmodifiableSet(
                EnumSet.of(USER_DEFINED, SYSTEM_ENABLED));

        Origin(int xmlaOrdinal, String description) {
            this.xmlaOrdinal = xmlaOrdinal;
            this.description = description;
        }

        public String xmlaName() {
            return "MD_" + name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** A hint to the client application on how to show a hierarchy. */
    enum InstanceSelection implements XmlaConstant {
        /**
         * No instance selection.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_INSTANCE_SELECTION_NONE</code> (1).</p>
         */
        NONE(1),

        /**
         * Dropdown.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_INSTANCE_SELECTION_DROPDOWN</code> (2).</p>
         */
        DROPDOWN(2),

        /**
         * List.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_INSTANCE_SELECTION_LIST</code> (3).</p>
         */
        LIST(3),

        /**
         * Filtered list.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_INSTANCE_SELECTION_FILTEREDLIST</code> (4).</p>
         */
        FILTEREDLIST(4),

        /**
         * Mandatory filter.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_INSTANCE_SELECTION_MANDATORYFILTER</code> (5).</p>
         */
        MANDATORYFILTER(5);

        private final int xmlaOrdinal;

        public static final Dictionary<InstanceSelection> DICTIONARY =
            DictionaryImpl.forClass(InstanceSelection.class);

        InstanceSelection(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MD_INSTANCE_SELECTION_" + name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** Expected grouping behavior of clients of a hierarchy. */
    enum GroupingBehavior implements XmlaConstant {
        EncourageGrouping(1),

        DiscourageGrouping(2);

        private final int xmlaOrdinal;

        public static final Dictionary<GroupingBehavior> DICTIONARY =
            DictionaryImpl.forClass(GroupingBehavior.class);

        GroupingBehavior(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** Type of hierarchy. */
    enum Structure implements XmlaConstant {
        /**
         * <p>Corresponds to the XMLA constant
         * <code>MD_STRUCTURE_FULLYBALANCED</code> (0).</p>
         */
        FULLYBALANCED(0),

        /**
         * <p>Corresponds to the XMLA constant
         * <code>MD_STRUCTURE_RAGGEDBALANCED</code> (1).</p>
         */
        RAGGEDBALANCED(1),

        /**
         * <p>Corresponds to the XMLA constant
         * <code>MD_STRUCTURE_UNBALANCED</code> (2).</p>
         */
        UNBALANCED(2),

        /**
         * <p>Corresponds to the XMLA constant
         * <code>MD_STRUCTURE_NETWORK</code> (3).</p>
         */
        NETWORK(3);

        private final int xmlaOrdinal;

        public static final Dictionary<Structure> DICTIONARY =
            DictionaryImpl.forClass(Structure.class);

        Structure(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MD_STRUCTURE_" + name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** Type of hierarchy. */
    enum StructureType implements XmlaConstant {
        Natural(1),
        Unnatural(2),
        Unknown(3);

        private final int xmlaOrdinal;

        public static final Dictionary<StructureType> DICTIONARY =
            DictionaryImpl.forClass(StructureType.class);

        StructureType(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End Hierarchy.java
