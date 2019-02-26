package xw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import generateTopology.Main;

public class PrimMst {
    
    public boolean[] marked;
    public List<Edge> mst;
    public PriorityQueue<Edge> pq;
	
	public  void visit(Graph g,int v) throws Exception
	{
		marked[v]=true;
		for(Edge e: g.adj(v))
		{
			if(!marked[e.other(v)])
				pq.add(e);

		}
		
	}
	
	public PrimMst(Graph G) throws Exception
	{
		
		pq=new PriorityQueue<Edge>();
		marked=new boolean[G.V()];
		mst=new ArrayList<Edge>();
		
		visit(G,0);
		while(!pq.isEmpty())
		{
			Edge e=pq.remove();
			int v=e.either(),w=e.other(v);
			if(marked[v]&&marked[w]) continue;
			mst.add(e);
			if(!marked[v]) visit(G,v);
			if(!marked[w]) visit(G,w);

		}
	}
	
	public static List<Edge> getCombineEdge(String in) throws IOException
	{
		BufferedReader r=Main.getReader(in, "utf-8");
		List<Edge> res=new ArrayList<>();
		
		String line="";
		while((line=r.readLine())!=null)
		{
			String ele[]=line.split(",");
			Edge e=new Edge(Integer.valueOf(ele[0]),Integer.valueOf(ele[1]),Integer.valueOf(ele[2]));
			res.add(e);
			Edge e1=new Edge(Integer.valueOf(ele[1]),Integer.valueOf(ele[0]),Integer.valueOf(ele[2]));
			res.add(e1);
		}
		
		return res;
		
	}
	
	public static void main(String args[]) throws Exception
	{
		System.out.println("江苏山东跨省子网分析：");
		//江苏
		Graph g1=new Graph("E:/work/标识站/江苏标识点图.csv");	
		Graph g2=new Graph("E:/work/标识站/江苏标识点图2.csv");
		List<Edge> cLine=getCombineEdge("E:/work/标识站/edgeConbine江苏.csv");		
		Graph g3=g1.combineGraph(g2, cLine);

		
        //山东
        Graph g11=new Graph("E:/work/标识站/山东标识点图.csv");
		Graph g22=new Graph("E:/work/标识站/山东标识点图2.csv");
		List<Edge> cLine2=getCombineEdge("E:/work/标识站/edgeConbine山东.csv");		
		Graph g33=g1.combineGraph(g22, cLine2);
		
		
		List<Edge> cLineCross=getCombineEdge("E:/work/标识站/edgeConbine江苏山东.csv");
		
		Graph gAll=g33.combineGraph(g3,cLineCross);
		System.out.println("顶点数:"+gAll.V()+"边数:"+gAll.E());
		PrimMst pAll=new PrimMst(gAll);
		Graph all=new Graph(pAll.mst,gAll.V());
		System.out.println("顶点数:"+all.V()+"边数:"+all.E());
		
		
	}
	
	
}
