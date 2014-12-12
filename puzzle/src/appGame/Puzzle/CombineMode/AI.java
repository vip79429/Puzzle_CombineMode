package appGame.Puzzle.CombineMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import android.util.Log;


public class AI {
	// Tiles for successfully completed puzzle.
    static final int [] goalTiles = { 0,1,2,3,4,5,6,7,8 };
    static ArrayList<Integer> slideto; //答案陣列
    
    int q=0; //紀錄拼圖是否符合規則的次數
    // A* priority queue.
    final PriorityQueue <State> queue = new PriorityQueue<State>(100, new Comparator<State>() {
        public int compare(State a, State b) { 
            return a.priority() - b.priority();
        }
    });

    // The closed state set.
    final HashSet <State> closed = new HashSet <State>();

    // State of the puzzle including its priority and chain to start state.
    class State {
        final int [] tiles;    // Tiles left to right, top to bottom.
        final int spaceIndex;   // Index of space (zero) in tiles  
        final int g;            // Number of moves from start.
        final int h;            // Heuristic value (difference from goal)
        
        final State prev;       // Previous state in solution chain.
        
        // A* priority function (often called F in books).
        int priority() {
            return g + h;
        }

        // Build a start state.
        State(int [] initial) {
        	slideto = new ArrayList<Integer>();
            tiles = initial;
            spaceIndex = index(tiles, 0);
            g = 0;
            h = heuristic(tiles);
            prev = null;
        }

        // Build a successor to prev by sliding tile from given index.
        State(State prev, int slideFromIndex) {
            tiles = Arrays.copyOf(prev.tiles, prev.tiles.length);
            tiles[prev.spaceIndex] = tiles[slideFromIndex];
            tiles[slideFromIndex] = 0;
            spaceIndex = slideFromIndex;         
            g = prev.g + 1;
            h = heuristic(tiles);
            this.prev = prev;
        }

        // Return true iif this is the goal state.
        boolean isGoal() {
            return Arrays.equals(tiles, goalTiles);
        }

        // Successor states due to south, north, west, and east moves.
        State moveS() { return spaceIndex > 2 ? new State(this, spaceIndex - 3) : null; }       
        State moveN() { return spaceIndex < 6 ? new State(this, spaceIndex + 3) : null; }       
        State moveE() { return spaceIndex % 3 > 0 ? new State(this, spaceIndex - 1) : null; }       
        State moveW() { return spaceIndex % 3 < 2 ? new State(this, spaceIndex + 1) : null; }

        // Print this state.
        void print() {
            for (int i = 0;i < tiles.length; i++){
            	if (tiles[i] == 0){
	            	slideto.add(i); //紀錄被移動的空格，表示被移動的空格是答案
            	}
            }
        }

        // Print the solution chain with start state first.
        void printAll() {
            if (prev != null) prev.printAll();
//            System.out.println();
            print();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof State) {
                State other = (State)obj;
                return Arrays.equals(tiles, other.tiles);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(tiles);
        }
    }

    // Add a valid (non-null and not closed) successor to the A* queue.
    void addSuccessor(State successor) {
        if (successor != null && !closed.contains(successor)) 
            queue.add(successor);
    }

    public AI() {
    	
    }

	public boolean AI_check(int [] initial , int threshold) {

        queue.clear();
        closed.clear();

        // Click the stopwatch.
        long start = System.currentTimeMillis();

        // Add initial state to queue.
        queue.add(new State(initial));
        while (!queue.isEmpty()) {
        	q++;
        	if (q > threshold){ //過大表示不符合規則
        		q = 0;
        		Log.e("over", "over");
        		return false;
        	}
            // Get the lowest priority state.
            State state = queue.poll();

            // If it's the goal, we're done.
            if (state.isGoal()) { //當計算出結果
            	Log.e("q", String.valueOf(q));
//            	Log.e("state.isGoal()", "state.isGoal()");
//                long elapsed = System.currentTimeMillis() - start;
                state.printAll();
//                Log.e("state.isGoal()", "elapsed (ms) = " + String.valueOf(elapsed));
                Log.e("移動步數", String.valueOf(slideto.size()));
//                String bb = "";
//                for (int i = 0; i < slideto.size(); i++){
//                	bb = bb + String.valueOf(slideto.get(i)); //紀錄答案陣列
//                }
//                Log.e("slideto array", bb);
                return true;
            }

            // Make sure we don't revisit this state.
            closed.add(state);

            // Add successors to the queue.
            addSuccessor(state.moveS());
            addSuccessor(state.moveN());
            addSuccessor(state.moveW());
            addSuccessor(state.moveE());
        }
		return true;
    }
    // Return the index of val in given byte array or -1 if none found.
    static int index(int [] a, int val) {
        for (int i = 0; i < a.length; i++)
            if (a[i] == val) return i;
        return -1;
    }

    // Return the Manhatten distance between tiles with indices a and b.
    static int manhattenDistance(int a, int b) {
        return Math.abs(a / 3 - b / 3) + Math.abs(a % 3 - b % 3);
    }

    // For our A* heuristic, we just use sum of Manhatten distances of all tiles.
    static int heuristic(int [] tiles) {
        int h = 0;
        for (int i = 0; i < tiles.length; i++)
            if (tiles[i] != 0)
                h += manhattenDistance(i, tiles[i]);
        return h;
    }

//    public static void main(String[] args) {
//
//        // This is a harder puzzle than the SO example
//        byte [] initial = { 8, 0, 6, 5, 4, 7, 2, 3, 1 };
//
//        // This is taken from the SO example.
//        //byte [] initial = { 1, 4, 2, 3, 0, 5, 6, 7, 8 };
//
//        new AI().solve(initial);
//    }
}
