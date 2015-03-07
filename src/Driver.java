import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by minchen on 15/3/7.
 */
public class Driver {
    private static HashMap<String, HashMap<String, ArrayList<Integer>>> map = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
    private static String currentFileName;
    private static int currentWordIndex;
    private static List<OneQuery> querys = new ArrayList<OneQuery>();

    public static void main(String[] args) throws IOException {
        String pathIn = "./";
        String pathOut = "./index.txt";
        if (args.length>=1 && args[0].equals("-d")) pathIn = args[1];
        getQuerys("./src/query.txt");
        getFiles(pathIn);

        if (args.length>=3 && args[2].equals("-i")) {
            if (args.length > 3) pathOut = args[3];
        }

        outPut(pathOut);
    }

    public static void getFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if(!files[i].isDirectory()){
                if(files[i].toString().toLowerCase().endsWith(".txt")) {
                    handleOneFile(files[i].getPath());
                }
            }
            else getFiles(files[i].getPath());
        }
    }

    public static void handleOneFile(String path) {
        try {
            FileReader fr = new FileReader (path);
            BufferedReader br = new BufferedReader(fr);

            currentFileName = path;
            currentWordIndex = 1;

            String str;
            while ((str= br.readLine()) != null){
                handleWords(filteredLine(str));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println ("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String filteredLine(String str){
        String pattern = "[^a-zA-Z0-9]+";
        return str.replaceAll(pattern," ");
    }

    public static void handleWords(String str) {
        String[] words = str.split(" ");
        for (int i=0; i<words.length; i++) {
            String word = words[i].toLowerCase().trim();
            if (word.equals("")) continue;
            storeWord(word);
            currentWordIndex++;
        }
    }

    public static void storeWord(String str) {
        if(map.containsKey(str)) {

            if(map.get(str).containsKey(currentFileName)) {
                map.get(str).get(currentFileName).add(currentWordIndex);
            }
            else {
                ArrayList<Integer> temAL = new ArrayList<Integer>();
                temAL.add(currentWordIndex);
                map.get(str).put(currentFileName,temAL);
            }
        }
        else {
            ArrayList<Integer> temAL = new ArrayList<Integer>();
            temAL.add(currentWordIndex);
            HashMap<String,ArrayList<Integer>> temMap = new HashMap<String, ArrayList<Integer>>();
            temMap.put(currentFileName, temAL);
            map.put(str, temMap);
        }

        for (int i=0; i<querys.size(); i++) {
            if (querys.get(i).has(str)) {
                querys.get(i).add(currentFileName, currentWordIndex);
            }
        }
    }

    public static void outPut(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        boolean firstFlag = true;
        Object[] key_arr = map.keySet().toArray();
        Arrays.sort(key_arr);
        for (Object key : key_arr) {
            if (firstFlag) {
                out.write(key.toString());
                firstFlag = false;
            }
            else out.write("\n\n" + key);
            HashMap map2 = map.get(key);

            Object[] key_arr2 = map2.keySet().toArray();
            Arrays.sort(key_arr2);
            for (Object key2 : key_arr2) {
                out.write("\n\"" + key2 + "\"");
                ArrayList value2 = (ArrayList) map2.get(key2);

                for (int i=0; i<value2.size(); i++) {
                    out.write(", " + value2.get(i));
                }
            }
        }
        out.flush();
        out.close();
    }

    public static void getQuerys(String path) {
        try {
            FileReader fr = new FileReader (path);
            BufferedReader br = new BufferedReader(fr);

            String str;
            while ((str= br.readLine()) != null){
                querys.add(new OneQuery(str));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println ("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
