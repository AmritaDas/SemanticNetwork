/* Author: Amrita Das
 * Unity ID: adas5
*/
import java.util.*;
public class SearchRomania{
     public String src, dest;
     int srcNum, destNum, fndDest;
     ArrayList<Integer> tree = new ArrayList<Integer>();
     ArrayList<Integer> path = new ArrayList<Integer>();
     ArrayList<Integer> newpath = new ArrayList<Integer>();
     Node[] Rmap;
     public static void main(String []args){
         if(args.length<3){
             System.out.println("Not enough arguments");
         }
         SearchRomania bd = new SearchRomania();
         bd.Rmap = new Node[20];
         bd.src = args[1];
         bd.dest = args[2];
         String type = args[0];
         bd.fndDest=0;
         bd.makeMap();
         bd.srcNum = bd.getCity(bd.src);
         bd.destNum = bd.getCity(bd.dest);
         if(bd.srcNum==-1 || bd.destNum==-1){
             System.out.println("Cities not found in map");
         }
         else{
             bd.tree.add(bd.srcNum);
             bd.path.add(bd.srcNum);
             bd.Rmap[bd.srcNum].exp=1;
             if(type.equals("bfs")||type.equals("BFS")){
                 bd.BFS(bd.srcNum, bd.destNum);
             }
             else if(type.equals("dfs")||type.equals("DFS")){
                 bd.DFS(bd.srcNum, bd.destNum);
             }
             else{
                 System.out.println("Search type is not BFS or DFS");
             }
         }
     }
     
     void makeMap(){
         Rmap[0] = new Node("arad", 3); Rmap[0].num=0;
         Rmap[1] = new Node("bucharest", 4); Rmap[1].num=1;
         Rmap[2] = new Node("craiova", 3); Rmap[2].num=2;
         Rmap[3] = new Node("dobreta", 2); Rmap[3].num=3;
         Rmap[4] = new Node("eforie", 1); Rmap[4].num=4;
         Rmap[5] = new Node("fagaras", 2); Rmap[5].num=5;
         Rmap[6] = new Node("giurgiu", 1); Rmap[6].num=6;
         Rmap[7] = new Node("hirsova", 2); Rmap[7].num=7;
         Rmap[8] = new Node("iasi", 2); Rmap[8].num=8;
         Rmap[9] = new Node("lugoj", 2); Rmap[9].num=9;
         Rmap[10] = new Node("mehadia", 2); Rmap[10].num=10;
         Rmap[11] = new Node("neamt", 1); Rmap[11].num=11;
         Rmap[12] = new Node("oradea", 2); Rmap[12].num=12;
         Rmap[13] = new Node("pitesti", 3); Rmap[13].num=13;
         Rmap[14] = new Node("rimnicu_vilcea", 3); Rmap[14].num=14;
         Rmap[15] = new Node("sibiu", 4); Rmap[15].num=15;
         Rmap[16] = new Node("timisoara", 2); Rmap[16].num=16;
         Rmap[17] = new Node("urziceni", 3); Rmap[17].num=17;
         Rmap[18] = new Node("vaslui", 2); Rmap[18].num=18;
         Rmap[19] = new Node("zerind", 2); Rmap[19].num=19;
         
         Rmap[0].adjs[0]=Rmap[15]; Rmap[0].adjs[1]=Rmap[16]; 
         Rmap[0].adjs[2]=Rmap[19];
         Rmap[1].adjs[0]=Rmap[5]; Rmap[1].adjs[1]=Rmap[6]; 
         Rmap[1].adjs[2]=Rmap[13]; Rmap[1].adjs[3]=Rmap[17];
         Rmap[2].adjs[0]=Rmap[3]; Rmap[2].adjs[1]=Rmap[13]; 
         Rmap[2].adjs[2]=Rmap[14];
         Rmap[3].adjs[0]=Rmap[2]; Rmap[3].adjs[1]=Rmap[10];
         Rmap[4].adjs[0]=Rmap[7];
         Rmap[5].adjs[0]=Rmap[1]; Rmap[5].adjs[1]=Rmap[15];
         Rmap[6].adjs[0]=Rmap[1]; 
         Rmap[7].adjs[0]=Rmap[4]; Rmap[7].adjs[1]=Rmap[17];
         Rmap[8].adjs[0]=Rmap[11]; Rmap[8].adjs[1]=Rmap[18];
         Rmap[9].adjs[0]=Rmap[10]; Rmap[9].adjs[1]=Rmap[16];
         Rmap[10].adjs[0]=Rmap[3]; Rmap[10].adjs[1]=Rmap[9];
         Rmap[11].adjs[0]=Rmap[8];
         Rmap[12].adjs[0]=Rmap[15]; Rmap[12].adjs[1]=Rmap[19];
         Rmap[13].adjs[0]=Rmap[1]; Rmap[13].adjs[1]=Rmap[2]; 
         Rmap[13].adjs[2]=Rmap[14];
         Rmap[14].adjs[0]=Rmap[2]; Rmap[14].adjs[1]=Rmap[13]; 
         Rmap[14].adjs[2]=Rmap[15];
         Rmap[15].adjs[0]=Rmap[0]; Rmap[15].adjs[1]=Rmap[5]; 
         Rmap[15].adjs[2]=Rmap[12]; Rmap[15].adjs[3]=Rmap[14];
         Rmap[16].adjs[0]=Rmap[0]; Rmap[16].adjs[1]=Rmap[9];
         Rmap[17].adjs[0]=Rmap[1]; Rmap[17].adjs[1]=Rmap[7]; 
         Rmap[17].adjs[2]=Rmap[18];
         Rmap[18].adjs[0]=Rmap[8]; Rmap[18].adjs[1]=Rmap[17];
         Rmap[19].adjs[0]=Rmap[0]; Rmap[19].adjs[1]=Rmap[12];
     }
     
