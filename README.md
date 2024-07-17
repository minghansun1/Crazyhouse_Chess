# Crazyhouse Chess
![CrazyHouse Screenshot](CrazyHouse%20Screenshot.png)
## Project Description:
A version of chess where after a piece is captured it can be placed during future turns.
This version of crazyhouse is implemented with Java Swing, and must be run using a Java IDE.
Created for CIS 1200 Programming Languages and Techniques 1 final project.
## Rules:
1) Standard chess rules
2) After a piece is captured by a player, that player can place the captured piece on the board 
during future turns. Placing a piece takes one turn.
3) Pieces cannot be placed on other pieces, but can block and give checks.
4) Pawns cannot be placed on the 1st or 8th row.
## How to Install:
Download all files in the folder.
## How to Play:
<p> Run the file called "Game".
<p> Click and drag pieces from oowne square to another in order to move them.
<p> Click and drag pieces from the list of captured pieces on the right side of the board
and ending on a square.
<p> When the game ends, a checkmate banner appears to the top left of the screen and play stops.</p>

### Concept 1: 2D arrays
- 2D array simulating the board. Filled with GamePiece objects, because that makes it easy to 
check if moves are valid for different pieces, and draw the objects differently
- Only one board used
- Array not changable in other classes
- Used 2D array since chess boards are basically 2d arrays
### Concept 2: Collections
- Used treesets to keep track of which pieces are still on the board.
- Iterated using for loop
- Only used 2 treesets to keep track of pieces for both sides
- Treeset is not passed to other classes
- The number of pieces on the board changes so the set must resize, so collections are appropriate
### Concept 3: Inheritance
- Parent class is GamePiece, King, Queen, Rook, Knight, Bishop, and Pawn extend it
- The subtype is a dynamic class. This needs to be done becuase different pieces are drawn
in different ways, and move differently.
- The method isValid() is implemented differently in each subclass.
- Dynamic dispatch is used when calling methods of pieces from the board array, since isValid() is
different for each piece and the board is filled with GamePieces
### Concept 4: Complex Game Logic
- Implemented all of chess's complex game logic, plus 
