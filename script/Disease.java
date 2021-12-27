import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class Disease {
	public static void main(String[] args) throws IOException{		 
		
		String title = null;
		
		// OMIM All
		BufferedReader br_mim2gene = new BufferedReader(new FileReader(args[0]));
		
		HashMap<String, String> OMIM_All_Map = new HashMap<String, String>();
		
		String line;
		String[] split;
		
		String OMIM_ID = null, Orphanet_ID = null, type = null;
		
		for (int i = 0; i < 5; i++)
			br_mim2gene.readLine();

		while((line = br_mim2gene.readLine()) != null)
		{
			split = line.split("\t");
			
			OMIM_ID = split[0];
			type = split[1];
			
			if (type.equals("gene/phenotype") || type.equals("phenotype") || type.equals("predominantly phenotypes"))
				if(OMIM_All_Map.get(OMIM_ID) == null)
					OMIM_All_Map.put(OMIM_ID,"");
		}
		br_mim2gene.close();

		
		// OMIM label JP
		BufferedReader br_UR_DBMS = new BufferedReader(new FileReader(args[1]));
		HashMap<String, String> UR_OMIM_jp_label_Map = new HashMap<String, String>();
		String OMIM_jp = null;

		while((line = br_UR_DBMS.readLine()) != null)
		{
			try {
				split = line.split("\t");
			
				OMIM_ID = split[0].replace("OMIM:", "");
				OMIM_jp = split[1];
			}
			catch (Exception e) {	continue;	}

			if(UR_OMIM_jp_label_Map.get(OMIM_ID) == null)
				UR_OMIM_jp_label_Map.put(OMIM_ID, OMIM_jp);
		}
		br_UR_DBMS.close();

		
		// Inheritance
		BufferedReader br_Inheritance = new BufferedReader(new FileReader(args[2]));
		
		HashMap<String, String> Inheritance_Map = new HashMap<String, String>();
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
					Inheritance_Map.put(OMIM_ID, inheritance);
				else
				{
					flag = false;
					temp = Inheritance_Map.get(OMIM_ID);
					
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						if(split[i].equals(inheritance))
							flag = true;
					}
					if(!flag)
					{
						temp = temp + "\t" + inheritance;
						Inheritance_Map.put(OMIM_ID, temp);
					}
				}
			}
		}
		br_Inheritance.close();
		
		
		// OMIM_MONDO, Orphanet_OMIM_Map, Orphanet_MONDO_Map, GTR
		BufferedReader br_MONDO = new BufferedReader(new FileReader(args[3]));
		
		HashMap<String, String> OMIM_MONDO_Map = new HashMap<String, String>();
		HashMap<String, String> OMIM_UMLS_Map = new HashMap<String, String>();

		HashMap<String, String> Orphanet_All_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_MONDO_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_OMIM_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_UMLS_Map = new HashMap<String, String>();
		
		ArrayList<String> OMIM_IDs = new ArrayList<String>();
		ArrayList<String> MONDO_IDs = new ArrayList<String>();
		ArrayList<String> UMLS_IDs = new ArrayList<String>();
		ArrayList<String> Orphanet_IDs = new ArrayList<String>();
		
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
							MONDO_IDs.add(line.split("MONDO:")[1]);
						else if(title.equals("xref:"))
						{
							if(split[1].split(":")[0].equals("OMIM"))
							{
								if(line.contains("MONDO:equivalentTo"))
									OMIM_IDs.add(split[1].split(":")[1]);
							}
							else if(split[1].split(":")[0].equals("Orphanet"))
							{
								if(line.contains("MONDO:equivalentTo"))
									Orphanet_IDs.add(split[1].split(":")[1]);
							}
							else if(split[1].split(":")[0].equals("UMLS"))
							{
								if(line.contains("MONDO:equivalentTo"))
									UMLS_IDs.add(split[1].split(":")[1]);
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

							for(String i : OMIM_IDs)
							{
								if(OMIM_All_Map.get(i) == null)
									OMIM_All_Map.put(i,"");
								
								// OMIM MONDO Map
								if(OMIM_MONDO_Map.get(i) == null)
									OMIM_MONDO_Map.put(i, MONDO_IDs.get(0));
								else
								{
									temp = OMIM_MONDO_Map.get(i);
									temp = temp + "\t" + MONDO_IDs.get(0);
									OMIM_MONDO_Map.put(i, temp);
								}
								
								// OMIM UMLS Map
								for(String j : UMLS_IDs)
								{
									if(OMIM_UMLS_Map.get(i) == null)
										OMIM_UMLS_Map.put(i, j);
									else
									{
										temp = OMIM_UMLS_Map.get(i);
										temp = temp + "\t" + j;
										OMIM_UMLS_Map.put(i, temp);
									}
								}
							}
							for(String i : Orphanet_IDs)
							{
								if(Orphanet_All_Map.get(i) == null)
								{
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
			catch (Exception e) {	continue;	}
		}
		br_MONDO.close();
		
		
		// Gene_Review
		BufferedReader br_Gene_Review = new BufferedReader(new FileReader(args[4]));
		HashMap<String, String> Gene_Review_Map = new HashMap<String, String>();
		String NBK_ID = null;
		br_Gene_Review.readLine();

		while((line = br_Gene_Review.readLine()) != null)
		{
			split = line.split("\t");
		
			NBK_ID = split[0];
			OMIM_ID = split[2];			
			
			if(Gene_Review_Map.get(OMIM_ID) == null)
				Gene_Review_Map.put(OMIM_ID, NBK_ID);
			else
			{
				temp = Gene_Review_Map.get(OMIM_ID);
				temp = temp + "\t" + NBK_ID;
				Gene_Review_Map.put(OMIM_ID, temp);
			}
		}
		br_Gene_Review.close();
		
		
		// UR-DBMS_Link = RDB data
		BufferedReader br_UR_DBMS_Link_OMIM = new BufferedReader(new FileReader(args[5]));
		HashMap<String, String> OMIM_UR_DBMS_Link_Map = new HashMap<String, String>();
		String UR_DBMS_Link = null;
		
		while((line = br_UR_DBMS_Link_OMIM.readLine()) != null)
		{
			split = line.split("\t");
			
			if(split[4].equals("UR-DBMS"))
			{
				OMIM_ID = split[1].replace("OMIM:", "");
				UR_DBMS_Link = split[3];
				
				if (UR_DBMS_Link.equals(""))
					continue;
				
				if(OMIM_UR_DBMS_Link_Map.get(OMIM_ID) == null)
					OMIM_UR_DBMS_Link_Map.put(OMIM_ID, UR_DBMS_Link);				
			}
		}
		br_UR_DBMS_Link_OMIM.close();
		
		BufferedReader br_UR_DBMS_Link_orphanet = new BufferedReader(new FileReader(args[6]));
		HashMap<String, String> Orphanet_UR_DBMS_Link_Map = new HashMap<String, String>();
		while((line = br_UR_DBMS_Link_orphanet.readLine()) != null)
		{
			split = line.split("\t");
			
			if(split[4].equals("UR-DBMS"))
			{
				Orphanet_ID = split[1].replace("\"", "").replace("ORDO:", "");
				UR_DBMS_Link = split[3];
				
				if (UR_DBMS_Link.equals(""))
					continue;
				
				if(Orphanet_UR_DBMS_Link_Map.get(Orphanet_ID) == null)
					Orphanet_UR_DBMS_Link_Map.put(Orphanet_ID, UR_DBMS_Link);
			}
		}
		br_UR_DBMS_Link_orphanet.close();

		
		// KEGG
		BufferedReader br_KEGG = new BufferedReader(new FileReader(args[7]));
		HashMap<String, String> KEGG_Map = new HashMap<String, String>();
		String KEGG_ID = null;
		
		while((line = br_KEGG.readLine()) != null)
		{
			split = line.split("\t");
		
			OMIM_ID = split[0];
			KEGG_ID = split[1];
			
			if(KEGG_Map.get(OMIM_ID) == null)
				KEGG_Map.put(OMIM_ID, KEGG_ID);
		}
		br_KEGG.close();


		BufferedWriter bw_OMIM = new BufferedWriter(new FileWriter("./OMIM.ttl"));
		
		bw_OMIM.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_OMIM.newLine();
		bw_OMIM.write("PREFIX nando: <http://nanbyodata.jp/ontology/nando#>");bw_OMIM.newLine();
		bw_OMIM.write("PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>");bw_OMIM.newLine();
		bw_OMIM.write("PREFIX med2rdf: <http://med2rdf.org/ontology/>");bw_OMIM.newLine();
		bw_OMIM.write("PREFIX mim: <http://identifiers.org/mim/>");bw_OMIM.newLine();
		bw_OMIM.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_OMIM.newLine();
		bw_OMIM.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_OMIM.newLine();	
		bw_OMIM.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_OMIM.newLine();

		// OMIM
		for(String key : OMIM_All_Map.keySet())
		{	
			bw_OMIM.write("mim:" + key);bw_OMIM.newLine();
			bw_OMIM.write("    a med2rdf:Disease, ncit:C7057 ;");bw_OMIM.newLine();
			bw_OMIM.write("    dcterms:identifier \"" + key + "\"");
			
			// JP label output
			if(UR_OMIM_jp_label_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				temp = UR_OMIM_jp_label_Map.get(key);
				bw_OMIM.write("    rdfs:label \"" + temp + "\"@ja");				
			}
			
			// Inheritance output
			if(Inheritance_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				bw_OMIM.write("    nando:hasInheritance ");
				temp = Inheritance_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_OMIM.write("obo:HP_" + split[i]);
						else
							bw_OMIM.write("obo:HP_" + split[i] + ", ");
					}
				}
			}
			
			// MONDO output
			if(OMIM_MONDO_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				bw_OMIM.write("    rdfs:seeAlso ");
				temp = OMIM_MONDO_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_OMIM.write("obo:MONDO_" + split[i]);
						else
							bw_OMIM.write("obo:MONDO_" + split[i] + ", ");
					}
				}
				
			}
			
			// UR-DBMS Link output
			if(OMIM_UR_DBMS_Link_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				temp = OMIM_UR_DBMS_Link_Map.get(key);
				bw_OMIM.write("    rdfs:seeAlso <" + temp + ">");
			}
			
			// KEGG output
			if(KEGG_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				temp = KEGG_Map.get(key);
				bw_OMIM.write("    rdfs:seeAlso <https://www.kegg.jp/dbget-bin/www_bget?" + temp + ">");
			}
			
			// Gene Review output
			if(Gene_Review_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				bw_OMIM.write("    rdfs:seeAlso ");
				temp = Gene_Review_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_OMIM.write("<https://www.ncbi.nlm.nih.gov/books/" + split[i] + "/>");
						else
							bw_OMIM.write("<https://www.ncbi.nlm.nih.gov/books/" + split[i] + "/>" + ", ");
					}
				}
			}
			
			// GTR output
			if(OMIM_UMLS_Map.get(key) != null)
			{
				bw_OMIM.write(" ;");bw_OMIM.newLine();
				bw_OMIM.write("    rdfs:seeAlso ");
				temp = OMIM_UMLS_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_OMIM.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/> .");
						else
							bw_OMIM.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/>" + ", ");
					}
				}
			}
			else
				bw_OMIM.write(" .");bw_OMIM.newLine();
		}
		bw_OMIM.close();
		
		
		// Orphanet
		BufferedWriter bw_Orphanet = new BufferedWriter(new FileWriter("./Orphanet.ttl"));		
		
		bw_Orphanet.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX nando: <http://nanbyodata.jp/ontology/nando#>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX med2rdf: <http://med2rdf.org/ontology/>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX ordo: <http://www.orpha.net/ORDO/>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_Orphanet.newLine();
		bw_Orphanet.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_Orphanet.newLine();

		
		// Orphanet
		for(String key : Orphanet_All_Map.keySet())
		{	

			String omim = Orphanet_OMIM_Map.get(key);
			
			bw_Orphanet.write("ordo:Orphanet_" + key);bw_Orphanet.newLine();
			bw_Orphanet.write("    a med2rdf:Disease, ncit:C7057 ;");bw_Orphanet.newLine();
			bw_Orphanet.write("    dcterms:identifier \"" + key + "\"");
			
			// JP label output
			if(UR_OMIM_jp_label_Map.get(omim) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				temp = UR_OMIM_jp_label_Map.get(omim);
				bw_Orphanet.write("    rdfs:label \"" + temp + "\"@ja");				
			}
		
			// Inheritance
			if(Inheritance_Map.get(omim) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				bw_Orphanet.write("    nando:hasInheritance ");
				temp = Inheritance_Map.get(omim);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_Orphanet.write("obo:HP_" + split[i]);
						}
						else
							bw_Orphanet.write("obo:HP_" + split[i] + ", ");
					}
				}
				
			}
			
			// Orphanet_MONDO_Map
			if(Orphanet_MONDO_Map.get(key) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				temp = Orphanet_MONDO_Map.get(key);
				bw_Orphanet.write("    rdfs:seeAlso obo:MONDO_" + temp);
			}
			
			// UR-DBMS Link
			if(Orphanet_UR_DBMS_Link_Map.get(key) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				temp = Orphanet_UR_DBMS_Link_Map.get(key);
				bw_Orphanet.write("    rdfs:seeAlso <" + temp + ">");
			}

			// KEGG output
			if(KEGG_Map.get(omim) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				temp = KEGG_Map.get(omim);
				bw_Orphanet.write("    rdfs:seeAlso <https://www.kegg.jp/dbget-bin/www_bget?" + temp + ">");
			}
			
			// Gene Review
			if(Gene_Review_Map.get(omim) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				bw_Orphanet.write("    rdfs:seeAlso ");
				temp = Gene_Review_Map.get(omim);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_Orphanet.write("<https://www.ncbi.nlm.nih.gov/books/"+split[i]+"/>");
						else
							bw_Orphanet.write("<https://www.ncbi.nlm.nih.gov/books/"+split[i]+"/>" + ", ");
					}
				}
				
			}

			// GTR output
			if(Orphanet_UMLS_Map.get(key) != null)
			{
				bw_Orphanet.write(" ;");bw_Orphanet.newLine();
				bw_Orphanet.write("    rdfs:seeAlso ");
				temp = Orphanet_UMLS_Map.get(key);
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_Orphanet.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/> .");
						else
							bw_Orphanet.write("<https://www.ncbi.nlm.nih.gov/gtr/all/tests/?term=" + split[i] + "/>" + ", ");
					}
				}
			}
			else
				bw_Orphanet.write(" .");bw_Orphanet.newLine();
		}
		bw_Orphanet.close();
	}
}
