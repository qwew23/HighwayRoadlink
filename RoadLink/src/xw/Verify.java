package xw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

import generateTopology.Main;
import pengrui.Path;

public class Verify {
   
	public static HashMap<String,String> readStationMap(String in) throws IOException
	{
		BufferedReader r=Main.getReader(in, "GBK");
		String line="";
		HashMap<String,String> id_nameStation=new HashMap<>();
		String ele[]=null;
		while((line=r.readLine())!=null)
		{
			ele=line.split(",");
			id_nameStation.put(ele[0], ele[1]);
			
		}
		return id_nameStation;
	}
	
	public static void getODstatics(String inTrade,String inStation,String out) throws IOException
	{
		BufferedReader r=Main.getReader(inTrade, "GBK");
		BufferedWriter w=Main.getWriter(out, "GBK");
		
		String line="";
		String []ele=null;
		HashMap<String,Integer> res=new HashMap<>();
		HashMap<String,String> station=readStationMap(inStation);
		String inStationID="";
		String outStationID="";
		
		while((line=r.readLine())!=null)
		{
			ele=line.split(";");
			try{
			inStationID=ele[3].substring(0,14);
			outStationID=ele[7].substring(0,14);
			}
			catch(Exception e)
			{
				System.out.println("wrong data"+line);
				continue;
			}
			
			String inStationName=station.containsKey(inStationID)?station.get(inStationID):inStationID;
			String outStationName=station.containsKey(outStationID)?station.get(outStationID):outStationID;
			
		    if(res.containsKey(inStationName+","+outStationName))
		    {
		    	res.put(inStationName+","+outStationName, res.get(inStationName+","+outStationName)+1);
		    }
		    else if(res.containsKey(outStationName+","+inStationName))
		    {
		    	res.put(outStationName+","+inStationName, res.get(outStationName+","+inStationName)+1);
		    }
		    else
		    {
		    	res.put(inStationName+","+outStationName,1);
		    }
			
		}
		
		for(String s:res.keySet())
		{
			w.write(s+","+res.get(s)+"\n");
			
		}
		r.close();
		w.close();
	}
	
	public static void  main(String args[]) throws IOException
	{
	//	Path.readTrade("D:/2018-05", "D:/outShandong.csv", "37");
		
		getODstatics("D:/outShandong.csv", "C:/Users/16507/Desktop/掌行通收费站.csv", "E:/work/标识站/OD統計山东.csv");
		
		
	}
	
}
