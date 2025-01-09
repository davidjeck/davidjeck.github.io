import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Graph object represents a graph that can be either directed or undirected.
 * Edges can have weights, which by default are equal to 1.  (Edges of 
 * weight 1 can be used to implement an unweighted graph.  Vertices
 * in the graph can be "marked" with arbitrary integer values, and
 * a vertex can have another vertex as a "parent".  Marks and parents
 * are not used by this class in any way; instead, they are provided
 * for use by graph algorithms that need them.  A graph is represented
 * using adjacency lists.
 */
public class Graph {
	
	/**
	 * The class Graph.Edge represents one edge in a graph.  An edge
	 * has a "from" vertex number and a "to" vertex number.  It also
	 * has a weight of type double.  All member variables are final.
	 * It is not possible to create an Edge object from outside this
	 * class.  Edges are added to a graph by calling one of the graph's
	 * addEdge() methods.
	 */
	public static final class Edge {
		public final int from, to;
		public final double weight;
		private Edge( int from, int to, double weight) {
			this.from = from;
			this.to= to;
			this.weight = weight;
		}
	}
	
	/** A constant with value 0 meant for use for marking vertices. */
	public final static int UNDISCOVERED = 0;
	
	/** A constant with value 1 meant for use for marking vertices. */
	public final static int DISCOVERED = 1;
	
	/** A constant with value 2 meant for use for marking vertices. */
	public final static int FINISHED = 2;
	
	
	private final ArrayList<ArrayList<Edge>> edgeLists;  // A list of outgoing edges for each vertex.
	private final int[] vertexMarks;    // An integer "mark" value for each vertex.
	private final int[] vertexParents;  // A "parent" vertex number for each vertex; -1 means "no parent."
	private final boolean directed;     // True if this is a directed graph.
	private final int vertexCount;      // The number of vertices in the graph.
	
	private int edgeCount;              // The number of edges, which changes as edges are added.
	                                    // (Although an directed edge in an undirected graph is in two 
	                                    //  edge lists, it is only counted once in this count.)
	
	/**
	 * Create an undirected graph that initially has no edges.
	 * Vertices are identified by integers, with the first vertex being
	 * vertex zero and the last being the number of vertices minus one.
	 * @param vertexCount the number of vertices, which cannot be changed after the graph 
	 *    is constructed
	 */
	public Graph( int vertexCount ) {
		this(vertexCount, false);
	}
	
	/**
	 * Create a graph that initially has no edges.  The graph can be directed or 
	 * undirected.  The number of vertices is set when the graph is created and cannot
	 * be changed.  Vertices are identified by integers, with the first vertex being
	 * vertex zero and the last being the number of vertices minus one.
	 * @param vertexCount  the number of vertices in the graph
	 * @param directed true if this will be a directed graph, false for an undirected graph
	 */
	public Graph( int vertexCount, boolean directed ) {
		this.directed = directed;
		this.vertexCount = vertexCount;
		this.edgeLists = new ArrayList<>(vertexCount);
		this .vertexMarks = new int[vertexCount];
		this.vertexParents = new int[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			edgeLists.add(new ArrayList<>(2));
		}		
		clearParents();
	}
	
	/**
	 * Add an edge between two specified vertices.  The weight of the edge is set to 1.
	 * Note that in an undirected graph, an edge is added to the adjacency lists for
	 * both vertices; however, the edge only counts for one in the edge count.
	 * @param from  one vertex of the edge.  For a directed graph, this is the source vertex.
	 * @param to another vertex of the edge.  For a directed graph, this is the destination vertex.
	 * @throws IllegalArgumentException if either vertex number is not in the legal range
	 *     or if the two vertex numbers are the same.  Self loops are not allowed in the graph.
	 */
	public void addEdge( int from, int to) {
		addEdge(from,to,1);
	}
	
	/**
	 * Add an edge between two specified vertices, with a specified weight.
	 * Note that in an undirected graph, an edge is added to the adjacency list for
	 * both vertices; however, the edge only counts for one in the edge count.
	 * It is not allowed to add an edge to the graph that already exists in the graph.
	 * @param from  one vertex of the edge.  For a directed graph, this is the source vertex.
	 * @param to another vertex of the edge.  For a directed graph, this is the destination vertex.
	 * @param weight the weight of the edge.
	 * @throws IllegalArgumentException if either vertex number is not in the legal range
	 *     or if the two vertex numbers are the same, or if the edge already exists in 
	 *     the graphs.  Self loops are not allowed in the graph.
	 */
	public void addEdge( int from, int to, double weight ) {
		if (from < 0 || from >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + from);
		if (to < 0 || to >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + to);
		if (edgeExists(from,to))
			throw new IllegalArgumentException(
					"Edge (" + from + "," + to + ") already exists.");
		edgeLists.get(from).add( new Edge( from, to, weight ));
		if ( ! directed )
			edgeLists.get(to).add( new Edge( to, from, weight ));
		edgeCount++;
	}
	
	/**
	 * Tests whether an edge exists from one specified vertex to another.
	 * Vertex numbers outside the legal range will result in a return value
	 * of false rather than an exception.
	 */
	public boolean edgeExists( int from, int to ) {
		if (from < 0 || from >= vertexCount)
			return false;
		if (to < 0 || to >= vertexCount)
			return false;
		for (Edge e : edgeLists.get(from) ) {
			if (e.to == to)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns a List that contains all edges that leave a specified vertex.
	 * The list that is returned is unmodifiable.  An attempt to modify it
	 * will result in an UnsupportedOperationException.  The list supports
	 * efficient random access, since the base list is an ArrayList.
	 * @param vertexNum the number of the vertex whose edges will be accessed.
	 * @throws IllegalArgumentException if the vertex number is out of the legal range
	 */
	public List<Graph.Edge> edgeList( int vertexNum ) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		return Collections.unmodifiableList(edgeLists.get(vertexNum));
	}
	
	/**
	 * Returns an array containing the destination vertex of every edge
	 * leaving a specified vertex.  If the vertex has no outgoing edges,
	 * an array of length zero is returned.  Note that this is less 
	 * efficient than using edgeList(vertexNum) to get a list of Edges
	 * that leave the given vertex.
	 * @param vertexNum the number of the source vertex
	 * @throws IllegalArgumentException if the vertex number is out of the legal range
	 */
	public int[] getConnectedVertices( int vertexNum ) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		ArrayList<Edge> edges = edgeLists.get(vertexNum);
		int[] v = new int[edges.size()];
		for (int i = 0; i < edges.size(); i++)
			v[i] = edges.get(i).to;
		return v;
	}
	
	/**
	 * Returns an array containing every edge in the graph.  For an undirected
	 * graph, only one copy of each edge is returned.
	 */
	public Graph.Edge[] getAllEdges() {
		Edge[] edges = new Edge[edgeCount];
		int i = 0;
		for ( ArrayList<Edge> eList : edgeLists) {
			for (Edge e : eList) {
				if (directed || e.to > e.from)
					edges[i++] = e;
			}
		}
		assert i == edges.length;
		return edges;
	}
	
	/**
	 * Mark a given vertex with a given value.  Marks are not used inside this
	 * class.  They are for use by graph algorithms.
	 * @param vertexNum  the number of the vertex to be marked
	 * @param mark  the value of the mark.  Can be an arbitrary integer, but 
	 *    note that constants Graph.UNDISCOVERED, Graph.DISCOVERED, and
	 *    Graph.FINISHED are available to represent common uses of marks.
	 * @throws IllegalArgumentException if the vertex number is out of the legal range
	 */
	public void mark(int vertexNum, int mark) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		vertexMarks[vertexNum] = mark;
	}
	
	/**
	 * Mark all vertices with a given value.  Same as calling mark(vertexNum,mark) for each vertex.
	 */
	public void markAll(int mark) {
		Arrays.fill(vertexMarks, mark);
	}
	
	/**
	 * Return the integer mark value for a specified vertex.
	 * @param vertexNum  the number of the vertex whose mark is being queried
	 * @return the mark value for the vertex
	 * @throws IllegalArgumentException if the vertex number is out of the legal range
	 */
	public int getMark( int vertexNum ) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		return vertexMarks[vertexNum];
	}
	
	/**
	 * Set the parent of each vertex in the graph to -1.
	 */
	public void clearParents() {
		Arrays.fill(vertexParents, -1);
	}
	
	/**
	 * Get the parent of a specified vertex.
	 * @param vertexNum the number of the vertex whose parent is being queried
	 * @return the number of the parent vertex, or -1 if there is no parent
	 */
	public int getParent(int vertexNum) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		return vertexParents[vertexNum];
	}

	/**
	 * Set the "parent" of a specified vertex.  Parent information is not used
	 * in this class.  The ability to set parents is provided for graph algorithms
	 * that need it.
	 * @param vertexNum the number of the vertex whose parent is to be set
	 * @param parentVertex  the number of the vertex that will be the parent,
	 *    or -1 to indicate "no parent".
	 * @throws IllegalArgumentException if the specified vertex number is not in
	 *    the legal range of vertex numbers or if the specified parent number
	 *    is not either a legal vertex number or -1
	 */
	public void setParent(int vertexNum, int parentVertex) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		if (parentVertex < -1 || parentVertex >= vertexCount)
			throw new IllegalArgumentException(
					"Parent must be -1 or a legal vertex number: " + vertexNum);
		vertexParents[vertexNum] = parentVertex;
	}
	
	/**
	 * Returns true if this is a directed graph; false if it is undirected.
	 */
	public boolean isDirected() {
		return directed;
	}

	/**
	 * Returns the number of vertices in the graph.
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/**
	 * Returns the number of edges currently in the graph.
	 */
	public int getEdgeCount() {
		return edgeCount;
	}
	
	/**
	 * Return the degree of a specified vertex. For a directed graph, this is the out-degree.
	 * The return value is the length of the adjacency list for the vertex.
	 * @throws IllegalArgumentException if the vertex number is out of the legal range
	 */
	public int getDegree(int vertexNum) {
		if (vertexNum < 0 || vertexNum >= vertexCount)
			throw new IllegalArgumentException("No such vertex: " + vertexNum);
		return edgeLists.get(vertexNum).size();
	}

} // end class Graph
