import sys
import time
import re
from time import sleep
import pubchempy as pcp
import argparse

parser = argparse.ArgumentParser(description="Fetch Pubchem data using cids")
# Input Arguments
parser.add_argument(
    '--input',
    help = 'Enter the input filename',
    required = True
    )
parser.add_argument(
    '--output',
    help = 'Enter the output filename as desired',
    required = True
)
args= parser.parse_args()

def main():
    start = time.time()

    f = open(args.output,"w")

    cids = []
    coconut_ids = []
    coconut_ids_unprocessed = []

    with open(args.input) as fp:
        for i,line in enumerate (fp):
            cid = line.strip().split("\t")[2]
            coconut_id = line.strip().split("\t")[1]
            cids.append(cid)
            coconut_ids.append(coconut_id)

    print("Total Pubchem IDs: ",len(cids)," and total coconut_ids: ",len(coconut_ids),flush=True)


    for i in range(len(cids)):
        try:
            retrieve = pcp.Compound.from_cid(cids[i])
            if len(retrieve.synonyms)>0:
                f.write(cids[i]+"\t"+str(coconut_ids[i])+"\t"+str(retrieve.synonyms[0])+
                    "\t"+str(retrieve.iupac_name)+"\t"+get_cas(retrieve)+"\t"+str(retrieve.synonyms) +"\n")
                f.flush()
            else:
                f.write(cids[i]+"\t"+str(coconut_ids[i])+"\t"+
                    "\t"+str(retrieve.iupac_name)+"\n")
                f.flush()
            
        except Exception as e:
            print(e,cids[i],flush = True)
            coconut_ids_unprocessed.append(coconut_ids[i])

    for i in range(len(coconut_ids_unprocessed)):
        f.write("Unprocessed ID: "+str(coconut_ids_unprocessed[i]))

    f.close()

def get_cas(retrieve):
    cas_iter = []
    cas_syn = retrieve.synonyms
    for i in range(len(cas_syn)):
        cas_match = re.match('(\d{2,7}-\d\d-\d)', cas_syn[i])
        if cas_match:
            cas_iter.append(cas_match.group(1))

    return str(cas_iter)

if __name__ == '__main__':
    main()