     int getCity(String city) {
         int found=-1;
         for(int i=0; i<20; i++){
             if(city.equals(Rmap[i].name)){
                 found = Rmap[i].num;
                 break;
             }
         }
         return found;
     }
     
     void BFS(int num, int desnum){
         ArrayList<Integer> othpath = new ArrayList<Integer>();
         ArrayList<Integer> op = new ArrayList<Integer>();
         while(tree.size()<20){
             for(int n:path){
                 op = BFST(n, desnum);
                 for(int m:op){
                     othpath.add(m);
                 }
                 if(fndDest==1)
                     break;
             }
             path.clear();
             Collections.sort(othpath);
             for(int pp: othpath){
                 path.add(pp);
                 tree.add(pp);
             }
             othpath.clear();
             if(fndDest==1){
                 printpath();
                 System.exit(0);
             }
         }
         printpath();
     }
  
     ArrayList<Integer> BFST(int num, int desnum){
         newpath.clear();
         fndDest=0;
         Node m;
         for(int i=0; i<Rmap[num].nb; i++){
             m=Rmap[num].adjs[i];
             if(m.exp == 0){
                 newpath.add(m.num);
                 m.exp=1;
                 if(m.num==desnum){
                     fndDest=1;
                     break;
                 }
             }
         }
         return newpath;
     }
     
     void DFS(int num, int desnum){
         int rem=-1, flag=0;
         while(tree.size()<20){
             flag=0;
             int n = path.get(path.size()-1);
             rem = DFST(n, desnum);
             if(rem != n){
                 flag=1;
             }
             else{
                 flag=-1;
             }
             if(flag==1){
                 path.add(rem);
             }
             else if(flag==-1){
                 path.remove(path.get(path.size()-1));
             }
         }
         printpath();
     }
     
     int DFST(int num, int desnum){
         int fndDest=0, fndNode=-1;
         Node m;
         for(int i=0; i<Rmap[num].nb; i++){
             m=Rmap[num].adjs[i];
             if(m.exp == 0){
                 fndNode = m.num;
                 m.exp = 1;
                 if(m.num == desnum){
                     fndDest = 1;
                 }
                 break;
             }
         }
         if(fndNode >= 0){
             tree.add(fndNode);
             if(fndDest == 1){
                 printpath();
                 System.exit(0);
             }
             return fndNode;
         }
         return num;
     }
     void printpath(){
         for(int l:tree){
             System.out.println(Rmap[l].name);
         }
     }
}
class Node{
    String name;
    int nb, num, exp;
    Node adjs[];
    public Node(String n, int noOfNeighbors){
        name = n; 
        nb = noOfNeighbors;
        exp = 0;
        adjs = new Node[noOfNeighbors];
    }
}