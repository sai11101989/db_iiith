package iiith.sstomar.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;


public class FileUtils
{
		
			
	/**
	 * This method reads configuration files and populates ConfigDetail 
	 * object
	 * 
	 * @param file
	 * @param configDetail
	 * @return
	 */
	public static ConfigDetail readConfigFile(String file, ConfigDetail configDetail)
	{
		
		 String line = null; 
	  boolean tableStart = false;
	  boolean tableEnd = false;
	  boolean isNextLineTableName = false; 
	  Table tableName = null;
	  String tableNameStr =null;
	  Column [] columnArray = new Column[1];
	  Column column =null; 
	  int colCount = 1;
	  int tableCount = 0;
	  Scanner lineScanner = null;
	  FileInputStream fileInputStream = null;
	  Scanner fileScanner = null;
    
    try 
    {
	     /////
	     fileInputStream = new FileInputStream(file);
	     fileScanner = new Scanner(fileInputStream);
	   
	     while(fileScanner.hasNextLine())
		    { 
		       
		     	line = fileScanner.nextLine();	       
		     	//Log.message("readConfigFile", " line=>[" + line +"]");		     	
	     	 if(line.contains("PAGE_SIZE"))
	     	 {
	     	 	configDetail.setPageSize(line.substring(9));
	     	 	//System.out.println("page ["+ line.substring(9) +	 	"]");
	     	 }
	     	 if(line.contains("NUM_PAGES"))
	     	 {
	     	 	configDetail.setNumPages(line.substring(10));
	     	 	//System.out.println("num ["+ line.substring(10) +	 	"]");
	     	 }
	     	 
	     	 if(line.contains("PATH_FOR_DATA"))
	     	 {
	     	 	configDetail.setDataPath(line.substring(14));	     	 	
	     	 }
	     	
	     	 // Read Line after" BEGIN and TABLE-NAME 
	     	 //      	
	     	 if(tableStart && 
	     	 		 (!tableEnd) && 
	     	 		 (!isNextLineTableName)
	     	 		 && !line.contains("END") )
	     	 {      	   
	     	 	 //System.out.println("name="+ line);
	     	 	 lineScanner = new Scanner(line);
	     	 	 lineScanner.useDelimiter(",");
	     		  
	     			 //System.out.println("line======"+ line + "-" +scanner.hasNext());
	     		  if (lineScanner.hasNext())
	     		  {
	     		    //assumes the line has a certain structure
	     		    String name = lineScanner.next();
	     		    //System.out.println("name="+ name);	     		    
	     		    String type = lineScanner.next();
	     		    //System.out.println("type="+ type);
	     		    
	     		    column = new Column();
	     		    column.setName(name);
	     		    
	     		    if(type.contains("fixed_char"))
	     		    {	     		    	
	     		    	column.setType("char");	     		    	
	     		    	//System.out.println(type.indexOf("("));
	     		    	//System.out.println(type.indexOf(")")); 		    	
	     		    	
	     		    	//System.out.println(type.substring(type.indexOf("(")+1, type.indexOf(")")));
	     		    	
	     		    	column.setLength( type.substring(type.indexOf("(")+1, type.indexOf(")"))  );
	     		    }
	     		    else
	     		    {
	     		    	column.setType(type);
	     		    }	     		    
	     		    //System.out.println("Column==" + column.toString());
	     		    lineScanner.close();
	     		  } // column and its type present 	     		  
	     		       		  
	     		  /////////////////// increase array 
	     		  if(colCount > columnArray.length)
	     		  {
	     		  	columnArray = Arrays.copyOf(columnArray, colCount);
	     		  }
	     		  		
	     		  columnArray[colCount-1] = column;
	     		  tableName.setColumn(column);
	     		  colCount++;
	     		  
	     	 } //////////////// end of column processing 
	     	 
	     	 // Check: after BEGIN, there should be table name 
	     	 if(isNextLineTableName)
	     	 {
		     	 	isNextLineTableName= false;
		     	 	tableName = new Table(line);
		     	 	
		     	 	tableCount++; 
		     	 	configDetail.setNoOfTables(tableCount); 
		     	 	
		     	 	tableNameStr = line;
		     	 	colCount = 1;      	 	
	     	 } //// table name has been read from file
	     	 
	     	 if(line.contains("BEGIN"))
	     	 {
	     	 	 tableStart = true;      
	     	 	 isNextLineTableName = true;
	     	 	 tableEnd = false;
	     	 } // start of table BEGIN
	     	 
	     	 if(line.contains("END"))
	     	 {
	     	 	 tableEnd = true;  
	     	 	 tableStart = false;
	     	 	 isNextLineTableName = false;     	 	 
	     	 	 
	     	 	 // Populate table object
	     	 	 configDetail.setTableDetail(tableNameStr, columnArray);
	     	 	 
	     	 	 // Reset column array to 1
	     	 	 columnArray = null;
	     	 	 colCount = 0;
	     	 	 columnArray = new Column[1];
	     	 } // end of table END     	 
     } ////// end of while: read line       
  } 
  catch(IOException e)
  {
  	 e.printStackTrace();
  }
  finally 
  {
	    //input.close();
	  	fileScanner.close();
	  	try
	  	{
	  	 fileInputStream.close();
	  	}
	  	catch(Exception e){}  	
  }  
  return configDetail;		
	} //////////////////////////// end of readConfigFile()//////////////////
	
	/** TODO: Not in-use
	 * 
	 * This method processes a line, read from configuration file
	 * 
	 * @param aLine
	 */
	protected static void processLine(String aLine)
	{
	  //use a second Scanner to parse the content of each line 
	  Scanner scanner = new Scanner(aLine);
	  scanner.useDelimiter(",");
	  
	  if (scanner.hasNext()){
	    //assumes the line has a certain structure
	    String name = scanner.next();
	    String value = scanner.next();
	    //log("Name is : " + quote(name.trim()) + ", and Value is : " + quote(value.trim()));
	  }
	  else {
	    //log("Empty or invalid line. Unable to process.");
	  }
 }
}
