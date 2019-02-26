package pengrui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import generateTopology.Main;

public class write {
	private static String PNamehebeiMid="D:/mapinfo_ditu/地图/level2/level2/hebei/other/PNamehebei.mid";
	private static String POIhebeiMid="D:/mapinfo_ditu/地图/level2/level2/hebei/index/POIhebei.mid";
	private static String CHebeiMid="G:/最新地图/road2018q2/hebei/road/Chebei.mid";
	private static String CHebeiMif="G:/最新地图/road2018q2/hebei/road/Chebei.mif";
	private static String outPath16Hebei="G:/地图/收费站数据/16PoiHeibei.csv";
	private static String outPath18Hebei="G:/地图/收费站数据/18PoiHeibei.csv";
	
	private static String shortestLinkFile="G:/地图/djjkstra最短路径输出/OD最短路径（不同收费广场）";
	private static String tollStation="G:/地图/收费站.xlsx";
	private static String chedao="G:/地图/shoufeichedao.csv";
	
	private static String PNamechongqingMid="D:/mapinfo_ditu/地图/level2/level2/chongqing/other/PNamechongqing.mid";
	private static String POIchongqingMid="D:/mapinfo_ditu/地图/level2/level2/chongqing/index/POIchongqing.mid";
	private static String CChongqingMid="G:/最新地图/road2018q2/chongqing/road/Cchongqing.mid";
	private static String CChongqingMif="G:/最新地图/road2018q2/chongqing/road/Cchongqing.mif";
	private static String outPath16Chongqing="G:/地图/收费站数据/16PoiChongqing.csv";
	private static String outPath18Chongqing="G:/地图/收费站数据/18PoiChongqing.csv";
	
