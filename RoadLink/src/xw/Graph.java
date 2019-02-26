package xw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import generateTopology.Main;

public class Graph
{
     int [][] martix;
     
     public Graph(String in) throws IOException
     {
    	BufferedReader read=Main.getReader(in, "utf-8");
 		String line=read.readLine();
 		int size=Integer.valueOf(line);
 		martix=new int[size][size];
 	    for(int i=0;i<size;i++)
 	    	for(int j=0;j<size;j++)
 	    		martix[i][j]=-1;
 	    
 	    int count=0;
 	    String line1="";
 	    
 	    while((line=read.readLine())!=null)
 	    {
 	    	line1=","+line+",";
 	    	String ele[]=line1.split(",");
 	    	for(int i=0;i<ele.length;i++)
 	    	{
 	    		if(!ele[i].equals(""))
 	    		{	
 	    			try{
 	    			martix[count][i]=Integer.valueOf(ele[i]);
 	    			}catch(Exception e)
 	    			{
 	    				System.out.println(line1);
 	    				
 	    			}
 	    		}
 	    	}
 	    	
 	    	count++;
 	    }
 	    
 	    for(int i=0;i<size;i++)
 	    {
 	    	for(int j=0;j<i;j++)
 	    
 	    	{
 	    		martix[i][j]=martix[j][i];

 	    	}
 	    }
 	    
    	 
    	 
     }
     
     public Graph(List<Edge> ele,int numV)
     {
    	 martix=new int[numV][numV];
    	 for(int i=0;i<numV;i++)
    	 {
    		 for(int j=0;j<numV;j++)
    		 {
    			 martix[i][j]=-1;
    		 }
    		 
    	 }
    	 for(Edge e:ele)
    	 {
    		 martix[e.v][e.w]=e.weight;
    		 martix[e.w][e.v]=e.weight;
    		 
    	 }
    	 
    	 
    	 
     }
     
     
     public Graph() {
		
	}

	public void writeOut(String out) throws IOException
     {
    	 BufferedWriter w=Main.getWriter(out, "utf-8");
    	 for(int i=0;i<this.V();i++)
    	 {
    		 for(int j=0;j<this.V();j++)
    		 {
    			w.write(martix[i][j]+","); 
    			 
    		 }
    		 w.write("\n");
    	 }
    	 w.close();
     }
     
     public Graph  combineGraph(Graph other,List<Edge>combineLine)
     {
    	 Graph res=new Graph();
    	 int size=this.V()+other.V(); 
    	 res.martix=new int [size][size];
    	 for(int i=0;i<size;i++)
    	 {
    		 for(int j=0;j<size;j++)
    		    res.martix[i][j]=-1;
    	 }
    	 
    	 for(int i=0;i<this.V();i++)
    	 {
    		 for(int j=0;j<this.V();j++)
    		    res.martix[i][j]=this.martix[i][j];
    		 
    	 }
    	 for(int i=this.V();i<this.V()+other.V();i++)
    	 {
    		 for(int j=this.V();j<this.V()+other.V();j++)
    		 {
    			 res.martix[i][j]=other.martix[i-this.V()][j-this.V()];
    		 }
    	 }
    	 
    	 for(Edge e:combineLine)
    	 {
    		 res.martix[e.v][e.w]=e.weight;
    	 }
    	 
    	 return res;
     }
     
     
     public boolean compareToBig(Graph big)
     {
    	 
    	 for(int i=0;i<this.V();i++)
    	 {
    		 for(int j=0;j<this.V();j++)
    		 {
    			 if(this.martix[i][j]!=big.martix[i][j])
    			      return false;
    			 
    		 }
    		 
    	 }
    	 
    	 return true;
    	 
     }
     
     public void compareCount(Graph big)
     {
    	 int countThis=0,countBig=0;
    	 for(int i=0;i<this.V();i++)
    	 {
    		 for(int j=0;j<this.V();j++)
    		 {
    			 if(this.martix[i][j]!=-1)
    				 countThis++;
    			 
    			 if(big.martix[i][j]!=-1)
                      countBig++;
    			 
    		 }
    		 
    	 }
    	 
    	 System.out.println("原子图生成树边数:"+countThis/2);
    	 System.out.println("合成图中子图生成树边数:"+countBig/2);
    	 
     }
     
     
     
     public void changeWeight()
     {
    	 int count=1;
    	 for(int i=0;i<this.V();i++)
    	 {
    		 for(int j=0;j<this.V();j++)
    		 {
    			 if(this.martix[i][j]==1)
    				 this.martix[i][j]=count++;
    		 }
    			 
    			 
    	 }
    	 
    	 
     }
     
     public int V()
     {
    	 
    	 return martix[0].length;
    	 
     }
     
     public int E()
     {
    	 int count=0;
    	 for(int i=0;i<V();i++)
    	 {
    		 for(int j=0;j<i;j++)
    		 {
    			 if(martix[i][j]!=-1)
    			       count++;
    			 
    		 }
    	 }
    	 return count;
    	 
     }
     
     public List<Edge> adj(int v)
     {
    	 List<Edge> res=new ArrayList<>();
    	 
    	 for(int i=0;i<V();i++)
    	 {
    		 if(martix[v][i]!=-1)
                 res.add(new Edge(v,i,martix[v][i])); 
    	 }
    	 return res;
     }
     
}
