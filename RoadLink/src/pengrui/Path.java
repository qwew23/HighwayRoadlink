package pengrui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.RoadLink;
import generateTopology.Main;

public class Path {
	private static String shortestPath="G:/地图/djjkstra最短路径输出/路网拓扑结果/收费站最短距离20180508225319.csv";
	private static String shortestLinkFile="E:/work/outTopology/贵州最短路径";
	private static String tollStation="E:/work/新建文件夹 (2)/收费站.xlsx";
	private static String chedao="E:/work/新建文件夹 (2)/shoufeichedao.csv";
	private static String nameOdLink="E:/work/outTopology/河南中间文件/henannameOdLink.csv";
	
	private static String trade="D:/2018-01";
	private static String provName="E:/work/outTopology/guizhou.csv";
	
	public static String getValue(Cell cell){
		if(cell.getCellType()==cell.CELL_TYPE_NUMERIC){
			DecimalFormat df = new DecimalFormat("0"); 
			String strCell = df.format(cell.getNumericCellValue());
			return strCell;
		}else if(cell.getCellType()==cell.CELL_TYPE_STRING){
			return cell.getStringCellValue();
		}
		else{
			return cell.getStringCellValue();
		}
	}
	/**
	 * 通过收费车道id前14位匹配收费站id，得到每个收费车道所在的收费站名称
	 * @param inTollStation 收费站数据
	 * @param inCheDao	收费车道数据
	 * @return
	 */
	public static Map<String,String> getCheDaoToll(String inTollStation,String inCheDao,String prov){
		Map<String,String> mapToll=new HashMap<>();
		Map<String,String> mapCheDaoTollName=new HashMap<>();
		boolean isXlsx=false;
		if(inTollStation.endsWith("xlsx")){
			isXlsx=true;
		}
		try{
			String encoding="UTF-8";
			String encoding1="GBK";
			InputStream input=new FileInputStream(inTollStation);
			Workbook wb=null;
			if(isXlsx){
				wb=new XSSFWorkbook(input);
			}else{
				wb=new HSSFWorkbook(input);
			}
			Sheet sheet=wb.getSheetAt(0);
			for(int rowNum=1;rowNum<=sheet.getLastRowNum();rowNum++){
				Row row=sheet.getRow(rowNum);
				if(row!=null){
					String idToll=getValue(row.getCell(0));
					String province=getValue(row.getCell(7));
					String stationName=getValue(row.getCell(4));
					if(province.equals(prov)){
						mapToll.put(idToll, stationName);
					}
				}
			}
			wb.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		BufferedReader reader=Main.getReader(inCheDao, "GBK");
		try{
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",15);
				String cheDao=data[0].replaceAll("\"", "").trim();
				String province=data[7].replaceAll("\"", "").trim();
				if(province.equals(prov)){
					if(cheDao.length()>18){
						String tollId=cheDao.substring(0, 14);
						if(mapToll.containsKey(tollId)){
							String stationName=mapToll.get(tollId);
							mapCheDaoTollName.put(cheDao, stationName);
						}
					}
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
//		for(String key:mapCheDaoTollName.keySet()){
//			System.out.println(key+","+mapCheDaoTollName.get(key));
//		}
		return mapCheDaoTollName;
	}
	/**
	 * 从最短路径输出的各文件名（poi收费站名）和收费数据中的收费站名匹配，输出车道号对应的poi收费站名
	 * @param shortestLinkFile 最短路径输出的各文件名（poi收费站名）路径
	 * @param inTollStation	收费站数据
	 * @param inChedao	收费车道数据
	 */
	public static Map<String,String> matchChedaoName(String shortestLinkFile,String inTollStation,String inChedao,String prov){
		Map<String,String> mapCheDaoTollName=getCheDaoToll(inTollStation,inChedao,prov);//保存车道id对应的收费站名称
		File file=new File(shortestLinkFile);
		List<String> listName=Arrays.asList(file.list());	
		Map<String,List<String>> map=new HashMap<>();//暂存收费站名称对应的多个poi收费站名称
		Map<String,String> mapCheDaoToName=new HashMap<>();//保存车道id对应的poi收费站名称
		for(String chedao:mapCheDaoTollName.keySet()){
			String cheDaoStationName=mapCheDaoTollName.get(chedao);
			for(int i=0;i<listName.size();i++){
				String poiName=listName.get(i);
				String shortName=poiName.replaceAll("收费站", "");
				if(cheDaoStationName.contains(shortName)){
					if(map.containsKey(cheDaoStationName)){
						List<String> list=map.get(cheDaoStationName);
						if(!list.contains(poiName)){
							list.add(poiName);
							map.put(cheDaoStationName, list);
						}
					}else{
						List<String> list=new ArrayList<>();
						list.add(poiName);
						map.put(cheDaoStationName, list);
					}
				}
			}
			if(map.containsKey(cheDaoStationName)){
				List<String> list=map.get(cheDaoStationName);
				if(list.size()==0){
					mapCheDaoToName.put(chedao, list.get(0));
				}else{
					String maxName="";
					int flag=0;
					for(int i=0;i<list.size();i++){
						String inListName=list.get(i);
						int a=0;
						for(int j=0;j<cheDaoStationName.length();j++){
							if(inListName.contains(""+cheDaoStationName.charAt(j))){
								a++;
							}
						}
						if(flag<a){
							flag=a;
							maxName=inListName;
						}
					}
					mapCheDaoToName.put(chedao, maxName);
				}
			}
		}
//		for(String key:mapCheDaoToName.keySet()){
//			
//			System.out.println(key+":"+mapCheDaoToName.get(key));
//		}
		System.out.println("map:"+map.size());
		return mapCheDaoToName;
	}
	
	public static void getNameOdLink(String shortestLinkFile,String outPath){
		BufferedWriter writer=Main.getWriter(outPath, "GBK");
		File file=new File(shortestLinkFile);
		List<String> list=Arrays.asList(file.list());
		try{
			for(int i=0;i<list.size();i++){
				String inStationName=list.get(i);
				String path=shortestLinkFile+"/"+inStationName;
				
				
				
				File fileIn=new File(path);
				
				if(fileIn.list()==null)
				{
					System.out.println(path);
					
				}
				List<String> listIn=Arrays.asList(fileIn.list());
				for(int j=0;j<listIn.size();j++){
					String outStationName=listIn.get(j);
					String pathIn=path+"/"+outStationName;
					File fileLast=new File(pathIn);
					List<String> listLast=Arrays.asList(fileLast.list());
					if(listLast.size()>0){
						String pathLast=pathIn+"/"+listLast.get(0);
						BufferedReader reader=Main.getReader(pathLast, "GBK");
						String line="";
						String shortestLinkPath="";
						while((line=reader.readLine())!=null){
							String[] data=line.split(",",20);
							String linkId=data[0];
							if(shortestLinkPath.equals("")){
								shortestLinkPath=linkId;
							}else{
								shortestLinkPath+=","+linkId;
							}
						}
						reader.close();
//						System.out.println(inStationName+"->"+outStationName+":"+shortestLinkPath);
						writer.write(inStationName+";"+outStationName+";"+shortestLinkPath+"\n");
					}
				}
				System.out.println(inStationName+" finish!");
			}
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
//	public static void readNameOdLink(String nameOdLink){
//		BufferedReader reader=Main.getReader(nameOdLink, "GBK");
//		BufferedWriter writer=Main.getWriter("G:/地图/djjkstra最短路径输出/河北/nameOdLink1.csv", "GBK");
//		try{
//			String line="";
//			while((line=reader.readLine())!=null){
//				String[] data=line.split(",");
//				String inStationName=data[0];
//				String outStationName=data[1];
//				String a="";
//				for(int i=2;i<data.length;i++){
//					if(a.equals("")){
//						a=data[i];
//					}else{
//						a=a+","+data[i];
//					}
//				}
////				System.out.println(inStationName+";"+outStationName+";"+a);
//				writer.write(inStationName+";"+outStationName+";"+a+"\n");
//			}
//			reader.close();
//			writer.flush();
//			writer.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public static Map<String,String> readNameOdLink(String nameOdLink){
		Map<String,String> map=new HashMap<>();
		BufferedReader reader=Main.getReader(nameOdLink, "GBK");
		try{
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(";",3);
				String inStationName=data[0];
				String outStationName=data[1];
				String linkPath=data[2];
				map.put(inStationName+","+outStationName, linkPath);
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	public static void readTrade(String trade,String out,String prov) throws IOException{
		BufferedWriter writer=Main.getWriter(out, "GBK");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File file=new File(trade);
		List<String> list=Arrays.asList(file.list());
		String[] data=null;
		String id="";
		String outProvince="";
		String inLaneId="";
		String outLaneId="";
		String inProvince="";
		//内存不足，一次只能读取半个月的数据
		for(int i=16;i<70;i++){
			String inPath=trade+"/"+list.get(i);
			BufferedReader reader=Main.getReader(inPath, "utf-8");
			try{
				String line="";
				while((line=reader.readLine())!=null){
//					System.out.println(line);
                   try{
					data=line.split(",",26);
					id=data[1];
					outProvince=id.substring(5,7);
					inLaneId=data[5].substring(0, 21);
					outLaneId=id.substring(0, 21);
					inProvince=inLaneId.substring(5,7);
                   }
                   catch(Exception e)
                   {
                	   System.out.println("wrong data"+line);
                	   continue;
                   }

					if(inProvince.equals(prov)&&outProvince.equals(prov)){
						String inCx=data[14];
						String outCx=data[15];
						String inPlate=data[11];
						String outPlate=data[12];
						String weight=data[4];
						String inTime=data[6].split("T")[0]+" "+data[6].split("T")[1];
						String outTime=sdf1.format(sdf.parse(id.substring(21,35)));
						String fee=data[3];
						writer.write(inPlate+";"+inCx+";"+inTime+";"+inLaneId+";"+outPlate+";"+outCx+";"+outTime+";"+outLaneId+";"+weight+";"+fee+"\n");
					}
				}
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(inPath);
		}
		writer.flush();
		writer.close();
	}
	
	public static void addMap(Map<String,Integer> map,String key,int cs){
		if(map.containsKey(key)){
			map.put(key, map.get(key)+cs);
		}else{
			map.put(key, cs);
		}
	}
	public static void writeMap(Map<String,Integer> map,String out) throws IOException{
		BufferedWriter writer=Main.getWriter(out, "GBK");
		for(String key:map.keySet()){
			writer.write(key+","+map.get(key)+"\n");
		}
		writer.flush();
		writer.close();
	}
	public static void statics(String prov){
		Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//读取出入站名称对应的最短路径的一系列路链值，以入口站名称、出口站名称为key，路链值为value读入hashmaps
		System.out.println("read mapNameOdLink finish! mapNameOdLink.size():"+mapNameOdLink.size());
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,prov);//将poi中车道名称与收费数据中收费站名称对应，并以收费车道id为key，对上的收费站名称为value，存入hashmap
		System.out.println("read mapChedaoName finish! mapChedaoName.size():"+mapChedaoName.size());
		BufferedReader reader=Main.getReader(provName, "GBK");
		Map<String,Integer> mapOdCount=new HashMap<>();
		int count=0;
		try{
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(";",10);
				String inLaneId=data[3];
				String outLaneId=data[7];
				String inProvince=inLaneId.substring(5, 7);
				String outProvince=outLaneId.substring(5, 7);
				if(inProvince.equals("13")&&outProvince.equals("13")&&mapChedaoName.containsKey(inLaneId)&&mapChedaoName.containsKey(outLaneId)){
					String inStationName=mapChedaoName.get(inLaneId);
					String outStationName=mapChedaoName.get(outLaneId);
					String key=inStationName+","+outStationName;
					if(mapOdCount.containsKey(key)){
						mapOdCount.put(key, mapOdCount.get(key)+1);
					}else{
						mapOdCount.put(key, 1);
					}
				}else{
					count++;
				}
			}
			reader.close();
			int a=0;
			int amount=0;
			int countInLuduan=0;
			int countContainLuduan=0;
			int countPartOfLuduan=0;
			Map<String,Integer> mapInfuluenceOd=new HashMap<>();
			Map<String,Integer> mapInfuluenceStartStation=new HashMap<>();
			Map<String,Integer> mapInfuluenceEndStation=new HashMap<>();
			for(String od:mapOdCount.keySet()){
				if(mapNameOdLink.containsKey(od)){
					String link=mapNameOdLink.get(od);
					if(link.contains("17754310")||link.contains("84837503")||link.contains("2842623")||link.contains("17754312")){
						if(od.contains("新乐收费站")&&od.contains("石家庄机场收费站")){
							countInLuduan+=mapOdCount.get(od);
						}else if((link.contains("17754310")&&link.contains("84837503"))||(link.contains("2842623")||link.contains("17754312"))){
							countContainLuduan+=mapOdCount.get(od);
						}else if(od.contains("新乐收费站")||od.contains("石家庄机场收费站"))
						{
							countPartOfLuduan+=mapOdCount.get(od);
						}
						String inStation=od.split(",")[0];
						String outStation=od.split(",")[1];
						addMap(mapInfuluenceStartStation,inStation,mapOdCount.get(od));
						addMap(mapInfuluenceOd,od,mapOdCount.get(od));
						addMap(mapInfuluenceEndStation,outStation,mapOdCount.get(od));
					}
					a+=mapOdCount.get(od);
				}else{
//					System.out.println(od);
				}
				amount+=mapOdCount.get(od);
			}
			System.out.println("车道未匹配上的交易数："+count);
			System.out.println("车道匹配上的交易中，有最短路径的交易数："+a);
			System.out.println("车道匹配上的交易数："+amount);
			
			System.out.println("OD在该阻断路段内的交易数："+countInLuduan);
			System.out.println("OD经过该完整阻断路段的交易数："+countContainLuduan);
			System.out.println("OD经过部分该路段的交易数："+countPartOfLuduan);
			writeMap(mapInfuluenceStartStation,"E:/work/outTopology/结果统计/受影响入口站.csv");
			writeMap(mapInfuluenceOd,"E:/work/outTopology/结果统计/受影响od.csv");
			writeMap(mapInfuluenceEndStation,"E:/work/outTopology/结果统计/受影响出口站.csv");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void getInfuluence(){
		Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//读取出入站名称对应的最短路径的一系列路链值，以入口站名称、出口站名称为key，路链值为value读入hashmaps
		System.out.println("read mapNameOdLink finish!");
		int count=0;
		System.out.println("mapNameOdLink.size():"+mapNameOdLink.size());
		for(String name:mapNameOdLink.keySet()){
			String odLink=mapNameOdLink.get(name);
			if(odLink.contains("17754310")||odLink.contains("84837503")||odLink.contains("2842623")||odLink.contains("17754312")){
				if(name.contains("新乐收费站")&&name.contains("石家庄机场收费站")){
					System.out.println(name);
				}
				count++;
			}
		}
		System.out.println("count:"+count);
	}
	
	public static void testify(){
		String in="G:/地图/djjkstra最短路径输出/OD最短路径（不同收费广场）/石家庄机场收费站/安阳东收费站/351_3299999999999_440929.csv";
		String out="C:/Users/pengrui/Desktop/testify.csv";
		try{
			BufferedReader reader=Main.getReader(in, "GBK");
			BufferedWriter writer=Main.getWriter(out, "GBK");
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",");
				String lng=data[7];
				String lat=data[8];
				writer.write(lng+","+lat+"\n");
			}
			reader.close();
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void statics1(String prov){
		Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//读取出入站名称对应的最短路径的一系列路链值，以入口站名称、出口站名称为key，路链值为value读入hashmaps
		System.out.println("read mapNameOdLink finish! mapNameOdLink.size():"+mapNameOdLink.size());
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,prov);//将poi中车道名称与收费数据中收费站名称对应，并以收费车道id为key，对上的收费站名称为value，存入hashmap
		System.out.println("read mapChedaoName finish! mapChedaoName.size():"+mapChedaoName.size());
		BufferedReader reader=Main.getReader(provName, "GBK");
	//	Map<String,Integer> mapOdCount=new HashMap<>();
		Map<String,Integer> mapLinkCount=new HashMap<>();
		Map<String,String> mapOD=new HashMap<>();
		
		BufferedWriter writer=Main.getWriter("D:/test/out.csv", "GBK");
		
		int countAll=0;
		int countMatched=0;
		try{
			
			String line="";
			while((line=reader.readLine())!=null){
				countAll++;
				String[] data=line.split(";",10);
				String inLaneId=data[3];
				String outLaneId=data[7];
				String inProvince=inLaneId.substring(5, 7);
				String outProvince=outLaneId.substring(5, 7);
				if(inProvince.equals("41")&&outProvince.equals("41")&&mapChedaoName.containsKey(inLaneId)&&mapChedaoName.containsKey(outLaneId)){
					String inStationName=mapChedaoName.get(inLaneId);
					String outStationName=mapChedaoName.get(outLaneId);
					String key=inStationName+","+outStationName;
					
					if(mapNameOdLink.containsKey(key)){
						countMatched++;
						String link=mapNameOdLink.get(key);
			            String linkod[]=link.split(",");
			            for(String linkele:linkod)
			            {
			            	writer.write(linkele+",");
			            	for(int i=0;i<10;i++)
			            	{
			            		if(i==3||i==7)
			            			continue;
			            		else if(i==9)
			            			writer.write(data[i]+"\n");
			            		else
			            			writer.write(data[i]+",");
			            		
			            		
			            	}
			            }
			        }
					
					
                    
				}
			}
			reader.close();			
			writer.flush();
			writer.close();
			
			System.out.println("共计有"+countAll+"条交易数据");
			System.out.println("共计有"+countMatched+"条可匹配的交易数据");
			
	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testTrade(String trade,String out,String prov) throws IOException{
		BufferedWriter writer=Main.getWriter(out, "GBK");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File file=new File(trade);
		List<String> list=Arrays.asList(file.list());
		for(int i=0;i<list.size();i++){
			String inPath=trade+"/"+list.get(i);
			BufferedReader reader=Main.getReader(inPath, "utf-8");
			try{
				String line="";
				while((line=reader.readLine())!=null){

					String[] data=line.split(",",26);
					String id=data[1];
					String outProvince=id.substring(5,7);
					String inLaneId=data[5].substring(0, 21);
					String outLaneId=id.substring(0, 21);
					String inProvince=inLaneId.substring(5,7);
					
					if(inProvince.equals(prov)&&outProvince.equals(prov))
						writer.write(line+"\n");
				    
					}
				
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(inPath);
		}
		writer.flush();
		
	//	writer.fl
		writer.close();
	}
	
	
/*	public static void staticsJam(String provName)
	{
		
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,provName);
		BufferedReader r=Main.getReader(inTrade, "GBK");
		String line="";
		String subline="";
		
		String inStation="";
		String outStation="";
		String inLaneId="";
		String outLaneId="";
		String fileUrl="";
		String subFileUrl="";
		String []ele=null;
		HashMap<String,Integer> rCount = new HashMap<>();
		
		
		while((line=r.readLine())!=null)
		{    
			ele=line.split(";");
			inLaneId=ele[3];
			outLaneId=ele[7];
			if(mapChedaoName.containsKey(inLaneId)&&mapChedaoName.containsKey(outLaneId)){
				String inStationName=mapChedaoName.get(inLaneId);
				String outStationName=mapChedaoName.get(outLaneId);
				fileUrl="E:/work/outTopology/贵州最短路径"+"/"+inStationName+"/"+outStationName;
				File file=new File(fileUrl);
				List<String> fileList=Arrays.asList(file.list());
				if(fileList.size()!=0)
				{
					subFileUrl=fileUrl+"/"+fileList.get(0);
					BufferedReader readPath=Main.getReader(subFileUrl, "utf-8");
					while((subline=readPath.readLine())!=null)
					{
						RoadLink rd=new RoadLink(subline);
						if(rCount.containsKey(rd.ID))
						{
							rCount.put(rd.ID, rCount.get(rd.ID)+1);

						}
						else
						{
							rCount.put(rd.ID,1);
						}
					}
					}
				}
				
		}
		BufferedWriter w=Main.getWriter(out, "GBK");
		
		for(String eleRID:rCount.keySet())
		{
			w.write(eleRID+","+rCount.get(eleRID)+"\n");
			
		}
		
		
		
	}*/

	
	
	
	
	
	
	
	public static void main(String[] args) throws ParseException, IOException{
		//getNameOdLink(shortestLinkFile,nameOdLink);//读取最短路径文件下，每个出入站名称对应的最短路径的一系列路链值
//	    Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//读取出入站名称对应的最短路径的一系列路链值，以入口站名称、出口站名称为key，路链值为value读入hashmap
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,"GUI_ZHOU");//将poi中车道名称与收费数据中收费站名称对应，并以收费车道id为key，对上的收费站名称为value，存入hashmap
   	// 
		readTrade(trade,provName,"41");//从收费数据中挑选河北数据，精简字段输出,第三个参数代表省份编号
    //	testTrade("D:/test","E:/work/outTopology/test.csv","50");
	//	statics1("HE_NAN");
//		getInfuluence();
//		testify();
	}
}
