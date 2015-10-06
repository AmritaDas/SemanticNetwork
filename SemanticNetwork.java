/* Author: Amrita Das
 * Unity ID: adas5
*/

import java.io.*;

class Vertex{
	String name;
	Vertex(String n){
		name = n;
	}
}

class Edge{
	Vertex from;
	Vertex to;
	String name;
	Edge(Vertex f, String n, Vertex t){
		from = f;
		to = t;
		name = n;
	}
}

public class SemanticNetwork {

	Vertex[] vertices = new Vertex[5];
	Edge[] edges = new Edge[4];
	
	public static void main(String[] args) throws IOException {
		if(args.length!=1)
			System.out.println("Wrong format. ");
		else{
			SemanticNetwork sn = new SemanticNetwork();
			sn.createNetwork();
			String val = args[0];
			if(val.equalsIgnoreCase("listing."))
				sn.list(0);
			else{
				String w = sn.getWord(val, '(');
				int len = w.length();
				if(w.equalsIgnoreCase("value")){
					val = val.substring(len+1);
					System.out.println(sn.getValue(val));
				}
				else if(w.equalsIgnoreCase("listing")){
					val = val.substring(len+1);
					String e = sn.getWord(val, ')');
					len = e.length();
					val = val.substring(len+1);
					if(e.equalsIgnoreCase("value") && val.equalsIgnoreCase("."))
						sn.list(1);
				}
				else
					System.out.println("Wrong format. ");
			}
		}
	}
	
	String getWord(String word, char t){
		String w = "";
		int i = word.indexOf(t);
		if(i!=-1)
			w = word.substring(0, i);
		return w;
	}
	
	void createNetwork(){
		vertices[0] = new Vertex("david");
		vertices[1] = new Vertex("diabetics");
		vertices[2] = new Vertex("sugar");
		vertices[3] = new Vertex("candy");
		vertices[4] = new Vertex("snickers");
		
		edges[0] = new Edge(vertices[0], "isa", vertices[1]);
		edges[1] = new Edge(vertices[1], "shouldAvoid", vertices[2]);
		edges[2] = new Edge(vertices[3], "contains", vertices[2]);
		edges[3] = new Edge(vertices[4], "ako", vertices[3]);
	}
	
	void list(int a){
		
		System.out.println("value(A, B, C) :- ");
		System.out.println("\t edge(A, B, C).");
		
		System.out.println("value(A, C, D) :- ");
		System.out.println("\t edge(A, isa, B), ");
		System.out.println("\t value(B, C, D). ");
		
		System.out.println("value(A, C, D) :- ");
		System.out.println("\t edge(A, ako, B), ");
		System.out.println("\t value(B, C, D). ");
		
		System.out.println("value(B, shouldAvoid, A) :- ");
		System.out.println("\t value(A, contains, C), ");
		System.out.println("\t value(B, shouldAvoid, C). ");
		
		if(a==0){
			System.out.println();
			for(int o=0; o<4; o++)
				System.out.println("edge(" + edges[o].from.name + "," + edges[o].name + "," + edges[o].to.name + ").");
		}
		
		System.out.println("true");
	}
	
	boolean getValue(String val) throws IOException{
		boolean v = false;
		String node1, node2, slot;
		
		node1 = getWord(val, ',');
		int len = node1.length();
		node1 = node1.trim();
		val = val.substring(len+1);
		
		slot = getWord(val, ',');
		len = slot.length();
		slot = slot.trim();
		val = val.substring(len+1);
		
		node2 = getWord(val, ')');
		len = node2.length();
		node2 = node2.trim();
		val = val.substring(len+1);
		val = val.trim();
		
		if(val.length()>0 && val.charAt(0) == '.')
			if(node1.length()>0 && node2.length()>0 && slot.length()>0)
				v = findValue(node1, slot, node2);
			else
				System.out.print("Wrong format, hence ");
		else
			System.out.print("Wrong format, hence ");

		return v;
	}
	
