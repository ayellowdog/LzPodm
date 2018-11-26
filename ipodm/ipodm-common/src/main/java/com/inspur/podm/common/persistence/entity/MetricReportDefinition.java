/*
 * Copyright (c) 2016-2018 Intel Corporation
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

package com.inspur.podm.common.persistence.entity;


import static com.inspur.podm.common.utils.Contracts.requiresNonNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.MetricReportActionType;
import com.inspur.podm.common.intel.types.MetricReportType;
import com.inspur.podm.common.intel.types.TransmitFormat;
import com.inspur.podm.common.persistence.base.Entity;

//@javax.persistence.Entity
//@NamedQueries({
//    @NamedQuery(name = MetricReportDefinition.GET_ALL_METRIC_REPORT_DEFINITIONS_IDS,
//        query = "SELECT metricReportDefinition.entityId FROM MetricReportDefinition metricReportDefinition")
//})
//@Table(name = "metric_report_definition", indexes = @Index(name = "idx_metric_report_definition_entity_id", columnList = "entity_id", unique = true))
//@SuppressWarnings({"checkstyle:MethodCount"})
public class MetricReportDefinition extends DiscoverableEntity {
    /** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 5846239713555115019L;

	public static final String GET_ALL_METRIC_REPORT_DEFINITIONS_IDS = "GET_ALL_METRIC_REPORT_DEFINITIONS_IDS";

//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "metric_report_type")
//    @Enumerated(STRING)
    private MetricReportType metricReportType;

//    @Column(name = "metric_report_destination")
    private String metricReportDestination;

//    @Column(name = "polling_interval_milli_seconds")
    private BigDecimal pollingIntervalMilliSeconds;

//    @Column(name = "report_interval_milli_seconds")
    private BigDecimal reportIntervalMilliSeconds;

//    @Column(name = "transmit_format")
//    @Enumerated(STRING)
    private TransmitFormat transmitFormat;

//    @ElementCollection
//    @Enumerated(STRING)
//    @CollectionTable(name = "metric_report_definition_metric_report_action", joinColumns = @JoinColumn(name = "metric_report_definition_id"))
//    @Column(name = "metric_report_action")
//    @OrderColumn(name = "metric_report_action_order")
    private List<MetricReportActionType> metricReportActions = new ArrayList<>();

//    @OneToMany(mappedBy = "metricReportDefinition", fetch = EAGER, cascade = {MERGE, PERSIST})
    private Set<MetricItem> metricItems = new HashSet<>();

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public MetricReportType getMetricReportType() {
        return metricReportType;
    }

    public void setMetricReportType(MetricReportType metricReportType) {
        this.metricReportType = metricReportType;
    }

    public String getMetricReportDestination() {
        return metricReportDestination;
    }

    public void setMetricReportDestination(String metricReportDestination) {
        this.metricReportDestination = metricReportDestination;
    }

    public BigDecimal getPollingIntervalMilliSeconds() {
        return pollingIntervalMilliSeconds;
    }

    public void setPollingIntervalMilliSeconds(BigDecimal pollingIntervalMilliSeconds) {
        this.pollingIntervalMilliSeconds = pollingIntervalMilliSeconds;
    }

    public BigDecimal getReportIntervalMilliSeconds() {
        return reportIntervalMilliSeconds;
    }

    public void setReportIntervalMilliSeconds(BigDecimal reportIntervalMilliSeconds) {
        this.reportIntervalMilliSeconds = reportIntervalMilliSeconds;
    }

    public TransmitFormat getTransmitFormat() {
        return transmitFormat;
    }

    public void setTransmitFormat(TransmitFormat transmitFormat) {
        this.transmitFormat = transmitFormat;
    }

    public List<MetricReportActionType> getMetricReportActions() {
        return metricReportActions;
    }

    public void addMetricReportActions(MetricReportActionType metricReportAction) {
        metricReportActions.add(metricReportAction);
    }

    public Set<MetricItem> getMetricItems() {
        return metricItems;
    }

    public void addMetricItem(MetricItem metricItem) {
        requiresNonNull(metricItem, "metric");

        metricItems.add(metricItem);
        if (!this.equals(metricItem.getMetricReportDefinition())) {
            metricItem.setMetricReportDefinition(this);
        }
    }

    public void unlinkMetricItem(MetricItem metricItem) {
        if (metricItems.contains(metricItem)) {
            metricItems.remove(metricItem);
            if (metricItem != null) {
                metricItem.unlinkMetricReportDefinition(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkCollection(metricItems, this::unlinkMetricItem);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return false;
    }
}
