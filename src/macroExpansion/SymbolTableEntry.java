package macroExpansion;

import java.util.ArrayList;

public class SymbolTableEntry {
	
	String macroName;
	String definition;
	int argumentCount;
	ArrayList<String> arguments = new ArrayList<String>();
	public SymbolTableEntry(String macroName, String definition, int argumentCount, ArrayList<String> arguments) {
		super();
		this.macroName = macroName;
		this.definition = definition;
		this.argumentCount = argumentCount;
		this.arguments = arguments;
	}	
}