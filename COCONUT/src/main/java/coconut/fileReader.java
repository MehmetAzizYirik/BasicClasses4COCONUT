package coconut;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class fileReader {
	
	/**
	 * Reading all that data files then creating a tsv file in the format:
	 * 
	 * 1. COCONUT ID
	 * 2. Family Information
	 * 3. ChEMBL ID
	 * 4. ZINC ID
	 * 
	 * @param cocoIDpath String path to the COCONUT ID file
	 * @param CHEMBLpath String path to the CHEMBL ID file
	 * @param NPOIDpath	String path to the NPO ID file
	 * @param FamilyPath String path to the file for family genius etc information.
	 * @param output String path for the output tsv file.
	 * @throws IOException
	 */
	
	public static void tsv(String cocoIDpath, String CHEMBLpath, String NPOIDpath, String FamilyPath, String output) throws IOException {
		FileWriter fWriter = new FileWriter(output+".tsv");
		PrintWriter pWriter = new PrintWriter(fWriter);
		ArrayList<String[]> IDs=getID(cocoIDpath);
		pWriter.print("COCONUT ID	FAMILY INFO	CHEMBL ID	ZINC ID"+"\n");
		for(int i=0;i<IDs.size();i++) {
			String[] ids= new String[2];
			String NPC=IDs.get(i)[1];
			ids=getOtherDatabaseIDs(CHEMBLpath,NPC,ids);
			String NPO=findNPOID(NPOIDpath,NPC);
			String familyInfo= getFamilyInfo(FamilyPath,NPO);
			pWriter.print(IDs.get(i)[0]+"\t");
			pWriter.print(familyInfo+"\t");
			pWriter.print(ids[0]+"	"+ids[1]+"\t");
			pWriter.println();
		}
		pWriter.close();
		fWriter.close();
	}
	
	/**
	 * For a given NPOID, find all the family information
	 * such as species genius, and family information.
	 * @param path String file path
	 * @param NPOID String NPOID
	 * @return String family info.
	 * @throws IOException
	 */
	
	public static String getFamilyInfo(String path, String NPOID) throws IOException {
		FileInputStream fileStream = new FileInputStream(path);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream));
		String line;
		String info="";
		while ((line = buffer.readLine()) != null)   {
			String[] splited = line.split("	");
			if(splited[0].matches(NPOID)) {
				String[] family=redefineColumn(splited);
				info=info+(family[1]+"	"+family[2]);
				break;
			}
		}
		fileStream.close();
		buffer.close();
		return info;
	}
	
	/**
	 * Getting the other database IDs for a NPCID
	 * @param path String file path
	 * @param NPCID String NPCID
	 * @param ids String[] length 2 String array to fill with the entries if any.
	 * @return ids String[] 
	 * @throws IOException
	 */
	
	public static String[] getOtherDatabaseIDs(String path, String NPCID, String[] ids) throws IOException {
		FileInputStream fileStream = new FileInputStream(path);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream));
		String line;
		while ((line = buffer.readLine()) != null && line.length()!=0)   {
			if(line.contains(NPCID)) {
				String[] splited = line.split("	");
				for(String s: splited) {
					if(s.contains("CHEMBL")) {
						ids[0]=s;
					}
					if(s.contains("ZINC")) {
						ids[1]=s;
					}
				}
				break;
			}
		}
		fileStream.close();
		buffer.close();
		return ids;
	}
	
	/**
	 * Getting the ArrayList of COCONUT IDs and NPCIDs.
	 * @param path String file path
	 * @return ArrayList<String[]> ID list
	 * @throws IOException
	 */
	
	public static ArrayList<String[]> getID(String path) throws IOException {
		FileInputStream fileStream = new FileInputStream(path);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream));
		String line;
		ArrayList<String[]> ids= new ArrayList<String[]>();
		while ((line = buffer.readLine()) != null){
			String[] splited = line.split(",");
			ids.add(splited);
		}
		fileStream.close();
		buffer.close();
		return ids;
	}
	
	/**
	 * In the data file, find the corresponding NPO ID.
	 * @param path String file path
	 * @param ID 
	 * @return String NPOID
	 * @throws IOException
	 */
	
	public static String findNPOID(String path, String ID) throws IOException {
		FileInputStream fileStream = new FileInputStream(path);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream));
		String line;
		String id="";
		while ((line = buffer.readLine()) != null && line.length()!=0){
			if(line.contains(ID)) {
				String[] splited = line.split("	");
				id=id+splited[0];
				break;
			}
		}
		fileStream.close();
		buffer.close();
		return id;
	}
	
	/**
	 * Redefine columns of data file.
	 * @param array String[]
	 * @return String[]
	 */
	
	public static String[] redefineColumn(String[] array) {
		String[] arr= new String[3];
		arr[0]=array[0];
		arr[1]=array[1];
		arr[2]=joinAll(array);
		return arr;
	}
	
	/**
	 * Merge all the entries, put $ in between.
	 * @param array String[]
	 * @return String
	 */
	
	public static String joinAll(String[] array) {
		String total="";
		for(int i=2;i<(array.length-1);i++) {
			total=total+array[i]+"$";
		}
		return (total+array[array.length-1]);
	}
	
	public static void main(String[] args) throws IOException {
		/**
		 * An example case for the usage
		 */
		
		String cocoPath   = "C:\\Users\\mehme\\Desktop\\COCONUT\\COCONUT_w_source_cmaup.csv";
		String CHEMBLpath = "C:\\Users\\mehme\\Desktop\\COCONUT\\CMAUPv1.0_download_Ingredients_All.txt";
		String NPOIDpath  = "C:\\Users\\mehme\\Desktop\\COCONUT\\CMAUPv1.0_download_Plant_Ingredient_Associations_allIngredients.txt";
		String familyPath = "C:\\Users\\mehme\\Desktop\\COCONUT\\CMAUPv1.0_download_Plants.txt";
		String path= "C:\\Users\\mehme\\Desktop\\COCONUT\\output.tsv";
		tsv(cocoPath, CHEMBLpath, NPOIDpath, familyPath,path);
	
	}
}
