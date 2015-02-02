package etf.checkers.advanced;
import static etf.checkers.CheckersConsts.*;


public class ComplexEvaluator
{
	private int [] bs = null;
    
    // heuristic function constants
    private final int H_PAWN_VALUE = 1000;
    private final int H_KING_VALUE = 1500;
    private final int H_MID_FACTOR = 100;
    private final int H_LAST_LINE_BONUS = 900;
    private final int H_BLOCKED_MINUS = 900;
    private final int H_ROW_FACTOR = 150;
    private final int H_PAWN_NO_BACKUP = -800;
    private final int H_KING_NO_BACKUP = -900;
    private final int H_PAWN_BACKUPED = 450;
    private final int H_KING_BACKUPED = 600;

    private final int H_ENEMY_PAWNS_FACTOR = 200;
    private final int H_ENEMY_KINGS_FACTOR = 400;
 
    
	public int evaluate(int[] bs, int side)
    {
		this.bs = bs;
		
		if (side == RED) return calculateRed();
		return calculateBlack();
     } 
    
	private int calculateRed()
	{
		int score = 0;
		int blackPawnNum = 0;
		int blackKingNum = 0;
		
		for (int i = 0; i < H * W; i++)
	    {
					
			switch(bs[i])
	        {   
				case BLK_PAWN:
					blackPawnNum++;
					break;
				case BLK_KING:
					blackKingNum++;
					break;
				case RED_PAWN: 		
 		
	        		// h2 - last line of defense
	        		if (i == 58 || i == 60 || i == 62) score += H_LAST_LINE_BONUS;
	        		
	        		// h1 + h4 columns and rows + value
	        		score += H_PAWN_VALUE + rowColumnFactors(i, RED_PAWN);
	        		
	        		// h5 pawn attacked without backup
	        		score += checkRedPawnBackup(i);
	        		break;
	        	
	        	case RED_KING:	
	            	
	        		// h1 + h4 columns and rows + value
	        		score += H_KING_VALUE + rowColumnFactors(i, RED_KING);
	            	
	            	// h5 king attacked without backup
	            	score += checkRedKingBackup(i);
	            	break;
	        }
	    }
		
		// h3 blocked
		score = score - checkLeftBlockedReds() - checkRightBlockedReds(); 
		
		// h6 black number
		score = score + (12 - blackPawnNum) * H_ENEMY_PAWNS_FACTOR - blackKingNum * H_ENEMY_KINGS_FACTOR; 
		
		return score;
	}
	
	private int rowColumnFactors(int i, int figure)
	{
		// h1 - control of middle 
		// set column factors: 0 1 2 3 3 2 1 0
		int h1 = i % 8;
		if (h1 > 3) h1 = 7 - h1;
		
		// h4 offensive
		// set row factors: 0 1 2 3 4 5 6 0
		int h4 = i / 8;
		if (h4 == 7) h4 = 0;
		
		// negate direction for red pawns and black kings 0 6 5 4 3 2 1 0
		if (figure == RED_PAWN || figure == BLK_KING)
			h4 = 7 - h4;
		
		h1 = h1 * H_MID_FACTOR;
		h4 = h4 * H_ROW_FACTOR;
		
		return h1 + h4;	
	}
	
