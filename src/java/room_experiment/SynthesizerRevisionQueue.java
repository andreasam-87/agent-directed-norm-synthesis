package room_experiment;

import java.util.ArrayList;

public class SynthesizerRevisionQueue {

		private static SynthesizerRevisionQueue instance = null;
		private ArrayList<RevisionItem> revisionQueue = null;


		public static synchronized SynthesizerRevisionQueue getInstance()
		{
			if (instance == null) {
	            instance =  new SynthesizerRevisionQueue();
	        }    
			return instance;
		}
		
	    private SynthesizerRevisionQueue() {
	        revisionQueue = new ArrayList<RevisionItem>();
	    }

	    
	    public void addToQueue(String r, String a) {
	    	revisionQueue.add(new RevisionItem(r, a));
	    }

	  
	    public void removeFromQueue() {
	    	revisionQueue.remove(0);
	    }
	    
	    public RevisionItem getFromQueue() {
	    	if(revisionQueue.size()==0)
	    	{
	    		return null;
	    	}
	    	else
	    	{
	    		return revisionQueue.get(0);
	    	}
	        
	    }
	    
	    public Boolean checkEmpty() {
	    	if(revisionQueue.size()==0)
	    	{
	    		return true;
	    	}
	    	else
	    	{
	    		return false;
	    	}
	        
	    }
	
	
}
