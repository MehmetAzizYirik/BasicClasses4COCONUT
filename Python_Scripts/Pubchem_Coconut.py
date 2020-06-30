# Python script to retrive data from pubchem for COCONUT : https://coconut.naturalproducts.net

# This Software is under the MIT License
# Refer to LICENSE or https://opensource.org/licenses/MIT for more information
# Copyright (c) 2020, Kohulan Rajan

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

    print("Total Pubchem IDs: ",len(cids)," and total coconut_ids: ",len(coconut_ids)) # Just to cross check the data


    for i in range(len(cids)):
        try:
            retrieve = pcp.Compound.from_cid(cids[i])
            f.write(cids[i]+"\t"+str(coconut_ids[i])+"\t"+str(retrieve.synonyms[0])+
            	"\t"+retrieve.iupac_name+"\t"+get_cas(retrieve)+"\t"+str(retrieve.synonyms) +"\n")
            f.flush()

        except Exception as e:
            print(e,cids[i])
            coconut_ids_unprocessed.append(coconut_ids[i])
    
    if len(coconut_ids_unprocessed)>0:
        for i in range(len(coconut_ids_unprocessed)):
            f.write("Unprocessed ID: "+str(coconut_ids_unprocessed[i]))

    f.close()


# Fuction to retrive CAS Registry Numbers
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