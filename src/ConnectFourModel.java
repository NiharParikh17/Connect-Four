/**
	@author Nihar Parikh
	ConnectFourModel.java
*/

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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

    public ConnectFourModel(){
        observers = new ArrayList<>();
        winObservers = new ArrayList<>();

        player1 = new Player("Player 1", 1, Color.RED);
        player2 = new Player("Player 2", 2, Color.YELLOW);
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

    @Override
    public int getTotalRows() {
        return totalRows;
    }

    @Override
    public int getTotalColumns() {
        return totalColumns;
    }

    @Override
    public String getPlayer() {
        return currentPlayer.getName();
    }

    @Override
    public int getCurrentRow() {
        return currentRow;
    }

    @Override
    public int getCurrentColumn() {
        return currentColumn;
    }

    @Override
    public Color getColor() {
        return currentPlayer.getColor();
    }

    @Override
    public int getDisableButton() {
        return disablingButton;
    }

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

    @Override
    public void exit() {
        System.exit(0);
    }

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

    @Override
    public void registerObserver(ConnectFourObserver o) {
        observers.add(o);
    }

    @Override
    public void registerObserver(WinnerObserver o) {
        winObservers.add(o);
    }

    @Override
    public void removeObserver(ConnectFourObserver o) {
        observers.remove(o);
    }

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
