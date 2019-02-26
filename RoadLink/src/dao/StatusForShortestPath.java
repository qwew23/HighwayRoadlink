package dao;

import java.util.ArrayList;
import java.util.List;

public class StatusForShortestPath {
	public RoadLink roadLink;
	public double cost;
	public ArrayList<String> path;

	public StatusForShortestPath() {
		roadLink = null;
		cost = 0;
		path = new ArrayList<String>(200);
	}

	public StatusForShortestPath(RoadLink r) {
		// TODO Auto-generated constructor stub
		roadLink = r;
		cost = 0;
		path = new ArrayList<String>(200);
	}

	public StatusForShortestPath(RoadLink r, double c) {
		// TODO Auto-generated constructor stub
		roadLink = r;
		cost = c;
		path = new ArrayList<String>(200);
	}

	public StatusForShortestPath(RoadLink r, double c, List<String> p) {
		// TODO Auto-generated constructor stub
		roadLink = r;
		cost = c;
		path = new ArrayList<String>(p);
	}

	public StatusForShortestPath(StatusForShortestPath obj) {
		roadLink = new RoadLink(obj.roadLink);
		cost = obj.cost;
		path = new ArrayList<String>(obj.path);
	}

	public void append_Status_Path_Cost(StatusForShortestPath obj) {
		if (!path.isEmpty() && !obj.path.isEmpty() && path.get(path.size() - 1).equals(obj.path.get(0))) {
			path.remove(path.size() - 1);
		}
		path.addAll(obj.path);
		roadLink = obj.roadLink;
		cost += obj.cost;
	}

	public void add_RoadLink(RoadLink obj) {
		roadLink = obj;
		cost += obj.length;
		path.add(obj.ID);
	}
}
