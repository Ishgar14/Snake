import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;

public class Snake {
    Stack<Integer[]> snakebody;
    String lochistory;
    char grid[][];
    final int gridrow = 16, gridcol = 32;
    int foodx, foody;
    int headx, heady;
    int highscore;
    Scanner sc;
    Random random;

    public Snake(){
        random = new Random();
        sc     = new Scanner(System.in);
        grid   = new char[gridrow][gridcol];

        for (int i = 0; i < gridrow; i++)
            for(int j = 0; j < gridcol; j++)
                grid[i][j] = ' ';

        snakebody  = new Stack<>();
        headx = 4;
        heady = 4;
        snakebody.push(new Integer[]{headx, heady});
        snakebody.push(new Integer[]{headx - 1, heady - 1});
        cookFood();
        grid[headx][heady] = 'O';
        grid[headx - 1][heady] = 'O';
        lochistory = "";
    }

    public static void main(String[] args) {
        Snake snake = new Snake();
        snake.menu();
    }

    private void menu(){
        System.out.println("\t\t\tWELCOME TO SNAKE!");
        System.out.println("\t\t\t0. Exit\n");
        System.out.println("\t\t\t1. Start\t\tHighscore\n");
        System.out.println("\t\t\t2. Options\t\t   " + getHighscore() + "\n\n");
        
        int choice = 0;
        System.out.print("\t\t\tEnter your choice: ");
        try { choice = sc.nextInt(); }
        catch (InputMismatchException ime){
            System.err.println("Please enter a valid input!");
            menu();
        }
        
        if (choice == 0) System.exit(0);
        else if (choice == 1) this.start();
        else if (choice == 2) 
            System.out.println("\nWall Collision type: go-through");
        else
            System.out.println("Invalid Choice!\nPlease enter a valid choice...");

        try { Thread.sleep(3000); }
        catch(InterruptedException e) { e.printStackTrace(); }
        clearScreen();
        menu();        
    }

    private void start(){
        String movement;
        while (true) {
            clearScreen();
            draw();
            System.out.print("\nEnter \"wasd\" to move or ';' to exit\n>");
            movement = sc.next();
            if (movement.contains(";"))
                quit();
            else
                move(movement);
        }
    }

    private void clearScreen(){
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // to clean the screen (source: stackoverflow)
            System.out.flush();
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    private void draw() {
        for (int i = 0; i <= gridcol; i++)
            System.out.print("_");
        System.out.println();

        for (int i = 0; i < gridrow; i++) {
            System.out.print("|");
            for (int j = 0; j < gridcol; j++)
                System.out.print(grid[i][j]);

            System.out.println("|");
        }

        for (int i = 0; i <= gridcol; i++)
            System.out.print("-");
        System.out.println("\n\n" + lochistory);
    }

    private void cookFood() {
        while (grid[foodx][foody] != ' ') {
            foodx = random.nextInt(gridrow);
            foody = random.nextInt(gridcol);
        }
        grid[foodx][foody] = 'f';
    }

    private void move(String movement) {
        for (char ch : movement.toCharArray()) {
            if(ch == ';') quit();

            if (ch == 'w') headx -= 1;
            else if(ch == 'a') heady -= 1;
            else if(ch == 's') headx += 1;
            else if(ch == 'd') heady += 1;
            else return;

            if(headx < 0) headx = gridrow + headx;
            else if(headx >= gridrow) headx %= gridrow;

            if(heady < 0) heady = gridcol + heady;
            else if(heady >= gridcol) heady %= gridcol;

            if(grid[headx][heady] == 'O'){
                System.out.println("You ate yourself!\nGetting back to main menu...\n");
                menu();
                return;
            }
            boolean ateFood = false;
            if(grid[headx][heady] == 'f'){
                cookFood();
                ateFood = true;
            }
            snakebody.add(0, new Integer[]{headx, heady});

            var oldtail = snakebody.get(snakebody.size() - 1);
            if(!ateFood) snakebody.pop();
            grid[oldtail[0]][oldtail[1]] = ' ';
            highscore = snakebody.size();

            for(int i = 0; i < snakebody.size(); i++)
                grid[snakebody.elementAt(i)[0]][snakebody.elementAt(i)[1]] = 'O';
            
        }
        //lochistory  = lochistory + "=> {" + snakebodycoords() +"}";
    }

    private void quit(){
        int newhighscore = snakebody.size();
        if(newhighscore > highscore){
            try{
                highscore = newhighscore;
                FileWriter writer = new FileWriter(new File("snakehighscore.txt"));
                writer.write(String.valueOf(highscore));
            } catch(IOException e){ e.printStackTrace(); }
        }

        System.exit(0);
    }

    private int getHighscore(){
        File file = new File("snakehighscore.txt");
        if(file.exists()){
            try{
                FileReader reader = new FileReader(file);
                String result = "";
                int buffer;
                
                while ((buffer = reader.read()) != -1) 
                    result += String.valueOf(buffer);
                
                reader.close();
                return Integer.parseInt(result);
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        return 0;
    }
}
