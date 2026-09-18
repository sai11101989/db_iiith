package iiith.sstomar.db;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

/**
 * 
 * This class holds the init properties from config file
 * 
 * @author sstomar
 *
 */
public class ConfigDetail
{
	 public static final String FILE_EXT=".csv";
	 public static final String COLUMN_DELIMITER=",";
	 public static final String RECORD_DELIMITER= System.getProperty("line.separator");
	 //public static final String RECORD_DELIMITER= "\n";
		
	 private int PAGE_SIZE;
	 private int NUM_PAGES;
	 private String PATH_FOR_DATA;
	 
	 private int noOfTables;
	 
	 
	 // Store table column mapping 
	 private final HashMap<String, Column []> tableColumnMap;
	 
	 private static ConfigDetail configDetail = null;
	 
	 private ConfigDetail()
		{
			// TODO Auto-generated constructor stub
	 	tableColumnMap = new HashMap<String, Column []>();	
	 	
		}
	 
	 /**
	  * 
	  * @return
	  */
		public static ConfigDetail getInstance()
		{
				if(configDetail == null) 
				{
					configDetail = new ConfigDetail();
	   }
		  return configDetail;			 
		}	
		
		/*============================ Setter and Getter methods =======*/
  
		public void setPageSize(int page_size)
		{
			 PAGE_SIZE = page_size;			
		}
		public void setPageSize(String page_size)
		{
			 PAGE_SIZE = new Integer(page_size.trim()).intValue();			
		}
		public int getPageSize()
		{
			 return PAGE_SIZE;
		}
		
		/**
		 * 
		 * @param num_pages
		 */
		public void setNumPages(int num_pages)
		{
			 NUM_PAGES = num_pages;			
		}		
		public void setNumPages(String num_pages)
		{
			NUM_PAGES = new Integer(num_pages).intValue();			
		}
		public int getNumPages()
		{
			 return NUM_PAGES;
		}
		
		/**
		 * 
		 * @param path
		 */
		public void setDataPath(String path)
		{
			 PATH_FOR_DATA = path;			
		}	
		
		public String getDataPath()
		{
			 return PATH_FOR_DATA;
		}
		
		
		public void setTableDetail(String tableName, Column [] columns)
		{
			tableColumnMap.put(tableName, columns);
			
		}
		
		
		public void setNoOfTables(int count)
		{
			 this.noOfTables = count;
		}
		
		public int getNoOfTables()
		{
			 return this.noOfTables;
		}
		
  /**
   * 
   * @param tableName
   * @return
   */
		public Column [] getTableDetail(String tableName)
		{
			Column [] columns = tableColumnMap.get(tableName);
						
			//for (int i = 0; i < columns.length; i++) 
			//{    
				 //System.out.println(columns[i]);
   //}			
			
			return columns;
			
		}
		
		
		//
		/*
		public void getTableDetail(String tableName)
		{
			Column [] columns = tableColumnMap.get(tableName);
			
			System.out.println("BEGIN: ======================Column List =======");
			
			for (int i = 0; i < columns.length; i++) 
			{    
				 System.out.println(columns[i]);
   }			
			
			System.out.println("END: ======================Column List =======");
		}
		*/
		
		public void getAllTableDetail()
		{
			
			DataStore dataStore = new DataStore();
			
  	//iterating over keys only
			for (String table : tableColumnMap.keySet()) 
			{
			  			  
			  // CREATE database file for a table
			  dataStore.createDataFile(PATH_FOR_DATA + File.separator + table + FILE_EXT);
			  
			  // get table columns
			  getTableDetail(table);  
			  
			}
			
		} //// end of method 
		
		
		public Set<String> getTableList()
		{
			 return tableColumnMap.keySet(); 
		}
		
		/**
		 * 
		 * @param tableName
		 * @return
		 */
		public String getTableDataFile(String tableName)
		{
			 return getDataPath() + File.separator + tableName + configDetail.FILE_EXT;			
		}
		
		
} ///// end of class 
