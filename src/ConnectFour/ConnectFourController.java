package ConnectFour;

/**
	@author Nihar Parikh
	ConnectFour.ConnectFourController.java
*/

public class ConnectFourController implements ConnectFourControllerInterface {
    private ConnectFourModelInterface model;
    private ConnectFourView view;

    public ConnectFourController(ConnectFourModelInterface model){
        this.model = model;
        view = new ConnectFourView(model, this);
    }

    @Override
    public void dropChip(int column) {
        model.dropChip(column);
        view.switchTurns();
    }

    @Override
    public void exit() {
        model.exit();
    }

    @Override
    public void reset() {
        model.reset();
        view.resetView();
    }
}
