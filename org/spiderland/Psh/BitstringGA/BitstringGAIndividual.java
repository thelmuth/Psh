package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class BitstringGAIndividual extends GAIndividual {
	private static final long serialVersionUID = 1L;

	ArrayList<Boolean> _bits;
	
	public BitstringGAIndividual(){
		_bits = new ArrayList<Boolean>();
	}
	
	public BitstringGAIndividual(ArrayList<Boolean> inBits){
		_bits = copyBits(inBits);
	}
	
	public void setBitstring(ArrayList<Boolean> inBits){
		_bits = copyBits(inBits);
	}
	
	public String toString(){
		String result = "";
		for(Boolean b : _bits){
			if(b){
				result += "1";
			}
			else {
				result += "0";
			}
		}
		
		return result;
	}
	
	@Override
	public GAIndividual clone() {
		return new BitstringGAIndividual(_bits);
	}

	private ArrayList<Boolean> copyBits(ArrayList<Boolean> inBits){
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		
		for(Boolean val : inBits){
			result.add(val.booleanValue());
		}
		
		return result;
	}
	
}
