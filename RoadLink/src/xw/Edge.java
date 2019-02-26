package xw;


public class Edge implements Comparable<Edge>{
    int v;
    int w;
    int weight;
    
    public Edge(int v1,int w1,int weight1)
    {
    	v=v1;
    	w=w1;
    	weight=weight1;
    	
    }
    
    public int either()
    {
    	return v;
    }
    
    public int other(int vertical) throws Exception
    {
    	if(vertical==v)
    		return w;
    	else if(vertical==w)
    		return v;
    	else
    		throw new Exception("Wrong Input Vertical");
    }
    
    public int compareTo(Edge b)
    {
    
          return weight-b.weight;
    
    }
    
    
}
