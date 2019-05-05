package ConnectFour;

/**
	@author Nihar Parikh
	ConnectFour.ConnectFourController.java
*/

/**
 * Medium of connection between view and model
 * This is the CONTROLLER of the mvc architecture
 */
public class ConnectFourController implements ConnectFourControllerInterface {
    private ConnectFourModelInterface model;
    private ConnectFourView view;

    /**
     * Constructor of the ConnectFourController class
     * @param model connect four model
     */
    public ConnectFourController(ConnectFourModelInterface model){
        this.model = model;
        view = new ConnectFourView(model, this);
    }

    /**
     * Call model to drop chip in data
     * Call view to drop chip visually
     * @param column index of the column
     */
    @Override
    public void dropChip(int column) {
        model.dropChip(column);
        view.switchTurns();
    }

    /**
     * Call model to exit the game
     */
    @Override
    public void exit() {
        model.exit();
    }

    /**
     * Call model to reset the game data
     * Call view to reset the game visually
     */
    @Override
    public void reset() {
        model.reset();
        view.resetView();
    }
}
