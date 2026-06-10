import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;

import java.io.IOException;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorDeviceConfiguration;

/**
 * A basic Lanterna framework to make simple programs using Lanterna.
 * 
 * Has features to make a bordered panel, write messages to specific
 * locations in the window, have scrolling messages in the window, handle
 * some basic player key presses, and some debugging methods.
 * <p>
 * User can create multiple UI.Panel objects which are panels within the console. 
 * You can then write independently into each of these panels using that panel's
 * relative coordinate system and not worry about calculating the correct absolute 
 * screen postions. These Panel objects are not really like a typical windowing system and
 * are really meant for non-overlapping panels that are drawn in the console. There is
 * no depth-ordering or anything like that (so the last window created will be on top).
 * <p>
 * The UI supports multiple player inputs from the keyboard - nominally 2 players.
 * Each plalyer can designate keyboard characters that map to a command - like 'w' 
 * may map to 'up' for player 1 and the 'j' can map to 'left' for player 2. You can add
 * additional players, but this is probably not super useful as a single keyboard
 * does not really have room for that many players.
 * 
 * @version 1.01 - just changed the name of subclass to Panel (formerlly Window)
 * <p>
 * Old versions
 * <p>
 * @version 1.0 - added some color optoins and supported resizing of the terminal with better
 * refreshing of the panels upon resizing; Panels can be created and closed now
 * <p>
 * 0.99 - updated for Panels - creates a separate panel (screen subsection) for the screen instead
 * of using System.out; also the logger writes to System.out and a file now instead of
 * writing to the UI screen
 */
public class UI {

    DefaultTerminalFactory defaultTerminalFactory;
    Terminal terminal;
    Screen screen;
    boolean pauseMode = false;
    List<HashMap<Character,String>> playerKeyDef;
    List<List<Character>> playerMoves;
    ArrayList<Panel> panels = new ArrayList<Panel>();
    int cSize, rSize;

    /**
     * Creates a simple UI for a text based interactive program. Uses Lanterna
     * library and creates an even simpler front end API to write messages
     * and read input from the user.
     * 
     * console size will be a default sizing for a terminal console
     */
    public UI() {
        defaultTerminalFactory = new DefaultTerminalFactory();
        startUI();
    }

