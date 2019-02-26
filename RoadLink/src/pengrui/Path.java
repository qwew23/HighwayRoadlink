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
	private static String shortestPath="G:/��ͼ/djjkstra���·�����/·�����˽��/�շ�վ��̾���20180508225319.csv";
	private static String shortestLinkFile="E:/work/outTopology/�������·��";
	private static String tollStation="E:/work/�½��ļ��� (2)/�շ�վ.xlsx";
	private static String chedao="E:/work/�½��ļ��� (2)/shoufeichedao.csv";
	private static String nameOdLink="E:/work/outTopology/�����м��ļ�/henannameOdLink.csv";
	
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
	 * ͨ���շѳ���idǰ14λƥ���շ�վid���õ�ÿ���շѳ������ڵ��շ�վ����
	 * @param inTollStation �շ�վ����
	 * @param inCheDao	�շѳ�������
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
	 * �����·������ĸ��ļ�����poi�շ�վ�������շ������е��շ�վ��ƥ�䣬��������Ŷ�Ӧ��poi�շ�վ��
	 * @param shortestLinkFile ���·������ĸ��ļ�����poi�շ�վ����·��
	 * @param inTollStation	�շ�վ����
	 * @param inChedao	�շѳ�������
	 */
	public static Map<String,String> matchChedaoName(String shortestLinkFile,String inTollStation,String inChedao,String prov){
		Map<String,String> mapCheDaoTollName=getCheDaoToll(inTollStation,inChedao,prov);//���泵��id��Ӧ���շ�վ����
		File file=new File(shortestLinkFile);
		List<String> listName=Arrays.asList(file.list());	
		Map<String,List<String>> map=new HashMap<>();//�ݴ��շ�վ���ƶ�Ӧ�Ķ��poi�շ�վ����
		Map<String,String> mapCheDaoToName=new HashMap<>();//���泵��id��Ӧ��poi�շ�վ����
		for(String chedao:mapCheDaoTollName.keySet()){
			String cheDaoStationName=mapCheDaoTollName.get(chedao);
			for(int i=0;i<listName.size();i++){
				String poiName=listName.get(i);
				String shortName=poiName.replaceAll("�շ�վ", "");
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
//		BufferedWriter writer=Main.getWriter("G:/��ͼ/djjkstra���·�����/�ӱ�/nameOdLink1.csv", "GBK");
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
		//�ڴ治�㣬һ��ֻ�ܶ�ȡ����µ�����
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
		Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//��ȡ����վ���ƶ�Ӧ�����·����һϵ��·��ֵ�������վ���ơ�����վ����Ϊkey��·��ֵΪvalue����hashmaps
		System.out.println("read mapNameOdLink finish! mapNameOdLink.size():"+mapNameOdLink.size());
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,prov);//��poi�г����������շ��������շ�վ���ƶ�Ӧ�������շѳ���idΪkey�����ϵ��շ�վ����Ϊvalue������hashmap
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
						if(od.contains("�����շ�վ")&&od.contains("ʯ��ׯ�����շ�վ")){
							countInLuduan+=mapOdCount.get(od);
						}else if((link.contains("17754310")&&link.contains("84837503"))||(link.contains("2842623")||link.contains("17754312"))){
							countContainLuduan+=mapOdCount.get(od);
						}else if(od.contains("�����շ�վ")||od.contains("ʯ��ׯ�����շ�վ"))
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
			System.out.println("����δƥ���ϵĽ�������"+count);
			System.out.println("����ƥ���ϵĽ����У������·���Ľ�������"+a);
			System.out.println("����ƥ���ϵĽ�������"+amount);
			
			System.out.println("OD�ڸ����·���ڵĽ�������"+countInLuduan);
			System.out.println("OD�������������·�εĽ�������"+countContainLuduan);
			System.out.println("OD�������ָ�·�εĽ�������"+countPartOfLuduan);
			writeMap(mapInfuluenceStartStation,"E:/work/outTopology/���ͳ��/��Ӱ�����վ.csv");
			writeMap(mapInfuluenceOd,"E:/work/outTopology/���ͳ��/��Ӱ��od.csv");
			writeMap(mapInfuluenceEndStation,"E:/work/outTopology/���ͳ��/��Ӱ�����վ.csv");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void getInfuluence(){
		Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//��ȡ����վ���ƶ�Ӧ�����·����һϵ��·��ֵ�������վ���ơ�����վ����Ϊkey��·��ֵΪvalue����hashmaps
		System.out.println("read mapNameOdLink finish!");
		int count=0;
		System.out.println("mapNameOdLink.size():"+mapNameOdLink.size());
		for(String name:mapNameOdLink.keySet()){
			String odLink=mapNameOdLink.get(name);
			if(odLink.contains("17754310")||odLink.contains("84837503")||odLink.contains("2842623")||odLink.contains("17754312")){
				if(name.contains("�����շ�վ")&&name.contains("ʯ��ׯ�����շ�վ")){
					System.out.println(name);
				}
				count++;
			}
		}
		System.out.println("count:"+count);
	}
	
	public static void testify(){
		String in="G:/��ͼ/djjkstra���·�����/OD���·������ͬ�շѹ㳡��/ʯ��ׯ�����շ�վ/�������շ�վ/351_3299999999999_440929.csv";
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
		Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//��ȡ����վ���ƶ�Ӧ�����·����һϵ��·��ֵ�������վ���ơ�����վ����Ϊkey��·��ֵΪvalue����hashmaps
		System.out.println("read mapNameOdLink finish! mapNameOdLink.size():"+mapNameOdLink.size());
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,prov);//��poi�г����������շ��������շ�վ���ƶ�Ӧ�������շѳ���idΪkey�����ϵ��շ�վ����Ϊvalue������hashmap
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
			
			System.out.println("������"+countAll+"����������");
			System.out.println("������"+countMatched+"����ƥ��Ľ�������");
			
	
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
				fileUrl="E:/work/outTopology/�������·��"+"/"+inStationName+"/"+outStationName;
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
		//getNameOdLink(shortestLinkFile,nameOdLink);//��ȡ���·���ļ��£�ÿ������վ���ƶ�Ӧ�����·����һϵ��·��ֵ
//	    Map<String,String> mapNameOdLink=readNameOdLink(nameOdLink);//��ȡ����վ���ƶ�Ӧ�����·����һϵ��·��ֵ�������վ���ơ�����վ����Ϊkey��·��ֵΪvalue����hashmap
		Map<String,String> mapChedaoName=matchChedaoName(shortestLinkFile,tollStation,chedao,"GUI_ZHOU");//��poi�г����������շ��������շ�վ���ƶ�Ӧ�������շѳ���idΪkey�����ϵ��շ�վ����Ϊvalue������hashmap
   	// 
		readTrade(trade,provName,"41");//���շ���������ѡ�ӱ����ݣ������ֶ����,��������������ʡ�ݱ��
    //	testTrade("D:/test","E:/work/outTopology/test.csv","50");
	//	statics1("HE_NAN");
//		getInfuluence();
//		testify();
	}
}
