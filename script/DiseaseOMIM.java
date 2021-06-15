import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class DiseaseOMIM {
	public static void main(String[] args) throws IOException{		 
		
		int count = 0;
		String line;
		String[] split;
		String OMIM_ID = null, HPO_ID = null, temp = null, PMID = null;
		
		int b_number = 0;
		
		count = 0;
		
		HashMap<String, String> OMIM_Manual = new HashMap<String, String>();
		HashMap<String, String> OMIM_CaceReport = new HashMap<String, String>();
		
		// Manual
		BufferedReader br_OMIM_HPO_Manual = new BufferedReader(new FileReader(args[0]));

		for (int i = 0; i < 5; i++)
			br_OMIM_HPO_Manual.readLine();

		while((line = br_OMIM_HPO_Manual.readLine()) != null)
		{
			split= line.split("\t");

			if(split[0].split(":")[0].equals("OMIM"))
			{
				OMIM_ID = split[0].replace("OMIM:", "");
				HPO_ID = split[3].replace("HP:", "");

				if(OMIM_Manual.get(OMIM_ID + "\t" + HPO_ID) == null)
				{
					++count;
					OMIM_Manual.put(OMIM_ID + "\t" + HPO_ID, "Manual");
				}
			}
		}
		br_OMIM_HPO_Manual.close();
		//System.out.println("OMIM_HPO_Manual Count : " + count);
		
		// CaseReport		
		count = 0;
		BufferedReader br_OMIM_HPO_CaseReport = new BufferedReader(new FileReader(args[1]));

		while((line = br_OMIM_HPO_CaseReport.readLine()) != null)
		{
			split= line.split("\t");
			OMIM_ID = split[1].replace("OMIM:", "");
			HPO_ID = split[2].replace("HP:", "");
			
			if(!split[4].equals("HPO"))
			{
				if(OMIM_CaceReport.get(OMIM_ID + "\t" + HPO_ID) == null)
				{
					++count;
					OMIM_CaceReport.put(OMIM_ID + "\t" + HPO_ID, "CaseReport");
				}
			}
		}
		br_OMIM_HPO_CaseReport.close();
		//System.out.println("OMIM_HPO_CaseReport Count : " + count);

		HashMap<String, String> OMIM_Repot = new HashMap<String, String>();
		
		// CaseReport
		BufferedReader br_AnnotOntoOMIMHP = new BufferedReader(new FileReader(args[2]));
		BufferedWriter bw_OMIM_HP_CaseReport = new BufferedWriter(new FileWriter("./OMIM_HP_Association.ttl"));
		
		bw_OMIM_HP_CaseReport.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX foaf: <http://xmlns.com/foaf/0.1>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX ipubmed: <http://identifiers.org/pubmed/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX mim: <http://identifiers.org/mim/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX oa: <http://www.w3.org/ns/oa#>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_OMIM_HP_CaseReport.newLine();	
		bw_OMIM_HP_CaseReport.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_OMIM_HP_CaseReport.newLine();
		
		while((line = br_AnnotOntoOMIMHP.readLine()) != null)
		{
			split= line.split("\t");
			
			PMID = split[1];
			OMIM_ID = split[2].replace("OMIM:", "");
			HPO_ID = split[4].replace("HP:", "");

			if(OMIM_Repot.get(OMIM_ID + "\t" + HPO_ID) == null)
			{
				OMIM_Repot.put(OMIM_ID + "\t" + HPO_ID, PMID);
			}
			else
			{
				temp = OMIM_Repot.get(OMIM_ID + "\t" + HPO_ID);
				temp = temp + "\t" + PMID;
				OMIM_Repot.put(OMIM_ID + "\t" + HPO_ID, temp);
			}
		}
		br_AnnotOntoOMIMHP.close();

		int manual_count = 0;
		int tempc = 0, tempm = 0;
		
		for(String key : OMIM_CaceReport.keySet())
		{
			tempm = 0;
			
			OMIM_ID = key.split("\t")[0];
			HPO_ID = key.split("\t")[1];
			
			bw_OMIM_HP_CaseReport.write("<https://pubcasefinder.dbcls.jp/phenotype_context/disease:OMIM:" + OMIM_ID + "/phenotype:HP:" + HPO_ID + ">");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    a oa:Annotation ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    oa:hasTarget mim:" + OMIM_ID + " ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    oa:hasBody obo:HP_" + HPO_ID + " ;");bw_OMIM_HP_CaseReport.newLine();			
			
			tempc = ++b_number;
			
			bw_OMIM_HP_CaseReport.write("    dcterms:source _:b" + tempc + " ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    obo:ECO_9000001 obo:ECO_0007669 ");
			
			if(OMIM_Manual.get(key) != null)
			{
				++manual_count;
				tempm = ++b_number;
				bw_OMIM_HP_CaseReport.write(";");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    dcterms:source _:b" + tempm + " ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    obo:ECO_9000001 obo:ECO_0000218 .");bw_OMIM_HP_CaseReport.newLine();
				OMIM_Manual.remove(key);
			}
			else
			{
				bw_OMIM_HP_CaseReport.write(".");bw_OMIM_HP_CaseReport.newLine();
			}
				
			bw_OMIM_HP_CaseReport.write("_:b" + tempc);bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    dcterms:creator \"Database Center for Life Science\"");

			if(OMIM_Repot.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    dcterms:references ");
				temp = OMIM_Repot.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_OMIM_HP_CaseReport.write("ipubmed:" + split[i] + " .");bw_OMIM_HP_CaseReport.newLine();
						}
						else
							bw_OMIM_HP_CaseReport.write("ipubmed:" + split[i] + ", ");
					}
				}
			}
			else
			{
				bw_OMIM_HP_CaseReport.write(" .");bw_OMIM_HP_CaseReport.newLine();
			}
			
			if(tempm > 0)
			{
				bw_OMIM_HP_CaseReport.write("_:b" + tempm);bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    dcterms:creator \"Human Phenotype Ontology Consortium\" ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    foaf:page <http://compbio.charite.de/jenkins/job/hpo.annotations.current/lastSuccessfulBuild/artifact/current/phenotype.hpoa> .");bw_OMIM_HP_CaseReport.newLine();
			}
		}
		
		// Manual
		for(String key : OMIM_Manual.keySet())
		{
			++manual_count;
			OMIM_ID = key.split("\t")[0];
			HPO_ID = key.split("\t")[1];
			
			bw_OMIM_HP_CaseReport.write("<https://pubcasefinder.dbcls.jp/phenotype_context/disease:OMIM:" + OMIM_ID + "/phenotype:HP:" + HPO_ID + ">");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    a oa:Annotation ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    oa:hasTarget mim:" + OMIM_ID + " ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    oa:hasBody obo:HP_" + HPO_ID + " ;");bw_OMIM_HP_CaseReport.newLine();			
			
			bw_OMIM_HP_CaseReport.write("    dcterms:source _:b" + ++b_number + " ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    obo:ECO_9000001 obo:ECO_0000218 .");bw_OMIM_HP_CaseReport.newLine();
			
			bw_OMIM_HP_CaseReport.write("_:b" + b_number);bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    dcterms:creator \"Human Phenotype Ontology Consortium\" ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    foaf:page <http://compbio.charite.de/jenkins/job/hpo.annotations.current/lastSuccessfulBuild/artifact/current/phenotype.hpoa> .");bw_OMIM_HP_CaseReport.newLine();
		}
		//System.out.println(manual_count);
		
		bw_OMIM_HP_CaseReport.close();		
	}
}
