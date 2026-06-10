import com.googlecode.lanterna.TextColor;

public class UITester {

    // Basic message writing and clearing
    public static void testWriteMsg(UI ui) {
        ui.setString(0, 0, "hello world");
        ui.setString(10, 5, "waiting for 2 seconds....");
        ui.refresh();

        ui.wait(2000);
        ui.clear();
    
    }

    // Top level input()
    public static void testInput(UI ui) {
        // Input command
        String s = ui.input("Enter your name: ", 2,3);
        ui.setString(2,4, s);
        ui.refresh();
    }

    public static void testKeyPressWait(UI ui) {
        /* Wait for a key press */
        ui.setString(1,10, "Press q to continue");
        ui.refresh();
        ui.waitForKey('q');
    }

    public static void testMoveStar(UI ui) {
        ui.clear();
        /* Move a little '*' around the screen - press q to quit */
        int x = 10;
        int y = 5;
        while (true) {
            ui.wait(100);
            char ch = ui.getKeyPress();
            if (ch == 'w') y--;
            else if (ch == 'a') x--;
            else if (ch == 's') y++;
            else if (ch == 'd') x++;
            else if (ch == 'q') break;
    
            ui.clear();
            ui.setString(0, 0, "Move the * with wasd and q to quit");
            ui.setString(x, y, "*");
            ui.refresh();
        }
    }

    public static UI.Panel testPanel(UI ui, int c, int r, int w, int h) {
        UI.Panel win = ui.makePanel(c, r, w, h);
        win.setString(3, 5, "hello\nworld");
        win.println("hello worldhello");
        String s = win.input("Enter your name: ");
        win.println(s);
        win.close();
        return win;
        // s = win.input("Enter your name: ");
        // win.println(s);
        // s = win.input("Enter your name: ");
        // win.println(s);
        // s = win.input("Enter your name: ");
        // win.println(s);
        // s = win.input("Enter your name: ");
        // win.println(s);

        // win.println("Hello World 1"); ui.wait(100); ui.refresh();
        // win.println("Hello World 2"); ui.wait(100); ui.refresh();
        // win.println("Hello World 3"); ui.wait(100); ui.refresh();
        // win.println("Hello World 4"); ui.wait(100); ui.refresh();
        // win.println("Hello World 5"); ui.wait(100); ui.refresh();
        // win.println("Hello World 6"); ui.wait(100); ui.refresh();
        // win.println("Hello World 7"); ui.wait(100); ui.refresh();
        // win.println("Hello World 8"); ui.wait(100); ui.refresh();
        // win.println("Hello World 9"); ui.wait(100); ui.refresh();
        // win.println("Hello World 10"); ui.wait(100); ui.refresh();
        // win.resetTextBox(); ui.refresh();
        // win.println("Hello World 1"); ui.wait(100); ui.refresh();
        // win.println("Hello World 2"); ui.wait(100); ui.refresh();
        // win.println("Hello World 3"); ui.wait(100); ui.refresh();
        // win.println("Hello World 4"); ui.wait(100); ui.refresh();
        // win.println("Hello World 5"); ui.wait(100); ui.refresh();
        // win.println("Hello World 6"); ui.wait(100); ui.refresh();
        // win.println("Hello World 7"); ui.wait(100); ui.refresh();
        // win.println("Hello World 8"); ui.wait(100); ui.refresh();
        // win.println("Hello World 9"); ui.wait(100); ui.refresh();
        // win.println("Hello World 10"); ui.wait(100); ui.refresh();
    }

    // Test player input feature of UI
    public static void testPlayerInputs(UI ui) {
        ui.clear();

        ui.setString(0, 0, "Player move test - press awsd/ijkl/q");
        ui.refresh();

        ui.setPlayerKeyMap(0, 'a', "left");
        ui.setPlayerKeyMap(0, 'w', "up");
        ui.setPlayerKeyMap(0, 's', "down");
        ui.setPlayerKeyMap(0, 'd', "right");
        ui.setPlayerKeyMap(0, 'q', "quit");
   
        ui.setPlayerKeyMap(1, 'j', "left");
        ui.setPlayerKeyMap(1, 'i', "up");
        ui.setPlayerKeyMap(1, 'k', "down");
        ui.setPlayerKeyMap(1, 'l', "right");
        ui.setPlayerKeyMap(1, 'q', "quit");

        UI.Panel win = ui.makePanel(0, 5, 40, 3);
        while (true) {
            String cmd = ui.getPlayerMove(0);
            win.resetTextBox();
            win.setString(0, 0, cmd);
            if (cmd != null && cmd.equals("quit")) break;
            cmd = ui.getPlayerMove(1);
            win.setString(30, 0, cmd);
            if (cmd != null && cmd.equals("quit")) break;
            win.refreshTextBox();
            ui.wait(200);
        }
        win.close();
    }

    public static void testLogger(UI ui) {
        ui.clear();
        ui.setString(0, 0, "Making a Logger window and printing debug statements");
        for (int i = 0; i<10; i++) {
            ui.writeLog("debug: " + i);
            ui.wait(10);
        }
    }

    public static void testError() {
        int[] a = new int[5];

        for (int i = 0; i <= a.length; i++) {
            int x = a[i];
        }
    }

    public static void testSetStringColor(UI ui) {
        ui.clear();
        ui.setString(0, 0, "This is red", TextColor.ANSI.RED);
        // test other colors like green, blue, yellow, etc.
        ui.setString(0, 1, "This is green", TextColor.ANSI.GREEN);
        ui.refresh();
    }

    public static void testPanelColor(UI ui) {
        UI.Panel win = ui.makePanel(0, 0, 40, 10);
        win.println("This is red", TextColor.ANSI.RED);
        ui.refresh();
        ui.wait(2000);
        win.close();
    }

    public static void main(String[] args) {

        UI ui = null;
        try {
            ui = new UI();

            // testError();  should gracefully exit and print out exception error message

            // Screen level methods
            testWriteMsg(ui);
            testSetStringColor(ui);
            testInput(ui);
            testKeyPressWait(ui);
            testMoveStar(ui);

            // Panel methods
            UI.Panel w1 = testPanel(ui, 0, 5, 40, 10);
            UI.Panel w2 = testPanel(ui, 50, 5, 40, 10);
            w1.close(); w2.close();

            testPanelColor(ui);

            testPlayerInputs(ui);

            testLogger(ui);

            /* Wait for a key press */
            ui.clear();
            ui.setString(0, 0, "Press q to quit");
            ui.refresh();
            ui.waitForKey('q');
            ui.stopUI();

        } catch (Exception e) {
            if (ui != null) ui.stopUI();
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("UI Tester done");
    }
}

