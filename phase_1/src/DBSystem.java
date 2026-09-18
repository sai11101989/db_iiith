//package iiith.sstomar.db;

/**
 * 
 */
//package iiith.sstomar.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import iiith.sstomar.db.*;

/**
 * @author sstomar
 *
 */
public class DBSystem
{
  
	 
	 private static final String CLASS_NAME="iiith.sstomar.db.DBSystem";
	 
	 private LinkedHashMap <String, ArrayList <DataStoreMetadata>> mTableMetadataMap; 
	 private DataStore mDataStore ;	
	 private ConfigDetail mConfigDetail;
	 private CacheManager mCacheManager;
	 
	/**
	 * 
	 */
	public DBSystem()
	{
		 // TODO Auto-generated constructor stub
		 this.mTableMetadataMap = new  LinkedHashMap <String, ArrayList <DataStoreMetadata>>();
		 this.mDataStore = new DataStore();	
		 this.mConfigDetail = ConfigDetail.getInstance();
		 this.mCacheManager = CacheManager.getInstance();
	}
	
	
	/* You need to read the configuration file and extract the page size
	   and number of pages (these two parameter together define the
	   maximum main memory you can use). Values are in number of
	   bytes.
	   
	   You should read the names of the tables from the configuration file.
	   You can assume that the table data exists in a file named
	   <table_name>.csv at the path pointed by the config parameter
	   PATH_FOR_DATA.
	   
	   You will need other metadata information given in config file for
	   future deliverables. */
	
	public void readConfig(String configFilePath)
	{
		 //#1 Reads configuration files and populates in-memory 
		 //   configDetail object with the same detail.  
	  FileUtils.readConfigFile(configFilePath, mConfigDetail);	  
	  //#2 This call creates data-file for each if they are not present. 
	  mConfigDetail.getAllTableDetail();			
	}
	
	/* 
	 * The data present in each table needs to be represented in pages.
				Read the file corresponding to each table line by line (for now
				assume 1 line = 1 record)
				Maintain a mapping from PageNumber to (StartingRecordId,
				EndingRecordId) in memory.
				You can assume unspanned file organisation and record length will
				not be greater than page size. 
		*/
	
	public void populateDBInfo()
	{
		 String module = CLASS_NAME + ".populateDBInfo";
		 Set<String> tableList = mConfigDetail.getTableList();
		 
		 mTableMetadataMap.clear();
		 //Log.message(module, "BEGIN");		 
		 
		 // For each table, populate metadata. 
		 for (String tableName : tableList) 
		 { 	 	 
		 	
		 	 // for each table: read data
		 	  //ArrayList <DataStoreMetadata> pageMetadata = mDataStore.getMetadata(mConfigDetail.getTableDataFile(tableName), tableName);
		  	
		 	  ArrayList <DataStoreMetadata> pageMetadata = mDataStore.getMetadata2(mConfigDetail.getTableDataFile(tableName), tableName);
			  //dataStore.readDataFile(configDetail.getTableDataFile(tableName), tableName, tableMetadataMap);
		  	
		 	 mTableMetadataMap.put(tableName, pageMetadata);		 	 
   }		 
		 		 
		 //Log.message(module, "END");
	}
	
	
	/* 
	 * Get the corresponding record of the specified table.
				DO NOT perform I/O every time. Each time a request is received, if
				the page containing the record is already in memory, return that
				record else bring corresponding page in memory. You are supposed
				to implement LRU page replacement algorithm for the same. Print
				HIT if the page is in memory, else print
				MISS <pageNumber> where <pageNumber> is the page number of
				memory page which is to be replaced. (You can assume page
				numbers starting from 0. So, you have total 0 to <NUM_PAGES 1>
				pages.) 
				
	*/	
	
	public String getRecord(String tableName, int recordId)
	{ 
		 String record = null; 
		 	 
		 // It contain : page#1 0 4
		 //              page#2 5 7
		 ArrayList <DataStoreMetadata> pageMetadata = mTableMetadataMap.get(tableName);
	
		 int pageNo = -1; // pageno# starts from zero 
		 DataStoreMetadata [] dataStoreMetadataArr = null;
		 int pageStartRowId = -1;
		 int pageEndRowId = -1;
		 
		 //System.out.println("====================================="); 
		 
		 if(pageMetadata != null)
		 {
			 	for(int i=0; i< pageMetadata.size(); i++)
			 	{
			 		 //System.out.println("Page no.-> " + pageMetadata.get(i).getPageNo() 
			 			//	+ " fromRowID->" +pageMetadata.get(i).getFromROWID() + ", ToRowID->" + pageMetadata.get(i).getToROWID()); 		 		
			 		
			 			if(recordId >= pageMetadata.get(i).getFromROWID() && 
	 		 		 recordId <= pageMetadata.get(i).getToROWID() ) 
			 		 {
			 		 	 pageNo =  pageMetadata.get(i).getPageNo();
			 		 	 pageStartRowId =  pageMetadata.get(i).getFromROWID();
			 		 	 pageEndRowId = pageMetadata.get(i).getToROWID();
			 		 	 break;
			 		 }			
			 	}
		 }	/////////////// 	 	 
		 
	
		 if(pageNo== -1)
		 {		 	
		 	 System.out.println("Record "+ recordId +" does not exist in file " + tableName +".csv");
		 	 return "**** ERROR ****"; 		 	
		 }
		 {
		 	 // pageNo. found, check if pageNo exist in Cache. 
		 	 record = mCacheManager.getRecord(tableName, pageNo, recordId, pageStartRowId, pageEndRowId);		 	 
		 	 //System.out.println( "tableName->" + tableName + " Record# " + recordId + "[" + record +"]");
		 }		 
		 // get PageNo. in which this record exist. 
		 return record;
	}
	
