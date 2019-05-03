/**
	@author Nihar Parikh
	ConnectFourModelInterface.java
*/

import java.awt.*;

public interface ConnectFourModelInterface {
    int getTotalRows();
    int getTotalColumns();
    String getPlayer();
    int getCurrentRow();
    int getCurrentColumn();
    Color getColor();
    int getDisableButton();

    void dropChip(int column);

    void exit();
    void reset();

    void registerObserver(ConnectFourObserver o);
    void registerObserver(WinnerObserver o);
    void removeObserver(ConnectFourObserver o);
    void removeObserver(WinnerObserver o);
}
