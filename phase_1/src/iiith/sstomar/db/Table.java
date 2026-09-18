package iiith.sstomar.db;

import java.util.ArrayList;
import java.util.Iterator;

public class Table
{
 private String name;
 //protected Column[] columns;
 protected ArrayList <Column> columnList;
 
	public Table(String name)
	{
		 // TODO Auto-generated constructor stub
		 this.name = name;
		 columnList = new ArrayList <Column>();
	}
	
	public void setColumn(Column column)
	{
		 columnList.add(column);
	}
	
	
	public String toString()
	{
		 StringBuilder str = new StringBuilder();
		 str.append(name);
		 
		 Iterator <Column> iterator = columnList.iterator();
		 
		 while(iterator.hasNext())
		 {
		    Column column = iterator.next();
		    str.append("[" + column.toString() + "]");	
		 }
		 		 
		 return str.toString();
			
	}

}
