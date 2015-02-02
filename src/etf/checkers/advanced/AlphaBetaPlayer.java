
package etf.checkers.advanced;

import static etf.checkers.CheckersConsts.*;

import java.util.*;

import etf.checkers.*;


/** This is an alpha beta checkers player. */
public class AlphaBetaPlayer extends CheckersPlayer implements GradedCheckersPlayer
{
    /** The number of pruned subtrees for the most recent deepening iteration. */
    protected int pruneCount;
    protected int lastPrunedNodeScore;
    protected Evaluator sbe;
    protected Move bestMove = null;
    

    public AlphaBetaPlayer(String name, int side)
    { 
        super(name, side);
        // Use SimpleEvaluator to score terminal nodes
        sbe = new SimpleEvaluator();
    }
    
    /** This is a method that evaluates score of the current node based on a board state */
    protected int calculateScore(int [] bs)
    {
        /* Evaluate this board state */
        int score = sbe.evaluate(bs);
        
        /* Negate the score if not RED */
        if (side == BLK) score = -score;
    	
        return score;	
    }
    
    protected int nextSide(int side)
    {
    	if (side == BLK) return RED;
    	return BLK;  	
    }
    

    /** This is a depth limited minimax method with alpha-beta pruning
     * Arguments: board state, depth limit, current depth, max or min turn, alpha, beta, and flag to save move */
    protected int minimax(int [] boardState, int depthLimit, int currentDepth, int alpha, int beta)
    {
    	int bestValue = Integer.MIN_VALUE;
    	int currentValue = 0;
    	int numOfNodesVisitedOnLevel = 0;
        
    	// Get all the possible moves for this player on the provided board state
        List<Move> possibleMoves = Utils.getAllPossibleMoves(boardState, side);

        // Find best board state among those reachable from one move
        for (Move move : possibleMoves)
        {
        	numOfNodesVisitedOnLevel++;
        	
        	// play move so the board can be evaluated again
        	Stack<Integer> statesStack = Utils.execute(boardState, move);
            
            currentValue = minMove(boardState, depthLimit, currentDepth + 1, alpha, beta, nextSide(side));
            
            // undo last move
            Utils.revert(boardState, statesStack);

            if (currentValue > bestValue)
            {
            	bestValue = currentValue;
            	bestMove = move;
            	
            	// beta pruning 
            	if (bestValue >= beta ){
            		pruneCount += possibleMoves.size() - numOfNodesVisitedOnLevel; 
            		lastPrunedNodeScore = currentValue;
            		break;
            	}
            		
            	// alpha = max (alpha, bestValue)
            	if (bestValue > alpha) alpha = bestValue;
            
            }
        }
    	 	
    	return bestValue;
    }
    
    protected int maxMove(int [] boardState, int depthLimit, int currentDepth, int alpha, int beta, int side)
    {
    	
    	int bestValue = Integer.MIN_VALUE;
    	int currentValue = 0;
    	int numOfNodesVisitedOnLevel = 0;
    	
    	// Get all the possible moves for this player on the provided board state
        List<Move> possibleMoves = Utils.getAllPossibleMoves(boardState, side);
        
        // If this player has no moves or limit is reached, return score
        if (possibleMoves.size() == 0 || currentDepth == depthLimit)
        	return calculateScore(boardState);
        
    	 // Find best board state among those reachable from one move
        for (Move move : possibleMoves)
        {
        	numOfNodesVisitedOnLevel++;
        	
        	// play move so the board can be evaluated again
        	Stack<Integer> statesStack = Utils.execute(boardState, move);
            
            currentValue = minMove(boardState, depthLimit, currentDepth + 1, alpha, beta, nextSide(side));
            
            // undo last move
            Utils.revert(boardState, statesStack);

            if (currentValue > bestValue)
            {
            	bestValue = currentValue;
            	
            	// beta pruning 
            	if (bestValue >= beta ){
            		pruneCount += possibleMoves.size() - numOfNodesVisitedOnLevel; 
            		lastPrunedNodeScore = currentValue;
            		break;
            	}
            		
            	// alpha = max (alpha, bestValue)
            	if (bestValue > alpha) alpha = bestValue;
            }
        }
            	 	
        return bestValue;
   }
    	
    
   protected int minMove(int [] boardState, int depthLimit, int currentDepth, int alpha, int beta, int side)
   {
	   int bestValue = Integer.MAX_VALUE;
	   int currentValue = 0;
	   int numOfNodesVisitedOnLevel = 0;
    	
	   // Get all the possible moves for this player on the provided board state
	   List<Move> possibleMoves = Utils.getAllPossibleMoves(boardState, side);
        
       // If this player has no moves or limit is reached, return score
       if (possibleMoves.size() == 0 || currentDepth == depthLimit)
    	   return calculateScore(boardState);
        
       // Find best board state among those reachable from one move
       for (Move move : possibleMoves)
       {
    	   numOfNodesVisitedOnLevel++;
        	
    	   // play move so the board can be evaluated again
    	   Stack<Integer> statesStack = Utils.execute(boardState, move);
            
           currentValue = maxMove(boardState, depthLimit, currentDepth + 1, alpha, beta, nextSide(side));
            
           // undo last move
           Utils.revert(boardState, statesStack);

           if (currentValue < bestValue)
           {
           	bestValue = currentValue;

           	// alpha pruning 
           	if (bestValue <= alpha ){
           		pruneCount += possibleMoves.size() - numOfNodesVisitedOnLevel;
           		lastPrunedNodeScore = currentValue;
           		break;
           	}
           	
           	// beta = min (beta, bestValue)
           	if (bestValue < beta) beta = bestValue;
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
             
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
               
        // Iterative deepening until maximum depth limit is reached
        for (int limit = 1; limit < depthLimit; limit++) 
        {
        	bestScore = minimax(boardState, limit, 0, alpha, beta);
        	setMove(bestMove);
        	printStatus(limit);
        	printMove(bestMove, bestScore);
		}
        	
    }

    /** This interface provides getPruneCount so that each student's alpha-beta player 
     * * can be compared against a reference implementation.  */
    public int getPruneCount() {
    	return pruneCount;
    }
    
    /** This interface provides getPruneCount so that each student's alpha-beta player 
     * * can be compared against a reference implementation.  */
	public int getLastPrunedNodeScore() {
		return lastPrunedNodeScore;
	}
	
	protected void printMove(Move m, int score)
	{
		if (Utils.verbose)
			System.out.println("Chosen best move: " + m.toString() + " with score " + score);	
	}
	
	protected void printStatus(int iteration){
		
		if (Utils.verbose)
		{
			System.out.println("Pruned nodes number for first " + iteration + " iterations is: " + pruneCount);
			System.out.println("Score of last pruned node for first " + iteration + " iterations is: " + lastPrunedNodeScore);

		}
		
	}
}
