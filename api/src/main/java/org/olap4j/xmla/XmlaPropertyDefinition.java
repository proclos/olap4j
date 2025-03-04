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
package org.olap4j.xmla;

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.XmlaConstants;

import java.util.Set;

import static org.olap4j.metadata.XmlaConstants.*;

/**
 * Enumeration of standard XML for Analysis Properties.
 */
public enum XmlaPropertyDefinition {
    AxisFormat(
        XmlaType.Enumeration,
        Olap4jUtil.enumSetAllOf(XmlaConstants.AxisFormat.class),
        XmlaConstants.Access.Write,
        "",
        XmlaConstants.Method.EXECUTE,
        "Determines the format used within an MDDataSet result set to describe the axes of the multidimensional dataset. This property can have the values listed in the following table: TupleFormat (default), ClusterFormat, CustomFormat."),

    BeginRange(
        XmlaType.Integer,
        null,
        XmlaConstants.Access.Write,
        "-1",
        XmlaConstants.Method.EXECUTE,
        "Contains a zero-based integer value corresponding to a CellOrdinal attribute value. (The CellOrdinal attribute is part of the Cell element in the CellData section of MDDataSet.)\n"
        + "Used together with the EndRange property, the client application can use this property to restrict an OLAP dataset returned by a command to a specific range of cells. If -1 is specified, all cells up to the cell specified in the EndRange property are returned.\n"
        + "The default value for this property is -1."),

    Catalog(
        XmlaType.String,
        null,
        XmlaConstants.Access.ReadWrite,
        "",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "When establishing a session with an Analysis Services instance to send an XMLA command, this property is equivalent to the OLE DB property, DBPROP_INIT_CATALOG.\n"
        + "When you set this property during a session to change the current database for the session, this property is equivalent to the OLE DB property, DBPROP_CURRENTCATALOG.\n"
        + "The default value for this property is an empty string."),

    Content(
        XmlaType.EnumString,
        Olap4jUtil.enumSetAllOf(XmlaConstants.Content.class),
        XmlaConstants.Access.Write,
        XmlaConstants.Content.DEFAULT.name(),
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "An enumerator that specifies what type of data is returned in the result set.\n"
        + "None: Allows the structure of the command to be verified, but not executed. Analogous to using Prepare to check syntax, and so on.\n"
        + "Schema: Contains the XML schema (which indicates column information, and so on) that relates to the requested query.\n"
        + "Data: Contains only the data that was requested.\n"
        + "SchemaData: Returns both the schema information as well as the data."),

    Cube(
        XmlaType.String,
        null,
        XmlaConstants.Access.ReadWrite,
        "",
        XmlaConstants.Method.EXECUTE,
        "The cube context for the Command parameter. If the command contains a cube name (such as an MDX FROM clause) the setting of this property is ignored."),

    DataSourceInfo(
        XmlaType.String,
        null,
        XmlaConstants.Access.ReadWrite,
        "",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "A string containing provider specific information, required to access the data source."),

    // Mondrian-specific extension to XMLA.
    Deep(
        XmlaType.Boolean,
        null,
        XmlaConstants.Access.ReadWrite,
        "",
        XmlaConstants.Method.DISCOVER,
        "In an MDSCHEMA_CUBES request, whether to include sub-elements "
        + "(dimensions, hierarchies, levels, measures, named sets) of each "
        + "cube."),

    // Mondrian-specific extension to XMLA.
    EmitInvisibleMembers(
        XmlaType.Boolean,
        null,
        XmlaConstants.Access.ReadWrite,
        "",
        XmlaConstants.Method.DISCOVER,
        "Whether to include members whose VISIBLE property is false, or "
        + "measures whose MEASURE_IS_VISIBLE property is false."),

    EndRange(
        XmlaType.Integer,
        null,
        XmlaConstants.Access.Write,
        "-1",
        XmlaConstants.Method.EXECUTE,
        "An integer value corresponding to a CellOrdinal used to restrict an MDDataSet returned by a command to a specific range of cells. Used in conjunction with the BeginRange property. If unspecified, all cells are returned in the rowset. The value -1 means unspecified."),

    Format(
        XmlaType.EnumString,
        Olap4jUtil.enumSetAllOf(XmlaConstants.Format.class),
        XmlaConstants.Access.Write,
        "Native",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "Enumerator that determines the format of the returned result set. Values include:\n"
        + sameXmlaName("Tabular", XmlaConstants.Format.Tabular)
        + ": a flat or hierarchical rowset. Similar to the XML RAW format in SQL. The Format property should be set to Tabular for OLE DB for Data Mining commands.\n"
        + sameXmlaName(
            "Multidimensional", XmlaConstants.Format.Multidimensional)
        + ": Indicates that the result set will use the MDDataSet format (Execute method only).\n"
        + sameXmlaName("Native", XmlaConstants.Format.Native)
        + ": The client does not request a specific format, so the provider may return the format  appropriate to the query. (The actual result type is identified by namespace of the result.)"),

