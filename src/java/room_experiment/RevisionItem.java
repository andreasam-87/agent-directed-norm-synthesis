package room_experiment;

public class RevisionItem {
	private String request;
	private String agent;
	
	public RevisionItem(String req,String ag)
	{
		request = req;
		agent = ag;
	}
	
	public String getReq()
	{
		return request;
	}
	
	public String getAg()
	{
		return agent;
	}

}
