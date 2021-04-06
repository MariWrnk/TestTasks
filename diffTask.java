import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.min;

public class diffTask {

    static ArrayList<String> file1 = new ArrayList<>();
    static ArrayList<String> file2 = new ArrayList<>();
    static int[][][] lcsArr;
    static ArrayList<Integer> saved1ind = new ArrayList<>();
    static ArrayList<Integer> saved2ind = new ArrayList<>();
    static ArrayList<Integer> changed1ind = new ArrayList<>();
    static ArrayList<Integer> changed2ind = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        readFiles();
        findLCS();
        findChanged();
        fillHTML();
    }

    public static void readFiles() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите путь до первого файла:");
        BufferedReader br = new BufferedReader(new FileReader(sc.nextLine()));
        String line;
        while((line = br.readLine()) != null){
            file1.add(line);
        }
        System.out.println("Введите путь до второго файла:");
        br = new BufferedReader(new FileReader(sc.nextLine()));
        while((line = br.readLine()) != null){
            file2.add(line);
        }
        lcsArr = new int[file1.size() + 1][file2.size() + 1][3];
        br.close();
    }

    public static void findLCS(){
        for(int i = 0; i < lcsArr.length; i++){
            for(int j = 0; j < lcsArr[i].length; j++){
                if(i == 0 || j == 0){
                    lcsArr[i][j][0] = 0;
                    continue;
                }
                if(file1.get(i - 1).equals(file2.get(j - 1))){
                    lcsArr[i][j][0] = lcsArr[i - 1][j - 1][0] + 1;
                    lcsArr[i][j][1] = i - 1;
                    lcsArr[i][j][2] = j - 1;
                }
                else{
                    if(lcsArr[i - 1][j][0] >= lcsArr[i][j - 1][0]){
                        lcsArr[i][j][0] = lcsArr[i - 1][j][0];
                        lcsArr[i][j][1] = i - 1;
                        lcsArr[i][j][2] = j;
                    }
                    else{
                        lcsArr[i][j][0] = lcsArr[i][j - 1][0];
                        lcsArr[i][j][1] = i;
                        lcsArr[i][j][2] = j - 1;
                    }
                }
            }
        }
        int i = lcsArr.length - 1;
        int j = lcsArr[i].length - 1;
        while(i != 0 || j != 0){
            if(lcsArr[i][j][0] > lcsArr[lcsArr[i][j][1]][lcsArr[i][j][2]][0]){
                saved1ind.add(0, i - 1);
                saved2ind.add(0, j - 1);
            }
            i = lcsArr[i][j][1];
            j = lcsArr[i][j][2];
        }
    }

    public static void findChanged(){
        int sv1 = saved1ind.get(0);
        int sv2 = saved2ind.get(0);
        if(sv1 != 0 && sv2 != 0){
            int size = min(sv1, sv2);
            addRange(changed1ind, sv1 - size, size);
            addRange(changed2ind, sv2 - size, size);
        }
        for(int a = 0; a < saved2ind.size() - 1; a++){
            int currSv1 = saved1ind.get(a);
            int currSv2 = saved2ind.get(a);
            int nextSv1 = saved1ind.get(a + 1);
            int nextSv2 = saved2ind.get(a + 1);
            if(currSv1 != nextSv1 - 1 && currSv2 != nextSv2 - 1){
                int size = min(nextSv1 - currSv1 - 1, nextSv2 - currSv2 - 1);
                addRange(changed1ind, currSv1 + 1, size);
                addRange(changed2ind, currSv2 + 1, size);
            }
        }
        sv1 = saved1ind.get(saved1ind.size() - 1);
        sv2 = saved2ind.get(saved2ind.size() - 1);
        if(sv1 != file1.size() - 1 && sv2 != file2.size() - 1){
            int size = min(file1.size() - 1 - sv1, file2.size() - 1 - sv2);
            addRange(changed1ind, sv1 + 1, size);
            addRange(changed2ind, sv2 + 1, size);
        }
    }

    public static void addRange(ArrayList<Integer> list, int start, int size){
        for(int i = 0; i < size; i++){
            list.add(start);
            start++;
        }
    }

    public static void fillHTML() throws IOException {
        File f = new File("./resources/diff.html");
        f.createNewFile();
        PrintWriter pw = new PrintWriter(f);
        pw.println("<!DOCTYPE HTML>");
        pw.println("<html>");
        pw.println("\t<head>");
        pw.println("\t\t<title>Files' difference</title>");
        pw.println("\t</head>");
        pw.println("\t<body>");
        pw.println("\t\t<style>");
        pw.println("\t\t\t.added{\n" +
                "\t\t\t\tbackground-color: green;\n" +
                "\t\t\t}\n" +
                "\t\t\t.deleted{\n" +
                "\t\t\t\tbackground-color: grey;\n" +
                "\t\t\t}\n" +
                "\t\t\t.changed{\n" +
                "\t\t\t\tbackground-color: blue;\n" +
                "\t\t\t}");
        pw.println("\t\t</style>");
        pw.println("\t\t<table width=\"100%\">");
        pw.println("\t\t\t<tr>");
        pw.println("\t\t\t\t<th>File 1</th>");
        pw.println("\t\t\t\t<th>File 2</th>");
        pw.println("\t\t\t</tr>");

        for(int i = 0; i < min(file1.size(), file2.size()); i++){
            pw.println("\t\t\t<tr>");
            pw.print("\t\t\t\t<td");
            if(changed1ind.contains(i)){
                pw.print(" class=\"changed\"");
            }else{
                if(!saved1ind.contains(i)){
                    pw.print(" class=\"deleted\"");
                }
            }
            pw.println(">" + file1.get(i) + "</td>");
            pw.print("\t\t\t\t<td");
            if(changed2ind.contains(i)){
                pw.print(" class=\"changed\"");
            }else{
                if(!saved2ind.contains(i)){
                    pw.print(" class=\"added\"");
                }
            }
            pw.println(">" + file2.get(i) + "</td>");
            pw.println("\t\t\t</tr>");
        }

        if(file1.size() > file2.size()){
            for(int i = file2.size(); i < file1.size(); i++){
                pw.println("\t\t\t<tr>");
                pw.print("\t\t\t\t<td");
                if(changed1ind.contains(i)){
                    pw.print(" class=\"changed\"");
                }else{
                    if(!saved1ind.contains(i)){
                        pw.print(" class=\"deleted\"");
                    }
                }
                pw.println(">" + file1.get(i) + "</td>");
                pw.println("\t\t\t\t<td></td>");
                pw.println("\t\t\t</tr>");
            }
        }

        if(file1.size() < file2.size()){
            for(int i = file1.size(); i < file2.size(); i++){
                pw.println("\t\t\t<tr>");
                pw.println("\t\t\t\t<td></td>");
                pw.print("\t\t\t\t<td");
                if(changed2ind.contains(i)){
                    pw.print(" class=\"changed\"");
                }else{
                    if(!saved2ind.contains(i)){
                        pw.print(" class=\"added\"");
                    }
                }
                pw.println(">" + file2.get(i) + "</td>");
                pw.println("\t\t\t</tr>");
            }
        }

        pw.println("\t\t</table>");
        pw.println("\t</body>");
        pw.println("</html>");
        pw.close();
    }
}
