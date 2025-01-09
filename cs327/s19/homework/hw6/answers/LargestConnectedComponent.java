
/*-------------------------- Sample output from this program ----------------
 
 Looking for largest component in randomGraph(100,100,1): 
 Total vertices: 100
 Largest component size: 86
 Second largest component size: 2

 Looking for largest component in randomGraph(250000,300000,1): 
 Total vertices: 250000
 Largest component size: 219570
 Second largest component size: 10

 Vertex count is 10000.
  Graph with 1000 edges has a largest component size equal to 7
  Graph with 2000 edges has a largest component size equal to 12
  Graph with 3000 edges has a largest component size equal to 35
  Graph with 4000 edges has a largest component size equal to 58
  Graph with 5000 edges has a largest component size equal to 449
  Graph with 6000 edges has a largest component size equal to 3412
  Graph with 7000 edges has a largest component size equal to 5070
  Graph with 8000 edges has a largest component size equal to 6440
  Graph with 9000 edges has a largest component size equal to 7321
  Graph with 10000 edges has a largest component size equal to 7937
  Graph with 11000 edges has a largest component size equal to 8420
  Graph with 12000 edges has a largest component size equal to 8797
  Graph with 13000 edges has a largest component size equal to 9029
  Graph with 14000 edges has a largest component size equal to 9274
  Graph with 15000 edges has a largest component size equal to 9387
  Graph with 16000 edges has a largest component size equal to 9513
  Graph with 17000 edges has a largest component size equal to 9634
  Graph with 18000 edges has a largest component size equal to 9728
  Graph with 19000 edges has a largest component size equal to 9755
  Graph with 20000 edges has a largest component size equal to 9810

100 graphs with 20000 edges had average largest component size 9802.07
100 graphs with 30000 edges had average largest component size 9974.77
100 graphs with 40000 edges had average largest component size 9996.76
100 graphs with 50000 edges had average largest component size 9999.49
100 graphs with 60000 edges had average largest component size 9999.90
100 graphs with 70000 edges had average largest component size 9999.99
100 graphs with 80000 edges had average largest component size 10000.00
So, for 80000 edges, 100 random graphs were fully connected.

Let's try 1000 graphs with 80000 edges...
Whoops, found a counterexample after 344 tries.

Let's try 1000 graphs with 92104 (= 10000*ln(10000)) edges...
All 1000 graphs were fully connected.

 ----------------------------------------------------------------------------- */


import java.util.LinkedList;
import java.util.Random;

/**
 * A program to test how the size of the largest connected component in a
 * graph varies with the number of edges in the graph.
 */
public class LargestConnectedComponent {

	private static Graph randomGraph( int vertexCt, int edgeCt, long seed ) {  // Build a random graph
		Random rand = new Random(seed);
		Graph grph;
		grph = new Graph(vertexCt);
		while (grph.getEdgeCount() < edgeCt) {
			int a = rand.nextInt(vertexCt);
			int b = rand.nextInt(vertexCt);
			if ( a != b && ! grph.edgeExists(a, b) ) {
				grph.addEdge(a, b);
			}
		}
		return grph;
	}
	
	/**
	 * Counts vertices in connect component containing the start vertex.
	 * Assumes that vertices in than component are marked UNDISCOVERED
	 * before this method is called.
	 */
	private static int bfs(Graph g, int startVertex, LinkedList<Integer> queue) {
		int ct = 1;
		g.mark(startVertex, Graph.DISCOVERED);
		queue.addLast(startVertex);
		while ( ! queue.isEmpty() ) {
			int x = queue.removeFirst();
			int[] verts = g.getConnectedVertices(x);
			for (int to : verts) {
				if (g.getMark(to) == Graph.UNDISCOVERED) {
					g.mark(to, Graph.DISCOVERED);
					ct++;
					queue.addLast(to);
				}
			}
		}
		return ct;
	}
	
	/**
	 * Find the number of vertices in the largest connected component of an
	 * undirected graph.  This version of the algorithm outputs the sizes
	 * of the largest component and of the second largest component.
	 */
	private static int largestCompSizeWithOutput( Graph g ) {
		LinkedList<Integer> queue = new LinkedList<>();
		g.markAll(Graph.UNDISCOVERED);
		int max = -1;
		int max2 = -1;
		int total = 0;
		for (int i = 0; i < g.getVertexCount(); i++) {
			if (g.getMark(i) == Graph.UNDISCOVERED) {  // found another component
				int size = bfs(g,i,queue);
				total += size;
				if (size > max) {
					max2 = max;
					max = size;
				}
				else if (size > max2)
					max2 = size;
			}
		}
		System.out.println("Total vertices: " + total);
		System.out.println("Largest component size: " + max);
		System.out.println("Second largest component size: " + max2);
		System.out.println();
		return max;
	}

