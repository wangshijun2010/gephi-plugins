/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.streaming.server;

import java.util.HashMap;
import java.util.Map;

import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.streaming.api.CompositeOperationSupport;
import org.gephi.streaming.api.GraphUpdaterOperationSupport;
import org.gephi.streaming.api.OperationSupport;

/**
 * @author panisson
 *
 */
public class GraphBufferedOperationSupport extends CompositeOperationSupport {
    
    private Graph graph;
    private GraphUpdaterOperationSupport updater;
    private boolean sendVizData = true;

    public GraphBufferedOperationSupport(Graph graph) {
        this.graph = graph;
        this.updater = new GraphUpdaterOperationSupport(graph);
    }
    
    @Override
    public void addOperationSupport(OperationSupport operationSupport) {
        writeGraph(operationSupport);
        super.addOperationSupport(operationSupport);
    }

    @Override
    public void edgeAdded(String edgeId, String fromNodeId, String toNodeId,
            boolean directed, Map<String, Object> attributes) {
        updater.edgeAdded(edgeId, fromNodeId, toNodeId, directed, attributes);
        super.edgeAdded(edgeId, fromNodeId, toNodeId, directed, attributes);
    }

    @Override
    public void edgeRemoved(String edgeId) {
        updater.edgeRemoved(edgeId);
        super.edgeRemoved(edgeId);
    }

    @Override
    public void graphAttributeAdded(String attributeName, Object value) {
        updater.graphAttributeAdded(attributeName, value);
        super.graphAttributeAdded(attributeName, value);
    }

    @Override
    public void graphAttributeChanged(String attributeName, Object newValue) {
        updater.graphAttributeChanged(attributeName, newValue);
        super.graphAttributeChanged(attributeName, newValue);
    }

    @Override
    public void graphAttributeRemoved(String attributeName) {
        updater.graphAttributeRemoved(attributeName);
        super.graphAttributeRemoved(attributeName);
    }

    @Override
    public void nodeAdded(String nodeId, Map<String, Object> attributes) {
        updater.nodeAdded(nodeId, attributes);
        super.nodeAdded(nodeId, attributes);
    }

    @Override
    public void nodeRemoved(String nodeId) {
        updater.nodeRemoved(nodeId);
        super.nodeRemoved(nodeId);
    }
    
    private void writeGraph(OperationSupport operationSupport) {
        
        try {
            graph.readLock();
            
            for (Node node: graph.getNodes()) {
                String nodeId = node.getNodeData().getId();
                operationSupport.nodeAdded(nodeId, getNodeAttributes(node));
            }
            
            for (Edge edge: graph.getEdges()) {
                String edgeId = edge.getEdgeData().getId();
                String sourceId = edge.getSource().getNodeData().getId();
                String targetId = edge.getTarget().getNodeData().getId();
                operationSupport.edgeAdded(edgeId, sourceId, targetId, edge.isDirected(), getEdgeAttributes(edge));
                
                
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
                        && attributeValue.getValue()!=null)
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
                        && attributeValue.getValue()!=null)
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

}