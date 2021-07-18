import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class InstitutionHandler {

	JSONObject instRules;
	JSONObject modInstRulesDict;
	String iFileContents;
	String originalRuleSet;
	
	public InstitutionHandler (String file1path,String file2path)
	{
		originalRuleSet = "R1;R2;R3;R4;R5;R6;R7;R8;R9;R10;R11;R12;R13;R14;R15;R16;R17;R18;R19;R20;R21;R22;R23;R24;R24;R25;R26;R27;R28";
		
		try {
			parseInstRules(file1path,0);
			parseInstRules(file2path,1);
			System.out.println("Institutional rules file parsed");
		} catch (Exception e) {
			System.out.println("Error parsing json rules file");
		}
	}
	
	
	private void parseInstRules(String path,int flag) throws Exception{
        String jsonString;

        final BufferedReader fileReader = new BufferedReader(new FileReader(path));
        final StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line =  fileReader.readLine()) != null) {
            jsonContent.append(line);
        }
        fileReader.close();
        jsonString = jsonContent.toString();
       // System.out.println("About to parse");
        if(flag==0)
        	instRules = new JSONObject(jsonString);
        else
        	modInstRulesDict = new JSONObject(jsonString);
       // System.out.println("Finished parsing");

    }
	
	/*
	 * Function to build the institutional file
	 */
	private String buildFile(String rules2include)
	{
        String instString="";

        BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(new FileReader("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsTempl.txt"));
		
			final StringBuilder content = new StringBuilder();
	        String line;
	        String[] rule = rules2include.split(";");
	        while ((line =  fileReader.readLine()) != null) {
	        	if(line.equals("[rulesplaceholder]"))
	        	{
	        		//int c =1;
	        		for(String r:rule)
	        		{
	        			content.append("%%Rule "+r+" %%\n");
	        			//System.out.println("rules "+r);
	        			content.append(instRules.get(r).toString());
	        			content.append("\n\n");
	        			//c++;
	        		}
	        	}else
	        	{
	        		content.append(line);
	        		content.append("\n");
	        	}
	        }
	        fileReader.close();
	        instString = content.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		return instString;
	}
	
	public String getRuleSet(String prob, String act,String curRule)
	{
		String rulesSet="";
		JSONObject mods = (JSONObject) modInstRulesDict.get(StringUtils.substringBefore(act,"("));
		if(mods.has(prob))
		{
			System.out.println("A possible solution exist");
			String rule2change=(String)mods.get(prob);
			//String[] r = rule2change.split("-");
			if(curRule.equals(""))
				curRule=originalRuleSet;
			System.out.println("Before: "+curRule);
			
			
		//	rulesSet=StringUtils.replace(curRule, r[0],r[1]);
			rulesSet=updateRuleSet(curRule,rule2change);
			System.out.println("After: "+rulesSet);
		}
		else
		{
			System.out.println("Sorry, no solution exist");
			rulesSet="none";
		}
		//StringUtils.substringBefore(act,"(")
		return rulesSet;
	}
	
	
	/*
	 * function to update rules set based on the type of modification expected
	 * strings contains type-what-change eg mod-R7-M1R7,del-R7,add-R34 
	 * mod = modification or change
	 * del = delete
	 * add = add
	 */
	
	private String updateRuleSet(String toUpdate, String rules)
	{
		String updated="";
		String[] str = rules.split("-");
		if(str[0].equals("mod"))
			updated=StringUtils.replace(toUpdate, str[1],str[2]);
		else if(str[0].equals("del"))
			updated=StringUtils.replace(toUpdate, (str[1]+";"),"");
		else  if(str[0].equals("add"))
			updated= toUpdate.concat((";"+str[1]));
		else
			updated = toUpdate;
		return updated;
	}
	
	/*
	 * Function to update the institution given the new rules list/set
	 */
	public String reviseInst(String rulesList)
	{
		String file_contents = buildFile(rulesList);
		try {
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsInst.lp"), file_contents.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return file_contents;
	}
}
