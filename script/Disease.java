import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Disease {
	public static void main(String[] args) throws IOException{
		
		int count = 0;
		String line;
		String[] split;
		String Orphanet_ID = null, HPO_ID = null, temp = null;
		
		String PMID = null;

		int b_number = 0;
		
		count = 0;
		
		HashMap<String, String> Orphanet_Manual = new HashMap<String, String>();
		HashMap<String, String> Orphanet_CaceReport = new HashMap<String, String>();
		
		// Manual
		BufferedReader br_Orphanet_HPO_Manual = new BufferedReader(new FileReader(args[0]));

		for (int i = 0; i < 5; i++)
			br_Orphanet_HPO_Manual.readLine();
		
		while((line = br_Orphanet_HPO_Manual.readLine()) != null)
		{
			split= line.split("\t");

			if(split[0].split(":")[0].equals("ORPHA"))
			{
				Orphanet_ID = split[0].replace("ORPHA:", "");
				HPO_ID = split[3].replace("HP:", "");

				if(Orphanet_Manual.get(Orphanet_ID + "\t" + HPO_ID) == null)
				{
					++count;
					Orphanet_Manual.put(Orphanet_ID + "\t" + HPO_ID, "Manual");
				}
			}
		}
		br_Orphanet_HPO_Manual.close();
		//System.out.println("Orphanet_HPO_Manual Count : " + count);

		// CaseReport
		count = 0;
		BufferedReader br_Orphanet_HPO_CaseReport = new BufferedReader(new FileReader(args[1]));

		while((line = br_Orphanet_HPO_CaseReport.readLine()) != null)
		{
			split= line.split("\t");
			Orphanet_ID = split[1].replace("ORDO:", "");
			HPO_ID = split[2].replace("HP:", "");
			
			if(!split[4].equals("Orphanet"))
			{
				if(Orphanet_CaceReport.get(Orphanet_ID + "\t" + HPO_ID) == null)
				{
					++count;
					Orphanet_CaceReport.put(Orphanet_ID + "\t" + HPO_ID, "CaseReport");
				}
			}
		}
		br_Orphanet_HPO_CaseReport.close();
		//System.out.println("Orphanet_HPO_CaseReport Count : " + count);
		
		HashMap<String, String> ORDO_Repot = new HashMap<String, String>();
		
		// CaseReport
		BufferedReader br_AnnotOntoORDOHP = new BufferedReader(new FileReader(args[2]));
		BufferedWriter bw_ORDO_HP_CaseReport = new BufferedWriter(new FileWriter("./Orphanet_HP_Association.ttl"));		
		
		bw_ORDO_HP_CaseReport.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX foaf: <http://xmlns.com/foaf/0.1>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX ipubmed: <http://identifiers.org/pubmed/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX oa: <http://www.w3.org/ns/oa#>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX ordo: <http://www.orpha.net/ORDO/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_ORDO_HP_CaseReport.newLine();
		
		while((line = br_AnnotOntoORDOHP.readLine()) != null)
		{
			split= line.split("\t");
			
			PMID = split[1];
			Orphanet_ID = split[2].replace("ORDO:", "");
			HPO_ID = split[4].replace("HP:", "");

			if(ORDO_Repot.get(Orphanet_ID + "\t" + HPO_ID) == null)
			{
				ORDO_Repot.put(Orphanet_ID + "\t" + HPO_ID, PMID);
			}
			else
			{
				temp = ORDO_Repot.get(Orphanet_ID + "\t" + HPO_ID);
				temp = temp + "\t" + PMID;
				ORDO_Repot.put(Orphanet_ID + "\t" + HPO_ID, temp);
			}
		}
		br_AnnotOntoORDOHP.close();

		int tempc = 0, tempm = 0;
		
		for(String key : Orphanet_CaceReport.keySet())
		{
			tempm = 0;
			
			Orphanet_ID = key.split("\t")[0];
			HPO_ID = key.split("\t")[1];
			
			bw_ORDO_HP_CaseReport.write("<https://pubcasefinder.dbcls.jp/phenotype_context/disease:ORDO:" + Orphanet_ID + "/phenotype:HP:" + HPO_ID + ">");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    a oa:Annotation ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    oa:hasTarget ordo:Orphanet_" + Orphanet_ID + " ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    oa:hasBody obo:HP_" + HPO_ID + " ;");bw_ORDO_HP_CaseReport.newLine();			
			
			tempc = ++b_number;
			
			bw_ORDO_HP_CaseReport.write("    dcterms:source _:b" + tempc + " ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    obo:ECO_9000001 obo:ECO_0007669 ");
			
			if(Orphanet_Manual.get(key) != null)
			{
				tempm = ++b_number;
				bw_ORDO_HP_CaseReport.write(";");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    dcterms:source _:b" + tempm + " ;");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    obo:ECO_9000001 obo:ECO_0000218 .");bw_ORDO_HP_CaseReport.newLine();
				Orphanet_Manual.remove(key);
			}
			else
			{
				bw_ORDO_HP_CaseReport.write(".");bw_ORDO_HP_CaseReport.newLine();
			}

			bw_ORDO_HP_CaseReport.write("_:b" + tempc);bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    dcterms:creator \"Database Center for Life Science\"");

			if(ORDO_Repot.get(key) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    dcterms:references ");
				temp = ORDO_Repot.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_ORDO_HP_CaseReport.write("ipubmed:" + split[i] + " .");bw_ORDO_HP_CaseReport.newLine();
						}
						else
							bw_ORDO_HP_CaseReport.write("ipubmed:" + split[i] + ", ");
					}
				}
			}
			else
			{
				bw_ORDO_HP_CaseReport.write(" .");bw_ORDO_HP_CaseReport.newLine();
			}			
			if(tempm > 0)
			{
				bw_ORDO_HP_CaseReport.write("_:b" + tempm);bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    dcterms:creator \"Orphanet\" ;");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    foaf:page <http://compbio.charite.de/jenkins/job/hpo.annotations.current/lastSuccessfulBuild/artifact/current/phenotype.hpoa> .");bw_ORDO_HP_CaseReport.newLine();
			}
		}
		
		// Manual		
		for(String key : Orphanet_Manual.keySet())
		{
			Orphanet_ID = key.split("\t")[0];
			HPO_ID = key.split("\t")[1];
			
			bw_ORDO_HP_CaseReport.write("<https://pubcasefinder.dbcls.jp/phenotype_context/disease:ORDO:" + Orphanet_ID + "/phenotype:HP:" + HPO_ID + ">");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    a oa:Annotation ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    oa:hasTarget ordo:Orphanet_" + Orphanet_ID + " ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    oa:hasBody obo:HP_" + HPO_ID + " ;");bw_ORDO_HP_CaseReport.newLine();						
			
			bw_ORDO_HP_CaseReport.write("    dcterms:source _:b" + ++b_number + " ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    obo:ECO_9000001 obo:ECO_0000218 .");bw_ORDO_HP_CaseReport.newLine();
			
			bw_ORDO_HP_CaseReport.write("_:b" + b_number);bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    dcterms:creator \"Orphanet\" ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    foaf:page <http://compbio.charite.de/jenkins/job/hpo.annotations.current/lastSuccessfulBuild/artifact/current/phenotype.hpoa> .");bw_ORDO_HP_CaseReport.newLine();
		}
		
		bw_ORDO_HP_CaseReport.close();
	}
}

