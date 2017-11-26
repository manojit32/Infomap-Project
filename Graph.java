import java.io.FileInputStream;
import java.util.*;

class Block
{
	double value;
	int key;
}

public class Graph 
{
	TreeMap<String, Double> Edges;
	TreeMap<String, Double> EdgeJaccardWeights; 
	TreeMap<Integer,TreeMap<Integer, Double>> AdjList;
	TreeMap<Integer,TreeMap<Integer, Double>> AdjListOrg;
	TreeMap<Integer,Integer> degree;
	TreeMap<Integer, Block[]> nodeRoulette;
	//String graphFile="./res/graph_inp/random_edgelist.txt";
	//String graphJaccardFile="./res/graph_inp/random_jaccard.txt";
	int numberOfEdges;
	int numberOfNodes;
	
	public void initalize()
	{
		Edges = new TreeMap<String, Double>();
		EdgeJaccardWeights = new TreeMap<String, Double>();
		AdjList = new TreeMap<Integer, TreeMap<Integer,Double>>();
		AdjListOrg = new TreeMap<Integer, TreeMap<Integer,Double>>();
		degree = new TreeMap<Integer,Integer>();
		nodeRoulette = new TreeMap<Integer,Block[]>();		
	}
	
	public void addEdgeWeighted(int a, int b,double w)
	{
		Edges.put(a+" "+b, w);
		if(!AdjList.containsKey(a))
		{
			AdjList.put(a,new TreeMap<Integer, Double>());
			AdjListOrg.put(a,new TreeMap<Integer, Double>());
			degree.put(a, 0);
		}
		TreeMap<Integer, Double> hs = AdjList.get(a);
		degree.put(a, degree.get(a)+1);
		hs.put(b,w);
		hs = AdjListOrg.get(a);
		hs.put(b,w);
	}
	
	public void readGraphWeighted(String fileName)
	{
		try{
			Scanner sn = new Scanner(new FileInputStream(fileName));
			String lineArr[] = sn.nextLine().split(" ");
			numberOfNodes = Integer.parseInt(lineArr[0]);
			numberOfEdges = Integer.parseInt(lineArr[1]);
			System.out.println("Inside Reading Graph");
//			for(String s: lineArr)
//			{
//				System.out.println(s);
//			}
			int u = -1;
			int v = -1;
			double w = -1.0;
			for(int i = 0;i<numberOfEdges;i++)
			{
				lineArr = sn.nextLine().split(" ");
				u =  Integer.parseInt(lineArr[0]);
				v =  Integer.parseInt(lineArr[1]);
				w = Double.parseDouble(lineArr[2]);
				//System.out.println("got u,v,w");
				addEdgeWeighted(u, v, w);
			}
			System.out.println("Done");
			sn.close();
			
		}
		catch(Exception e)
		{
			System.out.println("Error reading Graph File, "+e.getMessage());
		}
	}
	
	public void addEdgeJaccard(int a, int b,double w)
	{
		String edge = a+" "+b;
		EdgeJaccardWeights.put(a+" "+b, w);
		
	}
	
	public void initializeRoulette()
	{
		for(int i = 0;i < numberOfNodes; i++)
		{
			nodeRoulette.put(i, new Block[degree.get(i)]);
			Block roulette[] = nodeRoulette.get(i);
			Set<Integer> neighbors = AdjList.get(i).keySet();
			int k = 0;
			
			for(int j : neighbors)
			{
				roulette[k] = new Block();
				if(k == 0){
					roulette[k].value = AdjList.get(i).get(j);
					roulette[k].key = j;
				}
				else
				{
					roulette[k].value = AdjList.get(i).get(j) + roulette[k-1].value;
					roulette[k].key = j;
				}
				k++;
			}
		}
	}
	
	public int findInRoulette(Block roulette[],double random_value)
	{
		int u = roulette.length;
//		for(Block b:roulette)
//		{
//			System.out.print(b.key+":"+b.value+"  ");
//		}
//		System.out.println();
		int l = 0;
		int mid = -1;
		boolean found = false;
		while(l<=u && l<roulette.length && u > -1 )
		{
			mid = (u+l)/2;
//			System.out.println("l="+l+" u="+u+" mid="+mid);
//			System.out.println("mid value= "+roulette[mid].value);
			if(random_value < roulette[mid].value)
			{
				if(mid-1 >= 0 && random_value < roulette[mid-1].value )
				{
					u = mid-1;
				}
				else
				{
					found = true;
					break;
				}
			}
			if(random_value > roulette[mid].value)
			{
				l = mid+1;
			}
			
		}
		if(found)
			return roulette[mid].key;
		else return -1;
	}
	
	public void updateRouletteValues(int Node,int neighborName)// we would be decreasing the
				//probability value for the neighbor exponentially
	{
		System.out.println("old Roulette:");
		for(Block i: nodeRoulette.get(Node))
		{
			System.out.print(i.key+":"+i.value+"  ");
		}
		TreeMap<Integer,Double> neighbors = AdjList.get(Node);
		double change = (neighbors.get(neighborName))/2;
		int deg = degree.get(Node);
		change = change/(deg-1);
		//Now update the weights
		neighbors.put(neighborName, neighbors.get(neighborName)/2);
		for(int c: neighbors.keySet())
		{
			if(c!=neighborName)
			{
				neighbors.put(c, (neighbors.get(c))+change);
			}
		}
		//Now update the roulette
		Block new_roulette[] = nodeRoulette.get(Node);
		int k = 0;
		for(int i: neighbors.keySet())
		{
			new_roulette[k] = new Block();
			if(k == 0){
				new_roulette[k].value = neighbors.get(i);
				new_roulette[k].key = i;
			}
			else
			{
				new_roulette[k].value = neighbors.get(i) + new_roulette[k-1].value;
				new_roulette[k].key = i;
			}
			k++;
		}		
		System.out.println("\nNEw Roulette:");
		for(Block i: nodeRoulette.get(Node))
		{
			System.out.print(i.key+":"+i.value+"  ");
		}
	}
	
	public void readGraphJaccardWeights(String fileName)
	{
		try{
			Scanner sn = new Scanner(new FileInputStream(fileName));
			String lineArr[] = sn.nextLine().split(" ");
			System.out.println("Reading Graph Jaccard");
//			for(String s: lineArr)
//			{
//				System.out.println(s);
//			}
			int numLines = Integer.parseInt(lineArr[1]);
//			System.out.println("numlines = "+numLines);
			int u = -1;
			int v = -1;
			double w = -1.0;
			while(sn.hasNextLine())
			{
				lineArr = sn.nextLine().split(" ");
				u =  Integer.parseInt(lineArr[0]);
				v =  Integer.parseInt(lineArr[1]);
				w = Double.parseDouble(lineArr[2]);
				addEdgeJaccard(u, v, w);
			}
			sn.close();
			System.out.println("Done");
		}
		catch(Exception e)
		{
			System.out.println("Error reading Graph File, "+e.getMessage());
		}
	}

}
