import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class NCBI_HGNC {
	public static void main(String[] args) throws IOException{

		String line;
		String[] split;
		String HGNC_ID = null, NCBI_ID = null, Gene_Symbol = null, temp = null;
		
		// Gene All
		BufferedReader br_Gene_All = new BufferedReader(new FileReader(args[0]));

		HashMap<String, String> HGNC_Gene_All_Map = new HashMap<String, String>();
		HashMap<String, String> NCBI_Gene_All_Map = new HashMap<String, String>();
		
		br_Gene_All.readLine();
		while((line = br_Gene_All.readLine()) != null)
		{
			try {
				split = line.split("\t");
				
				HGNC_ID = split[0].replace("HGNC:", "");
				Gene_Symbol = split[1];
				NCBI_ID = split[2];
				
				if(HGNC_Gene_All_Map.get(HGNC_ID) == null)
					HGNC_Gene_All_Map.put(HGNC_ID, Gene_Symbol);

				if(NCBI_Gene_All_Map.get(NCBI_ID) == null)
					NCBI_Gene_All_Map.put(NCBI_ID, HGNC_ID);
			}
			catch (Exception e) {	continue;	}
		}
		br_Gene_All.close();
		
		// NCBI OMIM
		BufferedReader br_NCBI_gene = new BufferedReader(new FileReader(args[1]));
		
		HashMap<String, String> NCBI_Synonyms_Map = new HashMap<String, String>();
		HashMap<String, String> NCBI_map_location_Map = new HashMap<String, String>();
		HashMap<String, String> NCBI_type_of_gene_Map = new HashMap<String, String>();
		HashMap<String, String> NCBI_Full_name_Map = new HashMap<String, String>();
		HashMap<String, String> NCBI_Other_designations_Map = new HashMap<String, String>();
		
		String Synonyms = null, map_location = null, type_of_gene = null, Full_name = null, Other_designations = null;
		
		while((line = br_NCBI_gene.readLine()) != null)
		{
			try {
				split = line.split("\t");
				NCBI_ID = split[1];
				Synonyms = split[4];
				map_location = split[7];
				type_of_gene = split[9];
				Full_name = split[11];
				Other_designations = split[13];
				
				if(NCBI_Synonyms_Map.get(NCBI_ID) == null && !Synonyms.equals("-"))
				{
					NCBI_Synonyms_Map.put(NCBI_ID, Synonyms);
				}
				
				if(NCBI_map_location_Map.get(NCBI_ID) == null && !map_location.equals("-"))
				{
					NCBI_map_location_Map.put(NCBI_ID, map_location);
				}
				
				if(NCBI_type_of_gene_Map.get(NCBI_ID) == null && !type_of_gene.equals("-"))
				{
					NCBI_type_of_gene_Map.put(NCBI_ID, type_of_gene);
				}
				
				if(NCBI_Full_name_Map.get(NCBI_ID) == null && !Full_name.equals("-"))
				{
					NCBI_Full_name_Map.put(NCBI_ID, Full_name);
				}
				
				if(NCBI_Other_designations_Map.get(NCBI_ID) == null && !Other_designations.equals("-"))
				{
					NCBI_Other_designations_Map.put(NCBI_ID, Other_designations);
				}
			}
			catch (Exception e) {	continue;	}
		}
		br_NCBI_gene.close();
		
		// NCBI OMIM
		BufferedReader br_mim2gene = new BufferedReader(new FileReader(args[2]));
		
		HashMap<String, String> NCBI_OMIM_Map = new HashMap<String, String>();
		
		String OMIM_ID = null, type = null;
		
		for (int i = 0; i < 5; i++)
			br_mim2gene.readLine();
		// 100050	predominantly phenotypes			
		while((line = br_mim2gene.readLine()) != null)
		{
			try {
				split = line.split("\t");
				
				OMIM_ID = split[0];
				type = split[1];
				NCBI_ID = split[2];
				
				if (type.equals("gene/phenotype") || type.equals("gene") && NCBI_ID != null)
				{
					if(NCBI_OMIM_Map.get(NCBI_ID) == null)
						NCBI_OMIM_Map.put(NCBI_ID,OMIM_ID);
					else
					{
						temp = NCBI_OMIM_Map.get(NCBI_ID);
						temp = temp + "\t" + OMIM_ID;
						NCBI_OMIM_Map.put(NCBI_ID, temp);
					}
				}
			}
			catch (Exception e) {	continue;	}
		}
		br_mim2gene.close();
		
		
		BufferedReader br_NCBI_gene_summary = new BufferedReader(new FileReader(args[3]));
		
		HashMap<String, String> NCBI_gene_summary_Map = new HashMap<String, String>();

		String summary = null;
		
		while((line = br_NCBI_gene_summary.readLine()) != null)
		{
			try {
				split = line.split("\t");
				NCBI_ID = split[1];
				summary = split[2];
				
				if(NCBI_gene_summary_Map.get(NCBI_ID) == null)
				{
					NCBI_gene_summary_Map.put(NCBI_ID,summary);
				}
				
			}
			catch (Exception e) {	continue;	}
		}
		br_NCBI_gene_summary.close();

		
		BufferedWriter bw_NCBI_HGNC = new BufferedWriter(new FileWriter("./NCBI_HGNC.ttl"));
		
		bw_NCBI_HGNC.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX hgnc: <https://www.genenames.org/data/gene-symbol-report/#!/hgnc_id/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX ncbigene: <http://identifiers.org/ncbigene/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX med2rdf: <http://med2rdf.org/ontology/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX mim: <http://identifiers.org/mim/>");bw_NCBI_HGNC.newLine();		
		bw_NCBI_HGNC.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX sio: <http://semanticscience.org/resource/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX nuc: <http://ddbj.nig.ac.jp/ontologies/nucleotide/>");bw_NCBI_HGNC.newLine();
		bw_NCBI_HGNC.write("PREFIX hop: <http://purl.org/net/orthordf/hOP/ontology#>");bw_NCBI_HGNC.newLine();
		

		// NCBI Gene에 대한 정보 출력
		for(String key : NCBI_Gene_All_Map.keySet())
		{	
			temp = NCBI_Gene_All_Map.get(key);
			
			bw_NCBI_HGNC.write("ncbigene:" + key);bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    a med2rdf:Gene, ncit:C16612 ;");bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    dcterms:identifier \"" + key + "\" ;");bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    sio:SIO_000205 hgnc:HGNC:" + temp);
			
			if(NCBI_Synonyms_Map.get(key) != null)
			{
				temp = NCBI_Synonyms_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    nuc:gene_synonym ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}

			if(NCBI_map_location_Map.get(key) != null)
			{
				temp = NCBI_map_location_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    nuc:map ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_type_of_gene_Map.get(key) != null)
			{
				temp = NCBI_type_of_gene_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    hop:typeOfGene ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_Full_name_Map.get(key) != null)
			{
				temp = NCBI_Full_name_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    dcterms:description ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_Other_designations_Map.get(key) != null)
			{
				temp = NCBI_Other_designations_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    dcterms:alternative ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_gene_summary_Map.get(key) != null)
			{
				temp = NCBI_gene_summary_Map.get(key);

				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    obo:NCIT_C42581 \"" + temp + "\"");
			}
			
			// NCBI OMIM output
			if(NCBI_OMIM_Map.get(key) != null)
			{
				temp = NCBI_OMIM_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    rdfs:seeAlso ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_NCBI_HGNC.write("mim:" + split[i] + " .");
						else
							bw_NCBI_HGNC.write("mim:" + split[i] + ", ");
					}
				}
				NCBI_OMIM_Map.remove(key);
			}
			else
				bw_NCBI_HGNC.write(" .");bw_NCBI_HGNC.newLine();
		}
		
		
		// NCBI OMIM output
		for(String key : NCBI_OMIM_Map.keySet())
		{	
			bw_NCBI_HGNC.write("ncbigene:" + key);bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    a med2rdf:Gene, ncit:C16612 ;");bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    dcterms:identifier \"" + key + "\"");
			
			if(NCBI_Synonyms_Map.get(key) != null)
			{
				temp = NCBI_Synonyms_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    nuc:gene_synonym ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}

			if(NCBI_map_location_Map.get(key) != null)
			{
				temp = NCBI_map_location_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    nuc:map ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_type_of_gene_Map.get(key) != null)
			{
				temp = NCBI_type_of_gene_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    hop:typeOfGene ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_Full_name_Map.get(key) != null)
			{
				temp = NCBI_Full_name_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    dcterms:description ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_Other_designations_Map.get(key) != null)
			{
				temp = NCBI_Other_designations_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    dcterms:alternative ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\\|").length; i++)
					{						
						split = temp.split("\\|");
						
						if(temp.split("\\|").length - 1 == i)
							bw_NCBI_HGNC.write("\"" + split[i] + "\"");
						else
							bw_NCBI_HGNC.write("\"" + split[i] + "\", ");
					}
				}
			}
			
			if(NCBI_gene_summary_Map.get(key) != null)
			{
				temp = NCBI_gene_summary_Map.get(key);

				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    obo:NCIT_C42581 \"" + temp + "\"");
			}
			
			if(NCBI_OMIM_Map.get(key) != null)
			{
				temp = NCBI_OMIM_Map.get(key);
				
				bw_NCBI_HGNC.write(" ;");bw_NCBI_HGNC.newLine();
				bw_NCBI_HGNC.write("    rdfs:seeAlso ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
							bw_NCBI_HGNC.write("mim:" + split[i] + " .");
						else
							bw_NCBI_HGNC.write("mim:" + split[i] + ", ");
					}
				}
			}
			else
				bw_NCBI_HGNC.write(" .");bw_NCBI_HGNC.newLine();
		}

		
		// HGNC gene
		for(String key : HGNC_Gene_All_Map.keySet())
		{	
			temp = HGNC_Gene_All_Map.get(key);
			
			bw_NCBI_HGNC.write("hgnc:HGNC:" + key);bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    a ncit:C43568 ;");bw_NCBI_HGNC.newLine();
			bw_NCBI_HGNC.write("    rdfs:label \"" + temp + "\" .");bw_NCBI_HGNC.newLine();
		}
		
		bw_NCBI_HGNC.close();
	}
}
