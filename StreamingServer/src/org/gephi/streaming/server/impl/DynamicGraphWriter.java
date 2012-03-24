/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Andre Panisson <panisson@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.streaming.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.attributes.type.DynamicType;
import org.gephi.data.attributes.type.Interval;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.streaming.api.GraphEventHandler;
import org.gephi.streaming.api.event.ElementType;
import org.gephi.streaming.api.event.EventType;
import org.gephi.streaming.api.event.GraphEvent;

/**
 * @author panisson
 *
 */
public class DynamicGraphWriter extends GraphWriter {
    
    public DynamicGraphWriter(Graph graph, boolean sendVizData) {
        super(graph, sendVizData);
    }
    
    @Override
    public void writeGraph(GraphEventHandler operationSupport) {

        try {
            graph.readLock();

            double minRange = Double.MAX_VALUE;
            double maxRange = Double.MIN_VALUE;
            Map<Double, List<GraphEvent>> creation = new HashMap<Double, List<GraphEvent>>();
            Map<Double, List<GraphEvent>> remotion = new HashMap<Double, List<GraphEvent>>();
            Map<Double, List<GraphEvent>> changing = new HashMap<Double, List<GraphEvent>>();
            
            for (Node node: graph.getNodes()) {
                
                String nodeId = node.getNodeData().getId();
                DynamicType timeInterval = (DynamicType)node.getNodeData().getAttributes().getValue(DynamicModel.TIMEINTERVAL_COLUMN);

                List<Interval> ranges = timeInterval.getIntervals();
                for (Interval range: ranges) {
                    double created = range.getLow();
                    double removed = range.getHigh();

                    if (created < minRange) {
                        minRange = created;
                    }
                    if (removed > maxRange) {
                        maxRange = removed;
                    }

                    List<GraphEvent> createdAt = creation.get(created);
                    if (createdAt == null) {
                        createdAt = new ArrayList<GraphEvent>();
                        creation.put(created, createdAt);
                    }

                    {
                        Map<String, Object> attributes = getNodeAttributes(node);
                        createdAt.add(eventBuilder.graphEvent(ElementType.NODE, EventType.ADD, nodeId, attributes));
                    }

                    List<GraphEvent> removedAt = remotion.get(removed);
                    if (removedAt == null) {
                        removedAt = new ArrayList<GraphEvent>();
                        remotion.put(removed, removedAt);
                    }
                    {
                        Map<String, Object> attributes = new HashMap<String, Object>();
                        removedAt.add(eventBuilder.graphEvent(ElementType.NODE, EventType.REMOVE, nodeId, attributes));
                    }
                    
                    AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                    if (row != null) {
                        for (AttributeValue attributeValue : row.getValues()) {
                            if (attributeValue.getValue() != null
                                    && attributeValue.getColumn().getType().isDynamicType()
                                    && !attributeValue.getColumn().getId().equals(DynamicModel.TIMEINTERVAL_COLUMN)) {

                                DynamicType dynamicValue = (DynamicType)attributeValue.getValue();
                                List<Interval> intervals = dynamicValue.getIntervals();

                                for (Interval interval: intervals) {

                                    Object value = interval.getValue();

                                    List<GraphEvent> changedAt = changing.get(interval.getLow());
                                    if (changedAt == null) {
                                        changedAt = new ArrayList<GraphEvent>();
                                        changing.put(interval.getLow(), changedAt);
                                    }
                                    Map<String, Object> attributes = new HashMap<String, Object>();
                                    attributes.put(attributeValue.getColumn().getTitle(), value);

                                    changedAt.add(eventBuilder.graphEvent(ElementType.NODE, EventType.CHANGE, nodeId, attributes));

                                }
                            }
                        }
                    }
                }
            }

            for (Edge edge: graph.getEdges()) {

                String edgeId = edge.getEdgeData().getId();
                DynamicType timeInterval = (DynamicType)edge.getEdgeData().getAttributes().getValue(DynamicModel.TIMEINTERVAL_COLUMN);

                List<Interval> ranges = timeInterval.getIntervals();
                for (Interval range: ranges) {
                    double created = range.getLow();
                    double removed = range.getHigh();

                    if (created < minRange) {
                        minRange = created;
                    }
                    if (removed > maxRange) {
                        maxRange = removed;
                    }

                    List<GraphEvent> createdAt = creation.get(created);
                    if (createdAt == null) {
                        createdAt = new ArrayList<GraphEvent>();
                        creation.put(created, createdAt);
                    }
                    {
                        Map<String, Object> attributes = getEdgeAttributes(edge);
                        String sourceId = edge.getSource().getNodeData().getId();
                        String targetId = edge.getTarget().getNodeData().getId();
                        createdAt.add(eventBuilder.edgeAddedEvent(edgeId, sourceId, targetId, edge.isDirected(), attributes));
                    }

                    List<GraphEvent> removedAt = remotion.get(removed);
                    if (removedAt == null) {
                        removedAt = new ArrayList<GraphEvent>();
                        remotion.put(removed, removedAt);
                    }
                    {
                        Map<String, Object> attributes = new HashMap<String, Object>();
                        removedAt.add(0,eventBuilder.graphEvent(ElementType.EDGE, EventType.REMOVE, edgeId, attributes));
                    }
                    
                    AttributeRow row = (AttributeRow) edge.getEdgeData().getAttributes();
                    if (row != null) {
                        for (AttributeValue attributeValue : row.getValues()) {
                            if (attributeValue.getValue() != null
                                    && attributeValue.getColumn().getType().isDynamicType()
                                    && !attributeValue.getColumn().getId().equals(DynamicModel.TIMEINTERVAL_COLUMN)) {

                                DynamicType dynamicValue = (DynamicType)attributeValue.getValue();
                                List<Interval> intervals = dynamicValue.getIntervals();

                                for (Interval interval: intervals) {

                                    Object value = interval.getValue();

                                    List<GraphEvent> changedAt = changing.get(interval.getLow());
                                    if (changedAt == null) {
                                        changedAt = new ArrayList<GraphEvent>();
                                        changing.put(interval.getLow(), changedAt);
                                    }
                                    Map<String, Object> attributes = new HashMap<String, Object>();
                                    attributes.put(attributeValue.getColumn().getTitle(), value);

                                    changedAt.add(eventBuilder.graphEvent(ElementType.EDGE, EventType.CHANGE, edgeId, attributes));

                                }
                            }
                        }
                    }
                }
            }
            
            List<GraphEvent> pre;
            pre = creation.get(Double.NEGATIVE_INFINITY);
            if (pre!=null) {
                List<GraphEvent> first = creation.get(minRange);
                first.addAll(pre);
                creation.remove(Double.NEGATIVE_INFINITY);
            }
            pre = changing.get(Double.NEGATIVE_INFINITY);
            if (pre!=null) {
                List<GraphEvent> first = creation.get(minRange);
                first.addAll(pre);
                changing.remove(Double.NEGATIVE_INFINITY);
            }
            
            List<GraphEvent> after = remotion.get(Double.POSITIVE_INFINITY);
            if (after!=null) {
                List<GraphEvent> last = remotion.get(maxRange);
                last.addAll(pre);
                remotion.remove(Double.POSITIVE_INFINITY);
            }

            TreeSet<Double> ordered = new TreeSet<Double>();
            ordered.addAll(creation.keySet());
            ordered.addAll(remotion.keySet());
            ordered.addAll(changing.keySet());

            for (Double ts: ordered) {
                
                List<GraphEvent> createdAt = creation.get(ts);
                if (createdAt!=null) {
                    for (GraphEvent e: createdAt) {
                        e.setTimestamp(ts);
                        operationSupport.handleGraphEvent(e);
                    }
                }
                
                List<GraphEvent> changedAt = changing.get(ts);
                if (changedAt!=null) {
                    for (GraphEvent e: changedAt) {
                        e.setTimestamp(ts);
                        operationSupport.handleGraphEvent(e);
                    }
                }
                
                //if (ts == ordered.last()) break;

                List<GraphEvent> removedAt = remotion.get(ts);
                if (removedAt!=null) {
                    for (GraphEvent e: removedAt) {
                        e.setTimestamp(ts);
                        operationSupport.handleGraphEvent(e);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            graph.readUnlock();
        }
    }

    private Map<String, Object> getNodeAttributes(Node node) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();

        if (row != null)
            for (AttributeValue attributeValue: row.getValues()) {
                if (attributeValue.getColumn().getIndex()!=PropertiesColumn.NODE_ID.getIndex()
                        && attributeValue.getValue()!=null
                        && !attributeValue.getColumn().getType().isDynamicType())
                    attributes.put(attributeValue.getColumn().getTitle(), attributeValue.getValue());
            }

        if (sendVizData) {
            attributes.put("x", node.getNodeData().x());
            attributes.put("y", node.getNodeData().y());
            attributes.put("z", node.getNodeData().z());

            attributes.put("r", node.getNodeData().r());
            attributes.put("g", node.getNodeData().g());
            attributes.put("b", node.getNodeData().b());

            attributes.put("size", node.getNodeData().getSize());
        }

        return attributes;
    }

    private Map<String, Object> getEdgeAttributes(Edge edge) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        AttributeRow row = (AttributeRow) edge.getEdgeData().getAttributes();
        if (row != null)
            for (AttributeValue attributeValue: row.getValues()) {
                if (attributeValue.getColumn().getIndex()!=PropertiesColumn.EDGE_ID.getIndex()
                        && attributeValue.getValue()!=null
                        && !attributeValue.getColumn().getType().isDynamicType())
                     attributes.put(attributeValue.getColumn().getTitle(), attributeValue.getValue());
            }

        if (sendVizData) {
            
            attributes.put("r", edge.getEdgeData().r());
            attributes.put("g", edge.getEdgeData().g());
            attributes.put("b", edge.getEdgeData().b());
            
            attributes.put("weight", edge.getWeight());
        }

        return attributes;
    }
    
    private class ChangingData {
        private Object object;
        private String attributeName;
        private Object attributeValue;
        
        public ChangingData(Object object, String attributeName, Object attributeValue) {
            this.object = object;
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }
    }

}
