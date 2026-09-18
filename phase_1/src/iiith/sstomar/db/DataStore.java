package iiith.sstomar.db;

import java.io.File;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class 
 * 
 * @author sstomar
 * 
 **/
public class DataStore
{
	 // keeps track of file postion where last page needs to be appended. 
	
	 private static final String CLASS_NAME="iiith.sstomar.db.DataStore";
	 private int  mPosition;
	 private static final String RECORD_DELIMITER = ConfigDetail.RECORD_DELIMITER;
	 private static final String COLUMN_DELIMITER = ConfigDetail.COLUMN_DELIMITER;
	 
		public DataStore()
		{
			// TODO Auto-generated constructor stub
		}
		
	
	/**
	 * 
	 * @param fileName
	 */
	public void createDataFile(String fileName)
	{
				
		File file = null;
  boolean isExist = false;  
  //Log.message(fileName);
  
  try
  {      
     // create new File object
  	 file = new File(fileName);  	 
     // if file exists
     if(file.exists())
     {
        // set file access to writable for everybody
     	isExist = file.setWritable(true, false);        
     }  
     else
     {
     	//file.mkdirs(); 
     	file.createNewFile();
     }
     
     //System.out.println("FILE EXISTS->" +isExist);
  }
  catch(Exception e)  
  {
     // if any error occurs
     e.printStackTrace();
  }
  finally
  {  	
  }
  
} ////end of method
	
	
	/**
	 *  The inout will be coming to Method like below: 
	 *  [ "sh_resource", "res_id 421, res_name MY PRINTER-1, res_loc 3RD FLOORE HIMALAYA" ] 
	 *  
	 * @param tableName
	 * @param record
	 * @return
	 */
	private String processInputRecord(String tableName, String record)
	{
			StringBuilder dataRec = new StringBuilder();
			//Check as per Table metadata
			// 
			Column [] columns = ConfigDetail.getInstance().getTableDetail(tableName);
			String column = null;
		
			// Assumption here is: <column value> will be seperated by ","
			String [] colValues = record.split(ConfigDetail.COLUMN_DELIMITER);
	 
			for (int i = 0; i < columns.length; i++) 
			{    			
				column = columns[i].getName();
				
				if(record.contains(column))
				{
					 // Store only column values 
				  /*
					 if(i + 1 != columns.length)
					 {
					  dataRec.append((colValues[i]).substring(column.length() +1) + ConfigDetail.COLUMN_DELIMITER); 
					 }
					 else
					 {
					 	dataRec.append((colValues[i]).substring(column.length() +1));
					 	dataRec.append(ConfigDetail.RECORD_DELIMITER); // record seperator 
					 }			
					 */
				 if(i==0)
				 {
				  dataRec.append((colValues[i]).substring(column.length() +1)); 
				 
				 }
				 else
				 {
				  dataRec.append( ConfigDetail.COLUMN_DELIMITER + (colValues[i]).substring(column.length() +1)); 
				 }
					 
					 
				}		
		}			
		
		dataRec.append(ConfigDetail.RECORD_DELIMITER);
		//System.out.println(dataRec);
		return dataRec.toString();
			
	} ///////////////////// end of processInputRecord 
	
	

