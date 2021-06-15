import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Orphanet_All {
	public static void main(String[] args) throws IOException{
		
		int count = 0;
		String line;
		String[] split;
		String MONDO_ID = null, Orphanet_ID = null, OMIM_ID = null, HPO_ID = null, title = null;
	
		// OMIM label JP
		count = 0;
		
		HashMap<String, String> OMIM_ja_label_Map = new HashMap<String, String>();
		
		BufferedReader br_omim_ja = new BufferedReader(new FileReader(args[0]));

		String OMIM_ja = null;

		while((line = br_omim_ja.readLine()) != null)
		{
			try {
				split = line.split("\t");
			
				OMIM_ID = split[0].replace("OMIM:", "");
				OMIM_ja = split[1];
			}
			catch (Exception e) {
				continue;
			}

			if(OMIM_ja_label_Map.get(OMIM_ID) == null)
			{
				++count;
				OMIM_ja_label_Map.put(OMIM_ID, OMIM_ja);
			}
		}
		br_omim_ja.close();
		//System.out.println("OMIM @ja Count : " + count);
		
		
		// Inheritance
		count = 0;
		
		HashMap<String, String> Inheritance_Map = new HashMap<String, String>();
		
		BufferedReader br_Inheritance = new BufferedReader(new FileReader(args[1]));

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
		
		
		//Orphanet_OMIM_Map, Orphanet_MONDO_Map, GTR
		count = 0;
		HashMap<String, String> Orphanet_All_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_OMIM_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_MONDO_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_UMLS_Map = new HashMap<String, String>();
		
		BufferedReader br_MONDO = new BufferedReader(new FileReader(args[2]));
		
		ArrayList<String> MONDO_IDs = new ArrayList<String>();
		ArrayList<String> OMIM_IDs = new ArrayList<String>();
		ArrayList<String> Orphanet_IDs = new ArrayList<String>();
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
							else if(split[1].split(":")[0].equals("Orphanet"))
							{
								if(line.contains("MONDO:equivalentTo"))
								{
									Orphanet_IDs.add(split[1].split(":")[1]);
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
							MONDO_IDs.clear();
							OMIM_IDs.clear();
							Orphanet_IDs.clear();
							UMLS_IDs.clear();
							break;
						}
						else if(line.equals(""))
						{
							if(OMIM_IDs.size() > 1)
							{
								//System.out.println(OMIM_IDs.get(0));
							}
							
							// Orphanet UMLS Map
							for(String i : Orphanet_IDs)
							{
								if(Orphanet_All_Map.get(i) == null)
								{
									++count;
									Orphanet_All_Map.put(i,"");
								}
								
								if(Orphanet_MONDO_Map.get(i) == null)
								{
									Orphanet_MONDO_Map.put(i, MONDO_IDs.get(0));
									if(OMIM_IDs.size() == 1)
										Orphanet_OMIM_Map.put(i, OMIM_IDs.get(0));
								}
								
								for(String j : UMLS_IDs)
								{
									if(Orphanet_UMLS_Map.get(i) == null)
									{
										Orphanet_UMLS_Map.put(i, j);
									}
									else
									{
										temp = Orphanet_UMLS_Map.get(i);
										temp = temp + "\t" + j;
										Orphanet_UMLS_Map.put(i, temp);
									}
								}
							}
							
							MONDO_IDs.clear();
							OMIM_IDs.clear();
							Orphanet_IDs.clear();
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
		//System.out.println("Orphanet OMIM Count : " + count);
		
		// UR-DBMS_Link = RDB data
		count = 0;
		
		HashMap<String, String> UR_DBMS_Link_Map = new HashMap<String, String>();
		
		BufferedReader br_UR_DBMS_Link = new BufferedReader(new FileReader(args[3]));

		String UR_DBMS_Link = null;

		while((line = br_UR_DBMS_Link.readLine()) != null)
		{
			split = line.split("\t");
			
			if(split[4].equals("UR-DBMS"))
			{
				Orphanet_ID = split[1].replace("\"", "").replace("ORDO:", "");
				UR_DBMS_Link = split[3];
				
				if (UR_DBMS_Link.equals(""))
					continue;
				
				if(UR_DBMS_Link_Map.get(Orphanet_ID) == null)
				{
					
					++count;
					UR_DBMS_Link_Map.put(Orphanet_ID, UR_DBMS_Link);
				}
			}
		}
		
		br_UR_DBMS_Link.close();
		//System.out.println("Orphanet UR_DBMS_Link Count : " + count);
		
		// KEGG
		count = 0;
		
		HashMap<String, String> KEGG_Map = new HashMap<String, String>();
		
		BufferedReader br_KEGG = new BufferedReader(new FileReader(args[4]));

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
		
		BufferedReader br_Gene_Review = new BufferedReader(new FileReader(args[5]));

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
		
		

		BufferedWriter bw_ORDO_HP_CaseReport = new BufferedWriter(new FileWriter("./Orphanet.ttl"));		
				
		bw_ORDO_HP_CaseReport.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX nando: <http://nanbyodata.jp/ontology/nando#>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX med2rdf: <http://med2rdf.org/ontology/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX ordo: <http://www.orpha.net/ORDO/>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_ORDO_HP_CaseReport.newLine();
		bw_ORDO_HP_CaseReport.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_ORDO_HP_CaseReport.newLine();

		
		// Orphanet

		for(String key : Orphanet_All_Map.keySet())
		{	

			String omim = Orphanet_OMIM_Map.get(key);
			
			bw_ORDO_HP_CaseReport.write("ordo:Orphanet_" + key);bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    a med2rdf:Disease, ncit:C7057 ;");bw_ORDO_HP_CaseReport.newLine();
			bw_ORDO_HP_CaseReport.write("    dcterms:identifier \"" + key + "\"");
			
			// JP label output
			if(OMIM_ja_label_Map.get(omim) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				temp = OMIM_ja_label_Map.get(omim);
				bw_ORDO_HP_CaseReport.write("    rdfs:label \"" + temp + "\"@ja");				
			}
		
			// Inheritance
			if(Inheritance_Map.get(omim) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    nando:hasInheritance ");
				temp = Inheritance_Map.get(omim);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_ORDO_HP_CaseReport.write("obo:HP_" + split[i]);
						}
						else
							bw_ORDO_HP_CaseReport.write("obo:HP_" + split[i] + ", ");
					}
				}
				
			}
			
			// Orphanet_MONDO_Map
			if(Orphanet_MONDO_Map.get(key) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				temp = Orphanet_MONDO_Map.get(key);
				bw_ORDO_HP_CaseReport.write("    rdfs:seeAlso obo:MONDO_" + temp);
			}
			
			// UR-DBMS Link
			if(UR_DBMS_Link_Map.get(key) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				temp = UR_DBMS_Link_Map.get(key);
				bw_ORDO_HP_CaseReport.write("    rdfs:seeAlso <" + temp + ">");
			}

			// KEGG output
			if(KEGG_Map.get(omim) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				temp = KEGG_Map.get(omim);
				bw_ORDO_HP_CaseReport.write("    rdfs:seeAlso <https://www.kegg.jp/dbget-bin/www_bget?" + temp + ">");
			}
			
			// Gene Review
			if(Gene_Review_Map.get(omim) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    rdfs:seeAlso ");
				temp = Gene_Review_Map.get(omim);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_ORDO_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/books/"+split[i]+"/>");
						}
						else
							bw_ORDO_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/books/"+split[i]+"/>" + ", ");
					}
				}
				
			}

			// GTR output
			if(Orphanet_UMLS_Map.get(key) != null)
			{
				bw_ORDO_HP_CaseReport.write(" ;");bw_ORDO_HP_CaseReport.newLine();
				bw_ORDO_HP_CaseReport.write("    rdfs:seeAlso ");
				temp = Orphanet_UMLS_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_ORDO_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/> .");bw_ORDO_HP_CaseReport.newLine();
						}
						else
							bw_ORDO_HP_CaseReport.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/>" + ", ");
					}
				}
				
			}
			else
			{
				bw_ORDO_HP_CaseReport.write(" .");bw_ORDO_HP_CaseReport.newLine();
			}
			
			
		}
		
		bw_ORDO_HP_CaseReport.close();

	}
}

