package macroExpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.*;

public class Expand {
	
	public static ArrayList<SymbolTableEntry> symbolTable = new ArrayList<SymbolTableEntry>();	// for holding macro definition

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		// required data-structures 
		
		// taking input from file
		FileInputStream fin = new FileInputStream("C:\\Users\\nishant\\workspace\\macroExpansion\\src\\macroExpansion\\code.c");
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		// file output stream for log file
		FileOutputStream login = new FileOutputStream("C:\\Users\\nishant\\workspace\\macroExpansion\\src\\macroExpansion\\log.txt");
		// file output stream for output file
		FileOutputStream fout = new FileOutputStream("C:\\Users\\nishant\\workspace\\macroExpansion\\src\\macroExpansion\\ex_code.c");
		
		String strLine;
		int lineNumber=1;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null){
			lineNumber++;
			if (strLine.contains("#define")){
				byte b[] = ("macro definition detected on line number : " + lineNumber+"\n").getBytes();
				login.write(b);
				b = ("adding macro to symbol table\n").getBytes();
				login.write(b);
				String macrolist[] = new String[3];
				macrolist = strLine.split(" ");
				b = ("macro name is : "+macrolist[1]+"\nmacro definition is : "+macrolist[2]+"\n").getBytes();
				login.write(b);
				// adding information about the macro in the symbol table
				
				String macrodef = macrolist[2];
				String macroname = getmacroname(macrolist[1]);
				int num_args = getNumArgs(macrolist[1]);
				ArrayList<String> arglist = getArgList(macrolist[1]);
				symbolTable.add(new SymbolTableEntry(macroname,macrodef,num_args,arglist));
			}
		}
		fin.getChannel().position(0);
		br = new BufferedReader(new InputStreamReader(fin));
		lineNumber=1;
		while((strLine = br.readLine()) != null){
			strLine = strLine.trim();
			if(strLine.startsWith("#define")){
				byte b[] = ("macro definition was on line number : " + lineNumber+"\n").getBytes();
				login.write(b);
				fout.write((strLine+"\n").getBytes());
			}
			else{
				ArrayList<String> wordSet = splitLineToWords(strLine);
				if(wordSet !=null){
					Iterator<String> it1 = wordSet.iterator();
					int macrocount=0;
					while(it1.hasNext()){
						String tmpword = it1.next();
						if(isMacro(tmpword)>=0){
							macrocount++;
							byte b[] = ("macro detected on line number : " + lineNumber+"\n").getBytes();
							login.write(b);
							b = ("macro was found to be "+tmpword+"\n").getBytes();
							login.write(b);
							login.write(("Expanding macro\n").getBytes());
							tmpword = macroExpand(tmpword,isMacro(tmpword));
							login.write(("macro expanded successfully\n").getBytes());
							login.write(("macro was expanded to  : "+tmpword+"\n").getBytes());
							fout.write(tmpword.getBytes());
						}
						else{
							fout.write(tmpword.getBytes());
						}
					}
					if(macrocount == 0){
						login.write(("neither macro definition nor macro was on line number : "+ lineNumber+"\n").getBytes());
					}
					fout.write("\n".getBytes());
				}
			}
			lineNumber++;
		}
		fin.close();
		login.close();
		fout.close();
		print();
		System.out.println("Macro Expansion Done");
	}
	
	public static int isMacro(String text ){
		int ret=-1;
		Iterator<SymbolTableEntry> it = symbolTable.iterator();
		int flag=0;
		while(it.hasNext() && flag==0){
			SymbolTableEntry tmp = it.next();
			if(tmp.argumentCount==0){
				if(text.equals(tmp.macroName)){
					ret = symbolTable.indexOf(tmp);
					flag=1;
				}
			}
			else{
				if(text.contains(tmp.macroName+"(")){
					ret = symbolTable.indexOf(tmp);
					flag=1;
				}
			}
		}
		return ret;
	}
	
	public static Boolean isAlphaNum(char ch){
		if( (ch>='a' && ch <='z') ||(ch>='A' && ch<='Z') || (ch>='0' && ch<='9'))
			return true;
		return false;
	}
	public static Boolean isNonAlphaNum(char ch){
		if( (ch>='a' && ch <='z') ||(ch>='A' && ch<='Z') || (ch>='0' && ch<='9'))
			return false;
		return true;
	}
	public static String getmacroname(String text){
		return text.contains("(")?text.substring(0,text.indexOf("(")):text;
	}
	public static int getNumArgs(String text){
		return text.contains("(")?text.substring(text.indexOf("(")+1,text.length()-1).split(",").length:0;
	}
	public static ArrayList<String> getArgList(String text){
		if(text.contains("(")){
			String retarr[] =  text.substring(text.indexOf("(")+1,text.length()-1).split(",");
			ArrayList<String> ret = new ArrayList<String>(Arrays.asList(retarr));
			return ret;
		}
		
		return null;
	}
	
	public static String macroExpand(String text, int index){
		String ret=text;
        String param[] = new String[10];
        int pcount = 0;
        if(text.contains("(")){ // function like macro
        	int index1 = text.indexOf("(");
        	String sub_macro = text.substring(index1+1);
        	int bcount = 0;
        	int lcount=0;
        	int ps_count=0, pe_count=0;
        	while (lcount<sub_macro.length()){
        		if (sub_macro.charAt(lcount) == '('){
        			bcount++;
        		}
        		else if(sub_macro.charAt(lcount) == ')' && bcount>0){
        			bcount--;
        		}
        		if((sub_macro.charAt(lcount) ==',' && bcount==0 ) || (sub_macro.charAt(lcount) ==')' && lcount==sub_macro.length()-1 )){
        			param[pcount] = sub_macro.substring(ps_count,pe_count);
        			int imacro = isMacro(param[pcount]);
        			if (imacro>=0){
        				param[pcount] = macroExpand(param[pcount], imacro);
        			}
        			pe_count = lcount;
        			ps_count = lcount+1;
        			pcount++;
        		}
        		pe_count++;
        		lcount++;
        	}
        	int rindex=0;
    		int sindex=0;
    		String tmp = symbolTable.get(index).definition;
    		while (sindex<symbolTable.get(index).argumentCount){
    			tmp = tmp.replace(symbolTable.get(index).arguments.get(rindex),param[sindex]);
    			rindex++;
    			sindex++;
    		}
    		ret = tmp;
        }else{ // normal macro
        ret = symbolTable.get(index).definition;
        }
        
        if(isMacro(ret)>=0){
        	ret = macroExpand(ret, isMacro(ret));
        }
        return ret;
	}
	public static ArrayList<String> splitLineToWords(String text){
		ArrayList<String> wordList = new ArrayList<String>();
		int i=0;
		int index1=0,index2=0;
		int bcount=0;
		for (i=0;i<text.length();i++){
			if(text.charAt(i)=='('){
				bcount++;
				i++;
				index2++;
				while(bcount!=0){
					if(text.charAt(i)=='(')
						bcount++;
					else if (text.charAt(i)==')')
						bcount--;
					i++;
					index2++;
				}
				i--;
				
			}
			else if(isNonAlphaNum(text.charAt(i))){
				if (index2>index1){
					wordList.add(text.substring(index1, index2));
				}
				wordList.add(text.substring(index2, index2+1));
				index1=index2+1;
				index2++;
			}
			else if( isAlphaNum(text.charAt(i))){
				index2++;
			}
		}
		return wordList;
	}
	
	public static void print(){
		System.out.println("printing symbol table");
		Iterator<SymbolTableEntry> it = symbolTable.iterator();
		while(it.hasNext()){
			SymbolTableEntry s = it.next();
			System.out.println("macro name : "+s.macroName+"   macro arg count : "+s.argumentCount);
			System.out.println("marco definition : "+s.definition);
		}
		
	}
}