	/** 
	 * This method write a data-page, from specified position 
	 * , which would be a starting position of a page/block. 
	 * 
	 * 
	 * @param fileName
	 */
	private void writeDataToFile(String file, String page, int position)
	{		
				RandomAccessFile randomAccessfile = null;
		  try
	   {
		  	 randomAccessfile = new RandomAccessFile(file,"rw");
		  	 randomAccessfile.seek(position);  	 
		  		randomAccessfile.write(page.getBytes());
		  }
		  catch(Exception e)
		  {
		  	 e.printStackTrace();
		  	 //throw new Exception(""); 
		  }
		  finally
		  {
		  	 try
		  	 {
		  	  randomAccessfile.close(); 	
		  	 }
		  	 catch(Exception e) {}
		  }		 				
 } ////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Process data-block 
	 * 
	 * @param dataPage
	 */
 public String[] getRecordsFromDataPage(String dataPage)
 {
 	 String[] result = null;
	 	// Prcess each line and then store in Cache. 
	 	try
	 	{
	 		 result = dataPage.split(RECORD_DELIMITER);
	 	}
	 	catch(Exception e)
	 	{
	 		
	 	}
		 return result; 
		 
 } //////////////////////processDataPage()//////////////////////////////
	
 
 
 
	/**
	 * 
	 * @param file file with absoludate path 
	 * 
	 * @return
	 */
	public String getLastDataPage(String file, int recordLength)
	{
			int pageRead = 0;
			RandomAccessFile randomAccessFile = null;
			double fileSize;
	  double maxPages;
		 int seekPosition = 0;
		 double fullPages;
	  
		 int pageSize =  ConfigDetail.getInstance().getPageSize();
	  
	  byte[] pageData = new byte[pageSize];
	  StringBuilder actualData = new StringBuilder();
	  int temp=0;
	  	  
	  try
	  {
	  	 randomAccessFile = new RandomAccessFile(file, "r");
	    fileSize = randomAccessFile.getChannel().size();
	    
	    maxPages = Math.ceil(fileSize / pageSize);
	    fullPages =  Math.floor(fileSize / pageSize);
	    
	    //Log.message("getLastDataPage", "fileSize->" + fileSize + ", maxPages->" + maxPages + ", fullPages->" + fullPages + " recordLength->" + recordLength);
	    
	    // Check if there is a space in last block: 
	    // the check is: adding current blocks bytes in fileSize, it should not cross
	    // max-pages. 
	    if ( (maxPages > fullPages) &&
	    		   (Math.ceil( (fileSize + recordLength) / pageSize) > maxPages)) 
	    {	    		    	
	    		// no space in page, page chaining, set position to next position 
	    		seekPosition =  pageSize * (int)maxPages;
	    	
	    }	   
	    else  // noOfPages =fullPages
	    {
	    	seekPosition = ((int) fullPages *pageSize) ;
	    	
	    	// Ex: fileSize=2124, now we want to reach at 1125 position
	    	//     So, 2*1024 + (2124 - 2048) = 2048 + 76
	    	//seekPosition = (int) ((fullPages * pageSize) + (fileSize - fullPages * pageSize)) + 1;    
	    	
	    }
	    
	    //Log.message("getLastDataPage", " Seek Position=>" + seekPosition );
	    randomAccessFile.seek(seekPosition);
	    mPosition = seekPosition;
	    
	    /////// Read 1-data-block //////
	    // this method behaves in exactly the same way as the InputStream.read(byte[]) method of InputStream.	    
	    int noofBytes = randomAccessFile.read(pageData);
	    
	    if(noofBytes == -1)
	    {
	    	 //pageData[0] = "\n".getBytes();
	    	 //actualData.append((char)(pageData[0]));
	    	 //return actualData.toString();
	    	 //Log.message("getLastDataPage", "  There is no data in file from postion=" + seekPosition );
	    	 
	    	 return "";
	    }
	    
	    // data-block size [1024] might not contain data in all bytes.
	    // So only get actual-data
	    while (temp < pageSize && pageData[temp] !=0)
	  		{
	    	 actualData.append((char)(pageData[temp]));
	    	 temp++;
	  		}
	    
	    // return new String(pageData);
	    // return  actualData.toString();	  	
	  }
	  
	  //  if end-of-file is reached before the desired number of bytes has been read, 
	  //  an EOFException (which is a kind of IOException) is thrown.	  
	  catch(EOFException e)
	  {
	  	 e.printStackTrace();
	  	 
	  } 	  
	  catch(IOException e)
	  {
	  	 e.printStackTrace();
	  }
	  finally
	  {
	  	 try
	  	 {
	  	  randomAccessFile.close(); 	
	  	 }
	  	 catch(Exception e) {}
	  }
   
	  // return new String(pageData);
	  // Log.message("getLastDataPage", " Actual data[" +actualData.toString() +"]");
	  
   return  actualData.toString();
   
	} /////////////////// END OF getLastDataPage() /////////////////////////////////////
	
		
	
	
 /**
  * 1. This method brings last-page in-memory
  * 2. Appends the new record-data into in-memory last-page data 
  * 3. Puts-back complete data-page to data-file. 
  * 
  * @param file
  * @param record
  */
	public void writeData(String file, String tableName, String record)
	{
			 String module = CLASS_NAME +".writeData";
			 
				FileWriter fileWriter = null;		
				StringBuilder inputDataRec = null;
				StringBuilder lastDataPage = null;
		
				//Log.message(module, "BEGIN:" + file + ", " + tableName +", [" + record +"]");
				
			 //#1: processInputRecord() adds new line also 
			 inputDataRec = new StringBuilder(processInputRecord(tableName, record));
			 
			 //#2: Data block / page: 
			 //Log.message(module, "Get last page in-memory");
			 
			 byte[] bytes = inputDataRec.toString().getBytes();
			 //Log.message(module, "length in bytes" + bytes.length); 
			 //Log.message(module, "length in chars" + inputDataRec.length()); 
			 
			 lastDataPage = new StringBuilder(getLastDataPage(file, inputDataRec.length()));
				
			 //#3: Append to data-block 
			 //Log.message(module, "Append new record to the last page (in-memory)");
			 lastDataPage.append(inputDataRec);
			 
			 //#4: write data-block / page to file. 
			 //Log.message(module, "Write page back to data-file");
			 //Log.message(module, "Data-page [" + lastDataPage.toString() +"]" );
			 writeDataToFile(file, lastDataPage.toString(), mPosition);			 			 
				
				//Log.message(module, "END");
		
		
} ////////////////////////// end of method: writeData() //////////////////////////////////
	
	

