
/*------------------- Sample output from this program -----------------------
 
Finding MST for the graph read from the file weighted-grid-graph...
  Slow Prim total weight of MST:  45.00
  Time for SlowPrim: 3,522,858 nanoseconds
  Fast Prim total weight of MST:  45.00
  Time for FastPrim: 256,619 nanoseconds
  Kruskal total weight:  45.00
  Time for kruskal: 64,520,384 nanoseconds

Finding MST for randomWeightedGraph(100,100,1)...
  Slow Prim total weight of MST:  264.00
  Time for SlowPrim: 726,762 nanoseconds
  Fast Prim total weight of MST:  264.00
  Time for FastPrim: 571,130 nanoseconds
  Kruskal total weight:  264.00
  Time for kruskal: 3,461,078 nanoseconds

Finding MST for randomWeightedGraph(50000,300000,100,8)...
  Time to generate graph: 189,633,630 nanaoseconds
  Slow Prim total weight of MST:  529758.00
  Time for SlowPrim: 6,919,910,237 nanoseconds
  Fast Prim total weight of MST:  529758.00
  Time for FastPrim: 94,212,128 nanoseconds
  Kruskal total weight:  529758.00
  Time for kruskal: 336,744,421 nanoseconds

Finding MST for randomWeightedGraph(300000,5000000,1000,2)...
  Time to generate graph: 6,394,626,262 nanaoseconds
  Fast Prim total weight of MST:  10957974.00
  Time for FastPrim: 832,343,128 nanoseconds
  Kruskal total weight:  10957974.00
  Time for kruskal: 1,612,476,111 nanoseconds

----------------------------------------------------------------------------*/



import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


/**
 * This program demos several algorithms for finding minimal spanning
 * trees in weighted, undirected graphs.  The are applied to three 
 * different graphs, and the time for each algorithm is output.
 * The first graph is read from a file named "weighted-graph.txt."
 */
public class MSTAlgorithms {
	
	/** 
	 * Find the weight of the MST for three graphs, using 2 or 3 different MST
	 * algorithms.  (The slowest algorithm is not run on the largest graph.)
	 */
	public static void main(String[] args) {
		Graph g = readGraph("weighted-graph.txt");
		if (g == null)
			System.out.println("Could not read the file weighted-grid-graph.txt");
		else {
			System.out.println("Finding MST for the graph read from the file weighted-grid-graph...");
			applyAlgorithms(g, true);
		}
		System.out.println();
		System.out.println("Finding MST for randomWeightedGraph(100,100,1)...");
		applyAlgorithms(randomWeightedGraph(100,300,10,1), true);

		System.out.println();
		System.out.println("Finding MST for randomWeightedGraph(50000,300000,100,8)...");
		long start = System.nanoTime();
		Graph g3 = randomWeightedGraph(50000,300000,100,8);
		long time = System.nanoTime() - start;
		System.out.printf("  Time to generate graph: %,d nanaoseconds%n", time);
		applyAlgorithms(g3, true);
		
		System.out.println();
		System.out.println("Finding MST for randomWeightedGraph(300000,5000000,1000,2)...");
		start = System.nanoTime();
		Graph g2 = randomWeightedGraph(300000,5000000,1000,2);
		time = System.nanoTime() - start;
		System.out.printf("  Time to generate graph: %,d nanaoseconds%n", time);
		applyAlgorithms(g2, false);		
	}
	
