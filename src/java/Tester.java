import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Tester {
	
	public Tester()
	{
		
	}
	
	public static void main(String[] args) {
		
		System.out.println("hello");
		String file1 = "/Users/andreasamartin/Documents/InstalExamples/rooms/rooms_revised_synthesizer1_5.ial";
		String file2 = "/Users/andreasamartin/Documents/InstalExamples/rooms/rooms_revised_synthesizer2_4.ial";
		//String file2 = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsInst.ial";
		
		//System.out.println("////\\\\\n"+x.modifyFact("holdsat(capacityExceededViol(room1),rooms,0).","capacityExceededViol", "capacityExceededViol(room1)", 3, 1));
		Tester test = new Tester();
//		.
		try {
			System.out.println("////\\\\\\\n"+test.compareFiles(file1,file2));
			System.out.println("////Length\\\\\\\n"+new File(file1).length());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.out.println("Hello, World!"); 
        String fact = "initially(vip_room(room1),rooms)";
         String viproom = fact;
		 int i = viproom.indexOf("(");
		  int i2 = viproom.lastIndexOf(")");
		  System.out.println(i+"-"+i2);
        String temp = viproom.substring(i+1,i2);
        System.out.println("Temp string: "+temp);
       // String room = temp.substring(temp.indexOf("("));
        String room = StringUtils.substringBetween(temp, "(", ")");
        
        viproom = room;
        System.out.println("String after: "+viproom);
	}
	
	private boolean compareFiles(String file1_path,String file2_path) throws IOException
	{
		boolean comp = FileUtils.contentEquals(new File(file1_path),new File(file2_path));
		//FileUtils.co
	
		return comp;
	}
}