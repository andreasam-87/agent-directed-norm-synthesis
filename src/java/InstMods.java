
public class InstMods {

	
	int id;
	String problemReported;
	String InstModfile;
	String actionAttempted; 
	String ruleSet;
	String fileContent;
	

	
//	public InstMods(int id,String prob,String file,String holdsat)
//	{
//		this.id = id;
//		strProblem = prob;
//		InstModfile = file;
//		actionAttempted = holdsat;
//		
//	}
	
	public InstMods(String act,String file,String prob)
	{
		problemReported = prob;
		InstModfile = file;
		actionAttempted = act;
		
	}
	
	public InstMods(String act,String prob,String rule, String file)
	{
		problemReported = prob;
		ruleSet = rule;
		actionAttempted = act;
		fileContent=file;
		
	}

//	public void setInstId(int id)
//	{
//		instId = id;
//	}
//	
//	public int getInstId()
//	{
//		return instId;  
//	}
	
	public String getAction()
	{
		return actionAttempted;
	}
	
	public String getProblem()
	{
		return problemReported;
	}
	
	public String getInstFile()
	{
		return InstModfile;
	}
	
	public String getFileConts()
	{
		return fileContent;
	}
	
	public String getRuleSet()
	{
		return ruleSet;
	}
	
}
