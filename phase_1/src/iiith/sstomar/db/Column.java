package iiith.sstomar.db;

public class Column
{
 private String name;
 private String type;
 private String length;
 private String order; 
 
 
 public Column(){
 	
 }
	public Column(String name, String type, String length, String order)
	{
			// TODO Auto-generated constructor stub
			this.name = name;
			this.type = type;
			this.length= length;
			this.order = order;
			
	} 
	
	// 
	public void setName(String name)
	{
		 this.name = name;
	}
	public String getName()
	{
		 return this.name;
	}
	
	// 
	public void setType(String type)
	{
		 this.type = type;
	}
	
	public String getType()
	{
		 return this.type;
	}
	
	
	// 
	public void setLength(String length)
	{
		 this.length = length;
	}
	
	public String getLength()
	{
		 return this.length;
	}
	
	
	/**
	 * 
	 */
	public String toString()
	{
		 return name +";" + type + ";" + length;
	}
	
}
