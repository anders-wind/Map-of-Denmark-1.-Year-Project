/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Anders
 */
public class Edge {

    private final int fromNode;
    private final int toNode;
	
	private Node fromNodeTrue = null;
	private Node toNodeTrue = null;
	private Node midNodeTrue = null;
	
    private final int roadType;
    private final String roadName;

    // Mangler TYPE
    public Edge(int fromNode, int toNode, int roadType, String roadName) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.roadType = roadType;
        this.roadName = roadName;
    }

    public int getFromNode() {
        return fromNode;
    }

    public int getToNode() {
        return toNode;
    }

    public String getRoadName() {
        return roadName;
    }

    public int getRoadType() {
        return roadType;
    }
	
		public Node getFromNodeTrue()
	{
		return fromNodeTrue;
	}

	public Node getToNodeTrue()
	{
		return toNodeTrue;
	}

	public Node getMidNodeTrue()
	{
		return midNodeTrue;
	}

	public void setFromNodeTrue(Node fromNodeTrue)
	{
		this.fromNodeTrue = fromNodeTrue;
	}

	public void setToNodeTrue(Node toNodeTrue)
	{
		this.toNodeTrue = toNodeTrue;
	}

	public void setMidNodeTrue(Node midNodeTrue)
	{
		this.midNodeTrue = midNodeTrue;
	}
	
	
}
