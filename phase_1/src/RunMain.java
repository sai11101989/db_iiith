/*====== 
 * 
 * This class is used to execute classes from
 * iiit.sstomar.db packages 
 * 
 */
//package iiith.sstomar.db;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class RunMain
{

	public RunMain()
	{
		// TODO Auto-generated constructor stub
	}
 
	/**
	 * 
	 * @param args
	 */
	
	public static void Tester()  throws Exception
	{
  String configFile = "C:\\Users\\sai11101989\\workspace\\DB_Phase1\\sstomar_config.txt";
  String dataFile = "C:\\Users\\sai11101989\\workspace\\DB_Phase1\\docs\\countries.csv";
  
   int input[] = {0,1,2,1,2,2,3,41,9,39,28,1,30,38,39,31,-1, 42,28};
   //int input[] = {0,1,2,1,2,2,3,4,4,1,41,9,39,28,1,30,38,39,31,-1, 42,28};
 		
		List<String> lines = new ArrayList<String>();
		RandomAccessFile f = new RandomAccessFile(dataFile, "r");
		
		String s = new String();
		while((s=f.readLine()) != null) {
			lines.add(s);
		}
		
		DBSystem dbs = new DBSystem();
		dbs.readConfig(configFile);
		dbs.populateDBInfo();
		int i=0;
		for(int inp : input) {
			if(inp !=-1) {
				String op = dbs.getRecord("countries",inp);
				if(lines.get(inp).equals(op)){
					
				} else {
					System.out.println("Fail for record number " + inp + " expected = "+ lines.get(inp) + " actual= " + op);
					System.exit(-1);
				}
			}
			else
				dbs.insertRecord("countries", "record");
		}
		System.exit(0);
	}
	

	
	
	public static void main(String[] args) throws Exception 
	{
		 // TODO Auto-generated method stub
		 
		 /////////////////////////////////////////
		 //Tester();
		 
		 //System.exit(0);
		 
		 ////////////////////////////////
		 DBSystem dbSystem  = new DBSystem();
		 
		 StringBuilder strb = new StringBuilder();
		 
		 //*********************** Read Config file TEST**********************
		 dbSystem.readConfig("C:\\Users\\sai11101989\\workspace\\DB_Phase1\\sstomar_config.txt");
		 
		 //dbSystem.readConfig("D:\\ECLIPSE_WS\\IIITH\\config.txt");
		 
				
		// ***********************insert TEST **********************
		// insert into sh_resource 
	  
			for(int i=0; i<5; i++)
			{			
				 strb.append("res_id \""+ i + "\", res_name \"MY PRINTER-" + i + "\", res_loc \"3RD FLOORE HIMALAYA" + i +"\"");			
			 
			  dbSystem.insertRecord("sh_resource", strb.toString());			
			  strb.setLength(0);				
		}
				
		 
		 ///////insert into sh_resource 
			
			for(int i=0; i<35; i++)
			{			
				 strb.append("res_id "+ i + ", res_usage 200" + i + ", res_used_by sstomar from oracle" + i);			
				 dbSystem.insertRecord("sh_resource_usage", strb.toString());			
				 strb.setLength(0);				
			}					
		 
			
			///////////////////////Delivery:Project:1/////////////////////////
			
			dbSystem.populateDBInfo();
	 
			dbSystem.getRecord("sh_resource", 2);
			dbSystem.getRecord("sh_resource", 4);
			dbSystem.getRecord("sh_resource", 5);
			dbSystem.getRecord("sh_resource", 8);
			dbSystem.getRecord("sh_resource", 4);
			dbSystem.getRecord("sh_resource", 34);
			
		 //////////////////////////////////////////////////////////////////////

	}
 
}
