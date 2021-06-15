import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class NCBI_HGNC_Gene_All {
	public static void main(String[] args) throws IOException{

		int count = 0, count1 = 0;
		String HPO_ID = null;
		
		// Gene All
		HashMap<String, String> HGNC_Gene_All_Map = new HashMap<String, String>();
		HashMap<String, String> NCBI_Gene_All_Map = new HashMap<String, String>();
		
		BufferedReader br_Gene_All = new BufferedReader(new FileReader(args[0]));
		
		String line;
		String[] split;
		
		String HGNC_ID = null, NCBI_ID = null, Gene_Symbol = null, temp = null;
		
		br_Gene_All.readLine();

		while((line = br_Gene_All.readLine()) != null)
		{
			try {
				split = line.split("\t");
				
				HGNC_ID = split[0].replace("HGNC:", "");
				Gene_Symbol = split[1];
				
				if(HGNC_Gene_All_Map.get(HGNC_ID) == null)
				{
					++count;
					HGNC_Gene_All_Map.put(HGNC_ID, Gene_Symbol);
				}

				NCBI_ID = split[2];
				
				if(NCBI_Gene_All_Map.get(NCBI_ID) == null)
				{
					++count1;
					NCBI_Gene_All_Map.put(NCBI_ID, HGNC_ID);
				}
				
			}
			catch (Exception e) {
				continue;
			}
		}
		br_Gene_All.close();
		//System.out.println("HGNC_Gene All Count : " + count);
		//System.out.println("NCBI_Gene All Count : " + count1);

		count = 0;
		// NCBI OMIM
		HashMap<String, String> NCBI_OMIM_Map = new HashMap<String, String>();
		
		BufferedReader br_mim2gene = new BufferedReader(new FileReader(args[1]));
		
		String OMIM_ID = null, type = null;
		
		for (int i = 0; i < 5; i++)
			br_mim2gene.readLine();
		
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
					{
						++count;
						NCBI_OMIM_Map.put(NCBI_ID,OMIM_ID);
					}
					else
					{
						++count;
						temp = NCBI_OMIM_Map.get(NCBI_ID);
						temp = temp + "\t" + OMIM_ID;
						NCBI_OMIM_Map.put(NCBI_ID, temp);
						
					}
				}
			}
			catch (Exception e) {
				continue;
			}
		}
		br_mim2gene.close();
		//System.out.println("NCBI OMIM Count : " + count);
		
		
		BufferedWriter bw_GeneAll = new BufferedWriter(new FileWriter("./NCBI_HGNC.ttl"));
		
		bw_GeneAll.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX hgnc: <https://www.genenames.org/data/gene-symbol-report/#!/hgnc_id/>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX ncbigene: <http://identifiers.org/ncbigene/>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX med2rdf: <http://med2rdf.org/ontology/>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX mim: <http://identifiers.org/mim/>");bw_GeneAll.newLine();		
		bw_GeneAll.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_GeneAll.newLine();
		bw_GeneAll.write("PREFIX sio: <http://semanticscience.org/resource/>");bw_GeneAll.newLine();
		

		// NCBI Gene
		count = 0;
		for(String key : NCBI_Gene_All_Map.keySet())
		{	
			++count;
			temp = NCBI_Gene_All_Map.get(key);
			
			bw_GeneAll.write("ncbigene:" + key);bw_GeneAll.newLine();
			bw_GeneAll.write("    a med2rdf:Gene, ncit:C16612 ;");bw_GeneAll.newLine();
			bw_GeneAll.write("    dcterms:identifier \"" + key + "\" ;");bw_GeneAll.newLine();
			bw_GeneAll.write("    sio:SIO_000205 hgnc:HGNC:" + temp);

			// NCBI OMIM output
			if(NCBI_OMIM_Map.get(key) != null)
			{
				temp = NCBI_OMIM_Map.get(key);
				
				bw_GeneAll.write(" ;");bw_GeneAll.newLine();
				bw_GeneAll.write("     rdfs:seeAlso ");
				
				if(temp != null)
				{
					for(int i = 0; i < temp.split("\t").length; i++)
					{
						split = temp.split("\t");
						
						if(temp.split("\t").length - 1 == i)
						{
							bw_GeneAll.write("mim:" + split[i] + " .");
							bw_GeneAll.newLine();
						}
						else
							bw_GeneAll.write("mim:" + split[i] + ", ");
					}
				}
				
				NCBI_OMIM_Map.remove(key);
			}
			else
			{
				bw_GeneAll.write(" .");bw_GeneAll.newLine();
			}
		}
		//System.out.println("NCBI_Gene All Count : " + count1);
		
		count = 0;
		for(String key : NCBI_OMIM_Map.keySet())
		{	
			++count;
			temp = NCBI_OMIM_Map.get(key);
			
			bw_GeneAll.write("ncbigene:" + key);bw_GeneAll.newLine();
			bw_GeneAll.write("    a med2rdf:Gene, ncit:C16612 ;");bw_GeneAll.newLine();
			bw_GeneAll.write("    dcterms:identifier \"" + key + "\" ;");bw_GeneAll.newLine();
			bw_GeneAll.write("    rdfs:seeAlso ");
			if(temp != null)
			{
				for(int i = 0; i < temp.split("\t").length; i++)
				{
					split = temp.split("\t");
					
					if(temp.split("\t").length - 1 == i)
					{
						bw_GeneAll.write("mim:" + split[i] + " .");
						bw_GeneAll.newLine();
					}
					else
						bw_GeneAll.write("mim:" + split[i] + ", ");
				}
			}
		}
		//System.out.println("NCBI OMIM All Count : " + count1);
		
		// HGNC Gene
		count = 0;
		for(String key : HGNC_Gene_All_Map.keySet())
		{	
			++count;
			temp = HGNC_Gene_All_Map.get(key);
			
			bw_GeneAll.write("hgnc:HGNC:" + key);bw_GeneAll.newLine();
			bw_GeneAll.write("    a ncit:C43568 ;");bw_GeneAll.newLine();
			bw_GeneAll.write("    rdfs:label \"" + temp + "\" .");bw_GeneAll.newLine();
			
		}
		//System.out.println("HGNC_Gene All Count : " + count);
		
		bw_GeneAll.close();
	}
}
