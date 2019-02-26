package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Config {
	public static SimpleDateFormat DF=new SimpleDateFormat("yyyyMMddHHmmss");
	public static double EARTH_RADIUS=6371393;    //地球半径 米
	public static int SPEED_UPBOUND=250;   //最高限速约定
	public static int SPEED_LOWBOUND=0;    //最低限速约定
	public static int HIGHWAY_COUNT=34000;    //从之前的数据了解到，高速公路路链总共是14000条左右
	public static int NUMBERS_OF_ROADLINK=34000;
	public static int water=0;
	
	public static String SHORTEST_PATH_DIR="G:/地图/输出数据/";
	public static String OD_DIJKSTRA_DIR="E:/work/outTopology/浙江最短路径\\";
	public static String STATION_SHORTEST_PATH_LENGTH="E:/work/outTopology/浙江最短路径\\收费站最短距离"+DF.format(new Date())+".csv";  //生成的中间文件
	
	// 底下的加了CQ 不知道是谁的。。。
	public static String SHORTEST_PATH_DIR_CQ="G:/重庆/输出数据/";
	public static String OD_DIJKSTRA_DIR_CQ="G:/重庆/djjkstra最短路径输出/OD最短路径（不同收费广场）\\";
	public static String STATION_SHORTEST_PATH_LENGTH_CQ="G:/重庆/djjkstra最短路径输出/路网拓扑结果\\收费站最短距离"+DF.format(new Date())+".csv";  //生成的中间文件

}
