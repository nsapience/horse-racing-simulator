import java.util.Random;

import com.googlecode.lanterna.TextColor;
public class Horse {
    private String[] physTraits;
    private int[] numTraits;
    Random rand = new Random();

    public Horse(){
        physTraits = new String[3];
        physTraits();
        numTraits = new int[3];
        numTraits();
    }

    public String random(String[] stringList){
        //create a random between 0 and length of stringList-1 inclusive
        int randomNum = rand.nextInt(stringList.length);
        return stringList[randomNum];
    }

    public String name(){
        /* creates a random name concatinating random adjective + noun */
        //use random() above
        
        String name = "";
        String[] adjectives = {"Scarlet", "Golden", "Funny", "Smart", "Silly", "Esctatic", "Calm", "Super", "Interesting", "Fair", "Great", "Brainy", "Bright", "Enlightened"};
        String[] nouns = {"Pharaoh","Kingdom","Cavier","Ruler","Glory","Maverick","Admiral","Fleet","Genesis","Flash","Classic","Tempo","Magic","Wonder","Star","Halo"};
        int adjName = rand.nextInt(adjectives.length);
        int nounName = rand.nextInt(nouns.length);

        // make the horseName using one adjective and one noun to resemble real funky horse race names
        name = adjectives[adjName] + " " + nouns[nounName];
        return name;
    }

    public void physTraits(){
        /*
        Traits - color, gender

        1. physTraits[1] = RNG color
            random choice from colors
        2. physTraits[2] = RNG gender
            random choice from genders
        3. physTraits[0] = random name using name()
        */

        //use random() method
        String[] colors = {"Bay", "Chestnut", "Gray", "Brown"};
        String[] genders = {"male", "female"};

        //physTraits[0] = random name using name() method
        int randColor = rand.nextInt(colors.length);
        int randGender = rand.nextInt(genders.length);

        physTraits[0] = name();
        physTraits[1] = colors[randColor];
        physTraits[2] = genders[randGender];

    }
    public TextColor getHorseTextColor() {
        String color = getColor();

        if (color.equals("Bay")) {
            return TextColor.ANSI.RED;
        }
        else if (color.equals("Chestnut")) {
            return TextColor.ANSI.YELLOW;
        }
        else if (color.equals("Gray")) {
            return TextColor.ANSI.WHITE;
        }
        else {
            return TextColor.ANSI.MAGENTA;
        }
    }
    

    public void numTraits(){
        /*
        Traits - speed, strength, stamina
        1. RNG speed (1-10)
        2. RNG strength (1-10)
        3. RNG stamina (1-10)
        */
        int speed = rand.nextInt(10)+1;
        int strength = rand.nextInt(10)+1;
        int stamina = rand.nextInt(10)+1;

        numTraits[0] = speed; 
        numTraits[1] = strength; 
        numTraits[2] = stamina; 


    }
    
    public String getName() { return physTraits[0]; }
    public String getColor() { return physTraits[1]; }
    public String getGender() { return physTraits[2]; }
    public int getSpeed() { return numTraits[0]; } 
    public int getStrength(){ return numTraits[1]; }
    public int getStamina() { return numTraits[2]; }

    public static void main(String[] args) {
        Horse horseName = new Horse();

    }
    public int trainStat(String focus, int hours, int intensity){
        // simple formula to calculate improvement based on the amount of hours trianed
        int improve = hours/4 + intensity;

        if (focus.equals("1")){
            numTraits[1] += improve;
            //Cap the trait rating at 10
            if (numTraits[1] > 10){
                numTraits[1] = 10;
            }
        }
        else if (focus.equals("2")){
            numTraits[2] += improve;
            //Cap the trait rating at 10
            if (numTraits[2] > 10){
                numTraits[2] = 10;
            }
        }
        else{
            numTraits[0] += improve;
            //Cap the trait rating at 10
            if (numTraits[0] > 10){
                numTraits[0] = 10;
            }
        }
        return improve;

    }

}