	private static String PNamehenanMid="D:/mapinfo_ditu/地图/level2/level2/henan/other/PNamehenan.mid";
	private static String POIhenanMid="D:/mapinfo_ditu/地图/level2/level2/henan/index/POIhenan.mid";
	private static String CHenanMid="G:/最新地图/road2018q2/henan/road/Chenan.mid";
	private static String CHenanMif="G:/最新地图/road2018q2/henan/road/Chenan.mif";
	private static String outPath16Henan="G:/地图/收费站数据/16PoiHenan.csv";
	private static String outPath18Henan="G:/地图/收费站数据/18PoiHenan.csv";
	public static Map<String,String> getTollStationPoiId(String path){
		Map<String,String> poiIdToll=new HashMap<>();
		BufferedReader reader=Main.getReader(path,"GBK");
		String line="";
		try{
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",10);
				String poiId=data[0].replaceAll("\"", "").trim();
				String name=data[2].replaceAll("\"", "").trim();
				if(name.endsWith("收费站")){
					poiIdToll.put(poiId, name);
				}
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return poiIdToll;
	}

	public static void write16PoiGps(String path,Map<String,String> poiIdToll,String outPath16){
		BufferedReader reader=Main.getReader(path,"GBK");
		BufferedWriter writer=Main.getWriter(outPath16, "GBK");
		String line="";
		try{
			writer.write("poiId,stationName,lng,lat\n");
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",23);
				String poiId=data[7].replaceAll("\"", "").trim();
				String linkId=data[12].replaceAll("\"", "").trim();
				String lng=data[5].replaceAll("\"", "").trim();
				String lat=data[6].replaceAll("\"", "").trim();
				if(poiIdToll.containsKey(poiId)){
					String tollName=poiIdToll.get(poiId);
					writer.write(poiId+","+tollName+","+lng+","+lat+"\n");
				}
			}
			reader.close();
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void readCMid(String path,Map<Integer,String> idWithPointMessage){
		BufferedReader reader=Main.getReader(path,"GBK");
		String line="";
		int lineId=0;
		try{
			while((line=reader.readLine())!=null){
				lineId++;
				String[] data=line.split(",",10);
				String inLinkId=data[3].replaceAll("\"", "").trim();
				String outLinkId=data[4].replaceAll("\"", "").trim();
				String CondType=data[5].replaceAll("\"", "").trim();
				idWithPointMessage.put(lineId, inLinkId+";"+outLinkId+";"+CondType);
			}
			reader.close();
//			System.out.println("Rhebei.mid:"+lineId);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void addGpsPointCmid(String path,Map<Integer,String> idWithPointMessage,String outPath18) throws IOException{
		BufferedReader reader=Main.getReader(path,"GBK");
		BufferedWriter writer=Main.getWriter(outPath18, "GBK");
		writer.write("linkId,lng,lat\n");
		String line="";
		int lineId=0;
		try{
			while((line=reader.readLine())!=null){
				if(line.startsWith("Point")){
					String f="";
					lineId++;
					String[] data=line.split(" ",3);
					f+=data[1]+" "+data[2];
					String value=idWithPointMessage.get(lineId);
					value=value+";"+f;
					idWithPointMessage.put(lineId, value);
				}
			}
			reader.close();
			System.out.println("mif:"+lineId);
		}catch(Exception e){
			e.printStackTrace();
		}
		Map<String,List<String>> mapTollCollection=new HashMap<>();
		Set setPoint=idWithPointMessage.entrySet();
		Map.Entry[] entriesPoint = (Map.Entry[])setPoint.toArray(new Map.Entry[setPoint.size()]);
		for(int i=0;i<entriesPoint.length;i++){
			String value=entriesPoint[i].getValue().toString();
			String[] v=value.split(";",4);
			String inLinkId=v[0];
			String outLinkId=v[1];
			String contentType=v[2];
			String gps=v[3];
			String lngLat=gps.split(" ",2)[0]+","+gps.split(" ",2)[1];
			if(contentType.equals("3")){
				writer.write(inLinkId+","+lngLat+"\n");
				writer.write(outLinkId+","+lngLat+"\n");
			}
		}
		writer.flush();
	}
	
	public static Map<String,List<String>> getPoiNameGps(String PoiHeibei16,String PoiHeibei18){
		Map<String,String> map=new HashMap<>();
		BufferedReader readerPoiHeibei16=Main.getReader(PoiHeibei16, "GBK");
		try{
			readerPoiHeibei16.readLine();
			String line="";
			while((line=readerPoiHeibei16.readLine())!=null){
				String[] data=line.split(",",4);
				String lng=data[2];
				String lat=data[3];
				String name=data[1];
				map.put(lng+","+lat, name);
			}
			readerPoiHeibei16.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		Map<String,List<String>> mapNameGps=new HashMap<>();//poi名称对应的多个GPS
		BufferedReader readerPoiHeibei18=Main.getReader(PoiHeibei18, "GBK");
		try{
			readerPoiHeibei18.readLine();
			String line="";
			while((line=readerPoiHeibei18.readLine())!=null){
				String[] data=line.split(",",3);
				String lng=data[1];
				String lat=data[2];
				if(map.containsKey(lng+","+lat)){
					String name=map.get(lng+","+lat);
					if(mapNameGps.containsKey(name)){
						List<String> list=mapNameGps.get(name);
						list.add(lng+","+lat);
						mapNameGps.put(name, list);
					}else{
						List<String> list=new ArrayList<>();
						list.add(lng+","+lat);
						mapNameGps.put(name, list);
					}
				}
			}
			readerPoiHeibei16.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapNameGps;
	}
	
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
	public static Map<String,String> getCheDaoToll(String inTollStation,String inCheDao){
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
					if(province.equals("HE_BEI")){
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
				if(province.equals("HE_BEI")){
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
	public static Map<String,String> matchChedaoName(String shortestLinkFile,String inTollStation,String inChedao){
		Map<String,String> mapCheDaoTollName=getCheDaoToll(inTollStation,inChedao);//保存车道id对应的收费站名称
		File file=new File(shortestLinkFile);
		List<String> listName=Arrays.asList(file.list());	
		Map<String,List<String>> map=new HashMap<>();//暂存收费站名称对应的多个poi收费站名称
		Map<String,String> mapStationNameToPoiName=new HashMap<>();//保存收费数据中的收费站名称对应的唯一poi收费站名称
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
					mapStationNameToPoiName.put(cheDaoStationName, list.get(0));
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
					mapStationNameToPoiName.put(cheDaoStationName, maxName);
				}
			}
		}
//		for(String key:mapCheDaoToName.keySet()){
//			
//			System.out.println(key+":"+mapCheDaoToName.get(key));
//		}
		System.out.println("map:"+map.size());
		return mapStationNameToPoiName;
	}
	
	public static void writeStationNameGps() throws IOException{
		BufferedWriter writer=Main.getWriter("G:/地图/stationGps.csv", "GBK");
		Map<String,List<String>> mapPoiNameGps=getPoiNameGps("G:/地图/16PoiHeibei.csv","G:/地图/18PoiHeibei.csv");
		Map<String,String> mapStationNameToPoiName=matchChedaoName(shortestLinkFile,tollStation,chedao);
		for(String stationName:mapStationNameToPoiName.keySet()){
			String poiName=mapStationNameToPoiName.get(stationName);
			List<String> list=mapPoiNameGps.get(poiName);
//			System.out.println(poiName+","+list.get(0));
			writer.write(poiName+","+list.get(0)+"\n");
		}
		writer.flush();
		writer.close();
	}
	
	public static void writeZhanshi(){
		BufferedReader readerNameGps=Main.getReader("G:/地图/stationGps.csv", "GBK");
		BufferedWriter writer1=Main.getWriter("G:/地图/展示/1.csv", "GBK");
		BufferedWriter writer2=Main.getWriter("G:/地图/展示/2.csv", "GBK");
		Map<String,String> mapNameGps=new HashMap<>();
		try{
			String line="";
			while((line=readerNameGps.readLine())!=null){
				String[] data=line.split(",",3);
				String lng=data[1];
				String lat=data[2];
				String name=data[0];
				mapNameGps.put(name,lng+","+lat);
			}
			readerNameGps.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		BufferedReader reader=Main.getReader("G:/地图/djjkstra最短路径输出/河北/受影响出口站.csv", "GBK");
		try{
			String line="";
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",2);
				String name=data[0];
				String value=data[1];
				String gps=mapNameGps.get(name);
				writer1.write("\""+name+"\""+":["+gps+"],"+"\n");
				writer2.write("{name:\""+name+"\", value: "+value+"},\n");
			}
			reader.close();
			writer1.flush();
			writer1.close();
			writer2.flush();
			writer2.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void write(String path,String outPath){
		File file=new File(path);
		List<String> list=Arrays.asList(file.list());
		for(int i=0;i<list.size();i++){
			String pName=path+"/"+list.get(i)+"/other/PName"+list.get(i)+".mid";
			String poi=path+"/"+list.get(i)+"/index/POI"+list.get(i)+".mid";
			String out=outPath+"/"+list.get(i)+".csv";
			Map<String,String> poiIdToll=getTollStationPoiId(pName);
			write16PoiGps(poi,poiIdToll,out);
			System.out.println(list.get(i)+" finish!");
		}
	}
	public static void write(String path) throws IOException{
		File file=new File(path);
		List<String> list=Arrays.asList(file.list());
		BufferedWriter writer=Main.getWriter("I:/16poi.csv", "gbk");
		for(int i=0;i<list.size();i++){
			String province=list.get(i).split("\\.")[0];
			String in=path+"/"+list.get(i);
			BufferedReader reader=Main.getReader(in, "GBK");
			String line="";
			reader.readLine();
			while((line=reader.readLine())!=null){
				writer.write(line+","+province+"\n");
			}
			reader.close();
			System.out.println(province+" finish!");
		}
		writer.flush();
		writer.close();
	}
	
	public static void read(String path){
		try{
			BufferedReader reader=Main.getReader(path, "gbk");
			String line="";
			int amount=0;
			int count=0;
			while((line=reader.readLine())!=null){
				String[] data=line.split(",",3);
				String d=data[2];
				if(!d.equals("0")&&!d.equals("null")){
					amount++;
					double distance=Double.parseDouble(d);
					if(distance<=2){
						count++;
					}
				}
			}
			reader.close();
			System.out.println("od距离不为空："+amount);
			System.out.println("od距离小于2公里："+count);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException{
//		Map<String,String> poiIdToll=getTollStationPoiId(PNamehenanMid);
//		write16PoiGps(POIhenanMid,poiIdToll,outPath16Henan);
		
//		Map<Integer,String> idWithPointMessage=new HashMap<>();
//		readCMid(CHenanMid,idWithPointMessage);
//		addGpsPointCmid(CHenanMif,idWithPointMessage,outPath18Henan);
//		writeStationNameGps();//输出对应上的收费站的一个车道的gps，有很多车道，这里只输出一个，为了展示
//		writeZhanshi();
//		write("D:/mapinfo_ditu/地图/level2/level2","G:/地图/收费站数据/各省16年poi数据");
//		write("I:/各省16年poi数据");
		
		read("G:/重庆/djjkstra最短路径输出/路网拓扑结果/收费站最短距离20180601185300.csv");
	}
}
