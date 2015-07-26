import java.util.*;
import java.io.*;

public class trans {
   public static void main(String[] args) {
      String ja = args[0];
      ArrayList<String> p = new ArrayList<String>();
      p = transition(p, ja);
      System.out.println(result(p));
   }
   
   public static ArrayList<String> transition(ArrayList<String> q, String p) {
      String temp = p;
      for (int i = 0; i < p.length(); i++) {
         if (p.charAt(i) == '(') {
            String ele = recursive(temp.substring(i + 1));
            if (!ele.contains("fst_acceptor")) q.add(ele);
         }
      }
      Scanner prob = new Scanner(p);
      ArrayList<String> temporary = new ArrayList<String>();
      while (prob.hasNext()) {
         temporary.add(prob.next());
      }
      q.add(temporary.get(temporary.size()-1));
      return q;
   }
   
   public static String recursive(String g) {
      String temp = "";
      for (int i = 0; i < g.length(); i++) {
         temp += g.charAt(i);
         if (g.charAt(i) == ')') {
            return temp.substring(0, i);
         }
      }
      return temp;
   }
   
   public static String result(ArrayList<String> p) {
      String str = "";
      for (int i = 0; i < p.size() - 1; i ++) {
         String edge = p.get(i);
         String output = edge.replaceAll(".*:\\s", "");
         output = output.replaceAll("/.*", "");
         output = output.replaceAll(" ", "");
         output = output.replaceAll(".*fsa.*", "");
         str += "\"" + output + "\" ";
      }
      return str += p.get(p.size() - 1);
   }
}