package connectfour.view;

import connectfour.controller.ConnectFourControllerInterface;
import connectfour.model.ConnectFourModelInterface;
import connectfour.observer.ConnectFourObserver;
import connectfour.observer.WinnerObserver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ConnectFourView}.
 *
 * <p>Both the {@link ConnectFourModelInterface} and
 * {@link ConnectFourControllerInterface} are mocked so no live game logic
 * runs and no {@link JOptionPane} dialog can block the test.</p>
 *
 * <p>All Swing operations are dispatched on the Event Dispatch Thread via
 * {@link SwingUtilities#invokeAndWait} to keep the tests thread-safe. The
 * frame is hidden immediately after construction and disposed in
 * {@code @AfterEach}.</p>
 *
 * <p>Private Swing fields ({@code slots}, {@code dropButtons}, etc.) are
 * accessed through reflection so that internal state can be asserted without
 * changing the production API.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConnectFourViewTest {

    @Mock
    private ConnectFourModelInterface mockModel;

    @Mock
    private ConnectFourControllerInterface mockController;

    private ConnectFourView view;

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @BeforeEach
    void setUp() throws Exception {
        // Provide values the constructor calls on the model
        when(mockModel.getTotalRows()).thenReturn(6);
        when(mockModel.getTotalColumns()).thenReturn(7);
        when(mockModel.getPlayer()).thenReturn("Player 1");

        // Build the real view on the EDT (avoids threading issues with Swing)
        SwingUtilities.invokeAndWait(() -> {
            view = new ConnectFourView(mockModel, mockController);
            view.setVisible(false); // suppress the window immediately
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (view != null) {
                view.dispose();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Reflection helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the value of the named {@code private} field from the view under
     * test.
     */
    @SuppressWarnings("unchecked")
    private <T> T field(String name) throws Exception {
        Field f = ConnectFourView.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(view);
    }

    // =========================================================================
    // Constructor — observer registration
    // =========================================================================

    @Test
    @DisplayName("constructor: registers the view as a ConnectFourObserver with the model")
    void constructor_registersAsConnectFourObserver() {
        verify(mockModel).registerObserver((ConnectFourObserver) view);
    }

    @Test
    @DisplayName("constructor: registers the view as a WinnerObserver with the model")
    void constructor_registersAsWinnerObserver() {
        verify(mockModel).registerObserver((WinnerObserver) view);
    }

    // =========================================================================
    // Constructor — initial label
    // =========================================================================

    @Test
    @DisplayName("constructor: turn label is initialised to 'Turn: Player 1'")
    void constructor_initialLabelShowsPlayer1() throws Exception {
        JLabel label = field("currentPlayer");
        assertEquals("Turn: Player 1", label.getText());
    }

    // =========================================================================
    // Constructor — board dimensions
    // =========================================================================

    @Test
    @DisplayName("constructor: creates 7 drop buttons (one per column)")
    void constructor_creates7DropButtons() throws Exception {
        JButton[] dropButtons = field("dropButtons");
        assertEquals(7, dropButtons.length);
    }

    @Test
    @DisplayName("constructor: creates a 6×7 slot grid")
    void constructor_creates6x7SlotGrid() throws Exception {
        JPanel[][] slots = field("slots");
        assertEquals(6, slots.length);
        assertEquals(7, slots[0].length);
    }

    @Test
    @DisplayName("constructor: all drop buttons are enabled initially")
    void constructor_allDropButtonsEnabledInitially() throws Exception {
        JButton[] dropButtons = field("dropButtons");
        for (int i = 0; i < dropButtons.length; i++) {
            assertTrue(dropButtons[i].isEnabled(),
                    "Drop button " + i + " should be enabled on construction");
        }
    }

    // =========================================================================
    // switchTurns
    // =========================================================================

    @Test
    @DisplayName("switchTurns: updates the turn label to the current player returned by the model")
    void switchTurns_updatesLabelText() throws Exception {
        when(mockModel.getPlayer()).thenReturn("Player 2");

        SwingUtilities.invokeAndWait(() -> view.switchTurns());

        JLabel label = field("currentPlayer");
        assertEquals("Turn: Player 2", label.getText());
    }

    @Test
    @DisplayName("switchTurns: uses the text returned by model.getPlayer()")
    void switchTurns_delegatesToModelGetPlayer() throws Exception {
        when(mockModel.getPlayer()).thenReturn("Player 1");

        SwingUtilities.invokeAndWait(() -> view.switchTurns());

        verify(mockModel, atLeastOnce()).getPlayer();
    }

    // =========================================================================
    // dropChipInView
    // =========================================================================

    @Test
    @DisplayName("dropChipInView: paints the target slot with RED")
    void dropChipInView_setsSlotBackgroundRed() throws Exception {
        SwingUtilities.invokeAndWait(() -> view.dropChipInView(5, 3, Color.RED));

        JPanel[][] slots = field("slots");
        assertEquals(Color.RED, slots[5][3].getBackground());
    }

    @Test
    @DisplayName("dropChipInView: paints the target slot with YELLOW")
    void dropChipInView_setsSlotBackgroundYellow() throws Exception {
        SwingUtilities.invokeAndWait(() -> view.dropChipInView(4, 6, Color.YELLOW));

        JPanel[][] slots = field("slots");
        assertEquals(Color.YELLOW, slots[4][6].getBackground());
    }

    @Test
    @DisplayName("dropChipInView: only the targeted slot is painted; adjacent slots are unaffected")
    void dropChipInView_onlyTargetSlotIsPainted() throws Exception {
        SwingUtilities.invokeAndWait(() -> view.dropChipInView(5, 0, Color.RED));

        JPanel[][] slots = field("slots");
        // The adjacent slot in the same row should still have no explicitly set background
        assertNotEquals(Color.RED, slots[5][1].getBackground(),
                "Slot [5][1] must not share the color set for [5][0]");
    }

    @Test
    @DisplayName("dropChipInView: correctly places chips in multiple distinct cells")
    void dropChipInView_multipleDistinctCells() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            view.dropChipInView(5, 0, Color.RED);
            view.dropChipInView(5, 1, Color.YELLOW);
            view.dropChipInView(4, 0, Color.RED);
        });

        JPanel[][] slots = field("slots");
        assertEquals(Color.RED,   slots[5][0].getBackground());
        assertEquals(Color.YELLOW, slots[5][1].getBackground());
        assertEquals(Color.RED,   slots[4][0].getBackground());
    }

    // =========================================================================
    // disableColumn
    // =========================================================================

    @Test
    @DisplayName("disableColumn: disables the drop button at the specified column")
    void disableColumn_disablesTargetButton() throws Exception {
        SwingUtilities.invokeAndWait(() -> view.disableColumn(2));

        JButton[] dropButtons = field("dropButtons");
        assertFalse(dropButtons[2].isEnabled());
    }

    @ParameterizedTest(name = "disableColumn({0}): disables button {0} only")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    @DisplayName("disableColumn: disables the correct single button for every column index")
    void disableColumn_correctButtonDisabledForEveryColumn(int col) throws Exception {
        SwingUtilities.invokeAndWait(() -> view.disableColumn(col));

        JButton[] dropButtons = field("dropButtons");
        assertFalse(dropButtons[col].isEnabled(),
                "Button at col " + col + " should be disabled");
        for (int i = 0; i < dropButtons.length; i++) {
            if (i != col) {
                assertTrue(dropButtons[i].isEnabled(),
                        "Button at col " + i + " should still be enabled");
            }
        }
    }

    // =========================================================================
    // resetView
    // =========================================================================

    @Test
    @DisplayName("resetView: clears explicitly set backgrounds from all slots")
    void resetView_clearsAllSlotBackgrounds() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            view.dropChipInView(5, 0, Color.RED);
            view.dropChipInView(3, 2, Color.YELLOW);
            view.resetView();
        });

        JPanel[][] slots = field("slots");
        // After setBackground(null) the background is no longer "set"
        assertFalse(slots[5][0].isBackgroundSet(),
                "Slot [5][0] should have no explicitly set background after reset");
        assertFalse(slots[3][2].isBackgroundSet(),
                "Slot [3][2] should have no explicitly set background after reset");
    }

    @Test
    @DisplayName("resetView: re-enables all drop buttons including previously disabled ones")
    void resetView_reEnablesAllDropButtons() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            view.disableColumn(0);
            view.disableColumn(6);
            view.resetView();
        });

        JButton[] dropButtons = field("dropButtons");
        for (int i = 0; i < dropButtons.length; i++) {
            assertTrue(dropButtons[i].isEnabled(),
                    "Drop button " + i + " should be re-enabled after resetView");
        }
    }

    @Test
    @DisplayName("resetView: updates the turn label to the model's current player")
    void resetView_updatesTurnLabel() throws Exception {
        // Model now reports Player 1 again after the model-side reset
        when(mockModel.getPlayer()).thenReturn("Player 1");

        SwingUtilities.invokeAndWait(() -> view.resetView());

        JLabel label = field("currentPlayer");
        assertEquals("Turn: Player 1", label.getText());
    }

    @Test
    @DisplayName("resetView: repeated resets always clear every slot and re-enable every button")
    void resetView_idempotent() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            view.dropChipInView(5, 3, Color.YELLOW);
            view.disableColumn(3);
            view.resetView();
            // Second reset on a clean board should also be safe
            view.resetView();
        });

        JButton[] dropButtons = field("dropButtons");
        JPanel[][]  slots      = field("slots");
        for (JButton btn : dropButtons) {
            assertTrue(btn.isEnabled());
        }
        assertFalse(slots[5][3].isBackgroundSet());
    }

    // =========================================================================
    // updateGame (ConnectFourObserver callback)
    // =========================================================================

    @Test
    @DisplayName("updateGame: reads position and color from the model and paints the correct slot")
    void updateGame_paintChipAtModelPosition() throws Exception {
        when(mockModel.getCurrentRow()).thenReturn(5);
        when(mockModel.getCurrentColumn()).thenReturn(3);
        when(mockModel.getColor()).thenReturn(Color.RED);
        when(mockModel.getDisableButton()).thenReturn(-1);

        SwingUtilities.invokeAndWait(() -> view.updateGame());

        JPanel[][] slots = field("slots");
        assertEquals(Color.RED, slots[5][3].getBackground());
    }

    @Test
    @DisplayName("updateGame: disables the column reported by model.getDisableButton()")
    void updateGame_disablesColumnWhenFull() throws Exception {
        when(mockModel.getCurrentRow()).thenReturn(0);
        when(mockModel.getCurrentColumn()).thenReturn(4);
        when(mockModel.getColor()).thenReturn(Color.YELLOW);
        when(mockModel.getDisableButton()).thenReturn(4);

        SwingUtilities.invokeAndWait(() -> view.updateGame());

        JButton[] dropButtons = field("dropButtons");
        assertFalse(dropButtons[4].isEnabled());
    }

    @Test
    @DisplayName("updateGame: does not disable any button when getDisableButton() returns -1")
    void updateGame_noButtonDisabledWhenColumnNotFull() throws Exception {
        when(mockModel.getCurrentRow()).thenReturn(4);
        when(mockModel.getCurrentColumn()).thenReturn(1);
        when(mockModel.getColor()).thenReturn(Color.RED);
        when(mockModel.getDisableButton()).thenReturn(-1);

        SwingUtilities.invokeAndWait(() -> view.updateGame());

        JButton[] dropButtons = field("dropButtons");
        for (int i = 0; i < dropButtons.length; i++) {
            assertTrue(dropButtons[i].isEnabled(),
                    "Button " + i + " must stay enabled when no column is full");
        }
    }

    // =========================================================================
    // updateWinner (WinnerObserver callback)
    // =========================================================================

    @Test
    @DisplayName("updateWinner: delegates to controller.reset()")
    void updateWinner_callsControllerReset() throws Exception {
        SwingUtilities.invokeAndWait(() -> view.updateWinner());

        verify(mockController).reset();
    }

    @Test
    @DisplayName("updateWinner: does not call any other controller method")
    void updateWinner_noOtherControllerInteractions() throws Exception {
        SwingUtilities.invokeAndWait(() -> view.updateWinner());

        verify(mockController).reset();
        verifyNoMoreInteractions(mockController);
    }

    // =========================================================================
    // actionPerformed — Exit button
    // =========================================================================

    @Test
    @DisplayName("Exit button click: delegates to controller.exit()")
    void exitButton_click_callsControllerExit() throws Exception {
        JButton exitBtn = field("exit");

        SwingUtilities.invokeAndWait(exitBtn::doClick);

        verify(mockController).exit();
    }

    @Test
    @DisplayName("Exit button click: does not call any other controller method")
    void exitButton_click_noOtherControllerInteractions() throws Exception {
        JButton exitBtn = field("exit");

        SwingUtilities.invokeAndWait(exitBtn::doClick);

        verify(mockController).exit();
        verifyNoMoreInteractions(mockController);
    }

    // =========================================================================
    // actionPerformed — Reset button
    // =========================================================================

    @Test
    @DisplayName("Reset button click: delegates to controller.reset()")
    void resetButton_click_callsControllerReset() throws Exception {
        JButton resetBtn = field("reset");

        SwingUtilities.invokeAndWait(resetBtn::doClick);

        verify(mockController).reset();
    }

    @Test
    @DisplayName("Reset button click: does not call any other controller method")
    void resetButton_click_noOtherControllerInteractions() throws Exception {
        JButton resetBtn = field("reset");

        SwingUtilities.invokeAndWait(resetBtn::doClick);

        verify(mockController).reset();
        verifyNoMoreInteractions(mockController);
    }

    // =========================================================================
    // actionPerformed — Drop buttons
    // =========================================================================

    @ParameterizedTest(name = "Drop button col {0}: delegates to controller.dropChip({0})")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    @DisplayName("Drop button click: delegates to controller.dropChip with the correct column index")
    void dropButton_click_callsControllerDropChipWithCorrectColumn(int col) throws Exception {
        JButton[] dropButtons = field("dropButtons");

        SwingUtilities.invokeAndWait(() -> dropButtons[col].doClick());

        verify(mockController).dropChip(col);
    }

    @Test
    @DisplayName("Drop button click: each drop button calls dropChip exactly once")
    void dropButton_click_exactlyOnce() throws Exception {
        JButton[] dropButtons = field("dropButtons");

        SwingUtilities.invokeAndWait(() -> dropButtons[0].doClick());

        verify(mockController, times(1)).dropChip(0);
    }

    @Test
    @DisplayName("Drop button click: multiple different columns each delegate independently")
    void dropButtons_multipleClicks_eachDelegatesCorrectly() throws Exception {
        JButton[] dropButtons = field("dropButtons");

        SwingUtilities.invokeAndWait(() -> {
            dropButtons[0].doClick();
            dropButtons[3].doClick();
            dropButtons[6].doClick();
        });

        verify(mockController).dropChip(0);
        verify(mockController).dropChip(3);
        verify(mockController).dropChip(6);
    }
}

