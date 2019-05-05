package ConnectFour;

/**
	@author Nihar Parikh
	ConnectFour.ConnectFourView.java
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectFourView extends JFrame implements ConnectFourObserver, WinnerObserver, ActionListener {
    private ConnectFourModelInterface model;
    private ConnectFourControllerInterface controller;

    private JPanel mainPanel;

    private JPanel infoPanel;
    private JLabel currentPlayer;

    private JPanel gamePanel;
    private JButton[] dropButtons;
    private JPanel[][] slots;

    private JPanel operationPanel;
    private JButton exit;
    private JButton reset;

    public ConnectFourView(ConnectFourModelInterface model, ConnectFourControllerInterface controller){
        super();
        this.model = model;
        this.controller = controller;
        buildView();
        model.registerObserver((ConnectFourObserver) this);
        model.registerObserver((WinnerObserver) this);
        pack();
        setVisible(true);
    }

    private void buildView(){
        setTitle("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildMainPanel();
        add(mainPanel);
    }

    private void buildMainPanel(){
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        buildInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        buildGamePanel();
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        buildOperationPanel();
        mainPanel.add(operationPanel, BorderLayout.SOUTH);
    }

    private void buildInfoPanel(){
        infoPanel = new JPanel();

        currentPlayer = new JLabel("Turn: " + model.getPlayer());
        infoPanel.add(currentPlayer);
    }

    private void buildGamePanel(){
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(model.getTotalRows() + 1, model.getTotalColumns()));
        //We add +1 to row for buttons row

        dropButtons = new JButton[model.getTotalColumns()];
        for(int button = 0; button < dropButtons.length; button++){
            dropButtons[button] = new JButton("Drop");
            dropButtons[button].addActionListener(this);
            gamePanel.add(dropButtons[button]);
        }

        slots = new JPanel[model.getTotalRows()][model.getTotalColumns()];
        for(int row = 0; row < model.getTotalRows(); row++){
            for(int col = 0; col < model.getTotalColumns(); col++){
                slots[row][col] = new JPanel();
                slots[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gamePanel.add(slots[row][col]);
            }
        }
    }

    private void buildOperationPanel(){
        operationPanel = new JPanel();

        exit = new JButton("Exit");
        exit.addActionListener(this);
        operationPanel.add(exit);
        reset = new JButton("Reset");
        reset.addActionListener(this);
        operationPanel.add(reset);
    }

    void resetView(){
        //Setting board empty
        for(int row = 0; row < model.getTotalRows(); row++){
            for(int col = 0; col < model.getTotalColumns(); col++){
                slots[row][col].setBackground(null);
            }
        }
        //Enabling buttons
        for(int button = 0; button < dropButtons.length; button++){
            dropButtons[button].setEnabled(true);
        }

        //Setting Current player to ConnectFour.Player 1
        currentPlayer.setText("Turn: " + model.getPlayer());
    }

    void disableColumn(int column){
        dropButtons[column].setEnabled(false);
    }

    void dropChipInView(int row, int col, Color color){
        slots[row][col].setBackground(color);
    }

    void switchTurns(){
        currentPlayer.setText("Turn: " + model.getPlayer());
    }

    public static void main(String[] args){
        ConnectFourModelInterface model = new ConnectFourModel();
        ConnectFourControllerInterface controller = new ConnectFourController(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == exit){
            controller.exit();
        }else if(e.getSource() == reset){
            controller.reset();
        }else{
            controller.dropChip(getDropButtonPressed(e));
        }
    }

    private int getDropButtonPressed(ActionEvent e){
        for(int but = 0; but < dropButtons.length; but++){
            if(e.getSource() == dropButtons[but]){
                return but;
            }
        }
        return -1;
    }

    @Override
    public void updateGame() {
        dropChipInView(model.getCurrentRow(), model.getCurrentColumn(), model.getColor());
        if(model.getDisableButton() != -1){
            disableColumn(model.getDisableButton());
        }
    }

    @Override
    public void updateWinner() {
        controller.reset();
    }
}
