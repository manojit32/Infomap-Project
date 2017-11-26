import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.*;

public class random_walker_1_7 
{
	Graph G;
	String WalkFile;
	String EdgeFreqFile;
	String IndividualEdgeFreqFile;
	String NodeFreqFile;
	String GraphFile;
	String GraphJaccardFile;
	int walkLength;
	int walkArray[];
	boolean walkTele[];
	TreeMap<String,Integer> edgeFreq;
	TreeMap<String, Integer> individualEdgeFreq;
	TreeMap<Integer, Integer> nodeFreq;
	public void startWalk()
	{
		initialiseForWalk();
		Random rand = new Random();
		int u = rand.nextInt(G.numberOfNodes);
		double r;
		double r2;
		int v;
		String edge;
		walkArray[0] = u;
		v = u;
		boolean teleport;
		nodeFreq.put(u,1);
		for(int i = 1;i<walkLength;i++)
		{	
			teleport = false;
//			System.out.println("u = "+u);
			r2 = rand.nextDouble();
			if(r2 < 0.15)
			{
				while(v==u)
					v = rand.nextInt(G.numberOfNodes);
				teleport = true;
			}
			else
			{
				r = rand.nextDouble();
//				System.out.println("r = "+r+" nodeRoulette = "+G.nodeRoulette.get(u));
				v = G.findInRoulette(G.nodeRoulette.get(u), r);
//				System.out.println(" v = "+v);
				if(v==-1)
				{
					while(v == -1 || v == u)
						v = rand.nextInt(G.numberOfNodes);
					teleport = true;
				}
				
			}
//			System.out.println(" v = "+v);
			nodeFreq.put(v, nodeFreq.get(v)+1);
			int x=0,y=0;
			if(u>v)//making x contain the smaller value, and y the larger value
			{
				x = v;
				y = u;
			}
			else
			{
				x = u;
				y = v;
			}
//			System.out.println("x = "+x+",y = "+y);
//			System.out.println("G.EdgeJaccardWeights.get(x+\" \"+y)="+G.EdgeJaccardWeights.get(x+" "+y));
			edge = x+" "+y;
			if(!teleport)
			{
				edgeFreq.put(edge, edgeFreq.get(edge)+1);
				individualEdgeFreq.put(u+" "+v, individualEdgeFreq.get(u+" "+v)+1);
			}
//			if(!teleport && G.EdgeJaccardWeights.get(edge) < G.AdjList.get(u).get(v)
//					&& G.degree.get(v)*2 > G.degree.get(u))//Penalty Part
//			{
//				
//				System.out.println(u+" "+v+",probability = "+G.AdjList.get(u).get(v));
//				G.updateRouletteValues(u, v);
//				System.out.println("Updated for edge:"+u+" "+v);
//				System.out.println(u+" "+v+",probability after updation = "+G.AdjList.get(u).get(v));
//			}
			u = v;
			walkArray[i] = u;
			walkTele[i] = teleport;
			
		}
		
	}
	void printWalk(String FileName)
	{
		try 
		{			
			FileWriter out = new FileWriter(FileName);
			for(int i=0;i<walkLength;i++)
			{
				if(walkTele[i] == false)
				{
					out.write(walkArray[i]+" ");
					//System.out.print(walkArray[i]+" ");
				}
				else
				{
					out.write("BB"+walkArray[i]+" ");
					//System.out.print("BB"+walkArray[i]+" ");
				}
			}
			out.write("\n\n");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		System.out.println();
	}
	void printEdgeFrequency(String FileName)
	{
		try{
			
			FileWriter fw = new FileWriter(FileName);
			for(String s:edgeFreq.keySet())
			{
				fw.write(s+" "+edgeFreq.get(s)+"\n");
			}
			fw.close();
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		
	}
	
	void printIndividualEdgeFrequency(String FileName)
	{
		try{
			
			FileWriter fw = new FileWriter(FileName);
			for(String s:individualEdgeFreq.keySet())
			{
				fw.write(s+" "+individualEdgeFreq.get(s)+"\n");
			}
			fw.close();
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		
	}
	
	void printNodeFrequency(String FileName)
	{
		try{
			
			FileWriter fw = new FileWriter(FileName);
			for(int u:nodeFreq.keySet())
			{
				fw.write(u+" "+nodeFreq.get(u)+"\n");
			}
			fw.close();
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		
	}
	
	void initialiseForWalk()
	{

		walkArray = new int[walkLength];
		walkTele = new boolean[walkLength];
		edgeFreq = new TreeMap<String,Integer>();
		individualEdgeFreq = new TreeMap<String,Integer>();
		nodeFreq = new TreeMap<Integer,Integer>();
		for(String s:G.Edges.keySet())
		{
			String arr[] = s.split(" ");
			int u = Integer.parseInt(arr[0]);
			int v = Integer.parseInt(arr[1]);
			int x,y;
			if(u>v)
			{
				x = v;
				y = u;
			}
			else{
				x = u;
				y = v;
			}
			edgeFreq.put(x+" "+y,0);
		}
		for(int n:G.AdjList.keySet())
		{
			nodeFreq.put(n, 0);
		}
		for(String s:G.Edges.keySet())
		{
			individualEdgeFreq.put(s,0);
		}
	}
	
	void initialize()
	{
		G = new Graph();
		G.initalize();
		String GRoot = "dolphin";
		String loc_prefix = "./res/graph_out/"+GRoot+"/"+GRoot+"_converged_nopenalty/"+GRoot;
		GraphFile="./res/graph_inp/"+GRoot+"/"+GRoot+"_converged_jacc_weight.txt";
		//GraphJaccardFile="./res/graph_inp/karate/karate_edge_jaccard.txt";
		WalkFile = loc_prefix+"_converged_avg_jaccard_walk_nopenalty.txt";
		EdgeFreqFile = loc_prefix+"_edge_freq_converged_avg_jacc_nopenalty.txt";
		IndividualEdgeFreqFile = loc_prefix+ "_individual_edge_freq_converged_avg_jacc_nopenalty.txt";
		NodeFreqFile = loc_prefix+"_node_freq_converged_avg_jacc_nopenalty.txt";
		G.readGraphWeighted(GraphFile);
		//G.readGraphJaccardWeights(GraphJaccardFile);
		walkLength = (G.numberOfEdges)*15/2;
		walkArray = new int[walkLength];
		walkTele = new boolean[walkLength];
		edgeFreq = new TreeMap<String,Integer>();
		nodeFreq = new TreeMap<Integer,Integer>();		
		G.initializeRoulette();
//		for(String s: G.EdgeJaccardWeights.keySet())
//		{
//			System.out.println(s+":"+G.EdgeJaccardWeights.get(s));
//		}
	}
	
	void writeOutputFiles()
	{
		printWalk(WalkFile);
		System.out.println("WalkFile done");
		printEdgeFrequency(EdgeFreqFile);
		System.out.println("Edge Freq file done");
		printIndividualEdgeFrequency(IndividualEdgeFreqFile);
		System.out.println("Individual Edge Freq file done");
		printNodeFrequency(NodeFreqFile);
		System.out.println("Node Freq file done");
	}
	
	public static void main(String args[])
	{
		random_walker_1_7 RW = new random_walker_1_7();
		RW.initialize();
		RW.startWalk();
		RW.printWalk(RW.WalkFile);
		RW.writeOutputFiles();
		System.out.println("Program exitted");
	}

}
