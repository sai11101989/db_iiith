package iiith.sstomar.db;


import iiith.sstomar.db.CacheManager.CacheData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
 
/**
 * 
 * @author sstomar
 *
 */
public final class CacheManager
{
		
	// no. of (pages * pagesize) in bytes: but we are strong one page 
		private static int  MAX_SIZE ;
		private CacheData mCacheData; 
		/*
		 * public class LinkedList<E>
       Doubly-linked list implementation of the List and Deque interfaces. 
    
     Data we want to store
      KEY=<TABLE, Page#>
      VALUE=<List OR array of table Records>
      
		 */
		
		private LinkedHashMap <String, CacheData> mTablePageData; 		
		private LinkedList <LinkedHashMap <String, CacheData>> mGlobalLinkedList; 	
	 private static CacheManager mCacheManager = null;
	 private DataStore mDataStore  = null;
	 
	 // Inner class: for record data
		class CacheData
		{		  
		  //String [] recordList;	
		  ArrayList <String> recordList;
		  
		  CacheData(String [] recordList )
			 {
				  this.recordList = (ArrayList<String>) Arrays.asList(recordList);// new ArrayList <String> (recordList);				
			 }			
		  CacheData(ArrayList <String> recordList )
			 {
				  this.recordList = recordList;				
			 }			
		  public String getRecord(int index)
		  {
		  	 return recordList.get(index);
		  }
		  
		  public ArrayList <String> getRecords()
		  {
		  	 return recordList;
		  }		 
		  
		  
		} ///////////////// end of Inner class //////////////////////////
		
		/**
		 * ********** NOT USE ************8
		 * @param maxSize
		 */
		private CacheManager() 
		{										
				// this.maxSize = maxSize;				
				//mGlobalPageList = new LinkedHashMap<String, CacheData>();
				mGlobalLinkedList = new  LinkedList <LinkedHashMap <String, CacheData>>();
			 mDataStore = new DataStore();			 
				//System.out.println("MAX SIZE--->" + MAX_SIZE);
		}
		
		public static CacheManager getInstance ()
		{			
				if(mCacheManager == null) 
				{
					mCacheManager = new CacheManager();
	   }
		  return mCacheManager;			 
		}
		
		public String getKey(String tableName, int pageNo)
		{
			 return tableName+"_"+pageNo;
		}
				
