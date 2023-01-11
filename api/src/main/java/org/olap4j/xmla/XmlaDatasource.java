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

import org.olap4j.metadata.Database;
import org.olap4j.metadata.XmlaConstants;

import java.util.List;

import static org.olap4j.metadata.XmlaConstants.*;

/**
 * XML for Analysis entity representing a Data Source.
 *
 * <p>Corresponds to the XML/A {@code DISCOVER_DATASOURCES} schema rowset.</p>
 */
public class XmlaDatasource extends Entity {
    public static XmlaDatasource INSTANCE =
        new XmlaDatasource();

    public RowsetDefinition def() {
        return RowsetDefinition.DISCOVER_DATASOURCES;
    }

    List<Column> columns() {
        return list(
            DataSourceName,
            DataSourceDescription,
            URL,
            DataSourceInfo,
            ProviderName,
            ProviderType,
            AuthenticationMode);
    }

    List<Column> sortColumns() {
        // XMLA does not specify a sort order, but olap4j does.
        return list(
            DataSourceName);
    }

    public final Column DataSourceName =
        new Column(
            "DataSourceName",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the data source, such as Adventure Works.");
    public final Column DataSourceDescription =
        new Column(
            "DataSourceDescription",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A description of the data source, as entered by the "
            + "publisher.");
    public final Column URL =
        new Column(
            "URL",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The unique path that shows where to invoke the XML for "
            + "Analysis (XMLA) methods for that data source.");
    public final Column DataSourceInfo =
        new Column(
            "DataSourceInfo",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A string containing any additional information required to "
            + "connect to the data source. This can include the Initial "
            + "Catalog property or other information for the provider.\n"
            + "Example: \"Provider=MSOLAP;Data Source=Local;\"");
    public final Column ProviderName =
        new Column(
            "ProviderName",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the provider behind the data source.\n"
            + "Example: \"MSOLAP\".");
    public final Column ProviderType =
        new Column(
            "ProviderType",
            XmlaType.EnumerationArray.of(Enumeration.PROVIDER_TYPE),
            Column.RESTRICTION,
            Column.REQUIRED,
            Column.UNBOUNDED,
            "The types of data supported by the provider. This array can "
            + "include one or more of the following types:\n"
            + sameXmlaName("MDP", Database.ProviderType.MDP)
            + ": multidimensional data provider.\n"
            + sameXmlaName("TDP", Database.ProviderType.TDP)
            + ": tabular data provider.\n"
            + sameXmlaName("DMP", Database.ProviderType.DMP)
            + ": data mining provider (implements the "
            + "OLE DB for Data Mining specification).");
    public final Column AuthenticationMode =
        new Column(
            "AuthenticationMode",
            XmlaType.EnumString.of(Enumeration.AUTHENTICATION_MODE),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Specification of what type of security mode the data source "
            + "uses. Values can be one of the following:\n"
            + sameXmlaName(
                "Unauthenticated",
                XmlaConstants.AuthenticationMode.Unauthenticated)
            + ": no user ID or password needs to be sent.\n"
            + sameXmlaName(
                "Authenticated", XmlaConstants.AuthenticationMode.Authenticated)
            + ": User ID and Password must be included in the "
            + "information required for the connection.\n"
            + sameXmlaName(
                "Integrated", XmlaConstants.AuthenticationMode.Integrated)
            + ": the data source uses the underlying security to "
            + "determine authorization, such as Integrated Security "
            + "provided by Microsoft Internet Information Services (IIS).");
}

// End XmlaDatasource.java
