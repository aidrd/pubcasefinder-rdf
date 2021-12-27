import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class HP_Inheritance {
	public static void main(String[] args) throws IOException{
		
		String line;
		String[] split;
		String HPO_ID = null, title = null, name = null, inheritance_jp = null, temp = null;
		
		// HP All
		BufferedReader br_HP_obo = new BufferedReader(new FileReader(args[0]));
		HashMap<String, String> HP_obo_Map = new HashMap<String, String>();
		
		while((line = br_HP_obo.readLine()) != null)
		{
			try {
				if(line.equals("[Term]"))
				{
					for(;;)
					{
						line = br_HP_obo.readLine();
										
						split = line.split(" ");
						title = split[0];
						
						if(title.equals("id:"))
							HPO_ID = line.split("HP:")[1];
						if(title.equals("name:"))
							name = line.split("name: ")[1];
						if(line.equals("is_obsolete: true"))
							break;							
						else if(line.equals(""))
						{
							if(HP_obo_Map.get(HPO_ID) == null)
								HP_obo_Map.put(HPO_ID, name);
							break;
						} 
					}
				}
			}
			catch (Exception e) {	continue;	}
		}
		br_HP_obo.close();
		
		
		// HP label JP
		BufferedReader br_HP_jp_label = new BufferedReader(new FileReader(args[1]));
		HashMap<String, String> UR_HP_jp_label_Map = new HashMap<String, String>();
		String HP_jp = null;

		while((line = br_HP_jp_label.readLine()) != null)
		{
			try {
				split = line.split("\t");
			
				HPO_ID = split[0].replace("HP:", "");
				HP_jp = split[1];
			}
			catch (Exception e) {	continue;	}

			if(UR_HP_jp_label_Map.get(HPO_ID) == null)
				UR_HP_jp_label_Map.put(HPO_ID, HP_jp);
		}
		br_HP_jp_label.close();

		BufferedWriter bw_ORDO_HP_CaseReport = new BufferedWriter(new FileWriter("./HP_Inheritance.ttl"));		
		
		bw_ORDO_HP_CaseReport.write("PREFIX ihp: <http://identifiers.org/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_ORDO_HP_CaseReport.newLine();
		
		// HPO
		for(String key : HP_obo_Map.keySet())
		{
			bw_ORDO_HP_CaseReport.write("obo:HP_" + key);bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    rdfs:seeAlso ihp:HP:" + key);

			if(UR_HP_jp_label_Map.get(key) != null)
			{				
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				temp = UR_HP_jp_label_Map.get(key);
				bw_ORDO_HP_CaseReport.write("    rdfs:label \"" + temp + "\"@ja .");				
			}
			else
				bw_ORDO_HP_CaseReport.write(" .");bw_ORDO_HP_CaseReport.newLine();
		}
		
		// Inheritance
		BufferedReader br_HPO_Inheritance_en_jp = new BufferedReader(new FileReader(args[2]));
		String inheritance_en = null;
		
		br_HPO_Inheritance_en_jp.readLine();

		while((line = br_HPO_Inheritance_en_jp.readLine()) != null)
		{
			split = line.split("\t");
			
			HPO_ID = split[0].split("HP:")[1];
			inheritance_en = split[1];
			inheritance_jp = split[2];
			
			bw_ORDO_HP_CaseReport.write("obo:HP_" + HPO_ID);bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    rdfs:label \"" + inheritance_en + "\"@en ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    rdfs:label \"" + inheritance_jp + "\"@ja .");bw_ORDO_HP_CaseReport.newLine();
		}
		br_HPO_Inheritance_en_jp.close();

		bw_ORDO_HP_CaseReport.close();
	}
}

