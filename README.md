# pubcasefinder-rdf

## Prerequisites
- Java

## Data Download
1. hp.obo    - http://purl.obolibrary.org/obo/hp.obo
2. mondo.obo    - http://purl.obolibrary.org/obo/mondo.obo
3. mim2gene.txt    - https://www.omim.org/static/omim/data/mim2gene.txt
4. phenotype.hpoa    - http://purl.obolibrary.org/obo/hp/hpoa/phenotype.hpoa

6. gencc-submissions.csv    - https://search.thegencc.org/download
7. MedGen_HPO_OMIM_Mapping.txt    - https://ftp.ncbi.nlm.nih.gov/pub/medgen/MedGen_HPO_OMIM_Mapping.txt.gz
8. NBKid_shortname_OMIM.txt    - https://ftp.ncbi.nlm.nih.gov/pub/GeneReviews/NBKid_shortname_OMIM.txt
9. mim2gene_medgen.txt    - https://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen
10. Homo_sapiens.gene_info    - https://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz
5. ORPHADATA
    - PHENOTYPES ASSOCIATED WITH RARE DISORDERS
      - en_product4.xml      - http://www.orphadata.org/data/xml/en_product4.xml
    - GENES ASSOCIATED WITH RARE DISEASES
      - en_product6.xml      - http://www.orphadata.org/data/xml/en_product6.xml
11. HGNC Custom downloads
    - HGNC_custom.txt    - https://www.genenames.org/download/custom/
      - Select column data
        - Curated by the HGNC
          - HGNC ID,	Approved symbol
        - Downloaded from external sources
          - NCBI Gene ID(supplied by NCBI)
        - Select status
          - Approved
12. pubcasefinder-rdf/data/source/
    - OMIM_id_ja.txt 
    - HPO_id_ja.txt
    - HPO_Inheritance_en_jp.txt
    - KEGG_disease.tsv
    - NCBI_gene_summary.txt (shin)
    - UR_DBMS_DiseaseLinkOMIM.csv (PCF RDB data)
    - UR_DBMS_DiseaseLink.csv (PCF RDB data)
    
-----------------
## Running
#### 1. Disease Gene Association
``` java
$ javac DiseaseGeneAssociation.java
$ java DiseaseGeneAssociation HGNC_custom.txt mondo.obo mim2gene_medgen.txt en_product6.xml gencc-submissions.csv
```

The output are written to the disk as **OMIM_Gene_Association.ttl** file. They look like this

    PREFIX dcterms: <http://purl.org/dc/terms/>
    PREFIX ncbigene: <http://identifiers.org/ncbigene/>
    PREFIX mim: <http://identifiers.org/mim/>
    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX sio: <http://semanticscience.org/resource/>
    <https://pubcasefinder.dbcls.jp/gene_context/disease:OMIM:613320/gene:ENT:51025>
        a sio:SIO_000983 ;
        sio:SIO_000628 mim:613320, ncbigene:51025 ;
        dcterms:source <ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen> .
    ...

The output are written to the disk as **Orphanet_Gene_Association.ttl** file. They look like this

    PREFIX dcterms: <http://purl.org/dc/terms/>
    PREFIX ncbigene: <http://identifiers.org/ncbigene/>
    PREFIX ordo: <http://www.orpha.net/ORDO/>
    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX sio: <http://semanticscience.org/resource/>
    <https://pubcasefinder.dbcls.jp/gene_context/disease:ORDO:178342/gene:ENT:1213>
        a sio:SIO_000983 ;
        sio:SIO_000628 ordo:Orphanet_178342, ncbigene:1213 ;
        dcterms:source <http://www.orphadata.org/data/xml/en_product6.xml> .
    ...
        
------------------------------------------------------------------------
#### 2. Disease HPO Association
- Java run
  - `$ javac DiseaseHpoAssociation.java`
  - `$ java DiseaseHpoAssociation phenotype.hpoa en_product4.xml`
- Output
  - OMIM_HP_Association.ttl
  - Orphanet_HP_Association.ttl

#### 3. NCBI HGNC Gene
- Java run
  - `$ javac NCBI_HGNC.java`
  - `$ java NCBI_HGNC HGNC_custom.txt Homo_sapiens.gene_info mim2gene.txt NCBI_gene_summary.txt`
- Output
  - NCBI_HGNC.ttl
#### 4. HP Inheritance
- Java run
  - `$ javac HP_Inheritance.java`
  - `$ java HP_Inheritance hp.obo HPO_id_ja.txt HPO_Inheritance_en_jp.txt`
- Output
  - HP_Inheritance.ttl
#### 5. Disease
- Java run
  - `$ javac Disease.java`
  - `$ java Disease mim2gene.txt OMIM_id_ja.txt MedGen_HPO_OMIM_Mapping.txt mondo.obo NBKid_shortname_OMIM.txt UR_DBMS_DiseaseLinkOMIM.csv UR_DBMS_DiseaseLink.csv KEGG_disease.tsv `
- Output
  - OMIM.ttl
  - Orphanet.ttl


