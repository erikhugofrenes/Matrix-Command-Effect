package primary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class main {
	
	static int min_length = 0;
    static int max_length = 4;	
	
    private class matrix_stream {
        int lane;
        int position = -1;
        int length;
        boolean is_default_weight;
       
        public matrix_stream(int lane, boolean is_default_weight) {
            this.lane = lane;
            this.length = rand.nextInt(min_length, max_length);
            this.is_default_weight = is_default_weight;
        }

        public void next_position() {
            this.position++;
        }

        public int get_lane() {
            return this.lane;
        }

        public int get_head_position() {
            return position;
        }

        public int get_tail_position() {
            return this.position - this.length;
        }

        public int get_matrix_stream_weight() {
            if (is_default_weight) return 1;
            int weight_calculation = (int) (Math.log(length) / Math.log(2));
            return weight_calculation + 1;
        }
    }

    public static int rows = 45;
    public static int cols = 300;
    public static int time = 0;
    public static boolean is_done = false;
    public static boolean is_infinite = false;
    
    static final int refreshes = 4;
    static int number_position_manipulations = 1000;
    static final int single_position_increase_time = 100;
    static final int second = 1000;
    static final int single_iteration_duration = single_position_increase_time / refreshes;

    static final String black_background = "\033[40m";
    static final String very_light_green = "\033[38;5;156m";
    static final String darker_green = "\033[38;5;34m";
    static final String RESET_color = "\033[0m";

    static ArrayList<matrix_stream> list_of_matrix_streams = new ArrayList<matrix_stream>();

    public static void main(String args[]) {

        int time_before_collision = 15 * second;

        if (args.length < 2) {
            System.out.println("Usage: java -jar MyAutoRunProgram.jar <prefix> <command> [<args>...]");
            return;
        }

        String prefix = args[0];
        StringBuilder commandBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String command = commandBuilder.toString().trim();

        System.out.println("Command after removing prefix: " + command);
        if(prefix.equals("infinite"))
        {
        rows  = 47;
        cols = 165;
        is_infinite = true;
        min_length = 0;
        max_length = 32;
        probability_threshold = 0.07;
        }
        else
        {
        	init_output_target(executeCommand(command));	
        }
        

        System.out.println("...");
        clearConsole();

        char screen[][] = new char[rows][cols];
        char color_mapping_array[][] = new char[rows][cols];
        int totalIterations = number_position_manipulations * refreshes;

        for (int i = 0; i < ((is_done) ? 0 : 1) * totalIterations; i++) {

            long startTime = System.currentTimeMillis();

            if (time > time_before_collision && !is_infinite) check_collisions = true;

            screen = reset(screen, "screen");
            color_mapping_array = reset(color_mapping_array, "color_mapping_array");

            if (i % refreshes == 0) {
                if (is_infinite)
                {
                	generate_new_matrix_streams(screen);
                }
                else if(!(depth == 0 && is_blank_line(explorable_text[0]))) {
                    generate_new_matrix_streams(screen);
                }
            }

            for (int j = 0; j < list_of_matrix_streams.size(); j++) {
                if (list_of_matrix_streams.get(j).get_tail_position() >= screen.length) {
                    list_of_matrix_streams.remove(j);
                    j--;
                } else if (check_collisions) {
                    if (list_of_matrix_streams.get(j).get_head_position() < rows
                            && list_of_matrix_streams.get(j).get_head_position() >= 0) {
                        if (check_collision(list_of_matrix_streams.get(j))) {
                            integrate_discovered_text(list_of_matrix_streams.get(j));
                            list_of_matrix_streams.remove(list_of_matrix_streams.get(j));
                            next_depth();
                            j--;
                        }
                    }
                }
            }

            for (matrix_stream n : list_of_matrix_streams) {

                screen = draw_stream(screen, n);
                color_mapping_array = update_color_mapping_array(color_mapping_array, n);

                if (i % refreshes < n.get_matrix_stream_weight()) {
                    n.next_position();
                }
            }
            if(!is_infinite)
            {
            screen = combine_with_explored(screen, "screen");
            color_mapping_array = combine_with_explored(color_mapping_array, "color_mapping_array");
            }
            
            if(!is_infinite)
            if (depth == 0 && list_of_matrix_streams.size() == 0 && is_blank_line(explorable_text[0])) {
                is_done = true;
            }

            refreshConsole();
            print_screen(screen, color_mapping_array);

            long endTime = System.currentTimeMillis();
            long iteration_duration = endTime - startTime;

            try {
                long sleepTime = single_iteration_duration - iteration_duration;
                if (sleepTime > 0)
                    Thread.sleep(sleepTime);
                time += sleepTime + iteration_duration;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("DONE");

    }

    public static char[][] reset(char[][] input, String arg) {
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                if (arg.equals("screen"))
                    input[i][j] = ' ';
                else if (arg.equals("color_mapping_array"))
                    input[i][j] = '0';
            }
        }
        return input;
    }

    public static char[][] draw_stream(char[][] screen, matrix_stream n) {
        for (int i = (n.get_tail_position() < 0) ? 0 : n.get_tail_position(); i <= ((n.get_head_position() > screen.length - 1) ? screen.length - 1 : n.get_head_position()); i++) {
            screen[i][n.get_lane()] = (char) rand.nextInt(50, 100);
        }
        return screen;
    }

    public static void print_screen(char[][] screen, char[][] color_mapping_array) {
        StringBuilder output = new StringBuilder();
        output.append(black_background).append(very_light_green);

        char last_color_mapping_id = '0';
        for (int i = 0; i < screen.length; i++) {
            for (int j = 0; j < screen[0].length; j++) {
                if (color_mapping_array[i][j] != last_color_mapping_id)
                    switch (color_mapping_array[i][j]) {
                        case '0':
                            output.append("");
                            break;
                        case '1':
                            output.append(very_light_green);
                            break;
                        case '2':
                            output.append(darker_green);
                            break;
                    }
                output.append(screen[i][j]);
                last_color_mapping_id = color_mapping_array[i][j];
            }
            output.append("\n");
        }
        System.out.print(output.toString());
    }

    private static final Random rand = new Random();
    public static double probability_threshold = 0.25;
    
    public static void generate_new_matrix_streams(char screen[][]) {
        main x = new main();

        int row_generate[] = new int[screen[0].length];
        for (int j = 0; j < row_generate.length; j++) {
            double random = rand.nextDouble();
            if (random <= probability_threshold) {
                row_generate[j] = 1;
            } else {
                row_generate[j] = 0;
            }
        }

        for (int j = 0; j < list_of_matrix_streams.size(); j++) {
            if (list_of_matrix_streams.get(j).get_head_position() < screen.length) {
                row_generate[list_of_matrix_streams.get(j).get_lane()] = 0;
            }
        }

        for (int j = 0; j < row_generate.length; j++) {
            if (row_generate[j] == 1) {
                main.matrix_stream new_matrix_stream = x.new matrix_stream(j, true);
                list_of_matrix_streams.add(new_matrix_stream);
            }
        }
    }

    public static char[][] update_color_mapping_array(char[][] color_mapping_array, matrix_stream n) {
        for (int i = (n.get_tail_position() < 0) ? 0 : n.get_tail_position(); i <= ((n.get_head_position() > color_mapping_array.length - 1) ? color_mapping_array.length - 1 : n.get_head_position()); i++) {
            if (i == n.get_head_position()) {
                color_mapping_array[i][n.get_lane()] = '1';
            } else {
                color_mapping_array[i][n.get_lane()] = '2';
            }
        }
        return color_mapping_array;
    }

    public static void init_output_target(char[][] input) {
        explorable_text = input;
        rows = explorable_text.length;
		cols = explorable_text[0].length;
        depth = explorable_text.length - 1;
        explored_text = new char[rows][cols];
        explored_text = reset(explored_text, "screen");
        next_depth();
    }

    public static int depth;
    public static char[][] explored_text;
    public static char[][] explorable_text;
    public static boolean check_collisions = false;

    public static boolean is_blank_line(char input[]) {
        for (char c : input) if (c != ' ') return false;
        return true;
    }

    public static void next_depth() {
        for (int i = depth; i > 0; i--) {
            if (is_blank_line(explorable_text[depth])) {
                depth--;
            }
        }
    }

    public static boolean check_collision(matrix_stream n) {
        if (explorable_text[0].length - 1 < n.get_lane()) return false;
        if ((n.get_head_position() == depth && explorable_text[depth][n.get_lane()] != ' ')) {
            return true;
        }
        if (explored_text[n.get_head_position()][n.get_lane()] != ' ') {
            return true;
        }
        return false;
    }

    public static void integrate_discovered_text(matrix_stream n) {
        if (explorable_text[0].length > n.get_lane())
            if (explorable_text[n.get_head_position()][n.get_lane()] != ' ') {
                explored_text[n.get_head_position()][n.get_lane()] = explorable_text[n.get_head_position()][n.get_lane()];
                explorable_text[n.get_head_position()][n.get_lane()] = ' ';
            }
    }

    public static char[][] combine_with_explored(char[][] input, String arg) {
        for (int i = 0; i < explored_text.length; i++) {
            for (int j = 0; j < explored_text[0].length; j++) {
                if (explored_text[i][j] != ' ') {
                    if (arg.equals("screen")) {
                        input[i][j] = explored_text[i][j];
                    } else if (arg.equals("color_mapping_array")) {
                        input[i][j] = '1';
                    }
                }
            }
        }
        return input;
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refreshConsole() {
        System.out.print("\033[H");
        System.out.flush();
    }

    public static char[][] executeCommand(String command) {
        List<char[]> outputList = new ArrayList<>();
        int maxLength = 0;

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                char[] lineArray = line.toCharArray();
                outputList.add(lineArray);
                if (lineArray.length > maxLength) {
                    maxLength = lineArray.length;
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        char[][] outputArray = new char[outputList.size()][maxLength];
        for (int i = 0; i < outputList.size(); i++) {
            char[] lineArray = outputList.get(i);
            System.arraycopy(lineArray, 0, outputArray[i], 0, lineArray.length);
            for (int j = lineArray.length; j < maxLength; j++) {
                outputArray[i][j] = ' ';
            }
        }

        return outputArray;
    }
}