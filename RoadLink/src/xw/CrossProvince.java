package xw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import generateTopology.Main;

public class CrossProvince {

	public static void readTrade(String trade) throws IOException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File file = new File(trade);
		List<String> list = Arrays.asList(file.list());
		BufferedWriter writer = Main.getWriter("D:/cross5-1.csv", "GBK");
		HashMap<String, List<String>> res = new HashMap<>();
		HashMap<String, String> Station = mapStation("E:/work/跨省/收费站csv.csv");
		try {
			for (int i = 5; i < 10; i++) {
				String inPath = trade + "/" + list.get(i);
				BufferedReader reader = Main.getReader(inPath, "utf-8");

				String[] data = null;
				String inStation = "", outStation = "", inPlate = "", outPlate = "", inKind = "", outKind = "",
						inTime = "", outTime = "";

				String line = "";
				while ((line = reader.readLine()) != null) {

					data = line.split(",", 26);
					if (data.length > 16) {
						try {
							inStation = data[5].substring(0, 14);
							outStation = data[1].substring(0, 14);
							inPlate = data[11];
							outPlate = data[12];
							inKind = data[14];
							outKind = data[15];
							inTime = data[6].split("T")[0] + " " + data[6].split("T")[1];
							outTime = sdf1.format(sdf.parse(data[1].substring(21, 35)));
						} catch (Exception e) {
							System.out.println("数据有误" + line);
						}
						if (Station.containsKey(inStation)&& Station.containsKey(outStation)) {
							if ((inPlate.equals(outPlate)) && (inKind.equals(outKind)) && (inPlate.length() > 8)
									&& (!inPlate.contains("00000")) && (!inPlate.contains("12345"))
									&& (!outPlate.contains("00000")) && (!outPlate.contains("12345"))
									&& (Station.get(inStation).equals("PROVINCE_STATION_COMMON")
											|| Station.get(outStation).equals("PROVINCE_STATION_COMMON"))) {
								if (res.containsKey(inPlate)) {
									res.get(inPlate).add(inStation + "," + outStation + "," + inTime + "," + outTime);
									res.put(inPlate, res.get(inPlate));
								} else {
									List<String> ele = new ArrayList<String>();
									ele.add(inStation + "," + outStation + "," + inTime + "," + outTime);
									res.put(inPlate, ele);

								}
							}
						}
					}
				}

				reader.close();

				System.out.println(inPath);
			}

			for (String s : res.keySet()) {
				if (res.get(s).size() > 1) {
					for (String ele : res.get(s)) {
						writer.write(s + "," + ele + "\n");

					}

				}

			}
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static HashMap<String, Integer> result(String in) throws IOException {

		BufferedReader reader = Main.getReader(in, "GBK");

		HashMap<String, String> CommonStation = mapStation("E:/work/跨省/收费站csv.csv");
		HashMap<String, Integer> Result = new HashMap<String, Integer>();
		// plate , inStation+","+outStation+","+inTime+","+outTime

		List<String[]> list = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String line = "";
		String[] data = null;
		String inPlate = "";
		String tmpPlate = "";
		int test = 0;
		while ((line = reader.readLine()) != null) {
			data = line.split(",");
			test++;
			tmpPlate = data[0];
			if (!tmpPlate.equals(inPlate)) {
				inPlate = tmpPlate;
				if (list.size() > 1) {
					Collections.sort(list, new Comparator<String[]>() {

						@Override
						public int compare(String[] s1, String[] s2) {

							Date d1 = null, d2 = null;
							try {
								d1 = sdf.parse(s1[4]);
								d2 = sdf.parse(s2[4]);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (d1.before(d2) || d1.equals(d2)) {
								return -1;
							} else {
								return 1;
							}

						}
					});
					int start = 0, end = 0;

					for (int i = 0; i < list.size() - 1; i++)

					{
						if (!list.get(i)[4].equals(list.get(i + 1)[3])) {
							if (start != end) {
								String key = list.get(start)[2].substring(5, 7) + ","
										+ list.get(end)[1].substring(5, 7);

								if (Result.containsKey(key))
									Result.put(key, Result.get(key) + 1);
								else
									Result.put(key, 1);
							}

							start = i + 1;
							end = start;
						} else {
							end++;
							if ((i == list.size() - 2)) {
								String key = list.get(start)[2].substring(5, 7) + ","
										+ list.get(end)[1].substring(5, 7);
								if (Result.containsKey(key))
									Result.put(key, Result.get(key) + 1);
								else
									Result.put(key, 1);
							}
						}

					}

				}
				list.clear();
				if (CommonStation.containsKey(data[1]) && CommonStation.containsKey(data[2])) {
					if (CommonStation.get(data[1]).equals("PROVINCE_STATION_COMMON")
							|| CommonStation.get(data[2]).equals("PROVINCE_STATION_COMMON"))

					{

						list.add(data);
					}
				}

			} else {
				if (CommonStation.containsKey(data[1]) && CommonStation.containsKey(data[2])) {
					if (CommonStation.get(data[1]).equals("PROVINCE_STATION_COMMON")
							|| CommonStation.get(data[2]).equals("PROVINCE_STATION_COMMON"))

					{

						list.add(data);
					}
				}
			}

		}
		System.out.println(test);
		return Result;

	}

	public static HashMap<String, String> mapStation(String in) throws IOException {
		BufferedReader readStation = Main.getReader(in, "utf-8");
		String line = "";
		HashMap<String, String> res = new HashMap<String, String>();
		String data[] = null;
		while ((line = readStation.readLine()) != null) {
			data = line.split(",");
			try {
				if (data[1] != "0") {
					res.put(data[0], data[2]);
					res.put(data[1], data[2]);
				}
			} catch (Exception e) {
				System.out.println(line);

			}

		}
		return res;

	}

	public static <S, T> void writeMap(HashMap<S, T> arg, String out) throws IOException {
		BufferedWriter writer = Main.getWriter(out, "GBK");
		for (S s : arg.keySet()) {
			writer.write(s + "," + arg.get(s) + "\n");

		}

		writer.close();

	}

	public static void main(String args[]) throws IOException {
		//readTrade("D:/2018-05");
		 HashMap<String, Integer> res = result("D:/cross5-2.csv");
		 writeMap(res,"D:/fool5-2.csv");

	}

}