	// Method to read 
	public StringBuilder readPage(String file, int offSet)
	{
		 RandomAccessFile randomAccessfile = null;
		 byte [] dataBlock = null;
		 int blockSize = ConfigDetail.getInstance().getPageSize();		 
		 int temp = 0;
		 StringBuilder actualData =null;
			
		 try
		 { 
		 	 //System.out.println("blockSize-->[" + blockSize + "]" );
		 	 dataBlock = new byte[blockSize]; 	
			 	actualData = new StringBuilder();
			 	
			 	randomAccessfile = new RandomAccessFile(file,"r");
			 	
			 	//System.out.println("offSet-->" + offSet );
			 	
			 	randomAccessfile.seek(offSet); 			 	 	
			 	randomAccessfile.read(dataBlock);			 	
			 	
			 	//System.out.println("data-->[" + new String(dataBlock) + "], length->" + dataBlock.length);
			 	//remove null bytes 
			 	temp = 0;
			  while (temp < blockSize && dataBlock[temp] !=0)
					{
			  	 //System.out.println("-->" + (char)(dataBlock[temp]));
			  	 actualData.append((char)(dataBlock[temp]));
			  	 temp++;
					}
	  }
		 catch(IOException e)
		 {
		 	 e.printStackTrace();
		 }
		 finally
		 {
		 	 try{randomAccessfile.close();}catch(IOException e){}
		 }
	 
		 return actualData; 
		 
	}////////////////////////////////////////////////////////////////////
	
//Method to read 
public ArrayList<String> readPage2(String file, int pageNo, int startLine, int EndLine)
{
	RandomAccessFile randomAccessfile = null;
 int blockSize = ConfigDetail.getInstance().getPageSize();
 
 int min_rowid = -1; 
 int max_rowid = -1; 
 int pageCount = 0;
 ArrayList <String> pageData = new ArrayList <String>();
 
 try
 {
	 randomAccessfile = new RandomAccessFile(file,"r");		 	  	 
	 
		String line = new String();
		int readBytesLen = 0;
		int counter = 0;
		//System.out.println("pageNo:" + pageNo + " " + startLine + " " + EndLine);
		while((line=randomAccessfile.readLine()) != null)
		{
			 
			  if(counter >= startLine  && counter <=EndLine)
			  {
			  	//System.out.println("Added:" + line);
			  	
			  	pageData.add(line);
			  	if(counter==EndLine)
			  	{
			  		break;
			  	}
			  }
			  counter ++; 
		}		 	
}
catch(Exception e)
{
	 e.printStackTrace();
}
finally
{
	 try{randomAccessfile.close();}catch(Exception e) {}
}

 return pageData; 
 
}////////////////////////////////////////////////////////////////////



	
	
	
	/** TODO: NOT IN USE 
	 * 
	 * @param file
	 * @param tableName
	 */
	/*
	public void readDataFile(String file, String tableName)
	{
		 RandomAccessFile randomAccessfile = null;
		 int blockSize = ConfigDetail.getInstance().getPageSize();
		 int offSet = 0;
		 StringBuilder actualData = null;
   try
   {
  	 randomAccessfile = new RandomAccessFile(file,"r");  	  	 
  	 double fileSize = randomAccessfile.getChannel().size();
  	 
  	 while(offSet <= (int) fileSize )
  	 {  	 	
  	 	actualData = readPage(file, offSet);   	 	
  	 	// Cache current data block
  	 	// processDataPage(tableName, actualData.toString());  	 	
  	 	// Increase offSet 
  	 	offSet = offSet + blockSize;  	  	 	
  	 }  		 
  }
  catch(Exception e)
  {
  	 e.printStackTrace();
  }
  finally
  {
  	 try
  	 {
  	  randomAccessfile.close(); 	
  	 }catch(Exception e) {}
  }
}
*/
	
