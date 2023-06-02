package timetable;

import java.util.Arrays;

class Ring {
    
	private int[] moduli; // mods array
    private int[] values; // current value of rings array
    
    public Ring(int[] moduli) {
        this.moduli = moduli;
        this.values = new int[moduli.length];
    }
    
    public void turn() {
        int i = 0;
        while (i < moduli.length) {
        	//Makes the mod sum
            values[i] = (values[i] + 1) % moduli[i];
            if (values[i] != 0) { //If not a complete turn, then it goes out to not continue 
                break;
            }
            i++;
        }
    }
    
    public int[] getValue() {
        return values;
    }
    
    @Override
	public String toString() {
		return Arrays.toString(values);
	}

}