	private static Graph randomWeightedGraph(int v, int e, int maxWeight, long seed) {  // make a random graph
		Random rand = new Random(seed);
		Graph g = new Graph(v);
		while (g.getEdgeCount() < e) {
			int x = rand.nextInt(v);
			int y = rand.nextInt(v);
			if (x == y || g.edgeExists(x, y))
				continue;
			g.addEdge(x, y, 1 + rand.nextInt(maxWeight));
		}
		return g;
	}
	
	
	private static Graph readGraph(String filepath) { // read a graph from a file
		try {
			Scanner in = new Scanner(new File(filepath));
			Graph g = new Graph(in.nextInt());
			int edgeCt = in.nextInt();
			for (int i = 0; i < edgeCt; i++) {
				g.addEdge(in.nextInt(), in.nextInt(), in.nextDouble());
			}
			in.close();
			return g;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Apply three or four graph algorithms to a given graph and time how long
	 * they take to find the MST.  The "slowPrim" algorithm is applied only
	 * if the second parameter, since it will take too long on large graphs.
	 */
	public static void applyAlgorithms( Graph g, boolean doSlowPrim ) {
		long start, time;
		if (doSlowPrim) {
			start = System.nanoTime();
			slowPrim(g);
			time = System.nanoTime() - start;
			System.out.printf("  Time for SlowPrim: %,d nanoseconds%n", time);
		}
		start = System.nanoTime();
		fastPrim(g);
		time = System.nanoTime() - start;
		System.out.printf("  Time for FastPrim: %,d nanoseconds%n", time);
		start = System.nanoTime();
		kruskal(g);
		time = System.nanoTime() - start;		
		System.out.printf("  Time for kruskal: %,d nanoseconds%n", time);
		start = System.nanoTime();
	}
	

	/**
	 * Apply Prim's algorithm to a weighted, undirected graph and print out 
	 * the total weight of a minimal spanning tree for the graph.
	 * This is the standard version of the algorithm that does not use
	 * a heap, and it is too slow to use on very large graphs.
	 */
	private static void slowPrim(Graph g) {
		int vertexCt = g.getVertexCount();
		boolean[] inTree = new boolean[vertexCt];
		double[] dist = new double[vertexCt];
		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		double totalWeight = 0;
		int vert = 0;
		for (int i = 1; i < vertexCt; i++) {
			inTree[vert] = true;
			List<Graph.Edge> edges = g.edgeList(vert);
			for (Graph.Edge edge : edges) {
				if ( !inTree[edge.to] && edge.weight < dist[edge.to]) {
					dist[edge.to] = edge.weight;
					g.setParent(edge.to, edge.from);
					// change priority
				}
			}
			//System.out.println(Arrays.toString(priorityQueue));
			double d = Double.POSITIVE_INFINITY;
			for (int v = 0; v < vertexCt; v++) {
			    if (!inTree[v] && dist[v] < d) {
			    		d = dist[v];
			    		vert = v;
			    }
			}
			if (d == Double.POSITIVE_INFINITY)
				throw new IllegalArgumentException("Graph is not connected");
			totalWeight += dist[vert];
			//System.out.printf("Add edge (%d,%d) with weight %1.2f%n", vert, g.getParent(vert), dist[vert]);
		}
		System.out.printf("  Slow Prim total weight of MST:  %1.2f%n", totalWeight);
	}
		

	/**
	 * Apply Prim's algorithm to a weighted, undirected graph and print out 
	 * the total weight of a minimal spanning tree for the graph.
	 * This version of Prim's algorithm uses a specially designed priority
	 * queue for a significant gain in efficiency.  In this version of
	 * a priority queue of integers, an auxiliary array is used to record
	 * the location of each integer in the heap. This works because the only
	 * things in the heap are distinct vertex numbers.  The heap is implemented
	 * directly, just using two arrays and a counter to keep track of the
	 * number of items in the heap.
	 */
	private static void fastPrim(Graph g) {
		int vertexCt = g.getVertexCount();
		boolean[] inTree = new boolean[vertexCt];  // inTree[v] tells whether vertex v has been added to the MST
		double[] dist = new double[vertexCt];  // for vertex v NOT in the MST, dist[v] is the distance of v from the MST
		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		int[] priorityQueue = new int[vertexCt-1]; // heap of vertex numbers
		int[] indexInPQ = new int[vertexCt];  // for a vertex v in the heap, priorityQueue[indexInPQ[v]] = v
		int pqSize = vertexCt-1;  // size of the heap
		for (int i = 0; i < vertexCt-1; i++) { // Put vertices, except for vertex 0, into the heap.
			   // Note that vertices are compared using distances in the dist[] array.
			   // There is no need to heapify the priorityQueue array, since all of the
			   // vertices initially have the same value for dist, namely infinity.
			priorityQueue[i] = i+1;
			indexInPQ[i+1] = i;
		}
		double totalWeight = 0;
		int vert = 0;
		for (int i = 1; i < vertexCt; i++) {
			inTree[vert] = true;
			List<Graph.Edge> edges = g.edgeList(vert);
			for (Graph.Edge edge : edges) {
				if ( !inTree[edge.to] && edge.weight < dist[edge.to]) {
					dist[edge.to] = edge.weight;
					g.setParent(edge.to, edge.from);
					// change priority
					int index = indexInPQ[edge.to];
					while (index > 0 && dist[priorityQueue[(index-1)/2]] > edge.weight) {  
						    // adjust heap to account for change in the priority (i.e. the dist)
						    // of vertex number edge.to.  Since the priority has decreased, the
						    // vertex needs to "bubble up" to its new correct position in the heap
						int p = priorityQueue[(index-1)/2];
						priorityQueue[(index-1)/2] = edge.to;
						priorityQueue[index] = p;
						indexInPQ[p] = index;
						indexInPQ[edge.to] = (index-1)/2;
						index = (index-1)/2;
					}
				}
			}
			//System.out.println(Arrays.toString(priorityQueue));
			vert = priorityQueue[0]; // get minimal value from the heap
			//System.out.printf("Add edge (%d,%d) with weight %1.2f%n", vert, g.getParent(vert), dist[vert]);
			if (dist[vert] == Double.POSITIVE_INFINITY)
				throw new IllegalArgumentException("Graph is not connected");
			totalWeight += dist[vert];
			pqSize--; // decrease the heap size
			// To complete the "remove" operation, move the last item from the heap to the top
			// and let it "bubble down" to its correct position.
			priorityQueue[0] = priorityQueue[pqSize];
			indexInPQ[priorityQueue[0]] = 0;
			int indx = 0;
			while (true) { // bubble down
				int leftChild = 2*indx+1;
				int rightChild = 2*indx+2;
				if (leftChild >= pqSize)
					break;
				int minChild = leftChild;
				if (rightChild < pqSize && dist[priorityQueue[rightChild]] < dist[priorityQueue[leftChild]])
					minChild = rightChild;
				if (dist[priorityQueue[minChild]] < dist[priorityQueue[indx]]) { //swap
					int temp = priorityQueue[minChild];
					priorityQueue[minChild] = priorityQueue[indx];
					priorityQueue[indx] = temp;
					indexInPQ[priorityQueue[minChild]] = minChild;
					indexInPQ[priorityQueue[indx]] = indx;
					indx = minChild;
				}
				else {
					break;
				}
			}
		}
		System.out.printf("  Fast Prim total weight of MST:  %1.2f%n", totalWeight);
	}
	

	/**
	 * Apply Kruskal's algorithm to a weighted, undirected graph and print out 
	 * the total weight of a minimal spanning tree for the graph, including
	 * a direct implementation of the union-find operation.
	 */
	private static void kruskal(Graph g) {
		int vertexCt = g.getVertexCount();
		int[] ufParent = new int[vertexCt];  // parent pointers for union-find
		int[] ufCount = new int[vertexCt];   // subset counts for union-find
		Arrays.fill(ufParent, -1);
		Arrays.fill(ufCount, 1);
		Graph.Edge[] edges = g.getAllEdges();
		Arrays.sort(edges, (a,b) -> (int)Math.signum(a.weight - b.weight) );
		double totalWeight = 0;
		int edgeCt = 0;
		for (Graph.Edge edge : edges) {
			int a = edge.from;
			int b = edge.to;
			while (ufParent[a] != -1)
				a = ufParent[a];
			while (ufParent[b] != -1)
				b = ufParent[b];
			if (a == b)
				continue;
			edgeCt ++;
			totalWeight += edge.weight;
			if (ufCount[a] < ufCount[b]) {
				ufParent[a] = b;
				ufCount[b] += ufCount[a];
			}
			else {
				ufParent[b] = a;
				ufCount[a] += ufCount[b];
			}
			if (edgeCt == vertexCt - 1)
				break;
		}
		System.out.printf("  Kruskal total weight:  %1.2f%n", totalWeight);
	}

}