		/**
		 * 
		 * @param tableName
		 * @param pageNo
		 * @param records
		 */
		public void addDataPageToCache(
				  String tableName, 
				  int pageNo, 
				  ArrayList <String> records)
		{
			 
			 MAX_SIZE = ConfigDetail.getInstance().getNumPages(); 
			 
			 CacheData cacheData = new CacheData( records);
			 boolean found = false;
			 String localKey = getKey(tableName, pageNo);
			 
			 mTablePageData = new LinkedHashMap <String, CacheData>();			 
			 mTablePageData.put(localKey, cacheData);			 
							 
			 // System.out.println("Size->" + mGlobalLinkedList.size() + " max size->" + MAX_SIZE); 		 
			 
			 if(mGlobalLinkedList.size() < MAX_SIZE)
			 {			 				 	 
			 	 	 	 
			 	 LinkedHashMap<String, CacheData> localTablePageData = null;		 	 
      
			 	 try
			 	 {//			 if it is first node, DON'T do any thing.
						 	if(!mGlobalLinkedList.getFirst().keySet().contains(localKey) )  
						 	{			 	
						 		 ListIterator<LinkedHashMap<String, CacheData>>	iterator =  mGlobalLinkedList.listIterator(0);		
						 		
						 	 	//System.out.println("Key not at first node ->" + localKey + " -> " + mGlobalLinkedList.getFirst().toString());
						 	 	
						 	 	// mGlobalLinkedList.addFirst(mTablePageData);				 	 	
							 	 // Remove node from List and then add at FIRST position
							 	 while(iterator.hasNext() && !found)
							 	 {
							 	 	 localTablePageData = iterator.next();			
							 	 	
							 	 	 //System.out.println(localKey + " key from map -->" + localTablePageData.get(localKey));
								 		 
							 	 	 if(localTablePageData.containsKey(localKey))
							 	 	 {
							 	 	 	 found = true;
							 	 	 	 //System.out.println("Key found and Removed->" + localKey + " -> " + localTablePageData.toString() );							 	 	 	 
							 	 	 	 iterator.remove();
							 	 	 	 //mGlobalLinkedList.remove();
							 	 	   break;
							 	   }
							 	 }
							 	 
							 	 // Add at first position 
							 	 mGlobalLinkedList.addFirst(mTablePageData);
							 	 //System.out.println("Verify first Key  -> " + mGlobalLinkedList.getFirst().keySet() );
			 	 	 	 
						 	} //// end: if node at first place. 
						 	
			 	 }
					 catch(java.util.NoSuchElementException e)
					 {
					 	 // CASE: when there is no NODE in list. 
					   mGlobalLinkedList.addFirst(mTablePageData);
					 }				 	
			 }
			 else // size crossed 
			 {
			 	 
			 	 //System.out.println("Size crossed , removed last," + mGlobalLinkedList.getLast().keySet().toString() +"  add ->" + localKey + " -> " + " at first " +  mTablePageData.toString() );
 	 	 
			 	 // use LRU and remove last access page from Cache			 	
			 	 mGlobalLinkedList.remove(mGlobalLinkedList.getLast());
			 	
			 	// if it is first node, DON'T do any thing.
			 	if(!mGlobalLinkedList.getFirst().keySet().contains(localKey) )  
			 	{			 	  
			 	  mGlobalLinkedList.addFirst(mTablePageData);
			 	}
			 	
			 	//System.out.println("Verify first node ->"  +  mGlobalLinkedList.getFirst().keySet());
 	 	 
			 }			
			 
		}
	 
		
		/**
		 * 
		 * @param tableName
		 * @param recordID
		 */
		public String getRecord(
				 String tableName, 
				 int pageNo, 
				 int recordID,
				 int pageStartRowId, 
				 int pageEndRowId )
		{
			 
				CacheData cacheData = null;
						
				String record = null;
			 int indexOffSet =  recordID - pageStartRowId ; 	 
			 System.out.println("Page No."+ pageNo + " recordID ->"+ recordID + " pageStartRowId->" + pageStartRowId + " pageEndRowId->" + pageEndRowId);
				
			 cacheData = getTablePageFromCache(tableName, pageNo);
			 
				if(cacheData == null)
				{
					 
					 System.out.println("MISS "+ pageNo);				 				 
					 
					 String file = ConfigDetail.getInstance().getTableDataFile(tableName);
					 int fileOffSet = pageNo * ConfigDetail.getInstance().getPageSize(); 					 
	     //	load page from data file.
					 /*///////OLD-IMPL
					 //StringBuilder dataPage = mDataStore.readPage(file, fileOffSet);					 
					 //Log.message("getRecord[", dataPage.toString());					 
					 
					 // 
					 //ArrayList<String> records =  new ArrayList<String>(Arrays.asList(mDataStore.getRecordsFromDataPage(dataPage.toString())));					 
					  /////// OLD-IMPL 
					 */
					 // NEW-IMPL
					 ArrayList<String> records =  mDataStore.readPage2(file, pageNo, pageStartRowId, pageEndRowId);					 
					 
					 //cacheData = new CacheData(records);
					 addDataPageToCache(tableName, pageNo, records);				 			 
					 record = records.get(indexOffSet); // [indexOffSet];				 
				}
				else
				{ 
					 //Page is in memory
					 System.out.println("HIT");					 
					 indexOffSet = recordID - pageStartRowId;					 
					 //now get ROW NOM from page 				 
					  ArrayList <String> records = cacheData.getRecords();
					 //System.out.println("No of records->" + records.length + " indexOffSet" + indexOffSet );
					 record = records.get(indexOffSet); // [indexOffSet];					 
					 // Change position in Cache List.
					 addDataPageToCache(tableName, pageNo, records);		
					 
				}			
				return record;			
		} ///////////////////// end of method //////////////////	
  
		/**
		 * 
		 * @param tableName
		 * @param pageNo
		 * @return
		 */
		public CacheData getTablePageFromCache(String tableName, int pageNo)
		{
			
				int counter = 0; 
				LinkedHashMap <String, CacheData> localTablePageData; 
				String localKey = getKey(tableName, pageNo); 
				CacheData cacheData = null;
				boolean found = false;
				
				while(counter < mGlobalLinkedList.size())
				{
					 localTablePageData = mGlobalLinkedList.get(counter);
					 if(localTablePageData.containsKey(localKey))
					 {
					 	 cacheData =localTablePageData.get(localKey); 
					 	 found = true;
					 	 break;
					 }								 
					 counter++;							 
				}
				return cacheData;
			
		}
		public ArrayList<String> getTablePageRecordsFromCache(String tableName, int pageNo)
		{
			CacheData cacheData = getTablePageFromCache(tableName, pageNo); 
			if(cacheData != null)
			{
				return cacheData.getRecords();	
			}
			else
				return null;
			
		}
		
} ///////////////////////////////////////////////////////////////////////////


