//package Graphs;

// this implementation of a weighted graph is from my CSC2001 assignments
// with only minor changes to make it compatible with the game
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;


// Represents a vertex in the graph.
class Vertex
{
	public Point2D.Float    name;   // Vertex name
	public List<Edge> adj;    // Adjacent vertices
	public double     dist;   // total distance from start
	public Vertex     prev;   // Previous vertex on shortest path
	public int        scratch;// Extra variable used in algorithm
	public double	  edgecost; // added variable for a counter for the weighted cost

	public Vertex( Point2D.Float nm ){
		name = nm; 
		adj = new LinkedList<Edge>( ); 
		reset( ); 
	}
	// if there are obstacle in the way compute path
	public void reset( ){
		//  dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }    
		dist = Double.MAX_VALUE; prev = null; scratch = 0; edgecost = Double.MAX_VALUE;
	}
}

// Represents an edge in the graph.
class Edge
{
	public Vertex     dest;   // Second vertex in Edge
	public double     cost;   // Edge cost

	public Edge( Vertex d, double c )
	{
		dest = d;
		cost = c;
	}

	public String toString()
	{
		return (cost+"");
	}
}


// Represents an entry in the priority queue for Dijkstra's algoritm
class Path implements Comparable<Path>
{
	public Vertex     dest;   // w
	public double     cost;   // d(w)

	public Path( Vertex d, double c )
	{
		dest = d;
		cost = c;
	}

	public int compareTo( Path rhs )
	{
		double otherCost = rhs.cost;
		return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
	}
}


// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted
// void dijkstra( String s )    --> Single-source weighted
// void negative( String s )    --> Single-source negative weighted
// void acyclic( String s )     --> Single-source acyclic
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.

public class Graph
{
	public static final double INFINITY = Double.MAX_VALUE;
	private Map<Point2D.Float,Vertex> vertexMap = new HashMap<Point2D.Float,Vertex>( );
	private LinkedList<Point2D.Float> path = new LinkedList<Point2D.Float>();
	private double cost;
	
	public Graph (){

	}


	/**
	 * Add a new edge to the graph.
	 */


	public void addEdge( Point2D.Float sourceName, Point2D.Float destName)
	{
		Vertex v = getVertex( sourceName );
		Vertex w = getVertex( destName );

		double cost = sourceName.distance(destName);
		v.adj.add( new Edge( w, cost ) );
	}

	/**
	 * Driver routine to handle unreachables and print total cost.
	 * It calls recursive routine to print shortest path to
	 * destNode after a shortest path algorithm has run.
	 */
	public void printPath( Point2D.Float destName )
	{
		Vertex w = vertexMap.get( destName );
		printPath( w );
	}

	/**
	 * Recursive routine to print shortest path to dest
	 * after running shortest path algorithm. The path
	 * is known to exist.
	 */
	private void printPath( Vertex dest )
	{
		if( dest.prev != null )
		{
			printPath( dest.prev );
		}
		path.add(dest.name);
	}



	/**
	 * If vertexName is not present, add it to vertexMap.
	 * In either case, return the Vertex.
	 */
	private Vertex getVertex(Point2D.Float vertexName )
	{
		Vertex v = vertexMap.get( vertexName );
		if( v == null )
		{
			v = new Vertex( vertexName );
			vertexMap.put( vertexName, v );
		}
		return v;
	}


	/**
	 * Initializes the vertex output info prior to running
	 * any shortest path algorithm.
	 */
	public void clearAll( )
	{
		// for every vertex in the hashmap
		// set the values to defualt
		for( Vertex v : vertexMap.values( ) )
			v.reset( );
	}


	/**
	 * Single-source weighted shortest-path algorithm. (Dijkstra) 
	 * using priority queues based on the binary heap
	 */
	public void dijkstra(Point2D.Float startName, Point2D.Float destName)
	{
		PriorityQueue<Path> pq = new PriorityQueue<Path>( );

		Vertex start = vertexMap.get(startName);
		Vertex end = vertexMap.get(destName);


		if( start == null )
			throw new NoSuchElementException( "Start vertex not found" );

		clearAll( );
		pq.add( new Path( start, 0 ) ); start.dist = 0; start.edgecost = 0;

		int nodesSeen = 0;
		while( !pq.isEmpty( ) && nodesSeen < vertexMap.size( ) )
		{
			Path vrec = pq.remove( );
			Vertex v = vrec.dest;
			if( v.scratch != 0 )  // already processed v
				continue;

			v.scratch = 1;
			nodesSeen++;

			for( Edge e : v.adj )
			{
				Vertex w = e.dest;
				double cvw = e.cost;

				if( w.edgecost > v.edgecost + cvw )
				{
					w.edgecost = v.edgecost +cvw;
					w.dist = v.dist+1;
					w.prev = v;
					pq.add( new Path( w, w.edgecost ) );
				}
				// here process if the path weights are the same
				// if we are at the destination vertex
				// the previous vertex is replaced by the one with the shortest path
				else if (end.name.equals(w.name) && (w.edgecost == v.edgecost + cvw)) {
					if (w.prev.dist > v.dist)
						w.prev = v;
				}
			}
		}
		cost = end.edgecost;
	}



	// compute the path between the two given points
	// return a list of all the points on the shortest path
	public LinkedList<Point2D.Float> processRequest(Point2D.Float s, Point2D.Float e)
	{
		Point2D.Float startName =s;
		Point2D.Float destName = e;


		this.dijkstra(startName, destName);
		this.printPath( destName );
		path.remove();
		return path;
	}

	/**
	 * A main routine that:
	 * 1. Reads a file containing edges (supplied as a command-line parameter);
	 * 2. Forms the graph;
	 * 3. Repeatedly prompts for two vertices and
	 *    runs the shortest path algorithm.
	 * The data file is a sequence of lines of the format
	 *    source destination cost
	 */
	public void addVertices(Point2D.Float[] edges)
	{
		for(int i= 0; i<edges.length-1; i++){

			Point2D.Float source  = edges[i];
			Point2D.Float dest    = edges[i+1];
			this.addEdge(source, dest);
		}

		Point2D.Float source  = edges[0];
		Point2D.Float dest    = edges[3];
		this.addEdge(source, dest);
	}
}
