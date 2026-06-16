import java.util.Random;
public class Competitors {
    // This class handles the positions of the four competitors that are random 
    Random rand = new Random();
    private int[] positions;

    public Competitors(){
        this.positions = new int[320];
        moves();
    }
    
    public int[] getpositions(){
        return positions;
    }
    public void moves(){
        positions[0] = 0;
        for(int i = 1; i<320; i++){
            if(positions[i-1] > 1600)
                positions[i] = positions[i-1];
            else{
                int plus = rand.nextInt(5,40);
                if(positions[i-1] + plus >1600)
                    positions[i] = 1600;
                else{
                    positions[i] = positions[i-1] + plus;
                }

            }
        }
    }
}