    LocaleIdentifier(
        XmlaType.UnsignedInteger,
        null,
        XmlaConstants.Access.ReadWrite,
        "None",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "Use this to read or set the numeric locale identifier for this request. The default is provider-specific.\n"
        + "For the complete hexadecimal list of language identifiers, search on \"Language Identifiers\" in the MSDN Library at http://www.msdn.microsoft.com.\n"
        + "As an extension to the XMLA standard, Mondrian also allows locale codes as specified by ISO-639 and ISO-3166 and as used by Java; for example 'en-US'.\n"),

    MDXSupport(
        XmlaType.EnumString,
        Olap4jUtil.enumSetAllOf(XmlaConstants.MdxSupport.class),
        XmlaConstants.Access.Read,
        "Core",
        XmlaConstants.Method.DISCOVER,
        "Enumeration that describes the degree of MDX support. At initial release Core is the only value in the enumeration. In future releases, other values will be defined for this enumeration."),

    Password(
        XmlaType.String,
        null,
        org.olap4j.metadata.XmlaConstants.Access.Read,
        "",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "This property is deprecated in XMLA 1.1. To support legacy applications, the provider accepts but ignores the Password property setting when it is used with the Discover and Execute method"),

    ProviderName(
        XmlaType.String,
        null,
        XmlaConstants.Access.Read,
        "olap4j-xmlaserver",
        XmlaConstants.Method.DISCOVER,
        "The XML for Analysis Provider name."),

    ProviderVersion(
        XmlaType.String,
        null,
        XmlaConstants.Access.Read,
        "0.1",
        XmlaConstants.Method.DISCOVER,
        "The version of the Mondrian XMLA Provider"),

    // Mondrian-specific extension to XMLA.
    /**
     * @see org.olap4j.metadata.XmlaConstants.ResponseMimeType
     */
    ResponseMimeType(
        XmlaType.String,
        null,
        XmlaConstants.Access.ReadWrite,
        "None",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "Accepted mime type for RPC response; accepted are 'text/xml' "
        + "(default), 'application/xml' (equivalent to 'text/xml'), or "
        + "'application/json'. If not specified, value in the 'Accept' header "
        + "of the HTTP request is used."),

    StateSupport(
        XmlaType.EnumString,
        Olap4jUtil.enumSetAllOf(XmlaConstants.StateSupport.class),
        XmlaConstants.Access.Read,
        "None",
        XmlaConstants.Method.DISCOVER,
        "Property that specifies the degree of support in the provider for state. For information about state in XML for Analysis, see \"Support for Statefulness in XML for Analysis.\" Minimum enumeration values are as follows:\n"
        + "None - No support for sessions or stateful operations.\n"
        + "Sessions - Provider supports sessions."),

    Timeout(
        XmlaType.UnsignedInteger,
        null,
        XmlaConstants.Access.ReadWrite,
        "Undefined",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "A numeric time-out specifying in seconds the amount of time to wait for a request to be successful."),

    UserName(
        XmlaType.String,
        null,
        XmlaConstants.Access.Read,
        "",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "Returns the UserName the server associates with the command.\n"
        + "This property is deprecated as writeable in XMLA 1.1. To support legacy applications, servers accept but ignore the password setting when it is used with the Execute method."),

    VisualMode(
        XmlaType.Enumeration,
        Olap4jUtil.enumSetAllOf(XmlaConstants.VisualMode.class),
        XmlaConstants.Access.Write,
        Integer.toString(XmlaConstants.VisualMode.VISUAL.ordinal()),
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "This property is equivalent to the OLE DB property, MDPROP_VISUALMODE.\n"
        + "The default value for this property is "
        + sameXmlaNameCode(
            "DBPROPVAL_VISUAL_MODE_DEFAULT (0)",
            XmlaConstants.VisualMode.DEFAULT)
        + "."),

    // mondrian-specific property for advanced drill-through
    TableFields(
        XmlaType.String,
        null,
        XmlaConstants.Access.Read,
        "",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "List of fields to return for drill-through.\n"
        + "The default value of this property is the empty string,"
        + "in which case, all fields are returned."),

    // mondrian-specific property for advanced drill-through
    AdvancedFlag(
        XmlaType.Boolean,
        null,
        XmlaConstants.Access.Read,
        "false",
        XmlaConstants.Method.DISCOVER_AND_EXECUTE,
        "");

    public final XmlaType type;
    public final Set<? extends Enum> enumSet;
    public final XmlaConstants.Access access;
    public final XmlaConstants.Method usage;
    public final String value;
    public final String description;

    XmlaPropertyDefinition(
        XmlaType type,
        Set<? extends Enum> enumSet,
        XmlaConstants.Access access,
        String value,
        XmlaConstants.Method usage,
        String description)
    {
        // Line endings must be UNIX style (LF) not Windows style (LF+CR).
        // Thus the client will receive the same XML, regardless
        // of the server O/S.
        assert description.indexOf('\r') == -1;
        assert value.indexOf('\r') == -1;
        assert (enumSet != null) == type.isEnum();
        this.type = type;
        this.enumSet = enumSet;
        this.access = access;
        this.usage = usage;
        this.value = value;
        this.description = description;
    }

    /**
     * Returns the description of this property.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }
}

// End XmlaPropertyDefinition.java
