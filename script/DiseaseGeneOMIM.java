import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DiseaseGeneOMIM {
	public static void main(String[] args) throws IOException{

		int count = 0;
		String line;
		String[] split;
		String OMIM_ID = null, NCBI_ID = null, type = null, temp = null;

		// OMIM NCBI Gene
		HashMap<String, String> OMIM_NCBIGene_Map = new HashMap<String, String>();
		
		BufferedReader br_mim2gene_medgen = new BufferedReader(new FileReader(args[0]));
		
		br_mim2gene_medgen.readLine();
		
		while((line = br_mim2gene_medgen.readLine()) != null)
		{
			try {
				split = line.split("\t");
				
				OMIM_ID = split[0];
				NCBI_ID = split[1];
				type = split[2];
				
				if (type.equals("phenotype") && !NCBI_ID.equals("-"))
				{
					if(OMIM_NCBIGene_Map.get(OMIM_ID + "\t" + NCBI_ID) == null)
					{
						++count;
						OMIM_NCBIGene_Map.put(OMIM_ID + "\t" + NCBI_ID, "MedGen");
					}
				}
			}
			catch (Exception e) {
				continue;
			}
		}
		br_mim2gene_medgen.close();
		//System.out.println("OMIM_NCBIGene All Count : " + count);

		count = 0;
	    BufferedReader br_GenCC_ncbigene_omim = new BufferedReader(new FileReader(args[1]));
	    
	    while((line = br_GenCC_ncbigene_omim.readLine()) != null)
		{
	    	split = line.split("\t");
			
	    	NCBI_ID = split[0];
	    	OMIM_ID = split[1];
			if(OMIM_NCBIGene_Map.get(OMIM_ID + "\t" + NCBI_ID) == null)
			{
				++count;
				OMIM_NCBIGene_Map.put(OMIM_ID + "\t" + NCBI_ID, "GenCC");
			}
			else
			{
				temp = OMIM_NCBIGene_Map.get(OMIM_ID + "\t" + NCBI_ID);
				temp = temp + "\tGenCC";
				OMIM_NCBIGene_Map.put(OMIM_ID + "\t" + NCBI_ID, temp);
			}
		}
	    br_GenCC_ncbigene_omim.close();
	    //System.out.println("GenCC_ncbigene_omim Count : " + count);
		
		BufferedWriter bw_DiseaseGeneOMIM = new BufferedWriter(new FileWriter("./OMIM_Gene_Association.ttl"));

		bw_DiseaseGeneOMIM.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_DiseaseGeneOMIM.newLine();
		bw_DiseaseGeneOMIM.write("PREFIX ncbigene: <http://identifiers.org/ncbigene/>");bw_DiseaseGeneOMIM.newLine();
		bw_DiseaseGeneOMIM.write("PREFIX mim: <http://identifiers.org/mim/>");bw_DiseaseGeneOMIM.newLine();		
		bw_DiseaseGeneOMIM.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_DiseaseGeneOMIM.newLine();
		bw_DiseaseGeneOMIM.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_DiseaseGeneOMIM.newLine();
		bw_DiseaseGeneOMIM.write("PREFIX sio: <http://semanticscience.org/resource/>");bw_DiseaseGeneOMIM.newLine();
		
		for(String key : OMIM_NCBIGene_Map.keySet())
		{
			OMIM_ID = key.split("\t")[0];
			NCBI_ID = key.split("\t")[1];

			temp = OMIM_NCBIGene_Map.get(key);
			
			bw_DiseaseGeneOMIM.write("<https://pubcasefinder.dbcls.jp/gene_context/disease:OMIM:" + OMIM_ID + "/gene:ENT:" + NCBI_ID + ">");bw_DiseaseGeneOMIM.newLine();
			bw_DiseaseGeneOMIM.write("    a sio:SIO_000983 ;");bw_DiseaseGeneOMIM.newLine();
			bw_DiseaseGeneOMIM.write("    sio:SIO_000628 mim:" + OMIM_ID + ", ncbigene:" + NCBI_ID + " ;");bw_DiseaseGeneOMIM.newLine();
			bw_DiseaseGeneOMIM.write("    dcterms:source ");
			for(int i = 0; i < temp.split("\t").length; i++)
			{
				split = temp.split("\t");

				if(temp.split("\t").length - 1 == i)
				{
					if(split[i].equals("MedGen"))
						bw_DiseaseGeneOMIM.write("<ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen> .");
					else
						bw_DiseaseGeneOMIM.write("<https://search.thegencc.org/download/action/submissions-export-csv> .");
					bw_DiseaseGeneOMIM.newLine();
				}
				else
				{
					if(split[i].equals("MedGen"))
						bw_DiseaseGeneOMIM.write("<ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen>, ");
					else
						bw_DiseaseGeneOMIM.write("<https://search.thegencc.org/download/action/submissions-export-csv>, ");
				}
			}
		}

		bw_DiseaseGeneOMIM.close();
	}
}