    /**
     * Crates a UI Console/Window of size col x row
     * @param col initial size of UI screen
     * @param row initial size of UI screen
     */
    public UI(int col, int row) {
        defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(col, row));
        startUI();
    }

    /**
     * Create Lanterna's terminal and screen. The UI object uses Lanterna's screen level
     * where the user writes to the screen (or backbuffer) and then when donw writing
     * to the screen, refreshes/updates the screen to the terminal that is being viewed
     */
    public void startUI() {
        try {
            terminal = defaultTerminalFactory.createTerminalEmulator();     // create a window instead of using the console
            // terminal = defaultTerminalFactory.createTerminal();
            screen = new TerminalScreen(terminal);

            screen.startScreen();
            screen.setCursorPosition(null);
            cSize = terminal.getTerminalSize().getColumns();
            rSize = terminal.getTerminalSize().getRows();
        }
        catch(IOException e) {
            stopUI();
            e.printStackTrace();
        }

        playerKeyDef = new ArrayList<HashMap<Character,String>>();
        playerKeyDef.add(new HashMap<Character, String>());
        playerKeyDef.add(new HashMap<Character, String>());

        playerMoves = new ArrayList<List<Character>>();
        playerMoves.add(new LinkedList<Character>());
        playerMoves.add(new LinkedList<Character>());
    }

    /**
     * Stops the UI and returns the console to be used with normal System.out / System.in
     */
    public void stopUI() {
        try {
            screen.stopScreen();
        }
        catch(IOException e) {
            stopUI();
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the screen with what has been written to the screen backbuffer
     */
    public void refresh() {
        try {
            screen.doResizeIfNecessary();
            TerminalSize s = terminal.getTerminalSize();
            if (cSize != s.getColumns() || rSize != s.getRows()) {
                for (Panel w : panels) {
                    w.refreshPanel();
                }
                cSize = s.getColumns();
                rSize = s.getRows();
            }
            screen.refresh();
        }
        catch(IOException e) {
            stopUI();
            e.printStackTrace();
        }
    }

    /**
     * Just refreshes the screen and not any of the sub-panels.
     */
    public void refreshScreen() {
        try {
            screen.refresh();
        }
        catch(IOException e) {
            stopUI();
            e.printStackTrace();
        }
    }

    /**
     * Clears the back buffer
     */
    public void clear() {
        screen.clear();
    }

    /**
     * Writes msg to the backbuffer at (col, row).
     * 
     * Note: refresh() must be called to actually see the message on the terminal
     * @param col x location of beginning of message
     * @param row y location of beginning of message
     * @param msg message to write to the back buffer
     */
    public void setString(int col, int row, String msg) {
        if (msg == null) msg = "null";
        TextGraphics g = screen.newTextGraphics();
        g.putString(col, row, msg);
    }

    /**
     * Writes msg to the backbuffer at (col, row) with a specific color.
     * @param col x location of beginning of message
     * @param row y location of beginning of message
     * @param msg message to write to the back buffer
     * @param color color of the text ex. TextColor.ANSI.RED
     */
    public void setString(int col, int row, String msg, TextColor color) {
        if (msg == null) msg = "null";
        TextGraphics g = screen.newTextGraphics();
        // set color of text
        g.setForegroundColor(color);
        g.putString(col, row, msg);
        // reset color of text
        g.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    /**
     * Waits...
     * @param msec number of milliseconds to wait
     */
    public void wait(int msec) {
        try {
            Thread.sleep(msec);
        }
        catch(InterruptedException ignore) {
            ;
        }
    }

    /**
     * Get the next keyboard character that was pressed - if nothing has been pressed
     * it will return null
     * @return character that was pressed - null if nothing was pressed
     */
    public char getKeyPress() {
        KeyStroke key = null;
        char ch = 0;

        try {
            key = screen.pollInput();
            if (key == null) {
                return ch;
            }
        }
        catch(IOException e) {
            stopUI();
            e.printStackTrace();
        }

        return key.getCharacter();
    }

    /**
     * Add/change a character command for playerNum
     * @param playerNum from 0 to maximum number of players - 1
     * @param ch keyboard character - ex. 'q' or 'o'
     * @param cmd command string - ex. 'quit' or 'open'
     * @return true normally, but false if playerNum is too large
     */
    public boolean setPlayerKeyMap(int playerNum, char ch, String cmd) {
        if (playerNum >= playerKeyDef.size()) return false;
        playerKeyDef.get(playerNum).put(ch, cmd);
        return true;
    }

    /**
     * Get the next player move command for the playerNum indicated.
     * Uses the playerKeyDef that maps a key press to a command string
     * like 'w' -> 'up'.
     * @param playerNum player number
     * @return String command according to the key pressed by the player
     */
    public String getPlayerMove(int playerNum) {

        // get any outstanding key presses in the queue before returning command
        char ch = getKeyPress();
        while (ch != 0) {
            if (pauseMode && ch == ' ') {
                waitForKey(ch);
            }
            // check ch in each of the playerMoves queues
            for (int p = 0; p < playerKeyDef.size(); p++) {
                HashMap<Character, String> map = playerKeyDef.get(p);
                // if the ch is a command of playerNum
                if (map.keySet().contains(ch)) {
                    // add ch to playerMove queue if not a repeated press
                    List<Character> moves = playerMoves.get(p);
                    if (moves.size() == 0 || moves.get(0) != ch) {
                        moves.add(0, ch);
                    }
                }
            }
            ch = getKeyPress();
        }

        // Once keyboard presses have been cleared, get key press from
        // playerMoves queue and look up the command name
        HashMap<Character, String> map = playerKeyDef.get(playerNum);
        List<Character> moves = playerMoves.get(playerNum);
        if (moves.size() == 0) return null;
        return map.get(moves.remove(moves.size()-1));
    }

    /**
     * Adds a player to the UI. The UI starts with 2 upon construction. Calling this
     * method adds an additional player. 
     * @return the max number of players
     */
    public int addPlayer() {
        playerKeyDef.add(new HashMap<Character, String>());
        playerMoves.add(new LinkedList<Character>());
        return playerMoves.size();
    }

    /**
     * Acts like the input() in python where it returns the line that the 
     * user types - it echoes all the characters pressed and handles the
     * delete key.
     * 
     * Not sure how it handles wierd stuff like control/alt key presses...
     * 
     * @param col the location of the place where the user starts to type
     * @param row the location of the place where the user starts to type
     * @return the line that was typed by the user
     */
    public String input(int col, int row) {
        KeyStroke key;
        StringBuilder s = new StringBuilder();
        try {
            screen.setCursorPosition(new TerminalPosition(col, row));
            refresh();
            key = screen.readInput();
            while (key.getKeyType() != KeyType.Enter) {
                char ch = key.getCharacter();
                if (key.getKeyType() == KeyType.Backspace)  {
                    if (s.length() > 0) {
                        s.deleteCharAt(s.length()-1);
                        TextGraphics g = screen.newTextGraphics();
                        col--;
                        g.setCharacter(col, row, ' ');
                        screen.setCursorPosition(new TerminalPosition(col, row));
                    }
                } else if (Character.isDefined(ch)) {
                    s.append(ch);
                    TextGraphics g = screen.newTextGraphics();
                    g.setCharacter(col, row, ch);
                    col++;
                    screen.setCursorPosition(new TerminalPosition(col, row));
                }
                refresh();
                key = screen.readInput();
            }
        }
        catch(IOException e) {
            stopUI();
            e.printStackTrace();
        }
        screen.setCursorPosition(null);
        return s.toString();
    }

    /**
     * Works like the python input() and places the prompt at the col, row specified
     * @param prompt customize the prompt for the user
     * @param col column position of prompt
     * @param row row position of prompt
     * @return the full line typed in by the user
     */
    public String input(String prompt, int col, int row) {
        TextGraphics g = screen.newTextGraphics();
        g.putString(col, row, prompt);
        refresh();

        return input(col + prompt.length(), row);
    }

    /**
     * Loops until a key has been pressed
     * @param key wait for this key to be pressed - like ' ' 
     */
    public void waitForKey(char key) {
        while (true) {
            char ch = getKeyPress();
            if (ch == key) {
                break;
            } 
            wait(100);
        }
    }

    /**
     * Turn on/off pause mode - when on, the UI will pause when the space bar is pressed
     * @param pause true to turn on pause mode, false to turn off pause mode
     */
    public void setPauseMode(boolean pause) {
        pauseMode = pause;
    }

    /**
     * Get the current state of the pause mode
     * @return true if pause mode is on, false if pause mode is off
     */
    public boolean getPauseMode() {
        return pauseMode;
    }

    /**
     * Makes a UI.Panel in the UI.
     * The Panel is a portion of the console and can be used to print messages and draw characters.
     * It has a 1-character border and printing and placing characters are done INSIDE the border.
     * So when creating a Panel of size 20 wide x 10 high the text window will a size of 18 wide x 8 high
     * <p>
     * These are not windows in the traditional sense of a windowing system - they are just a way to
     * draw text in a specific area of the console without having to deal with the absolute screen
     * coordinates. Instead, you can use the relative coordinates of the window to draw text.
     * <p>
     * There is no depth ordering or anything like that - the last panel created will be on top. When
     * the screen is resized, the console will be resized and all the panels will be redrawn in the order
     * they were created.
     * <p>
     * Best practice use of these Panel objects is to create them and not to have any overlap between
     * the panels. If you need to have overlapping panels, you will need to manage carefully the order
     * in which the panels are created and the order in which they are refreshed.
     * 
     * @param col top left corner of the window
     * @param row top left corner of the window
     * @param width size of window INCLUDING left/right border
     * @param height size of window INCLUDING top/bottom border
     * @return a UI.Panel that can then be used to print/draw characters onto the window
     */
    public Panel makePanel(int col, int row, int width, int height) {
        // return screen.newTextGraphics().newTextGraphics(new TerminalPosition(col, row), new TerminalSize(colsize, rowsize));
        Panel canvas = new Panel(screen.newTextGraphics(), col, row, width, height);
        panels.add(canvas);
        return canvas;
    }

    /**
     * A nexted class to create window within the UI.
     * The message window will have a border around it that is drawn upon creation.
     * The text box will be INSIDE this border - so when creating a Message Panel of size 20 wide x 10 high
     * the text portion will be inside and have a size of 18 wide and 8 high.
     */
    public class Panel {
        TextGraphics canvas;
        TextImage textbox;

        int cursorCol;
        int cursorRow;
        final TerminalPosition TEXT_BOX_POS = new TerminalPosition(1, 1);
        int ROW_MAX;

        /**
         * Creates a Panel to write messages and display text
         * @param s TextGrahpics screen that Panel draws on
         * @param col top-left location of Panel with respect to the screen
         * @param row top-left location of Panel with respect to the screen
         * @param width width of Panel - including the border on left and right
         * @param height height of Panel - including the border on top and bottom
         */
        public Panel(TextGraphics s, int col, int row, int width, int height) {
            TerminalPosition pos = new TerminalPosition(col, row);
            TerminalSize size = new TerminalSize(width, height);
            canvas = s.newTextGraphics(pos, size);

            textbox = new BasicTextImage(width-2, height-2);
            ROW_MAX = textbox.getSize().getRows() - 1;
            resetPanel();
            refresh();
        }

        /**
         * Clears panels, redraws borders, resets the cursor back to 0,0 for printing in the window.
         * Affects the backbuffer and needs refresh().
         */
        public void resetPanel() {
            drawBorder();
            resetTextBox();
            refreshScreen();
        }

        /**
         * Clears the textbox of the window - affects the back buffer and needs a refresh()
         */
        public void resetTextBox() {
            textbox.setAll(new TextCharacter(' '));
            cursorCol = 0;
            cursorRow = 0;
            refreshScreen();
        }

        /**
         * Refreshes the panel - mostly designed for use when resizing the overall terminal
         * and the individual panels/borders need to be redrawn
         * 
         * Affects the backbuffer and needs screen.refresh().
         */
        public void refreshPanel() {
            drawBorder();
            refreshTextBox();
        }
    
        /**
         * Draws whatever was written in the textbox to the console
         * 
         * Affects the backbuffer and needs screen.refresh().
         */
        public void refreshTextBox() {
            canvas.drawImage(TEXT_BOX_POS, textbox);
            refreshScreen();
        }

        /**
         * Get the height of the text window (not including border)
         * @return height of the window
         */
        public int getHeight() {
            return textbox.getSize().getRows();
        }

        /**
         * Get the width of the text window (not including border)
         * @return width of the window
         */
        public int getWidth() {
            return textbox.getSize().getColumns();
        }

        /**
         * Places the characters of the string onto the textbox (inner portion of the window).
         * Must refresh() to see the characters as it just gets drawn onto the backbuffer
         * @param col 0 is left column / getWidth()-1 is the right column
         * @param row 0 is top row / getHeight()-1 is the bottom row
         * @param str does not handle special characters like \n (just treats control characters as space)
         */
        public void setString(int col, int row, String str) {
            if (str == null) str = "null";
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (!Character.isISOControl(ch)) {
                    textbox.setCharacterAt(col+i, row, new TextCharacter(ch));
                }
            }
        }

        /**
         * A colored version of setString()
         * @param col 0 is left column / getWidth()-1 is the right column
         * @param row 0 is top row / getHeight()-1 is the bottom row
         * @param str does not handle special characters like \n (just treats control characters as space)
         * @param color color for the string that is written (ex. TextColor.ANSI.RED)
         */
        public void setString(int col, int row, String str, TextColor color) {
            if (str == null) str = "null";
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (!Character.isISOControl(ch)) {
                    textbox.setCharacterAt(col+i, row, new TextCharacter(ch, color, TextColor.ANSI.DEFAULT));
                }
            }
        }

        /**
         * Draws window border - affects the back buffer and needs refresh()
         */
        public void drawBorder() {
            int col = canvas.getSize().getColumns();
            int row = canvas.getSize().getRows();

            canvas.setCharacter(0, 0, '\u250c');            // upper left corner
            canvas.setCharacter(col-1, 0, '\u2510');        // upper right corner
            canvas.setCharacter(0, row-1, '\u2514');        // lower left corner
            canvas.setCharacter(col-1, row-1, '\u2518');    // lower right corner

            canvas.drawLine(new TerminalPosition(1, 0), new TerminalPosition(col-2, 0), '\u2500');
            canvas.drawLine(new TerminalPosition(1, row-1), new TerminalPosition(col-2, row-1), '\u2500');
            canvas.drawLine(new TerminalPosition(0, 1), new TerminalPosition(0, row-2), '\u2502');
            canvas.drawLine(new TerminalPosition(col-1, 1), new TerminalPosition(col-1, row-2), '\u2502');
        }

        /**
         * Prints out a line to the window.
         * Starts at the top line and then prints lines down.
         * It will automatically scroll when it gets to the bottom of the sub-screen.
         * 
         * @param line should be a single line that is less than the width of the window; it does not
         * handle \n inside of the string - so it should strictly a single line w/o special characters
         */
        public void println(String line) {
            // scroll up 1 line if at the bottom of the textbox
            if (cursorRow > ROW_MAX) {
                textbox.scrollLines(0, ROW_MAX, 1);
                cursorRow--;
            }
            setString(cursorCol, cursorRow, line);
            if (cursorRow <= ROW_MAX) {
                cursorRow++;
            }
            canvas.drawImage(TEXT_BOX_POS, textbox);
            refresh();
        }

        /**
         * A colored version of println(). Prints out a line to the window with a specific color.
         * @param line line to write to the window at the current cursor point, scrolling if necessary
         * @param color color of the text ex. TextColor.ANSI.RED
         */
        public void println(String line, TextColor color) {
            // scroll up 1 line if at the bottom of the textbox
            if (cursorRow > ROW_MAX) {
                textbox.scrollLines(0, ROW_MAX, 1);
                cursorRow--;
            }
            setString(cursorCol, cursorRow, line, color);
            if (cursorRow <= ROW_MAX) {
                cursorRow++;
            }
            canvas.drawImage(TEXT_BOX_POS, textbox);
            refresh();
        }

        /**
         * Acts like the input() in python where it returns the line that the 
         * user types - it echoes all the characters pressed and handles the
         * delete key.
         * 
         * Not sure how it handles wierd stuff like control/alt key presses...
         * 
         * @param prompt the prompt message
         * @return line that the user has typed in
         */
        public String input(String prompt) {
            final char CURSOR = '\u2588';
            println(prompt + CURSOR);  // print out prompt with a cursor unicode

            // Draw the cursor where user types
            cursorRow--;
            cursorCol = prompt.length();

            KeyStroke key;
            StringBuilder s = new StringBuilder();
            try {
                refresh();
                key = screen.readInput();
                while (key.getKeyType() != KeyType.Enter) {
                    char ch = key.getCharacter();
                    if (key.getKeyType() == KeyType.Backspace)  {
                        if (s.length() > 0) {
                            s.deleteCharAt(s.length()-1);
                            textbox.setCharacterAt(cursorCol, cursorRow, new TextCharacter(' '));
                            cursorCol--;
                            textbox.setCharacterAt(cursorCol, cursorRow, new TextCharacter(CURSOR));
                        }
                    } else if (Character.isDefined(ch)) {
                        s.append(ch);
                        textbox.setCharacterAt(cursorCol, cursorRow, new TextCharacter(ch));
                        cursorCol++;
                        textbox.setCharacterAt(cursorCol, cursorRow, new TextCharacter(CURSOR));
                    }
                    canvas.drawImage(TEXT_BOX_POS, textbox);
                    refresh();
                    key = screen.readInput();
                }
            }
            catch(IOException e) {
                stopUI();
                e.printStackTrace();
            }
            // after they press the Enter, remove cursor and update the row/col of cursor
            textbox.setCharacterAt(cursorCol, cursorRow, new TextCharacter(' '));
            canvas.drawImage(TEXT_BOX_POS, textbox);
            refresh();
            cursorCol = 0;
            cursorRow++;
            return s.toString();
        }

        /**
        * Removes the Panel from the overall list of panel being
        * displayed in the terminal
        */
        public void close() {
            panels.remove(this);
        }
    
    }

    PrintWriter log = null;
    /**
     * Write log message to System.out and also writes it to "log.txt" file
     * @param s debug message to display
     */
    public void writeLog(String s) {
        if (log == null) {
            try {
                FileWriter f = new FileWriter("log.txt");
                log = new PrintWriter(f);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(s);
        log.println(s);
        log.flush();
    }
}
