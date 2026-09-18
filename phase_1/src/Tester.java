import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Tester {
	
	public static void main(String[] args) throws Exception{
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader("testcase"));
		DBSystem dbs = new DBSystem();
		dbs.readConfig("C:\\Users\\sai11101989\\workspace\\DB_Phase1\\sstomar_config.txt");
		dbs.populateDBInfo();
		int i=0;
		String s;
		while((s=reader.readLine()) != null) {
			int inp = Integer.parseInt(s);
			if(inp !=-1) {
				dbs.getRecord("op",inp);
			}
			else
				dbs.insertRecord("op", "record");
		}
		System.exit(0);
	}
}
