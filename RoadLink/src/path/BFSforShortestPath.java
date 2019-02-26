package path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dao.RoadLink;
import dao.StatusForShortestPath;
import util.Config;

public class BFSforShortestPath {
	public static void main(String[] args) {
		try {
//			BufferedReader file_roadlink = new BufferedReader(new FileReader("F:\\地图\\quanguotuopu.csv"));
			BufferedReader file_roadlink = new BufferedReader(new FileReader("G:/地图/hebeiTopology.csv"));
			BufferedWriter file_SPResult = new BufferedWriter(new FileWriter("G:/地图/输出数据/shortest.csv"));
			HashMap<String, RoadLink> id_RoadLink_hash = new HashMap<String, RoadLink>();
			HashMap<String, List<String>> stationID_RoadLinkID_hash = new HashMap<String, List<String>>();
			List<String> stationID=new ArrayList<String>(220);
			String line;
			while ((line = file_roadlink.readLine()) != null) {
				// System.out.println(line);
				String[] array_line = line.split(",");
				RoadLink loop_roadlink = new RoadLink(array_line,true);
				id_RoadLink_hash.put(loop_roadlink.ID, loop_roadlink);
				if (loop_roadlink.station != null) {
					if (stationID_RoadLinkID_hash.containsKey(loop_roadlink.station)) {
						List<String> temp_str_list = stationID_RoadLinkID_hash.get(loop_roadlink.station);
						temp_str_list.add(loop_roadlink.ID);
						stationID_RoadLinkID_hash.put(loop_roadlink.station, temp_str_list);
					} else {
						List<String> temp_str_list = new ArrayList<String>(6);
						temp_str_list.add(loop_roadlink.ID);
						stationID_RoadLinkID_hash.put(loop_roadlink.station, temp_str_list);
					}
					if(!stationID.contains(loop_roadlink.station)){
						stationID.add(loop_roadlink.station);
					}
				}
			}
			file_roadlink.close();
			newDir(stationID,Config.SHORTEST_PATH_DIR);
			for (Iterator<String> iterator = stationID_RoadLinkID_hash.keySet().iterator(); iterator.hasNext();) {
				String loop_station = iterator.next();
				System.out.println(loop_station);
				for (Iterator<String> iterator1 = stationID_RoadLinkID_hash.keySet().iterator(); iterator1
						.hasNext();) {
					String loop_station1 = iterator1.next();
					if (loop_station.equals(loop_station1)) {
						//System.out.println(loop_station + "," + loop_station1 + "," + String.valueOf(0));
						file_SPResult.write(loop_station + "," + loop_station1 + "," + String.valueOf(0)+"\r\n");
						continue;
					}
					double minCost = 0;
					List<String> minCostPath=new ArrayList<String>(200);
					for (String loop_roadlink_id : stationID_RoadLinkID_hash.get(loop_station)) {
						for (String loop_roadlink_id1 : stationID_RoadLinkID_hash.get(loop_station1)) {
							ArrayDeque<StatusForShortestPath> queue = new ArrayDeque<StatusForShortestPath>();
							List<String> init_path=new ArrayList<String>(200);
							init_path.add(loop_roadlink_id);
							queue.add(new StatusForShortestPath(id_RoadLink_hash.get(loop_roadlink_id),0,init_path));
							while (!queue.isEmpty()) {
								StatusForShortestPath loop_status = queue.poll();
								if (!loop_status.roadLink.visit) {
									loop_status.roadLink.visit=true;
									if (loop_status.roadLink.ID.equals(loop_roadlink_id1)) {
										if (minCost == 0) {
											minCost = loop_status.cost;
											minCostPath=loop_status.path;
										} else if (minCost > loop_status.cost) {
											minCost = loop_status.cost;
											minCostPath=loop_status.path;
										}
										break;
									}
									if (loop_status.roadLink.next_ID == null)
										continue;
									for (String next_RoadLink_id : loop_status.roadLink.next_ID) {
										RoadLink temp_roadlink = id_RoadLink_hash.get(next_RoadLink_id);
										// System.out.println(loop_status.roadLink.ID);
										double temp_cost = loop_status.cost + temp_roadlink.length;
										List<String> temp_list=new ArrayList<String>(loop_status.path);
										temp_list.add(temp_roadlink.ID);
										queue.add(new StatusForShortestPath(temp_roadlink, temp_cost,temp_list));
									}
									id_RoadLink_hash.put(loop_status.roadLink.ID,loop_status.roadLink);
								}
							}
							//这块代码是将所有visit状态设置为false；
							for (Iterator<String> iterator2 = id_RoadLink_hash.keySet().iterator(); iterator2.hasNext();) {
								String reset_ID=iterator2.next();
								RoadLink reset_RoadLink = id_RoadLink_hash.get(reset_ID);
								reset_RoadLink.visit=false;
								id_RoadLink_hash.put(reset_ID,reset_RoadLink);
							}
						}
					}
					BufferedWriter loop_file=new BufferedWriter(new FileWriter(Config.SHORTEST_PATH_DIR+
							loop_station+"\\"+loop_station1+"\\"+String.valueOf((int)minCost)+"_"
							+String.valueOf(Config.water++)+".csv"));
					for(String loop_path_roadlink_id : minCostPath){
						loop_file.write(id_RoadLink_hash.get(loop_path_roadlink_id).toString()+"\r\n");
					}
					loop_file.close();
					file_SPResult.write(loop_station + "," + loop_station1 + "," + String.valueOf(minCost)+"\r\n");
				}
			}
			file_SPResult.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void newDir(List<String> array,String path){
		int length=array.size();
		for(int i=0;i<length;i++){
			for(int i1=0;i1<length;i1++){
				File loop_file=new File(path+array.get(i)+"\\"+array.get(i1));
				if(!loop_file.exists())
					loop_file.mkdirs();
			}
		}
	}
	/*
	public static void identify_Path(String dir_path,String o_id,String d_id,List<String> roadlink_path,double cost){
		try {
			BufferedWriter file=new BufferedWriter(new FileWriter(dir_path+o_id+"\\"+d_id+"\\"+String.valueOf((int)cost)+"_"+String.valueOf(Config.water++)+".csv"));
			for(String str : roadlink_path){
				file.write();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