	private int checkLeftBlockedReds()
	{
		int score = 0;
		
		// wall blocking left, black figure blocking right diagonal
		if (bs[8] == RED_PAWN && (bs[1] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		if (bs[24] == RED_PAWN && (bs[17] & 1) == 1 && (bs[10] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		if (bs[40] == RED_PAWN && (bs[33] & 1) == 1 && (bs[26] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		if (bs[56] == RED_PAWN && (bs[49] & 1) == 1 && (bs[42] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		
		return score;
	}
	
	private int checkRightBlockedReds()
	{
		int score = 0;
		
		// wall blocking right, black figure blocking left diagonal
		if (bs[23] == RED_PAWN && (bs[14] & 1) == 1 && (bs[5] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		if (bs[39] == RED_PAWN && (bs[30] & 1) == 1 && (bs[21] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		if (bs[55] == RED_PAWN && (bs[46] & 1) == 1 && (bs[37] & 1) == 1) 
			score += H_BLOCKED_MINUS;
		
		return score;
		
	}
	
	private int checkRedPawnBackup(int i)
	{
		int score = 0;
		
		// first or last row
		if (i/8 == 0 || i/8 == 7) return 0;
		
		// leftmost or rightmost column (blocking will fix this)
		if (i%8 == 0 || i%8 == 7) return 0;
		
		// right diagonal
		if ((bs[i-7]&1) == 1){
			
			if (bs[i+7] == BLANK) score += H_PAWN_NO_BACKUP;
			else if (bs[i+7] == BLK_PAWN) score += 0;
			else score += H_PAWN_BACKUPED;
		}
		
		// left diagonal
		if ((bs[i-9]&1) == 1){
			
			if (bs[i+9] == BLANK) score += H_PAWN_NO_BACKUP;
			else if (bs[i+9] == BLK_PAWN) score += 0;
			else score += H_PAWN_BACKUPED;
		}
		
		return score;
	}
	
	private int checkRedKingBackup(int i)
	{
		int score = 0;
		// first or last row
		if (i/8 == 0 || i/8 == 7) return 0;
		
		// leftmost or rightmost column (blocking will fix this)
		if (i%8 == 0 || i %8 == 7) return 0;
		
		// right diagonal
		if ((bs[i-7]&1) == 1){
			
			if (bs[i+7] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i+7] == BLK_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		
		// left diagonal
		if ((bs[i-9]&1) == 1){
			
			if (bs[i+9] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i+9] == BLK_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		
		// inverted
		// right diagonal
		if ((bs[i+7]&1) == 1){
			
			if (bs[i-7] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i-7] == BLK_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		
		// right diagonal
		if ((bs[i+9]&1) == 1){
			
			if (bs[i-9] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i-9] == BLK_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		return score;
	}
   
	private int calculateBlack()
	{
		int score = 0;
		int redPawnNum = 0;
		int redKingNum = 0;
		
		for (int i = 0; i < H * W; i++)
	    {
					
			switch(bs[i])
	        {   
				case RED_PAWN:
					redPawnNum++;
					break;
				case RED_KING:
					redKingNum++;
					break;
				case BLK_PAWN: 		
 		
	        		// h2 - last line of defense
	        		if (i == 1 || i == 3 || i == 5) score += H_LAST_LINE_BONUS;
	        		
	        		// h1 + h2 columns and rows + value
	        		score += H_PAWN_VALUE + rowColumnFactors(i, BLK_PAWN);
	        		
	        		// h5 pawn attacked without backup
	        		score += checkBlackPawnBackup(i);
	        		break;
	        	
	        	case BLK_KING:	
	            	
	        		// h1 + h2 columns and rows + value
	        		score += H_KING_VALUE + rowColumnFactors(i, BLK_KING);
	            	
	            	// h5 king attacked without backup
	            	score += checkBlackKingBackup(i);
	            	break;
	        }
	    }
		
		// h3 blocked
		score = score - checkLeftBlockedBlacks() - checkRightBlockedBlacks(); 
		
		// h6 black number
		score = score + (12 - redPawnNum) * H_ENEMY_PAWNS_FACTOR - redKingNum * H_ENEMY_KINGS_FACTOR; 
		
		return score;
		
	}
	
	private int checkRightBlockedBlacks() 
	{
		int score = 0;
		
		// wall blocking right, red figure blocking left diagonal
		if (bs[23] == BLK_PAWN && (bs[30] & 4) == 4 && (bs[37] & 4) == 4) 
			score += H_BLOCKED_MINUS;
		if (bs[39] == BLK_PAWN && (bs[46] & 4) == 4 && (bs[53] & 4) == 4) 
			score += H_BLOCKED_MINUS;
		
		return score;
	}

	private int checkLeftBlockedBlacks() 
	{
		int score = 0;
		
		// wall blocking left, red figure blocking right diagonal
		if (bs[8] == BLK_PAWN && (bs[17] & 4) == 4 && (bs[26] & 4) == 4) 
			score += H_BLOCKED_MINUS;
		if (bs[24] == BLK_PAWN && (bs[33] & 4) == 4 && (bs[42] & 4) == 4) 
			score += H_BLOCKED_MINUS;
		if (bs[40] == BLK_PAWN && (bs[49] & 4) == 4 && (bs[58] & 4) == 4) 
			score += H_BLOCKED_MINUS;
		
		return score;
	}

	private int checkBlackPawnBackup(int i) 
	{
		int score = 0;
		
		// first or last row
		if (i/8 == 0 || i/8 == 7) return 0;
		
		// leftmost or rightmost column (blocking will fix this)
		if (i%8 == 0 || i%8 == 7) return 0;
		
		// right diagonal
		if ((bs[i+7]&4) == 4){
			
			if (bs[i-7] == BLANK) score += H_PAWN_NO_BACKUP;
			else if (bs[i-7] == RED_PAWN) score += 0;
			else score += H_PAWN_BACKUPED;
		}
		
		// left diagonal
		if ((bs[i+9]&4) == 4){
			
			if (bs[i-9] == BLANK) score += H_PAWN_NO_BACKUP;
			else if (bs[i-9] == RED_PAWN) score += 0;
			else score += H_PAWN_BACKUPED;
		}
		
		return score;
	}

	private int checkBlackKingBackup(int i) 
	{
		int score = 0;
		
		// first or last row
		if (i/8 == 0 || i/8 == 7) return 0;
		
		// leftmost or rightmost column (blocking will fix this)
		if (i%8 == 0 || i %8 == 7) return 0;
		
		// right diagonal
		if ((bs[i+7]&4) == 4){
			
			if (bs[i-7] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i-7] == RED_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		
		// left diagonal
		if ((bs[i+9]&4) == 4){
			
			if (bs[i-9] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i-9] == RED_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		
		// inverted
		// right diagonal
		if ((bs[i-7]&4) == 4){
			
			if (bs[i+7] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i+7] == RED_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		
		// right diagonal
		if ((bs[i-9]&4) == 4){
			
			if (bs[i+9] == BLANK) score += H_KING_NO_BACKUP;
			else if (bs[i+9] == RED_PAWN) score += 0;
			else score += H_KING_BACKUPED;
		}
		return score;
	}
	
}


/**
 * This is complex static board evaluator for checkers. It combines few heuristic functions. 
 * - H0 simple count evaluation
 * each king on board (not been eaten in the last move) increases the value by 1500.
 * each pawn on board increases the value by 1000
 * 
 * - H1 gives bonus for center control (middle of table) 
 * with H1, 
 * firstly it will be harder to block our pawns, and even harder the kings to a wall, 
 * and secondly, the center of the board will be controlled, we want to control center so we can split 
 * enemy pieces in two groups
 * bonuses are: (in order by columns) 0, 100, 200, 300, 300, 200, 100, 0
 * 
 * - H2 gives bonuses for pawns defending the last line
 * if last line of defense stands back, the enemy player cannot take king
 * last line of defense will move when there is no other pawn to play, or jump by those is needed.
 * bonus is: 500 
 * 
 * - H3 decreases score for every blocked pawn by wall and black figure
 * this gives us more pawns to choose from and advance, every blocked figure lowers the winning chance
 * for every blocked figure -60 (max 7 figures)
 * 
 * - H4 first row has priority - go offense and take queen fast every row - advancing gives bonuses
 * this game cannot be win defensively
 * bonuses are: (in order by row) 0, 900, 750, 600, 450, 300, 150, 0
 * negate bonus for kings, once the king is taken it should go backward
 * 
 * - H5 group movement - every pawn and king more importantly need to have backup piece
 * pawn under attack and no pawn to backup him -700!
 * pawn that has backup behind him +400
 * king under attack and no pawn to backup him -1000!
 * 
 * - H6 number of black pawns and kings (enemy)
 * score decreases for 400 for each black king spawn
 * score increases for 200 for each black pawn that is spawned on the field 
 * 
 */