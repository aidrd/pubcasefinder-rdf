import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneCC {
	public static void main(String[] args) throws IOException{
		
		int count = 0, count1 = 0;
		String line;
		String[] split;
		String Orphanet_ID = null, OMIM_ID = null, title = null;
		String HGNC_ID = null, NCBI_ID = null, Gene_Symbol = null, temp = null, Disease = null;
		
		// Gene All
		HashMap<String, String> HGNC_ID_NCBI_ID_All_Map = new HashMap<String, String>();
		
		BufferedReader br_Gene_All = new BufferedReader(new FileReader(args[0]));
		
		br_Gene_All.readLine();
		// HGNC:5	A1BG	1
		while((line = br_Gene_All.readLine()) != null)
		{
			try {
				split = line.split("\t");
				
				HGNC_ID = split[0].replace("HGNC:", "");
				Gene_Symbol = split[1];
				NCBI_ID = split[2];
				if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) == null)
				{
					++count;
					HGNC_ID_NCBI_ID_All_Map.put(HGNC_ID, NCBI_ID);
				}
			}
			catch (Exception e) {
				continue;
			}
		}
		br_Gene_All.close();
		//System.out.println("HGNC ID NCBI ID All Count : " + count);
		
		
		//Orphanet_OMIM_Map, Orphanet_MONDO_Map, GTR
		count = 0;
		HashMap<String, String> MONDO_Orphanet_Map = new HashMap<String, String>();
		HashMap<String, String> MONDO_OMIM_Map = new HashMap<String, String>();
		HashMap<String, String> MONDO_is_obsolete_Map = new HashMap<String, String>();
		
		BufferedReader br_MONDO = new BufferedReader(new FileReader(args[1]));
		
		ArrayList<String> MONDO_IDs = new ArrayList<String>();
		ArrayList<String> Orphanet_IDs = new ArrayList<String>();
		ArrayList<String> OMIM_IDs = new ArrayList<String>();
		
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
							MONDO_is_obsolete_Map.put(MONDO_IDs.get(0), "");
							MONDO_IDs.clear();
							OMIM_IDs.clear();
							Orphanet_IDs.clear();
							break;
						}
						else if(line.equals(""))
						{
							if(OMIM_IDs.size() >=1 && MONDO_IDs.size() == 1)
							{
								++count;
								temp = OMIM_IDs.get(0);
								
								for(int i = 1; i < OMIM_IDs.size(); i++)
								{
									temp = temp + "\t" + OMIM_IDs.get(i);
								}
								MONDO_OMIM_Map.put(MONDO_IDs.get(0), temp);
							}
							
							if(Orphanet_IDs.size() >=1 && MONDO_IDs.size() == 1)
							{
								++count1;
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
			catch (Exception e) {
				continue;
			}
		}
		br_MONDO.close();
		//System.out.println("MONDO_OMIM_Map Count : " + count);
		//System.out.println("MONDO_Orphanet_Map Count : " + count1);
		
		HashMap<String, String> GenCC_ncbigene_omim_Map = new HashMap<String, String>();
		HashMap<String, String> GenCC_ncbigene_orpha_Map = new HashMap<String, String>();
		HashMap<String, String> GenCC_ncbigene_mondo_Map = new HashMap<String, String>();
		
		BufferedReader br_GenCC = new BufferedReader(new FileReader(args[2]));
		
		br_GenCC.readLine();
		//uuid	gene_curie	gene_symbol	disease_curie	disease_title	disease_original_curie	disease_original_title	classification_curie	classification_title	moi_curie	moi_title	submitter_curie	submitter_title	submitted_as_hgnc_id	submitted_as_hgnc_symbol	submitted_as_disease_id	submitted_as_disease_name	submitted_as_moi_id	submitted_as_moi_name	submitted_as_submitter_id	submitted_as_submitter_name	submitted_as_classification_id	submitted_as_classification_name	submitted_as_date	submitted_as_public_report_url	submitted_as_notes	submitted_as_pmids	submitted_as_assertion_criteria_url	submitted_as_submission_id	submitted_run_date
		//GENCC_000101-HGNC_10896-OMIM_182212-HP_0000006-GENCC_100001	HGNC:10896	SKI	MONDO:0008426	Shprintzen-Goldberg syndrome	OMIM:182212

		while((line = br_GenCC.readLine()) != null)
		{
			try {
				split = line.split(",");
				
				HGNC_ID = split[1].replace("HGNC:", "").replace("\"", "");
				Disease = split[5].replace("\"", "");
				
				if(Disease.split(":")[0].equals("OMIM"))
				{
					if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) != null)
					{
						NCBI_ID = HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID);
						if(GenCC_ncbigene_omim_Map.get(NCBI_ID + "\t" + Disease.split(":")[1]) == null)
							GenCC_ncbigene_omim_Map.put(NCBI_ID + "\t" + Disease.split(":")[1], "");
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
									if(GenCC_ncbigene_omim_Map.get(NCBI_ID + "\t" + OMIM_ID.split("\t")[i]) == null)
										GenCC_ncbigene_omim_Map.put(NCBI_ID + "\t" + OMIM_ID.split("\t")[i], "");
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
									if(GenCC_ncbigene_orpha_Map.get(NCBI_ID + "\t" + Orphanet_ID.split("\t")[i]) == null)
										GenCC_ncbigene_orpha_Map.put(NCBI_ID + "\t" + Orphanet_ID.split("\t")[i], "");
								}
							}
						}
					}
					if(MONDO_OMIM_Map.get(Disease.split(":")[1]) == null && MONDO_Orphanet_Map.get(Disease.split(":")[1]) == null)
					{
						if(MONDO_is_obsolete_Map.get(Disease.split(":")[1]) == null)
						{
							if(HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID) != null)
							{
								NCBI_ID = HGNC_ID_NCBI_ID_All_Map.get(HGNC_ID);
								
								if(GenCC_ncbigene_mondo_Map.get(NCBI_ID + "\t" + Disease.split(":")[1]) == null)
									GenCC_ncbigene_mondo_Map.put(NCBI_ID + "\t" + Disease.split(":")[1], "");
							}	
						}
					}
				}			
			}
			catch (Exception e) {

			}
		}

		BufferedWriter bw_GenCC_ncbigene_omim = new BufferedWriter(new FileWriter("./GenCC_ncbigene_omim.tsv"));
		for(String key : GenCC_ncbigene_omim_Map.keySet())
		{
			NCBI_ID = key.split("\t")[0];
			OMIM_ID = key.split("\t")[1];
			
			bw_GenCC_ncbigene_omim.write(NCBI_ID + "\t" + OMIM_ID);bw_GenCC_ncbigene_omim.newLine();
		}

		BufferedWriter bw_GenCC_ncbigene_orpha = new BufferedWriter(new FileWriter("./GenCC_ncbigene_orpha.tsv"));
		for(String key : GenCC_ncbigene_orpha_Map.keySet())
		{
			NCBI_ID = key.split("\t")[0];
			Orphanet_ID = key.split("\t")[1];
			
			bw_GenCC_ncbigene_orpha.write(NCBI_ID + "\t" + Orphanet_ID);bw_GenCC_ncbigene_orpha.newLine();
		}

		bw_GenCC_ncbigene_omim.close();
		bw_GenCC_ncbigene_orpha.close();
		br_GenCC.close();
	}
}

