import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class OMIM_All {
	public static void main(String[] args) throws IOException{		 
		
		int count = 0;
		String title = null, name = null, HPO_ID = null;
		
		// OMIM All
		HashMap<String, String> OMIM_All_Map = new HashMap<String, String>();
		
		BufferedReader br_mim2gene = new BufferedReader(new FileReader(args[0]));
		
		String line;
		String[] split;
		
		String OMIM_ID = null, type = null;
		
		for (int i = 0; i < 5; i++)
			br_mim2gene.readLine();

		while((line = br_mim2gene.readLine()) != null)
		{
			split = line.split("\t");
			
			OMIM_ID = split[0];
			type = split[1];
			
			if (type.equals("gene/phenotype") || type.equals("phenotype") || type.equals("predominantly phenotypes"))
			{
				if(OMIM_All_Map.get(OMIM_ID) == null)
				{
					++count;
					OMIM_All_Map.put(OMIM_ID,"");
				}
			}
		}
		br_mim2gene.close();
		//System.out.println("OMIM All Count : " + count);
		
		
		// OMIM label JP
		count = 0;
		
		HashMap<String, String> UR_OMIM_jp_label_Map = new HashMap<String, String>();
		
		BufferedReader br_UR_DBMS = new BufferedReader(new FileReader(args[1]));

		String OMIM_jp = null;

		while((line = br_UR_DBMS.readLine()) != null)
		{
			try {
				split = line.split("\t");
			
				OMIM_ID = split[0].replace("OMIM:", "");
				OMIM_jp = split[1];
			}
			catch (Exception e) {
				continue;
			}

			if(UR_OMIM_jp_label_Map.get(OMIM_ID) == null)
			{
				++count;
				UR_OMIM_jp_label_Map.put(OMIM_ID, OMIM_jp);
			}
		}
		br_UR_DBMS.close();
		//System.out.println("OMIM @ja Count : " + count);
		
		
		// Inheritance
		count = 0;
		
		HashMap<String, String> Inheritance_Map = new HashMap<String, String>();
		
		BufferedReader br_Inheritance = new BufferedReader(new FileReader(args[2]));

		String relationship = null, inheritance = null, temp = null;
		boolean flag = false;
		
		br_Inheritance.readLine();

		while((line = br_Inheritance.readLine()) != null)
		{
			split = line.split("\\|");
		
			OMIM_ID = split[1];
			relationship = split[3];
			inheritance = split[5].replace("HP:", "");//
			
			if(relationship.equals("inheritance_type_of"))
			{
				if(Inheritance_Map.get(OMIM_ID) == null)
				{
					++count;
					Inheritance_Map.put(OMIM_ID, inheritance);
				}
				else
				{
					flag = false;
					temp = Inheritance_Map.get(OMIM_ID);
					
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						if(split[i].equals(inheritance))
						{
							flag = true;
						}
					}
					if(!flag)
					{
						++count;
						temp = temp + "\t" + inheritance;
						Inheritance_Map.put(OMIM_ID, temp);
					}
				}
			}
		}
		br_Inheritance.close();
		//System.out.println("OMIM inheritance Count : " + count);
		
		
		// OMIM_MONDO, GTR
		count = 0;
		
		HashMap<String, String> OMIM_MONDO_Map = new HashMap<String, String>();
		HashMap<String, String> OMIM_UMLS_Map = new HashMap<String, String>();
		
		BufferedReader br_MONDO = new BufferedReader(new FileReader(args[3]));

		ArrayList<String> OMIM_IDs = new ArrayList<String>();
		ArrayList<String> MONDO_IDs = new ArrayList<String>();
		ArrayList<String> UMLS_IDs = new ArrayList<String>();
		
		while((line = br_MONDO.readLine()) != null)
		{
			try {
				if(line.equals("[Term]"))
				{
					for(;;)
					{
						line = br_MONDO.readLine();
										
						split = line.split(" ");
						title = split[0];
						
						if(title.equals("id:"))
						{
							MONDO_IDs.add(line.split("MONDO:")[1]);
						}
						else if(title.equals("xref:"))
						{
							if(split[1].split(":")[0].equals("OMIM"))
							{
								if(line.contains("MONDO:equivalentTo"))
								{
									OMIM_IDs.add(split[1].split(":")[1]);
								}
							}
							else if(split[1].split(":")[0].equals("UMLS"))
							{
								if(line.contains("MONDO:equivalentTo"))
								{
									UMLS_IDs.add(split[1].split(":")[1]);
								}
							}
						}
						
						if(line.equals("is_obsolete: true"))
						{
							OMIM_IDs.clear();
							MONDO_IDs.clear();
							UMLS_IDs.clear();
							break;
						}
						else if(line.equals(""))
						{

							for(String i : OMIM_IDs)
							{
								if(OMIM_All_Map.get(i) == null)
								{
									OMIM_All_Map.put(i,"");
								}
								
								// OMIM MONDO Map
								if(OMIM_MONDO_Map.get(i) == null)
								{
									++count;
									OMIM_MONDO_Map.put(i, MONDO_IDs.get(0));
								}
								else
								{
									++count;
									temp = OMIM_MONDO_Map.get(i);
									temp = temp + "\t" + MONDO_IDs.get(0);
									OMIM_MONDO_Map.put(i, temp);
								}
								
								// OMIM UMLS Map
								for(String j : UMLS_IDs)
								{
									if(OMIM_UMLS_Map.get(i) == null)
									{
										OMIM_UMLS_Map.put(i, j);
									}
									else
									{
										temp = OMIM_UMLS_Map.get(i);
										temp = temp + "\t" + j;
										OMIM_UMLS_Map.put(i, temp);
									}
								}
							}
							OMIM_IDs.clear();
							MONDO_IDs.clear();
							UMLS_IDs.clear();
							break;
						}
					}
					
				}
			}
			catch (Exception e) {
				continue;
			}
		}
		br_MONDO.close();
		
		
		// UR-DBMS_Link = RDB data
		count = 0;
		
		HashMap<String, String> UR_DBMS_Link_Map = new HashMap<String, String>();
		
		BufferedReader br_UR_DBMS_Link = new BufferedReader(new FileReader(args[4]));

		String UR_DBMS_Link = null;
		
		while((line = br_UR_DBMS_Link.readLine()) != null)
		{
			split = line.split("\t");
			
			if(split[4].equals("UR-DBMS"))
			{
				OMIM_ID = split[1].replace("OMIM:", "");
				UR_DBMS_Link = split[3];
				
				if (UR_DBMS_Link.equals(""))
					continue;
				
				if(UR_DBMS_Link_Map.get(OMIM_ID) == null)
				{
					++count;
					UR_DBMS_Link_Map.put(OMIM_ID, UR_DBMS_Link);
				}				
			}
		}
		br_UR_DBMS_Link.close();
		//System.out.println("OMIM UR_DBMS_Link Count : " + count);
		
		
		// KEGG
		count = 0;
		
		HashMap<String, String> KEGG_Map = new HashMap<String, String>();
		
		BufferedReader br_KEGG = new BufferedReader(new FileReader(args[5]));

		String KEGG_ID = null;
		
		while((line = br_KEGG.readLine()) != null)
		{
			split = line.split("\t");
		
			OMIM_ID = split[0];
			KEGG_ID = split[1];
			
			if(KEGG_Map.get(OMIM_ID) == null)
			{
				++count;
				KEGG_Map.put(OMIM_ID, KEGG_ID);
			}
		}
		br_KEGG.close();
		//System.out.println("OMIM KEGG Count : " + count);
		
		
		// Gene_Review
		count = 0;
		
		HashMap<String, String> Gene_Review_Map = new HashMap<String, String>();
		
		BufferedReader br_Gene_Review = new BufferedReader(new FileReader(args[6]));

		String NBK_ID = null;
		br_Gene_Review.readLine();

		while((line = br_Gene_Review.readLine()) != null)
		{
			split = line.split("\t");
		
			NBK_ID = split[0];
			OMIM_ID = split[2];			
			
			if(Gene_Review_Map.get(OMIM_ID) == null)
			{
				++count;
				Gene_Review_Map.put(OMIM_ID, NBK_ID);
			}
			else
			{
				++count;
				temp = Gene_Review_Map.get(OMIM_ID);
				temp = temp + "\t" + NBK_ID;
				Gene_Review_Map.put(OMIM_ID, temp);
			}

		}
		br_Gene_Review.close();
		//System.out.println("OMIM Gene_Review Count : " + count);

		BufferedWriter bw_OMIM_HP_CaseReport = new BufferedWriter(new FileWriter("./OMIM.ttl"));
		
		bw_OMIM_HP_CaseReport.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX nando: <http://nanbyodata.jp/ontology/nando#>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX med2rdf: <http://med2rdf.org/ontology/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX mim: <http://identifiers.org/mim/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_OMIM_HP_CaseReport.newLine();
		bw_OMIM_HP_CaseReport.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_OMIM_HP_CaseReport.newLine();	
		bw_OMIM_HP_CaseReport.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_OMIM_HP_CaseReport.newLine();

		
		// OMIM
		count = 0;
		for(String key : OMIM_All_Map.keySet())
		{	
			++count;
			bw_OMIM_HP_CaseReport.write("mim:" + key);bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    a med2rdf:Disease, ncit:C7057 ;");bw_OMIM_HP_CaseReport.newLine();
			bw_OMIM_HP_CaseReport.write("    dcterms:identifier \"" + key + "\"");
			
			// JP label output
			if(UR_OMIM_jp_label_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				temp = UR_OMIM_jp_label_Map.get(key);
				bw_OMIM_HP_CaseReport.write("    rdfs:label \"" + temp + "\"@ja");				
			}
			
			// Inheritance output
			if(Inheritance_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    nando:hasInheritance ");
				temp = Inheritance_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_OMIM_HP_CaseReport.write("obo:HP_" + split[i]);
						else
							bw_OMIM_HP_CaseReport.write("obo:HP_" + split[i] + ", ");
					}
				}
				
			}
			
			// MONDO output
			if(OMIM_MONDO_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    rdfs:seeAlso ");
				temp = OMIM_MONDO_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_OMIM_HP_CaseReport.write("obo:MONDO_" + split[i]);
						else
							bw_OMIM_HP_CaseReport.write("obo:MONDO_" + split[i] + ", ");
					}
				}
				
			}
			
			// UR-DBMS Link output
			if(UR_DBMS_Link_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				temp = UR_DBMS_Link_Map.get(key);
				bw_OMIM_HP_CaseReport.write("    rdfs:seeAlso <" + temp + ">");
			}
			
			// KEGG output
			if(KEGG_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				temp = KEGG_Map.get(key);
				bw_OMIM_HP_CaseReport.write("    rdfs:seeAlso <https://www.kegg.jp/dbget-bin/www_bget?" + temp + ">");
			}
			
			// Gene Review output
			if(Gene_Review_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    rdfs:seeAlso ");
				temp = Gene_Review_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_OMIM_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/books/" + split[i] + "/>");
						}
						else
							bw_OMIM_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/books/" + split[i] + "/>" + ", ");
					}
				}
				
			}
			
			// GTR output
			if(OMIM_UMLS_Map.get(key) != null)
			{
				bw_OMIM_HP_CaseReport.write(" ;");bw_OMIM_HP_CaseReport.newLine();
				bw_OMIM_HP_CaseReport.write("    rdfs:seeAlso ");
				temp = OMIM_UMLS_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_OMIM_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/> .");bw_OMIM_HP_CaseReport.newLine();
						}
						else
							bw_OMIM_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/>" + ", ");
					}
				}
				
			}
			else
			{
				bw_OMIM_HP_CaseReport.write(" .");bw_OMIM_HP_CaseReport.newLine();
			}
		}
		//System.out.println("OMIM All Count : " + count);

		bw_OMIM_HP_CaseReport.close();
	}
}
