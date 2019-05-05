package ConnectFour;

/**
	@author Nihar Parikh
	ConnectFour.ConnectFourModel.java
*/

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The game logic is found here
 * This is the MODEL of the mvc architecture
 */
public class ConnectFourModel implements ConnectFourModelInterface {
    private final int totalRows = 6;
    private final int totalColumns = 7;

    private ArrayList<ConnectFourObserver> observers;
    private ArrayList<WinnerObserver> winObservers;

    private Player player1;
    private Player player2;
    private Player currentPlayer;

    private int[][] board;
    private int[] availableSpot; //Available spot in each column
    private int currentRow;
    private int currentColumn;
    private int disablingButton;

    /**
     * Constructor for the ConnectFourModel class
     */
    public ConnectFourModel(){
        observers = new ArrayList<>();
        winObservers = new ArrayList<>();

        player1 = new Player("ConnectFour.Player 1", 1, Color.RED);
        player2 = new Player("ConnectFour.Player 2", 2, Color.YELLOW);
        currentPlayer = player1;

        board = new int[totalRows][totalColumns];
        for(int row = 0; row < totalRows; row++){
            for(int col = 0; col < totalColumns; col++){
                board[row][col] = 0;
            }
        }

        availableSpot = new int[totalColumns];
        for(int col = 0; col < availableSpot.length; col++){
            availableSpot[col] = totalRows - 1; //We do -1 because it starts with 0 and not 1
        }

        disablingButton = -1;
    }

    /**
     *
     * @return total rows in board
     */
    @Override
    public int getTotalRows() {
        return totalRows;
    }

    /**
     *
     * @return total columns in board
     */
    @Override
    public int getTotalColumns() {
        return totalColumns;
    }

    /**
     *
     * @return name of the current player
     */
    @Override
    public String getPlayer() {
        return currentPlayer.getName();
    }

    /**
     *
     * @return index of the current row
     */
    @Override
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     *
     * @return index of the current column
     */
    @Override
    public int getCurrentColumn() {
        return currentColumn;
    }

    /**
     *
     * @return color of the current player
     */
    @Override
    public Color getColor() {
        return currentPlayer.getColor();
    }

    /**
     *
     * @return -1 if no button to disable,
     *          index of the button otherwise
     */
    @Override
    public int getDisableButton() {
        return disablingButton;
    }

    /**
     * place chip in the available slot of
     *      the column of the current player
     * @param column index of the column
     */
    @Override
    public void dropChip(int column) {
        board[availableSpot[column]][column] = currentPlayer.getId();
        currentRow = availableSpot[column];
        currentColumn = column;
        availableSpot[column]--;
        if(availableSpot[column] < 0){
            disablingButton = column;
        }
        notifyGameObservers();
        disablingButton = -1;
        if(checkWin()){
            JOptionPane.showMessageDialog(null, currentPlayer.getName() + " wins!");
            notifyWinnerObserver();
            switchPlayer();
        }
        if(boardFull()){
            JOptionPane.showMessageDialog(null, "The game ended as draw!");
            notifyWinnerObserver();
            switchPlayer();
        }
        switchPlayer();
    }

    private boolean checkWin(){
        return checkHorizontalWin() || checkVerticalWin() || checkDiagonalWin();
    }

    private boolean checkHorizontalWin(){
        for(int row = 0; row < totalRows; row++){
            for(int col = 0; col < totalColumns - 3; col++){
                int startChip = board[row][col];
                if(startChip != 0){
                    if(board[row][col + 1] == startChip && board[row][col + 2] == startChip && board[row][col + 3] == startChip)
                        return true;
                }
            }
        }
        return false;
    }

    private boolean checkVerticalWin(){
        for(int row = 0; row < totalRows - 3; row++){
            for(int col = 0; col < totalColumns; col++){
                int startChip = board[row][col];
                if(startChip != 0){
                    if(board[row + 1][col] == startChip && board[row + 2][col] == startChip && board[row + 3][col] == startChip)
                        return true;
                }
            }
        }
        return false;
    }

    private boolean checkDiagonalWin(){
        //Checking left diagonal /
        for(int row = 0; row < totalRows - 3; row++){
            for(int col = 3; col < totalColumns; col++){
                int startChip = board[row][col];
                if(startChip != 0){
                    if(board[row + 1][col - 1] == startChip && board[row + 2][col - 2] == startChip && board[row + 3][col - 3] == startChip)
                        return true;
                }
            }
        }

        //Checking right diagonal \
        for(int row = 0; row < totalRows - 3; row++){
            for(int col = 0; col < totalColumns - 3; col++){
                int startChip = board[row][col];
                if(startChip != 0){
                    if(board[row + 1][col + 1] == startChip && board[row + 2][col + 2] == startChip && board[row + 3][col + 3] == startChip)
                        return true;
                }
            }
        }

        return false;
    }

    private void switchPlayer(){
        if(currentPlayer == player1){
            currentPlayer = player2;
        }else if(currentPlayer == player2){
            currentPlayer = player1;
        }
    }

    private boolean boardFull(){
        for(int row = 0; row < totalRows; row++)
            for(int col = 0; col < totalColumns; col++)
                if(board[row][col] == 0)
                    return false;
        return true;
    }

    /**
     * Exits the game
     */
    @Override
    public void exit() {
        System.exit(0);
    }

    /**
     * Resets the game in the model
     */
    @Override
    public void reset() {
        currentPlayer = player1;

        for(int row = 0; row < totalRows; row++){
            for(int col = 0; col < totalColumns; col++){
                board[row][col] = 0;
            }
        }

        for(int col = 0; col < availableSpot.length; col++){
            availableSpot[col] = totalRows - 1; //We do -1 because it starts with 0 and not 1
        }
    }

    /**
     * Registers the ConnectFourObserver
     * @param o connect four observer
     */
    @Override
    public void registerObserver(ConnectFourObserver o) {
        observers.add(o);
    }

    /**
     * Registers the WinnerObserver
     * @param o winner observer
     */
    @Override
    public void registerObserver(WinnerObserver o) {
        winObservers.add(o);
    }

    /**
     * Removes the ConnectFourObserver
     * @param o connect four observer
     */
    @Override
    public void removeObserver(ConnectFourObserver o) {
        observers.remove(o);
    }

    /**
     * Removes the WinnerObserver
     * @param o winner observer
     */
    @Override
    public void removeObserver(WinnerObserver o) {
        winObservers.remove(o);
    }

    private void notifyGameObservers(){
        for(ConnectFourObserver obs: observers){
            obs.updateGame();
        }
    }

    private void notifyWinnerObserver(){
        for(WinnerObserver obs: winObservers){
            obs.updateWinner();
        }
    }
}
