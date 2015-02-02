# checkers-players
Checkers board game with few AI players. AlphaBeta player is using minimax with alpha beta pruning. Advanced player is using advanced heuristic methods explained bellow. Developed in Java using Eclipse IDE.


This was a project for the course "Expert Systems" on my faculty.


Department of Computer Engineering and Information Theory.


School of Electrical Engineering, University of Belgrade, Serbia.


Developed by Marin Markić. No licence. May - June 2014.
- marinmarkic@mail.com


### Info
Run checkers game by running Checkers class with arguments, namespace.class1 namespace.class2 options. Supported players are: ui.Human, demo.Random, demo.Demo, advanced.AlphaBeta and advanced.Advanced. Options are not required and they include --turntime integer and --step (click is needed before each turn).


###Advanced player
Advanced players uses alpha beta pruning with complex board evaulation.

```java
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
 ```