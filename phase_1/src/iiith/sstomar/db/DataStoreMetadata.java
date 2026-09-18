package iiith.sstomar.db;

/**
 * This class stores the metadata about dataStore file
 * 
 * @author sstomar
 *
 */
public class DataStoreMetadata
{
 

	private int pageNo;
 private int fromROWID;
 private int toROWID;
 
 
 /**
	 * @param pageNo
	 * @param fromROWID
	 * @param toROWID
	 */
 public DataStoreMetadata(int pageNo, int fromROWID, int toROWID)
 {
	 //super();
	 this.pageNo = pageNo;
	 this.fromROWID = fromROWID;
	 this.toROWID = toROWID;
 }
 
	public DataStoreMetadata()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo()
	{
		return this.pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo)
	{
		this.pageNo = pageNo;
	}

	/**
	 * @return the fromROWID
	 */
	public int getFromROWID()
	{
		return fromROWID;
	}

	/**
	 * @param fromROWID the fromROWID to set
	 */
	public void setFromROWID(int fromROWID)
	{
		this.fromROWID = fromROWID;
	}

	/**
	 * @return the toROWID
	 */
	public int getToROWID()
	{
		return toROWID;
	}

	/**
	 * @param toROWID the toROWID to set
	 */
	public void setToROWID(int toROWID)
	{
		this.toROWID = toROWID;
	}
	
	public String toString()
	{
		
		return "PageNo->"+ this.pageNo + " fromRowId->" + fromROWID + " toRowID->" + toROWID;
	}

}
