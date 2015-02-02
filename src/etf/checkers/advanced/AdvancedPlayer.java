package etf.checkers.advanced;

import java.util.List;
import java.util.Stack;

import etf.checkers.Move;
import etf.checkers.Utils;


public class AdvancedPlayer extends AlphaBetaPlayer {

	private ComplexEvaluator complexEvaluator = null;
	
	public AdvancedPlayer(String name, int side) {
		super(name, side);
		complexEvaluator = new ComplexEvaluator();
	}
	
	protected int minimax(int [] boardState, int depthLimit, int currentDepth)
    {
    	int bestValue = Integer.MIN_VALUE;
    	int currentValue = 0;
    	int beta = Integer.MAX_VALUE;
        
    	// Get all the possible moves for this player on the provided board state
        List<Move> possibleMoves = Utils.getAllPossibleMoves(boardState, side);

        // Find best board state among those reachable from one move
        for (Move move : possibleMoves)
        {
        	// play move so the board can be evaluated again
        	Stack<Integer> statesStack = Utils.execute(boardState, move);
            
            currentValue = minMove(boardState, depthLimit, currentDepth + 1, bestValue, beta, nextSide(side));
            
            // undo last move
            Utils.revert(boardState, statesStack);
           
            if (currentValue > bestValue)
            {
            	bestValue = currentValue;
            	bestMove = move;
   		    }
        }
   	
    	return bestValue;
    }
	
    public void calculateMove(int[] boardState)
    {
    	// Get all the possible moves for this player on the provided board state
        List<Move> possibleMoves = Utils.getAllPossibleMoves(boardState, side);
        
        int bestScore = 0;
        
        bestMove = null;
        pruneCount = 0;
        lastPrunedNodeScore = 0;

        // If this player has no moves, return out
        if (possibleMoves.size() == 0) return;
                            
        // Iterative deepening until maximum depth limit is reached
        // 1, 2, 5, 8, 9, 10 ...
        for (int limit = 1; limit < depthLimit; limit++) 
        {
        	bestScore = minimax(boardState, limit, 0);
        	
        	setMove(bestMove);
        	        	
        	printStatus(limit);
        	printMove(bestMove, bestScore);
        	
        	if (limit == 2 && depthLimit > 5) limit = 4;
        	if (limit == 5 && depthLimit > 8) limit = 7;

         }
        	
    }
	
	
	
    protected int calculateScore(int [] bs)
    {
        /* Evaluate this board state */
        return complexEvaluator.evaluate(bs, side);
    }

}