	boolean findValue(String node1, String slot, String node2) throws IOException{
		boolean v = false;
		int n1p = 0, ep = 0, n2p = 0, sum; 
		for(int k=0; k<4; k++){
			if(slot.equalsIgnoreCase(edges[k].name)){
				ep = 1;
				break;
			}
		}
		for(int l=0; l<5; l++){
			if(node1.equalsIgnoreCase(vertices[l].name))
				n1p = 1;
			if(node2.equalsIgnoreCase(vertices[l].name))
				n2p = 1;
		}
		
		if(n1p==0 && (node1.charAt(0)<65 || node1.charAt(0)>90))
			return v;
		if(n2p==0 && (node2.charAt(0)<65 || node2.charAt(0)>90))
			return v;
		if(ep==0 && (slot.charAt(0)<65 || slot.charAt(0)>90))
			return v;
		
		sum = n1p + ep + n2p;
		
		if(sum==3)
			v = hasValue(node1, slot, node2, 0);
		else if(sum==0){
			String str = "";
			int i=0;
			do{
				if(i==4)
					break;
				System.out.println(node1 + " = " + edges[i].from.name + ",");
				System.out.println(slot + " = " + edges[i].name + ",");
				System.out.print(node2 + " = " + edges[i].to.name + " ");
				i++;
				BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
				str = br1.readLine();
			}while(str.equalsIgnoreCase(";"));
			
			if(str.equalsIgnoreCase(";")){
				for(int o=0; o<5; o++){
					for(int p=0; p<4; p++){
						for(int q=0; q<5; q++){
							if(vertices[o].name.equalsIgnoreCase(edges[p].from.name) && vertices[q].name.equalsIgnoreCase(edges[p].to.name))
								continue;
							if(hasValue(vertices[o].name, edges[p].name, vertices[q].name, 0)){
								System.out.println(node1 + " = " + vertices[o].name + ",");
								System.out.println(slot + " = " + edges[p].name + ",");
								System.out.print(node2 + " = " + vertices[q].name + " ");
	
								BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
								str = br1.readLine();
								if(!str.equalsIgnoreCase(";")){
									return v;
								}
							}
						}
					}
				}
			}
		}
		else if(sum==2){
			if(ep==0){
				for(int p=0; p<4; p++){
					if(hasValue(node1, edges[p].name, node2, 0)){
						System.out.print(slot + " = " + edges[p].name + " ");
					
						BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
						String str = br1.readLine();
						if(!str.equalsIgnoreCase(";")){
							return v;
						}
					}
				}
			}
			else if(n1p==0){
				for(int o=0; o<5; o++){
					if(hasValue(vertices[o].name, slot, node2, 0)){
						System.out.print(node1 + " = " + vertices[o].name + " ");
					
						BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
						String str = br1.readLine();
						if(!str.equalsIgnoreCase(";")){
							return v;
						}
					}
				}
			}
			else if(n2p==0){
				for(int q=0; q<5; q++){
					if(hasValue(node1, slot, vertices[q].name, 0)){
						System.out.print(node2 + " = " + vertices[q].name + " ");
					
						BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
						String str = br1.readLine();
						if(!str.equalsIgnoreCase(";")){
							return v;
						}
					}
				}
			}
		}
		else if(sum==1){
			if(ep==1){
				for(int o=0; o<5; o++){
					for(int q=0; q<5; q++){
						if(hasValue(vertices[o].name, slot, vertices[q].name, 0)){
							System.out.println(node1 + " = " + vertices[o].name + ",");
							System.out.print(node2 + " = " + vertices[q].name + " ");

							BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
							String str = br1.readLine();
							if(!str.equalsIgnoreCase(";")){
								return v;
							}
						}
					}
				}
			}
			else if(n1p==1){
				for(int p=0; p<4; p++){
					for(int q=0; q<5; q++){
						if(hasValue(node1, edges[p].name, vertices[q].name, 0)){
							System.out.println(slot + " = " + edges[p].name + ",");
							System.out.print(node2 + " = " + vertices[q].name + " ");

							BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
							String str = br1.readLine();
							if(!str.equalsIgnoreCase(";")){
								return v;
							}
						}
					}
				}
			}
			else if(n2p==1){
				for(int o=0; o<5; o++){
					for(int p=0; p<4; p++){
						if(hasValue(vertices[o].name, edges[p].name, node2, 0)){
							System.out.println(node1 + " = " + vertices[o].name + ",");
							System.out.print(slot + " = " + edges[p].name + " ");

							BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
							String str = br1.readLine();
							if(!str.equalsIgnoreCase(";")){
								return v;
							}
						}
					}
				}
			}
		}
		
		return v;
	}
	
	boolean hasValue(String node1, String slot, String node2, int cond){
		
		boolean value = false;
		
		for(int i=0; i<4; i++){
			Edge e = edges[i];
			
			//condition 1
			if(slot.compareToIgnoreCase(e.name)==0 && node1.compareToIgnoreCase(e.from.name)==0 && node2.compareToIgnoreCase(e.to.name)==0){
				value = true;
				return value;
			}
			
			//condition 2
			if(node1.compareToIgnoreCase(e.from.name)==0 && e.name.compareToIgnoreCase("isa")==0){
				value = hasValue(e.to.name, slot, node2, 0);
				if(value == true)
					return value;
			}
			
			//condition 3
			if(node1.compareToIgnoreCase(e.from.name)==0 && e.name.compareToIgnoreCase("ako")==0){
				value = hasValue(e.to.name, slot, node2, 0);
				if(value == true)
					return value;
			}
			
			//condition 4
			if(node1.compareToIgnoreCase(e.from.name)==0 && slot.compareToIgnoreCase("shouldAvoid")==0 && cond!=4){
				for(int j=0; j<5; j++){
					Vertex g = vertices[j];
					value = hasValue(node1, slot, g.name, 4);
					if(value == true){
						value = hasValue(node2, "contains", g.name, 4);
						if(value == true){
							return value;
						}
					}
				}
			}
		}
		return value;
	}
}
