package path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import dao.RoadLink;
import dao.StatusForShortestPath;
import util.Config;

public class DijkstraStation2 {
	static HashMap<String, RoadLink> id_RoadLink_hash;
	static HashMap<String, List<String>> stationID_RoadLinkID_hash;
	static List<String> stationID;
	static Comparator<StatusForShortestPath> order = new Comparator<StatusForShortestPath>() {
		public int compare(StatusForShortestPath obj1, StatusForShortestPath obj2) {
			if (obj1.cost < obj2.cost)
				return -1;
			else if (obj1.cost == obj2.cost)
				return 0;
			else {
				return 1;
			}
		}
	};

	public static void main(String[] args) {
		try {
//			BufferedReader file_roadlink = new BufferedReader(new FileReader(Config.ROADLINK_MIDDLE_FILE_SUB_OPT));// 读路链拓扑文件
			BufferedReader file_roadlink = new BufferedReader(new FileReader("G:/重庆/cqTopology.csv"));// 读路链拓扑文件
			id_RoadLink_hash = new HashMap<String, RoadLink>();
			stationID_RoadLinkID_hash = new HashMap<String, List<String>>();
			stationID = new ArrayList<String>(220);
			String line;
			while ((line = file_roadlink.readLine()) != null) {
				// System.out.println(line);
				String[] array_line = line.split(",");
				RoadLink loop_roadlink = new RoadLink(array_line, true);
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
					if (!stationID.contains(loop_roadlink.station)) {
						stationID.add(loop_roadlink.station);
					}
				}
			}
			file_roadlink.close();
			BFSforShortestPath.newDir(stationID, Config.OD_DIJKSTRA_DIR_CQ);
			BufferedWriter writer = new BufferedWriter(new FileWriter(Config.STATION_SHORTEST_PATH_LENGTH_CQ));
			for (String origin_station_ID : stationID) {
				System.out.println(origin_station_ID);
				ArrayList<HashMap<String, StatusForShortestPath>> arrayList = new ArrayList<HashMap<String, StatusForShortestPath>>();
				for (String station_roadlink_ID : stationID_RoadLinkID_hash.get(origin_station_ID)) {
					arrayList.add(dijkstra(station_roadlink_ID));
				}
				for (String destination_station_ID : stationID) {
					if (origin_station_ID.equals(destination_station_ID)) {
						writer.write(origin_station_ID + "," + destination_station_ID + "," + 0 + "\r\n");
						continue;
					}
					StatusForShortestPath min_Status = null;
					for (String station_roadlink_ID : stationID_RoadLinkID_hash.get(destination_station_ID)) {
						for (HashMap<String, StatusForShortestPath> hash : arrayList) {
							if (hash.containsKey(station_roadlink_ID))
//								to_dir_dir_File(Config.OD_DIJKSTRA_DIR_CQ, origin_station_ID, destination_station_ID,
//										hash.get(station_roadlink_ID));
							if (min_Status == null || (hash.containsKey(station_roadlink_ID)
									&& hash.get(station_roadlink_ID).cost < min_Status.cost))
								min_Status = hash.get(station_roadlink_ID);
						}
					}
					String min_cost="null";
					if(min_Status!=null)
						min_cost=String.valueOf(min_Status.cost);
					writer.write(origin_station_ID + "," + destination_station_ID + "," + min_cost + "\r\n");
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, StatusForShortestPath> dijkstra(String roadlink_ID) {
		HashMap<String, StatusForShortestPath> id_Status = new HashMap<String, StatusForShortestPath>(
				Config.NUMBERS_OF_ROADLINK / 2);
		Queue<StatusForShortestPath> priorityQueue = new PriorityQueue<StatusForShortestPath>(10, order);
		List<String> init_path = new ArrayList<String>(200);
		init_path.add(roadlink_ID);
		priorityQueue.add(new StatusForShortestPath(id_RoadLink_hash.get(roadlink_ID),
				id_RoadLink_hash.get(roadlink_ID).length, init_path));
		while (!priorityQueue.isEmpty()) {
			StatusForShortestPath loop_Status = priorityQueue.poll();
			id_Status.put(loop_Status.roadLink.ID, loop_Status);
			if (loop_Status.roadLink.next_ID == null)
				continue;
			for (String str : loop_Status.roadLink.next_ID) {
				RoadLink loop_RoadLink = id_RoadLink_hash.get(str);
				if ((!id_Status.containsKey(str)) || (id_Status.containsKey(str)
						&& id_Status.get(str).cost > loop_Status.cost + loop_RoadLink.length)) {
					StatusForShortestPath next_Status = new StatusForShortestPath(loop_Status);
					next_Status.add_RoadLink(loop_RoadLink);
					// id_Status.put(str, next_Status);
					priorityQueue.add(next_Status);
				}
			}
		}
		return id_Status;
	}

	public static void map_toFile(String origin_Roadlink_ID, HashMap<String, StatusForShortestPath> id_Status) {
		for (Iterator<StatusForShortestPath> iterator = id_Status.values().iterator(); iterator.hasNext();) {
			StatusForShortestPath loop_status = iterator.next();
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(Config.OD_DIJKSTRA_DIR_CQ
						+ loop_status.roadLink.ID + "_" + (int) loop_status.cost + "_" + (Config.water++) + ".csv"));
				for (String string : loop_status.path) {
					writer.write(id_RoadLink_hash.get(string).toString() + "\r\n");
				}
				writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void to_dir_dir_File(String file_path, String origin, String destination,
			StatusForShortestPath status) {
		try {
			StringBuilder str = new StringBuilder(file_path);
			String c=""+status.cost;
			c=c.split("\\.")[0]+"_"+c.split("\\.")[1];
			str.append(origin).append("\\").append(destination).append("\\").append(c).append("_")
					.append(Config.water++).append(".csv");
			BufferedWriter writer = new BufferedWriter(new FileWriter(str.toString()));
			for (String string : status.path) {
				writer.write(id_RoadLink_hash.get(string).toString() + "\r\n");
			}
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println(origin + "	" + destination + "	");
		}
	}
}
