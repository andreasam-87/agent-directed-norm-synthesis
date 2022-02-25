



public class Tester {
	
	
	
	public static void main(String[] args) {
		
		System.out.println("hello");
		
		JsonExtractor x =  new JsonExtractor();
		System.out.println("////\\\\\n"+x.modifyFact("holdsat(capacityExceededViol(room1),rooms,0).","capacityExceededViol", "capacityExceededViol(room1)", 3, 1));
		
	}
	
}