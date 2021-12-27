import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DiseaseGeneAssociation {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException{

		String line;
		String[] split;
		String OMIM_ID = null, NCBI_ID = null, type = null, temp = null, title = null;
		
		// HGNC NCBI gene
	    BufferedReader br_Gene_All = new BufferedReader(new FileReader(args[0]));
	    
	    HashMap<String, String> HGNC_ID_NCBI_ID_All_Map = new HashMap<String, String>();
 		HashMap<String, String> Gene_Symbol_NCBI_ID_All_Map = new HashMap<String, String>();

		String HGNC_ID = null, Gene_Symbol = null;
		
 		br_Gene_All.readLine();
 		while((line = br_Gene_All.readLine()) != null)
 		{
 			try {
 				split = line.split("\t"); 				
 				HGNC_ID = split[0].replace("HGNC:", "");
 				Gene_Symbol = split[1];
 				NCBI_ID = split[2];
 				
 				if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) == null)
 					HGNC_ID_NCBI_ID_All_Map.put(HGNC_ID, NCBI_ID);
 				
 				if(Gene_Symbol_NCBI_ID_All_Map.get(Gene_Symbol) == null)
 					Gene_Symbol_NCBI_ID_All_Map.put(Gene_Symbol, NCBI_ID);				
 			}
 			catch (Exception e) {	continue;	}
 		}
 		br_Gene_All.close();


 		// mondo
	    BufferedReader br_MONDO = new BufferedReader(new FileReader(args[1]));
	    
	    HashMap<String, String> MONDO_OMIM_Map = new HashMap<String, String>();
	    HashMap<String, String> MONDO_Orphanet_Map = new HashMap<String, String>();
		
		ArrayList<String> MONDO_IDs = new ArrayList<String>();
		ArrayList<String> OMIM_IDs = new ArrayList<String>();
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
						}
						
						if(line.equals("is_obsolete: true"))
						{
							MONDO_IDs.clear();
							OMIM_IDs.clear();
							Orphanet_IDs.clear();
							break;
						}
						else if(line.equals(""))
						{
							if(OMIM_IDs.size() >=1 && MONDO_IDs.size() == 1)
							{
								temp = OMIM_IDs.get(0);
								
								for(int i = 1; i < OMIM_IDs.size(); i++)
								{
									temp = temp + "\t" + OMIM_IDs.get(i);
								}
								MONDO_OMIM_Map.put(MONDO_IDs.get(0), temp);
							}
							
							if(Orphanet_IDs.size() >=1 && MONDO_IDs.size() == 1)
							{
								temp = Orphanet_IDs.get(0);
								
								for(int i = 1; i < Orphanet_IDs.size(); i++)
								{
									temp = temp + "\t" + Orphanet_IDs.get(i);
								}
								MONDO_Orphanet_Map.put(MONDO_IDs.get(0), temp);
							}
							MONDO_IDs.clear();
							OMIM_IDs.clear();
							Orphanet_IDs.clear();
							break;
						}
					}
				}
			}
			catch (Exception e) {	continue;	}
		}
		br_MONDO.close();
	    
		
		// OMIM NCBI Gene Association
		BufferedReader br_mim2gene_medgen = new BufferedReader(new FileReader(args[2]));
		
		HashMap<String, String> OMIM_NCBIGene_Map = new HashMap<String, String>();
		br_mim2gene_medgen.readLine();
		while((line = br_mim2gene_medgen.readLine()) != null)
		{
			try {
				split = line.split("\t");
				
				OMIM_ID = split[0];
				NCBI_ID = split[1];
				type = split[2];
				
				if (type.equals("phenotype") && !NCBI_ID.equals("-"))
					if(OMIM_NCBIGene_Map.get(OMIM_ID + "\t" + NCBI_ID) == null)
						OMIM_NCBIGene_Map.put(OMIM_ID + "\t" + NCBI_ID, "MedGen");

			}
			catch (Exception e) {	continue;	}
		}
		br_mim2gene_medgen.close();
		

		// Orphanet HGNC Gene Symbol Association
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(args[3]);
		
		HashMap<String, String> Orphanet_NCBIGene_Map = new HashMap<String, String>();
		
		Element root = document.getDocumentElement();
		NodeList DisorderList = root.getElementsByTagName("DisorderList");		
	    Element DisorderList_el = null;
    	NodeList Disorder = null;
    	Element Disorder_el = null;
    	NodeList OrphaNumber = null;
    	Element OrphaNumber_el = null;
    	NodeList Symbol = null;
    	Element Symbol_el = null;
       	      
	    for(int i = 0; i < DisorderList.getLength(); i++)
	    {
	    	DisorderList_el = (Element) DisorderList.item(i);
	    	Disorder = DisorderList_el.getElementsByTagName("Disorder");
	      
	    	for(int j = 0; j < Disorder.getLength(); j++)
	    	{
	    		Disorder_el = (Element) Disorder.item(j);
	    		OrphaNumber = Disorder_el.getElementsByTagName("OrphaCode");
	    		OrphaNumber_el = (Element) OrphaNumber.item(0);
	    		Symbol = Disorder_el.getElementsByTagName("Symbol");
	    		
	    		for(int k = 0; k < Symbol.getLength(); k++)
	    		{
	    			Symbol_el = (Element) Symbol.item(k);

	    			if(Gene_Symbol_NCBI_ID_All_Map.get(Symbol_el.getTextContent()) != null)
	    				if(Orphanet_NCBIGene_Map.get(OrphaNumber_el.getTextContent() + "\t" + Gene_Symbol_NCBI_ID_All_Map.get(Symbol_el.getTextContent())) == null)
							Orphanet_NCBIGene_Map.put(OrphaNumber_el.getTextContent() + "\t" + Gene_Symbol_NCBI_ID_All_Map.get(Symbol_el.getTextContent()), "Orphanet");
	    		}
	    	}
	    }

	    
	    // GeneCC
		BufferedReader br_GenCC = new BufferedReader(new FileReader(args[4]));
		
		String classification_curie = null, Disease = null, Orphanet_ID = null;

		br_GenCC.readLine();
		while((line = br_GenCC.readLine()) != null)
		{
			try {
				split = line.split("\",\"");
				HGNC_ID = split[1].replace("HGNC:", "").replace("\"", "");
				Disease = split[5].replace("\"", "");
				classification_curie = split[7];
				
				if(classification_curie.equals("GENCC:100001"))
				{
					if(Disease.split(":")[0].equals("OMIM"))
					{
						if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) != null)
						{
							NCBI_ID = HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID);
							if(OMIM_NCBIGene_Map.get(Disease.split(":")[1] + "\t" + NCBI_ID) == null)
							{
								OMIM_NCBIGene_Map.put(Disease.split(":")[1] + "\t" + NCBI_ID, "GenCC");
							}
							else
							{
								temp = OMIM_NCBIGene_Map.get(Disease.split(":")[1] + "\t" + NCBI_ID);
								if(!temp.contains("GenCC"))
								{
									temp = temp + "\tGenCC";
									OMIM_NCBIGene_Map.put(Disease.split(":")[1] + "\t" + NCBI_ID, temp);
								}
							}
						}
					}
					else
					{
						if(MONDO_OMIM_Map.get(Disease.split(":")[1]) != null)
						{
							OMIM_ID = MONDO_OMIM_Map.get(Disease.split(":")[1]);
							
							if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) != null)
							{
								NCBI_ID = HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID);
								
								if(OMIM_ID.split("\t").length >= 1)
								{
									for(int i = 0; i < OMIM_ID.split("\t").length; i++)
									{
										if(OMIM_NCBIGene_Map.get(OMIM_ID.split("\t")[i] + "\t" + NCBI_ID) == null)
										{
											OMIM_NCBIGene_Map.put(OMIM_ID.split("\t")[i] + "\t" + NCBI_ID, "GenCC");
										}
										else
										{
											temp = OMIM_NCBIGene_Map.get(OMIM_ID.split("\t")[i] + "\t" + NCBI_ID);
											if(!temp.contains("GenCC"))
											{
												temp = temp + "\tGenCC";
												OMIM_NCBIGene_Map.put(OMIM_ID.split("\t")[i] + "\t" + NCBI_ID, temp);
											}
										}
									}
								}
							}
						}
						if(MONDO_Orphanet_Map.get(Disease.split(":")[1]) != null)
						{
							Orphanet_ID = MONDO_Orphanet_Map.get(Disease.split(":")[1]);
							
							if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) != null)
							{
								NCBI_ID = HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID);
								
								if(Orphanet_ID.split("\t").length >= 1)
								{
									for(int i = 0; i < Orphanet_ID.split("\t").length; i++)
									{
										if(Orphanet_NCBIGene_Map.get(Orphanet_ID.split("\t")[i] + "\t" + NCBI_ID) == null)
										{
											Orphanet_NCBIGene_Map.put(Orphanet_ID.split("\t")[i] + "\t" + NCBI_ID, "GenCC");
										}
										else
										{
											temp = Orphanet_NCBIGene_Map.get(Orphanet_ID.split("\t")[i] + "\t" + NCBI_ID);
											if(!temp.contains("GenCC"))
											{
												temp = temp + "\tGenCC";
												Orphanet_NCBIGene_Map.put(Orphanet_ID.split("\t")[i] + "\t" + NCBI_ID, temp);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {}
		}
		br_GenCC.close();
		
		
		// output : OMIM_Gene_Association
		BufferedWriter bw_OMIM_Gene_Association = new BufferedWriter(new FileWriter("./OMIM_Gene_Association.ttl"));
		
		bw_OMIM_Gene_Association.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_OMIM_Gene_Association.newLine();
		bw_OMIM_Gene_Association.write("PREFIX ncbigene: <http://identifiers.org/ncbigene/>");bw_OMIM_Gene_Association.newLine();
		bw_OMIM_Gene_Association.write("PREFIX mim: <http://identifiers.org/mim/>");bw_OMIM_Gene_Association.newLine();		
		bw_OMIM_Gene_Association.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_OMIM_Gene_Association.newLine();
		bw_OMIM_Gene_Association.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_OMIM_Gene_Association.newLine();
		bw_OMIM_Gene_Association.write("PREFIX sio: <http://semanticscience.org/resource/>");bw_OMIM_Gene_Association.newLine();
		
		for(String key : OMIM_NCBIGene_Map.keySet())
		{
			OMIM_ID = key.split("\t")[0];
			NCBI_ID = key.split("\t")[1];

			temp = OMIM_NCBIGene_Map.get(key);
			
			bw_OMIM_Gene_Association.write("<https://pubcasefinder.dbcls.jp/gene_context/disease:OMIM:" + OMIM_ID + "/gene:ENT:" + NCBI_ID + ">");bw_OMIM_Gene_Association.newLine();
			bw_OMIM_Gene_Association.write("    a sio:SIO_000983 ;");bw_OMIM_Gene_Association.newLine();
			bw_OMIM_Gene_Association.write("    sio:SIO_000628 mim:" + OMIM_ID + ", ncbigene:" + NCBI_ID + " ;");bw_OMIM_Gene_Association.newLine();
			bw_OMIM_Gene_Association.write("    dcterms:source ");
			for(int i = 0; i < temp.split("\t").length; i++)
			{
				split = temp.split("\t");

				if(temp.split("\t").length - 1 == i)
				{
					if(split[i].equals("MedGen"))
						bw_OMIM_Gene_Association.write("<ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen> .");
					else
						bw_OMIM_Gene_Association.write("<https://search.thegencc.org/download/action/submissions-export-csv> .");
					bw_OMIM_Gene_Association.newLine();
				}
				else
				{
					if(split[i].equals("MedGen"))
						bw_OMIM_Gene_Association.write("<ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen>, ");
					else
						bw_OMIM_Gene_Association.write("<https://search.thegencc.org/download/action/submissions-export-csv>, ");
				}
			}			
		}
		bw_OMIM_Gene_Association.close();

		
		// output : Orphanet_Gene_Association
		BufferedWriter bw_Orphanet_Gene_Association = new BufferedWriter(new FileWriter("./Orphanet_Gene_Association.ttl"));
		
		bw_Orphanet_Gene_Association.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_Orphanet_Gene_Association.newLine();
		bw_Orphanet_Gene_Association.write("PREFIX ncbigene: <http://identifiers.org/ncbigene/>");bw_Orphanet_Gene_Association.newLine();
		bw_Orphanet_Gene_Association.write("PREFIX ordo: <http://www.orpha.net/ORDO/>");bw_Orphanet_Gene_Association.newLine();
		bw_Orphanet_Gene_Association.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_Orphanet_Gene_Association.newLine();
		bw_Orphanet_Gene_Association.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_Orphanet_Gene_Association.newLine();
		bw_Orphanet_Gene_Association.write("PREFIX sio: <http://semanticscience.org/resource/>");bw_Orphanet_Gene_Association.newLine();
		
		for(String key : Orphanet_NCBIGene_Map.keySet())
		{
			Orphanet_ID = key.split("\t")[0];
			NCBI_ID = key.split("\t")[1];
			
			temp = Orphanet_NCBIGene_Map.get(key);
			
			bw_Orphanet_Gene_Association.write("<https://pubcasefinder.dbcls.jp/gene_context/disease:ORDO:" + Orphanet_ID + "/gene:ENT:" + NCBI_ID + ">");bw_Orphanet_Gene_Association.newLine();
			bw_Orphanet_Gene_Association.write("    a sio:SIO_000983 ;");bw_Orphanet_Gene_Association.newLine();
			bw_Orphanet_Gene_Association.write("    sio:SIO_000628 ordo:Orphanet_" + Orphanet_ID + ", ncbigene:" + NCBI_ID + " ;");bw_Orphanet_Gene_Association.newLine();
			bw_Orphanet_Gene_Association.write("    dcterms:source ");
			for(int i = 0; i < temp.split("\t").length; i++)
			{
				split = temp.split("\t");

				if(temp.split("\t").length - 1 == i)
				{
					if(split[i].equals("Orphanet"))
						bw_Orphanet_Gene_Association.write("<http://www.orphadata.org/data/xml/en_product6.xml> .");
					else
						bw_Orphanet_Gene_Association.write("<https://search.thegencc.org/download/action/submissions-export-csv> .");
					bw_Orphanet_Gene_Association.newLine();
				}
				else
				{
					if(split[i].equals("Orphanet"))
						bw_Orphanet_Gene_Association.write("<http://www.orphadata.org/data/xml/en_product6.xml>, ");
					else
						bw_Orphanet_Gene_Association.write("<https://search.thegencc.org/download/action/submissions-export-csv>, ");
				}
			}	
		}
		bw_Orphanet_Gene_Association.close();
	}
}