	/* 
	 * Get the last page for the corresponding Table in main memory, if
				not already present.
				
				If the page has enough free space, then append the record to the
				page else, get new free page and add record to the new page.
				
				Flush modified page to the disk immediately. 
				
				ISHAYA BHATT 
				   -	"<attribute 1 value>,<attribute 2 value>, ..." 
				   - There is no restriction on number of pages that can be assigned to a particular table. 	 
				   - Page replacement is Global. 
					
		*/	
	public void insertRecord(String tableName, String record)
	{
	  String module = CLASS_NAME +".insertRecord";
		 		 
		 // Log.message(module, "BEGIN:" +  tableName +", [" + record +"]");
		 
	  ArrayList <DataStoreMetadata> metadataArrayList = null;
	  if (mTableMetadataMap.containsKey(tableName))
	  {
	  	 metadataArrayList = mTableMetadataMap.get(tableName);
	  	 
	  	 if(metadataArrayList.size() > 0)
	  	 {
	  	 	 DataStoreMetadata lastPageMetadata = metadataArrayList.get(metadataArrayList.size()-1);
	  	 	 int lastPage = lastPageMetadata.getPageNo();
	  	 	 
	  	 	 // Check if last page Data is in memory? 
	  	 	 ArrayList <String> records =  mCacheManager.getTablePageRecordsFromCache(tableName, lastPage);
	  	 	 int sizeInBytes = 0; 
	  	 	 if(records !=null)
	  	 	 {
	  	 	 	 for(int i=0; i< records.size(); i++)
	  	 	 	 {
	  	 	 	 	sizeInBytes = sizeInBytes + records.get(i).length() + ConfigDetail.RECORD_DELIMITER.length();
	  	 	 	 }	
	  	 	 	 
	  	 	 	 // Check if current page can accommodate this records. 
	  	 	 	 if ((sizeInBytes + record.length() /* without new line */ ) <= mConfigDetail.getPageSize()) 
	  	 	 	 {
	  	 	 	 	 // Last records in array. 
	  	 	 	 	 records.add(records.size(), record);
	  	 	 	 	 
	  	 	 	 	 mCacheManager.addDataPageToCache(tableName, lastPage, records);
	  	 	 	 	 
	  	 	 	 	 // may want to re/populate db info. populateDBInfo(); 
	  	 	 	 	 //
	  	 	 	 	 mDataStore.writeData(mConfigDetail.getTableDataFile(tableName), tableName, record);
	  	 	 	 	 return;
	  	 	 	 }
	  	 	 	 else
	  	 	 	 {
	  	 	 	 	 //// Add to New page Cache 
	  	 	 	   ArrayList <String> records2 = new ArrayList <String>();
	  	 	 	   records2.add(record);
	  	 	 	   mCacheManager.addDataPageToCache(tableName, lastPage+1, records2);
	  	 	 	   ////
	  	 	 	   System.out.println("else->" + record); 
	  	 	 	 	 // write new page to file 
	  	 	 	 	 mDataStore.writeData(mConfigDetail.getTableDataFile(tableName), tableName, record);
	  	 	 	 	 return;
	  	 	 	 }
	  	 	 }
	  	 }	  	 
	  }
		 // dataStore.writeData: does following tasks
		 //  #1. Parse record data from <column1 column1-data, column2 column2-data, ...>
		 //  #2. Brings last-data-page in main-memory and 
		 //  #3. Write parsed data to data-file
	  //System.out.println("record->" + record); 
	  mDataStore.writeData(mConfigDetail.getTableDataFile(tableName), tableName, record);
		 
		 // Log.message(module, "END");
		 // for testing only
		 // dataStore.readData(configDetail.getDataPath() + File.separator + tableName + configDetail.FILE_EXT);
	} ///// end of 

}
