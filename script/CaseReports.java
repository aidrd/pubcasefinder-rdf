import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CaseReports {
	public static void main(String[] args) throws IOException{
		
		BufferedReader br_CaseReports = new BufferedReader(new FileReader(args[0]));
		BufferedReader PMID_MESH = new BufferedReader(new FileReader(args[1]));
		
		BufferedWriter bw_CaseReports = new BufferedWriter(new FileWriter("./CaseReports.ttl"));
		
		bw_CaseReports.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_CaseReports.newLine();
		bw_CaseReports.write("PREFIX bibo: <http://purl.org/ontology/bibo/>");bw_CaseReports.newLine();
		bw_CaseReports.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_CaseReports.newLine();
		bw_CaseReports.write("PREFIX fabio: <http://purl.org/spar/fabio>");bw_CaseReports.newLine();
		bw_CaseReports.write("PREFIX mesh: <http://id.nlm.nih.gov/mesh/>");bw_CaseReports.newLine();
		bw_CaseReports.write("PREFIX pubmed: <http://rdf.ncbi.nlm.nih.gov/pubmed/>");bw_CaseReports.newLine();
		bw_CaseReports.write("PREFIX ipubmed: <http://identifiers.org/pubmed/>");bw_CaseReports.newLine();
		
		String Case_PMID = null, PMID_MESH_PMID = null;
		String SDUI = null;
		
		String Case_line, PMID_MESH_line;
		String[] Case_split = null, split = null;
		
		ArrayList<String> temp = new ArrayList<>();
		
		int count = 0;
		
		Case_line = br_CaseReports.readLine();
		PMID_MESH_line = PMID_MESH.readLine();
		
		Case_split = Case_line.split("\t");
		Case_PMID = Case_split[0].replace("\"", "");
		
		split = PMID_MESH_line.split("\t");
		PMID_MESH_PMID = split[1];
		
		while(true)
		{
			if(Integer.parseInt(Case_PMID) > Integer.parseInt(PMID_MESH_PMID))
			{
				
				if((PMID_MESH_line = PMID_MESH.readLine()) != null)
				{
					split = PMID_MESH_line.split("\t");
					PMID_MESH_PMID = split[1];
				}
				else
				{
					bw_CaseReports.write("ipubmed:" + Case_PMID);bw_CaseReports.newLine();
					bw_CaseReports.write("    a bibo:Article ;");bw_CaseReports.newLine();
					bw_CaseReports.write("    rdfs:seeAlso pubmed:" + Case_PMID + " .");bw_CaseReports.newLine();
					
					bw_CaseReports.flush();
					
					if((Case_line = br_CaseReports.readLine()) != null)
					{
						Case_split = Case_line.split("\t");
						Case_PMID = Case_split[0].replace("\"", "");
					}
					else
					{
						break;
					}
				}
			}
			else if(Integer.parseInt(Case_PMID) == Integer.parseInt(PMID_MESH_PMID))
			{
				SDUI = split[3];
				if(SDUI.length() > 6)
				{
					temp.add(SDUI);
				}
				
				if((PMID_MESH_line = PMID_MESH.readLine()) != null)
				{
					split = PMID_MESH_line.split("\t");
					PMID_MESH_PMID = split[1];
				}
			}
			else
			{				
				temp.sort(null);
				
				bw_CaseReports.write("ipubmed:" + Case_PMID);bw_CaseReports.newLine();
				bw_CaseReports.write("    a bibo:Article ;");bw_CaseReports.newLine();
				bw_CaseReports.write("    rdfs:seeAlso pubmed:" + Case_PMID);
				
				if(temp.size() == 0)
				{
					bw_CaseReports.write(" .");bw_CaseReports.newLine();
				}
				else
				{
					bw_CaseReports.write(" ;");bw_CaseReports.newLine();
					bw_CaseReports.write("    fabio:hasSubjectTerm ");
					
					for(int i = 0; i < temp.size(); i++)
					{
						if(temp.size() - 1 == i)
						{
							bw_CaseReports.write("mesh:" + temp.get(i).toString() + " .");bw_CaseReports.newLine();
						}
						else
							bw_CaseReports.write("mesh:" + temp.get(i).toString() + ", ");
					}
					temp.clear();
				}
				
				bw_CaseReports.flush();
				
				if((Case_line = br_CaseReports.readLine()) != null)
				{
					Case_split = Case_line.split("\t");
					Case_PMID = Case_split[0].replace("\"", "");
				}
				else
				{
					break;
				}
			}
		}
		bw_CaseReports.close();
	}
}