	/**
	 * This method extracts start_rowid and end_rowid from a given page (data block)
	 * 
	 * @param dataPage
	 * @return
	 */
	private int findROWIDs(int start_rowid, String dataPage)
	{
		 int count = 0; 
		 String module = CLASS_NAME + ".findROWIDs";
		 
		 Scanner lineScanner = new Scanner(dataPage);
		 lineScanner.useDelimiter(RECORD_DELIMITER);		 
		 //Log.message(module , "begin: data length->" + dataPage.length());
	 	
		 while(lineScanner.hasNextLine())
		 {		 	 
		 	 try
		 	 {
		 	 	//Log.message(module, " findROWIDs->["+ count +"]");
		 	  lineScanner.next();
		 	 	count++; 
		 	 }catch(NoSuchElementException e){
		 	 	 //e.printStackTrace(); //TODO: debug 
		 	 }		 	 
		 }	
		 
		 // CASE# when System.getProperty("line.separator") did not work, use "\n" new line. 
		 // 
		 if(count ==0){
		 	 Scanner lineScanner2 = new Scanner(dataPage);
		 	 lineScanner2.useDelimiter("\n");  // let's test with new line char as RECORD_DELIMITER is having some prob
		 	 while(lineScanner2.hasNextLine())
				 {		 	 
				 	 try
				 	 {
				 	 	//Log.message(module, " findROWIDs->["+ count +"]");
				 	  lineScanner2.next();
				 	 	count++; 
				 	 }catch(NoSuchElementException e) {
				 	 	 //e.printStackTrace(); //TODO: debug 
				 	 }		 	 
				 }	
		 	 lineScanner2.close();
		 }
		 		 				 
		 lineScanner.close();
		 //Log.message("findROWIDs-> " , "end-> max_rowid-> " + (start_rowid + count-1));
		 return (start_rowid + count -1);
	}
	
	/**
	 * 
	 * @param file
	 * @param tableName
	 * @param tableMetadataMap : table-name1
	 *                               [page-no1 0 5]
	 *                               [page-no2 6 12]
	 *                               ...
	 *                           table-name2
	 *                               [page-no1 0 4]
	 *                               [page-no2 6 8]
	 *                               ...                      
	 */
 public ArrayList <DataStoreMetadata> getMetadata(
 		  String file,
 		  String tableName)
 {
		 	
 	  RandomAccessFile randomAccessfile = null;
			 byte [] dataBlock = null;
			 int blockSize = ConfigDetail.getInstance().getPageSize();
			 int offSet = 0;
			 
			 int min_rowid = 0; 
			 int max_rowid = 0; 
			 int pageCount = 0;
			 
			 StringBuilder actualData =null;
			 
			 ArrayList <DataStoreMetadata> pageMetadata = new ArrayList <DataStoreMetadata>();
			 
		  try
		  {
		 	 randomAccessfile = new RandomAccessFile(file,"r");		 	  	 
		 	 int fileSize = (int) randomAccessfile.getChannel().size();
		 	 
		 	 //Log.message("getMetadata-> " , "tableName " +tableName + " fileSize "+ fileSize);
		 	 
		 	 while(offSet < fileSize )
		 	 {		 	 	
			 	 	
		 	 	 actualData = readPage(file, offSet);
			 	  
			 	 	//Log.message("DataStore.getMetadata-> " , "tableName ->" +tableName + " , calling findROWIDs ");			 	 	
			 	 	//Log.message("DataStore.getMetadata-> " , "actualData [" +actualData + "]");			 	 	
			 	 	
			 	 	// Cache current data block
			 	 	max_rowid = findROWIDs(min_rowid, actualData.toString());			 	 	
			 	 	//processDataPage(tableName, new String(dataBlock));			 	 	
			 	 					 	 	
			 	 	//Log.message("getMetadata-> " , "tableName " +tableName + " pageCount-> "+ pageCount + " min_rowid "+ min_rowid + " max_rowid" + max_rowid );
			 	 	DataStoreMetadata metadata = new DataStoreMetadata(pageCount, min_rowid, max_rowid);
			 	 	//Log.message("getMetadata-> object detail-> " , metadata.toString());
			 	 	pageMetadata.add(metadata);
			 	 	//Log.message("getMetadata-> " , "tableName " +tableName + " pageCount-> "+ pageCount + " min_rowid "+ min_rowid + " max_rowid" + max_rowid );
			 	 	pageCount = pageCount + 1;			 
			 	 	min_rowid = max_rowid+1;
			 	 	offSet = offSet + blockSize;  			 	 	
		 	 }
		 	 
		 }
		 catch(Exception e)
		 {
		 	 e.printStackTrace();
		 }
		 finally
		 {
		 	 try{randomAccessfile.close();}catch(Exception e) {}
		 }
 	
 	 return pageMetadata;
 	 
 } /////// end of method; 
	
