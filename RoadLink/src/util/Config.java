package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Config {
	public static SimpleDateFormat DF=new SimpleDateFormat("yyyyMMddHHmmss");
	public static double EARTH_RADIUS=6371393;    //����뾶 ��
	public static int SPEED_UPBOUND=250;   //�������Լ��
	public static int SPEED_LOWBOUND=0;    //�������Լ��
	public static int HIGHWAY_COUNT=34000;    //��֮ǰ�������˽⵽�����ٹ�··���ܹ���14000������
	public static int NUMBERS_OF_ROADLINK=34000;
	public static int water=0;
	
	public static String SHORTEST_PATH_DIR="G:/��ͼ/�������/";
	public static String OD_DIJKSTRA_DIR="E:/work/outTopology/�㽭���·��\\";
	public static String STATION_SHORTEST_PATH_LENGTH="E:/work/outTopology/�㽭���·��\\�շ�վ��̾���"+DF.format(new Date())+".csv";  //���ɵ��м��ļ�
	
	// ���µļ���CQ ��֪����˭�ġ�����
	public static String SHORTEST_PATH_DIR_CQ="G:/����/�������/";
	public static String OD_DIJKSTRA_DIR_CQ="G:/����/djjkstra���·�����/OD���·������ͬ�շѹ㳡��\\";
	public static String STATION_SHORTEST_PATH_LENGTH_CQ="G:/����/djjkstra���·�����/·�����˽��\\�շ�վ��̾���"+DF.format(new Date())+".csv";  //���ɵ��м��ļ�

}
