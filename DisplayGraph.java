import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
//import static Toolkit.*;

import java.io.*;
import java.util.*;


public class DisplayGraph
{
	String prefix = "./resources/screenshots/dolphin/dolphin_converged_avg_jacc_nopenalty/ss_";//location of ScreenShot
	String graphFileName = "./resources/graphFiles/dolphin/dolphin_rehashed_edgelist.inp";
	String styleSheet = "url(file:///C:/Users/manish/workspace/RandomWalkVisualization/stylesheet1.txt)";
	String walkFile = "./resources/randomWalkFiles/dolphin/dolphin_converged_avg_jaccard_walk_nopenalty.txt";
	
	public void buildGraph(Graph graph,String fileName)
	{
		try{
			//graph.setAutoCreate(true);
			Scanner sn = new Scanner(new FileInputStream(fileName));
			String u,v;
			int p,q,t;
			double w;
			while(sn.hasNextLine())
			{
				String s = sn.nextLine();
				String uvwlist[] = s.split(" ");
				u = uvwlist[0];
				v = uvwlist[1];
				if(uvwlist.length == 3){
					w = Double.parseDouble(uvwlist[2]);
				}
				else w = 0.0;
				
				p = Integer.parseInt(u);
				q = Integer.parseInt(v);
				if(p>q)
				{
					t = p;
					p = q;
					q = t;
				}
				
				
				graph.addEdge((p+""+q), p+"", q+"");
//				graph.addEdge((v+u), v, u);
				Edge e1 = graph.getEdge(p+""+q);
				e1.addAttribute("ui.class", "weight,frequency");
				e1.setAttribute("weight", w);
				e1.setAttribute("frequency", 0);
				e1.setAttribute("ui.label", e1.getAttribute("frequency"));
				
				
//				Edge e2 = graph.getEdge(v+u);
//				e2.addAttribute("ui.class", "weight,frequency");
//				e2.setAttribute("weight", w);
//				e2.setAttribute("frequency", 0);
//				e2.setAttribute("ui.label", e2.getAttribute("frequency"));
//				
			}
			sn.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in opening file"+e.getMessage());
		}
		
		
	}
	
	public void sleep(){
		try{
			Thread.sleep(300);
		}
		catch(Exception e)
		{
			System.out.println("Error in going to sleep");
		}
	}
	
	public void readCoordsFile(String file_name,double x[],double y[],double z[])
	{
		try{
			Scanner sn = new Scanner(new FileInputStream(file_name));
			sn.nextLine();
			String s[];
			int n;
			while(sn.hasNextLine())
			{
				s = sn.nextLine().split(",");
				n = Integer.parseInt(s[0]);
				x[n] = Double.parseDouble(s[1]);
				y[n] = Double.parseDouble(s[2]);
				z[n] = Double.parseDouble(s[3]);			
				
			}
			sn.close();
			
		}
		catch(Exception e)
		{
			System.out.println("Error in reading Co-ordinates");
		}
	}
	
	
	public String[] readWalkFile(String file)
	{
		String walk[] = null;
		try{
			Scanner sn = new Scanner(new FileInputStream(file));
			
			if(sn.hasNextLine())
				walk = sn.nextLine().split(" ");			
			sn.close();
					
		}
		catch(Exception e){System.out.println("Error reading walk file");}
		return walk;	
	}
	
	public void DisplayWalk(Graph graph,String walk[])
	{
		

		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.display();
		try{
			Thread.sleep(10000);
			
		}catch(Exception e)
		{
			System.out.println("Message "+e.getMessage());
		}
		if(walk.length == 0)
		{
			System.out.println("Walk length is zero");
			return;
		}
//		String prefix = "./test-screenshots/karate/withoutLayout/ss_";
		int frequency = -1;
		int maxFrequency = 75;
		int j = 0;
		String u = walk[0];
		Node n = graph.getNode(u);
		n.setAttribute("ui.class", "current");
		graph.addAttribute("ui.screenshot", prefix + j++ +".png");
		sleep();
		String v;
		int x,y,r,s;//x is going to be the smaller one
		boolean tele = false;
		int maxWalkLength = 700;
		if (walk.length < maxWalkLength)
			maxWalkLength = walk.length;
		for(int i = 1;i< maxWalkLength;i++)
		{
			tele = false;
			n.setAttribute("ui.class", "visited");
			v = walk[i];
			if(v.startsWith("BB"))
			{
				tele = true;
				v = v.substring(2);
			}
			r = Integer.parseInt(u);
			s = Integer.parseInt(v);
			if(r>s)
			{
				x = s;
				y = r;
			}
			else{
				x = r;
				y = s;
			}
			n = graph.getNode(v);
			n.setAttribute("ui.class", "current");
			if(!tele)
			{
				//graph.addAttribute("ui.label", "Not teleport");
				Edge ed = graph.getEdge(x+""+y);
				if( ed != null)
				{
					frequency = ed.getAttribute("frequency");
					frequency += 1;
					ed.setAttribute("frequency", frequency);
					//ed.setAttribute("ui.color", frequency*1.0/maxFrequency);
					ed.setAttribute("ui.label", frequency);
					ed.setAttribute("ui.class","current");
					graph.addAttribute("ui.screenshot", prefix + j++ +".png");
					sleep();
					ed.setAttribute("ui.class", "visited");
				}
				else{
					System.out.println("Edge x = "+x+" y ="+y+" not present");
					graph.addAttribute("ui.screenshot", prefix + j++ +".png");
					sleep();
				}
			}
			else
			{
				//graph.addAttribute("ui.label", "Teleport");
				graph.addAttribute("ui.screenshot", prefix+ j++ +".png");
				sleep();
			}
			u = v;
			System.out.println("j = "+j);
		}
	}
	
	
	public static void main(String args[])
	{
		DisplayGraph obj = new DisplayGraph();
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		Graph graph = new MultiGraph("Football");
		graph.setStrict(false);
		graph.setAutoCreate(true);
//		String graphFileName = "karate_edgelist.txt";
		obj.buildGraph(graph, obj.graphFileName);
		String walk[];
//		String walkFile = "karate_walk.out_tele0_15";
		walk = obj.readWalkFile(obj.walkFile);
//		double x[],y[],z[];
//		x = new double[graph.getNodeCount()];
//		y = new double[graph.getNodeCount()];
//		z = new double[graph.getNodeCount()];
//		String coords_file_name = "karate_edge_coordinates.csv";
//		obj.readCoordsFile(coords_file_name,x,y,z);
		graph.addAttribute("ui.stylesheet", obj.styleSheet);
//		
		int num;
		for(Node n:graph)
		{
			num = Integer.parseInt(n.getId());
//			n.setAttribute("xyz", x[num],y[num],z[num]);
			n.setAttribute("ui.label", num);
		}
		
		//graph.display(false);
		obj.DisplayWalk(graph, walk);
//		int num;
//		double positions[][] = new double[graph.getNodeCount()][];
//		graph.display();
//		Viewer viewer = graph.display();
//		for(Node n:graph)
//		{
//			num = Integer.parseInt(n.getId());
//			positions[num] = getPosition(n);
//		}
		
	}
	

}
