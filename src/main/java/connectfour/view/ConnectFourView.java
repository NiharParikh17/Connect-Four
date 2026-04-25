package connectfour.view;

import connectfour.controller.ConnectFourController;
import connectfour.controller.ConnectFourControllerInterface;
import connectfour.model.ConnectFourModel;
import connectfour.model.ConnectFourModelInterface;
import connectfour.observer.ConnectFourObserver;
import connectfour.observer.WinnerObserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * View component of the MVC architecture for the Connect Four game.
 *
 * <p>{@code ConnectFourView} is a Swing {@link JFrame} that renders the full
 * Connect Four GUI and handles all user interaction. The window is divided
 * into three sections:</p>
 * <ol>
 *   <li><b>Info panel (top)</b> — a label showing which player's turn it is.</li>
 *   <li><b>Game panel (center)</b> — a grid of "Drop" buttons (one per column)
 *       and {@link JPanel} slots representing the 6 × 7 board cells. Chip
 *       colors fill in as players take turns.</li>
 *   <li><b>Operation panel (bottom)</b> — "Reset" and "Exit" buttons.</li>
 * </ol>
 *
 * <p>This class implements both observer interfaces so it can react to Model
 * events without polling:</p>
 * <ul>
 *   <li>{@link ConnectFourObserver#updateGame()} — repaints the cell that was
 *       just filled and optionally disables the "Drop" button for a full
 *       column.</li>
 *   <li>{@link WinnerObserver#updateWinner()} — triggers a full game reset
 *       via the Controller after the win/draw dialog is dismissed.</li>
 * </ul>
 *
 * <p>The {@code main} entry point is located in this class and bootstraps the
 * application by creating the Model and Controller.</p>
 */
public class ConnectFourView extends JFrame implements ConnectFourObserver, WinnerObserver, ActionListener {
    private final ConnectFourModelInterface model;
    private final ConnectFourControllerInterface controller;

    private JPanel mainPanel;

    private JPanel infoPanel;
    /** Label displaying whose turn it currently is. */
    private JLabel currentPlayer;

    private JPanel gamePanel;
    /** One "Drop" button per column; disabled automatically when a column is full. */
    private JButton[] dropButtons;
    /** 2-D array of panels representing the board slots; backgrounds are colored per chip. */
    private JPanel[][] slots;

    private JPanel operationPanel;
    private JButton exit;
    private JButton reset;

    /**
     * Constructs the game window, builds all UI components, registers this
     * View as an observer with the Model, packs the frame, and makes it
     * visible.
     *
     * @param model      the game Model providing board dimensions and state;
     *                   must not be {@code null}
     * @param controller the Controller that processes user actions; must not
     *                   be {@code null}
     */
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

    /**
     * Configures top-level frame properties and assembles the main panel.
     */
    private void buildView(){
        setTitle("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildMainPanel();
        add(mainPanel);
    }

    /**
     * Creates the main {@link BorderLayout} panel and attaches the three
     * sub-panels: info (NORTH), game (CENTER), and operations (SOUTH).
     */
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

    /**
     * Builds the info panel containing the turn label initialized to the
     * current player's name.
     */
    private void buildInfoPanel(){
        infoPanel = new JPanel();

        currentPlayer = new JLabel("Turn: " + model.getPlayer());
        infoPanel.add(currentPlayer);
    }

    /**
     * Builds the game panel using a {@link GridLayout} with
     * {@code (totalRows + 1) × totalColumns} cells. The first row contains
     * the "Drop" buttons; the remaining rows contain board slot panels.
     */
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

    /**
     * Builds the operation panel containing the "Exit" and "Reset" buttons.
     */
    private void buildOperationPanel(){
        operationPanel = new JPanel();

        exit = new JButton("Exit");
        exit.addActionListener(this);
        operationPanel.add(exit);
        reset = new JButton("Reset");
        reset.addActionListener(this);
        operationPanel.add(reset);
    }

    /**
     * Resets the entire visual state of the board back to its initial
     * appearance:
     * <ul>
     *   <li>Clears the background color of every slot panel.</li>
     *   <li>Re-enables all "Drop" buttons (including any that were disabled
     *       because their column was full).</li>
     *   <li>Updates the turn label to show the current player (Player 1 after
     *       a reset).</li>
     * </ul>
     *
     * <p>Called by the Controller in response to a reset action or a
     * game-over event.</p>
     */
    public void resetView(){
        //Setting board empty
        for(int row = 0; row < model.getTotalRows(); row++){
            for(int col = 0; col < model.getTotalColumns(); col++){
                slots[row][col].setBackground(null);
            }
        }
        //Enabling buttons
        for (JButton dropButton : dropButtons) {
            dropButton.setEnabled(true);
        }

        //Setting Current player to Player 1
        currentPlayer.setText("Turn: " + model.getPlayer());
    }

    /**
     * Disables the "Drop" button above the specified column.
     * Called when the column's {@code availableSpot} counter drops below zero,
     * meaning the column is completely filled.
     *
     * @param column zero-based index of the column whose button should be
     *               disabled
     */
    void disableColumn(int column){
        dropButtons[column].setEnabled(false);
    }

    /**
     * Paints a chip in the board slot at the given position by setting the
     * slot panel's background color to the player's color.
     *
     * @param row   zero-based row index of the target slot
     * @param col   zero-based column index of the target slot
     * @param color {@link Color} of the player who just placed the chip
     */
    void dropChipInView(int row, int col, Color color){
        slots[row][col].setBackground(color);
    }

    /**
     * Updates the turn label to display the name of the player who will take
     * the next turn. Called by the Controller immediately after a chip is
     * successfully dropped.
     */
    public void switchTurns(){
        currentPlayer.setText("Turn: " + model.getPlayer());
    }

    /**
     * Application entry point. Instantiates the Model and then the Controller
     * (which in turn creates the View), starting the Connect Four game.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args){
        ConnectFourModelInterface model = new ConnectFourModel();
        ConnectFourControllerInterface controller = new ConnectFourController(model);
    }

    /**
     * Handles button click events for all interactive buttons in the View.
     *
     * <ul>
     *   <li>Clicking <b>Exit</b> delegates to {@link ConnectFourControllerInterface#exit()}.</li>
     *   <li>Clicking <b>Reset</b> delegates to {@link ConnectFourControllerInterface#reset()}.</li>
     *   <li>Clicking any <b>Drop</b> button determines the column index and
     *       delegates to {@link ConnectFourControllerInterface#dropChip(int)}.</li>
     * </ul>
     *
     * @param e the action event generated by a button click
     */
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

    /**
     * Determines which "Drop" button was clicked by comparing the event
     * source against each button in the {@code dropButtons} array.
     *
     * @param e the action event from a "Drop" button click
     * @return zero-based column index of the pressed button, or {@code -1} if
     *         the source does not match any known button (should not occur
     *         under normal usage)
     */
    private int getDropButtonPressed(ActionEvent e){
        for(int but = 0; but < dropButtons.length; but++){
            if(e.getSource() == dropButtons[but]){
                return but;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Queries the Model for the row, column, and color of the most recently
     * placed chip, then repaints the corresponding board slot. If the Model
     * reports a column to disable, the matching "Drop" button is also
     * disabled.</p>
     */
    @Override
    public void updateGame() {
        dropChipInView(model.getCurrentRow(), model.getCurrentColumn(), model.getColor());
        if(model.getDisableButton() != -1){
            disableColumn(model.getDisableButton());
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Triggers a full game reset through the Controller. This is called
     * after the win/draw dialog (shown by the Model) is dismissed, so the
     * board returns to its initial state for a new game.</p>
     */
    @Override
    public void updateWinner() {
        controller.reset();
    }
}

