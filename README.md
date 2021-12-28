# PubCaseFinder-RDF
This is a description of PubCaseFinder-RDF.

----------
## Prerequisites
First set up your environment:<br>
Make sure a proper JDK is installed, Java SE 1.8 or higher. Just a JRE isn't enough, since the project requires compilation.

### Data Download
<table style="width:39%;">
    <colgroup>
        <col width="19%" />
        <col width="19%" />
    </colgroup>
<thead>
    <tr class="header">
        <th>Data</th>
        <th>Resource link</th>
    </tr>
</thead>
<tbody>
    <tr class="odd">
        <td>hp.obo</td>
        <td>http://purl.obolibrary.org/obo/hp.obo</td>
    </tr>
    <tr class="even">
        <td>mondo.obo</td>
        <td>http://purl.obolibrary.org/obo/mondo.obo</td>
    </tr>
    <tr class="odd">
        <td>mim2gene.txt</td>
        <td>https://www.omim.org/static/omim/data/mim2gene.txt</td>
    </tr>
    <tr class="even">
        <td>phenotype.hpoa</td>
        <td>http://purl.obolibrary.org/obo/hp/hpoa/phenotype.hpoa</td>
    </tr>
    <tr class="odd">
        <td>gencc-submissions.csv</td>
        <td>https://search.thegencc.org/download</td>
    </tr>
    <tr class="even">
        <td>MedGen_HPO_OMIM_Mapping.txt</td>
        <td>https://ftp.ncbi.nlm.nih.gov/pub/medgen/MedGen_HPO_OMIM_Mapping.txt.gz</td>
    </tr>
    <tr class="odd">
        <td>NBKid_shortname_OMIM.txt</td>
        <td>https://ftp.ncbi.nlm.nih.gov/pub/GeneReviews/NBKid_shortname_OMIM.txt</td>
    </tr>
    <tr class="even">
        <td>mim2gene_medgen.txt</td>
        <td>https://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen</td>
    </tr>
    <tr class="odd">
        <td>Homo_sapiens.gene_info</td>
        <td>https://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz</td>
    </tr>
    <tr class="even">
        <td>en_product4.xml</td>
        <td>http://www.orphadata.org/data/xml/en_product4.xml<br>ORPHADATA PHENOTYPES ASSOCIATED WITH RARE DISORDERS</td>
    </tr>
    <tr class="odd">
        <td>en_product6.xml</td>
        <td>http://www.orphadata.org/data/xml/en_product6.xml<br>ORPHADATA GENES ASSOCIATED WITH RARE DISEASES</td>
    </tr>
    <tr class="even">
        <td>HGNC_custom.txt</td>
        <td>https://www.genenames.org/download/custom/</td>
    </tr>
    <tr class="odd">
        <td>OMIM_id_ja.txt</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/OMIM_id_ja.txt" class="uri">OMIM_id_ja.txt</a></td>
    </tr>
    <tr class="even">
        <td>HPO_id_ja.txt</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/HPO_id_ja.txt" class="uri">HPO_id_ja.txt</a></td>
    </tr>
    <tr class="odd">
        <td>HPO_Inheritance_en_jp.txt</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/HPO_Inheritance_en_jp.txt" class="uri">HPO_Inheritance_en_jp.txt</a></td>
    </tr>
    <tr class="even">
        <td>KEGG_disease.tsv</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/KEGG_disease.tsv" class="uri">KEGG_disease.tsv</a></td>
    </tr>
    <tr class="odd">
        <td>NCBI_gene_summary.txt</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/NCBI_gene_summary.txt" class="uri">NCBI_gene_summary.txt</a></td>
    </tr>
    <tr class="even">
        <td>UR_DBMS_DiseaseLinkOMIM.csv</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/UR_DBMS_DiseaseLinkOMIM.csv" class="uri">UR_DBMS_DiseaseLinkOMIM.csv</a></td>
    </tr>
    <tr class="odd">
        <td>UR_DBMS_DiseaseLink.csv</td>
        <td><a href="https://github.com/aidrd/pubcasefinder-rdf/blob/main/data/source/UR_DBMS_DiseaseLink.csv" class="uri">UR_DBMS_DiseaseLink.csv</a></td>
    </tr>
</tbody>
</table>

※ Precautions for **'HGNC_custom.txt'**.<br>
You can create custom files on the https://www.genenames.org/download/custom/ website.<br>
First, unselect everything. Then select the following information.
- Curated by the HGNC
    - HGNC ID,	Approved symbol
- Downloaded from external sources
    - NCBI Gene ID(supplied by NCBI)
- Select status
    - Approved

When you are done selecting, click the Submit button.
If the created file looks like this, it's a success.
```
HGNC ID         Approved symbol NCBI Gene ID(supplied by NCBI)
HGNC:5  	A1BG            1
HGNC:37133	A1BG-AS1        503538
HGNC:24086	A1CF            29974
HGNC:7          A2M             2
HGNC:27057	A2M-AS1         144571
HGNC:23336	A2ML1           144568
HGNC:41022	A2ML1-AS1   	100874108
HGNC:41523	A2ML1-AS2   	106478979
HGNC:8          A2MP1           3
...
```

----------
## Running
The script to use is <a href="https://github.com/aidrd/pubcasefinder-rdf/tree/main/script" class="uri">here</a>.
### 1. Disease Gene Association
#### Example run:
The following command outputs a file in Turtle format.
``` java
$ javac DiseaseGeneAssociation.java
$ java DiseaseGeneAssociation HGNC_custom.txt mondo.obo mim2gene_medgen.txt en_product6.xml gencc-submissions.csv
```
#### Output result file:
The out result file from the example run will at **'OMIM_Gene_Association.ttl'** and **'Orphanet_Gene_Association.ttl'**.

The output are written to the disk as **'OMIM_Gene_Association.ttl'** file. They look like this

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

The output are written to the disk as **'Orphanet_Gene_Association.ttl'** file. They look like this

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

### 2. Disease HPO Association
#### Example run:
``` java
$ javac DiseaseHpoAssociation.java
$ java DiseaseHpoAssociation phenotype.hpoa en_product4.xml
```
#### Output result file:
The out result file from the example run will at **'OMIM_HP_Association.ttl'** and **'Orphanet_HP_Association.ttl'**.

### 3. NCBI HGNC Gene
- Example run:
  - `$ javac NCBI_HGNC.java`
  - `$ java NCBI_HGNC HGNC_custom.txt Homo_sapiens.gene_info mim2gene.txt NCBI_gene_summary.txt`
- Output
  - NCBI_HGNC.ttl

### 4. HP Inheritance
- Example run:
  - `$ javac HP_Inheritance.java`
  - `$ java HP_Inheritance hp.obo HPO_id_ja.txt HPO_Inheritance_en_jp.txt`
- Output
  - HP_Inheritance.ttl

### 5. Disease
- Example run:
  - `$ javac Disease.java`
  - `$ java Disease mim2gene.txt OMIM_id_ja.txt MedGen_HPO_OMIM_Mapping.txt mondo.obo NBKid_shortname_OMIM.txt UR_DBMS_DiseaseLinkOMIM.csv UR_DBMS_DiseaseLink.csv KEGG_disease.tsv `
- Output
  - OMIM.ttl
  - Orphanet.ttl

----------
## Contact
- Jae-Moon Shin (shin@dbcls.rois.ac.jp)