	private static int largestCompSize( Graph g ) {
		LinkedList<Integer> queue = new LinkedList<>();
		g.markAll(Graph.UNDISCOVERED);
		int max = -1;
		for (int i = 0; i < g.getVertexCount(); i++) {
			if (g.getMark(i) == Graph.UNDISCOVERED) {  // found another component
				int size = bfs(g,i,queue);
				if (size > max) {
					max = size;
				}
			}
		}
		return max;
	}

	/**
	 * Looks at the largest connected component size in random undirected graphs containing 10000
	 * vertices.  First, it outputs the largest component size for graphs ranging in size from
	 * 1000 to 20000.  It then tries to determine how large a graph has to be so that all of
	 * the vertices are almost certain to be in one connected component.
	 */
	public static void main(String[] args) {
		
		System.out.println("Looking for largest component in randomGraph(100,100,1): ");
		largestCompSizeWithOutput(randomGraph(100,100,1));
		System.out.println("Looking for largest component in randomGraph(250000,300000,1): ");
		largestCompSizeWithOutput(randomGraph(250000,300000,1));
		
		System.out.println("Vertex count is 10000.");
		
		// First, print out the largest component sizes for edge counts
		// from 1000 to 20000.  Observation:  The size of the largest connected component
		// starts out very small but starts increasing rapidly at about 5000 edges.  By the
		// time there are 20000 edges, more than 98% of the vertices are in a single connected
		// component.
		
		for (int edgeCt = 1000; edgeCt <= 20000; edgeCt += 1000) {
			Graph g = randomGraph(10000, edgeCt, (int)(Math.random()*100000+1));
			int bigComp = largestCompSize( g );
			System.out.printf("  Graph with %d edges has a largest component size equal to %d%n", 
					edgeCt, bigComp);
		}
		System.out.println();
		
		// Next, look for a number of edges that will almost certainly make a fully connected
		// graph.  Here, I take "almost certain" to mean with at least 99% probability.
		// Observations:  This usually ends when edgeCt is 70000 or 80000.  At that
		// point, most graphs have a single connected component.
		
		int edgeCt = 20000;
		while (true) {
			int maxSum = 0;
			for (int i = 0; i < 100; i++) {
				maxSum += largestCompSize( randomGraph(10000,edgeCt,(int)(Math.random()*100000+1)));
			}
			System.out.printf("100 graphs with %d edges had average largest component size %1.2f%n", 
					edgeCt, maxSum/100.0);
			if (maxSum == 1000000)
				break;
			edgeCt += 10000;
		}
		System.out.println("So, for " + edgeCt + " edges, 100 random graphs were fully connected.");
		System.out.println();
		
		// Now, let's try a lot of graphs using the number we found.  This is to test whether it
		// is really "almost certain" that a graph of size edgeCt has a single connected component.
		// Observation:  In fact, in several runs of the program, it sometimes happened that the
		// program found a graph of size edgeCt that did NOT have a single connected component.
		// This happened both for edgeCt = 70000 and edgeCt = 80000, so even with that many
		// edges, it is still not "almost certain" to get a single component.
		
		System.out.println("Let's try 1000 graphs with " + edgeCt + " edges...");
		boolean ok = true;
		for (int i = 0; i < 1000; i++) {
			if ( largestCompSize( randomGraph(10000,92104,(int)(Math.random()*100000+1))) != 10000 ) {
				System.out.println("Whoops, found a counterexample after " + i + " tries.");
				ok = false;
				break;
			}
		}
		if (ok)
			System.out.println("All 1000 graphs were fully connected.");
		System.out.println();

		// Now, let's try a lot of graphs using the theoretical value for the required number of
		// edges, which is N*ln(N) for a graph with N vertices.  Observation:  I did not observe
		// a case where the graph had more than one connected component.
		
		System.out.println("Let's try 1000 graphs with 92104 (= 10000*ln(10000)) edges...");
		for (int i = 0; i < 1000; i++) {
			if ( largestCompSize( randomGraph(10000,92104,(int)(Math.random()*100000+1))) != 10000 ) {
				System.out.println("Whoops, found a counterexample after " + i + " tries.");
				return;
			}
		}
		System.out.println("All 1000 graphs were fully connected.");
	}
	
}