 /**
	 * 
	 * @param file
	 * @param tableName
	 * @param tableMetadataMap : table-name1
	 *                               [page-no1 0 5]
	 *                               [page-no2 6 12]
	 *                               ...
	 *                           table-name2
	 *                               [page-no1 0 4]
	 *                               [page-no2 6 8]
	 *                               ...                      
	 */
 public ArrayList <DataStoreMetadata> getMetadata2(
 		  String file,
 		  String tableName)
 {
		 	
 	  RandomAccessFile randomAccessfile = null;
			 int blockSize = ConfigDetail.getInstance().getPageSize();
			 
			 int min_rowid = -1; 
			 int max_rowid = -1; 
			 int pageCount = 0;
			 ArrayList <DataStoreMetadata> pageMetadata = new ArrayList <DataStoreMetadata>();
			 
		  try
		  {
		 	 randomAccessfile = new RandomAccessFile(file,"r");		 	  	 
		 	 // int fileSize = (int) randomAccessfile.getChannel().size();
		 	 		 	 
		 		String line = new String();
		 		int readBytesLenWithNewLine = 0;
		 		int readBytesLen = 0; 
		 		int counter = -1; 
		 		while((line=randomAccessfile.readLine()) != null)
		 		{ 
		 		  //System.out.println("line[" + line +"]");
	 			 
			 	//System.out.println("length->" + readBytesLen + " blockSize->" + blockSize);
			    if(min_rowid == -1)
			    {
			    	 min_rowid=0;
			    }		 		
			    
		 			 counter ++;
		 			  // lines.add(s);
		 			 
		 			 // Don't consider new line only from last-line  
		 			 readBytesLen = readBytesLenWithNewLine + line.length(); 
		 			 
		 			 readBytesLenWithNewLine  = readBytesLenWithNewLine + line.length() + ConfigDetail.RECORD_DELIMITER.length();  
		 			 	 						
		 			 		 			 
		 			 //System.out.println("length->" + readBytesLen + " blockSize->" + blockSize + " counter->" + counter);
		 			 
		 			 /// 
		 			 if((readBytesLenWithNewLine > blockSize) )
		 			 {
		 			    	 			 	
		 			 	  // check if last line without new line can be considered in this page. 	 
		 			 	  // using less-than-eq as System.lineSeperator takes 2-bytes. 
		 			    if (readBytesLen <= blockSize )
		 		 			 {
		 			    	 max_rowid++;
		 			    	 
		 			    	 // Add length of new line to next page, as current line we are considring in 
		 			    	 // current page
		 			    	 readBytesLenWithNewLine = ConfigDetail.RECORD_DELIMITER.length();
		 		 			 }	
		 			    else
		 			    {
		 			      readBytesLenWithNewLine = line.length() + ConfigDetail.RECORD_DELIMITER.length();  
		 			    }
		 			    
		 			    //System.out.println("getMetadata2->" + pageCount +" " + min_rowid + " " +  max_rowid);
		 			 	 	DataStoreMetadata metadata = new DataStoreMetadata(pageCount, min_rowid, max_rowid);
				 	 	  //Log.message("getMetadata-> object detail-> " , metadata.toString());
				 	 	  pageMetadata.add(metadata);
				 	 	 
			 			 	 pageCount = pageCount + 1;			 
					 	 	 min_rowid = max_rowid+1;
					 	 	 //max_rowid++;
					 	 	 //readBytesLen = 0;
					 	 	
		 			 }
		 			
		 			 max_rowid++;
		 		}	
		 		
		 		// Last set of page
		 		if(counter == max_rowid)
		 		{
		 			DataStoreMetadata metadata = new DataStoreMetadata(pageCount, min_rowid, max_rowid);		
		 			pageMetadata.add(metadata);
		 		}
		 }
		 catch(Exception e)
		 {
		 	 e.printStackTrace();
		 }
		 finally
		 {
		 	 try{randomAccessfile.close();}catch(Exception e) {}
		 }
 	
 	 return pageMetadata;
 	 
 } /////// end of method; 
 
 
} /// end of class/////////////////////////////////////////

