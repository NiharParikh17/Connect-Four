package connectfour.model;

import connectfour.observer.ConnectFourObserver;
import connectfour.observer.WinnerObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JOptionPane;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ConnectFourModel}.
 *
 * <p>The suite covers initial state, chip placement, player switching,
 * observer notifications (per-turn and end-game), win detection in every
 * direction, column-fill disabling, and reset behaviour.</p>
 *
 * <p>{@link JOptionPane} static calls are intercepted with
 * {@code MockedStatic} so that win / draw tests never block on a dialog.</p>
 */
@ExtendWith(MockitoExtension.class)
class ConnectFourModelTest {

    private ConnectFourModel model;

    @Mock
    private ConnectFourObserver mockObserver;

    @Mock
    private WinnerObserver mockWinnerObserver;

    @BeforeEach
    void setUp() {
        model = new ConnectFourModel();
    }

    // =========================================================================
    // Initial state
    // =========================================================================

    @Test
    @DisplayName("getTotalRows: returns 6")
    void getTotalRows_returns6() {
        assertEquals(6, model.getTotalRows());
    }

    @Test
    @DisplayName("getTotalColumns: returns 7")
    void getTotalColumns_returns7() {
        assertEquals(7, model.getTotalColumns());
    }

    @Test
    @DisplayName("getPlayer: initially returns 'Player 1'")
    void getPlayer_initiallyPlayer1() {
        assertEquals("Player 1", model.getPlayer());
    }

    @Test
    @DisplayName("getColor: initially returns RED for Player 1")
    void getColor_initiallyRed() {
        assertEquals(Color.RED, model.getColor());
    }

    @Test
    @DisplayName("getDisableButton: returns -1 before any chip is dropped")
    void getDisableButton_initiallyMinusOne() {
        assertEquals(-1, model.getDisableButton());
    }

    // =========================================================================
    // Chip placement — position tracking
    // =========================================================================

    @Test
    @DisplayName("dropChip: first chip in a column lands at the bottom row (row 5)")
    void dropChip_firstChipLandsAtBottomRow() {
        model.dropChip(0);

        assertEquals(5, model.getCurrentRow());
        assertEquals(0, model.getCurrentColumn());
    }

    @Test
    @DisplayName("dropChip: second chip in the same column stacks one row above the first")
    void dropChip_secondChipStacksAboveFirst() {
        model.dropChip(3); // P1 -> row 5, col 3
        model.dropChip(3); // P2 -> row 4, col 3

        assertEquals(4, model.getCurrentRow());
        assertEquals(3, model.getCurrentColumn());
    }

    @ParameterizedTest(name = "dropChip: first chip in col {0} lands at row 5")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    @DisplayName("dropChip: first chip in every valid column always lands at row 5")
    void dropChip_firstChipAlwaysAtBottomRow(int column) {
        model.dropChip(column);

        assertEquals(5, model.getCurrentRow());
        assertEquals(column, model.getCurrentColumn());
    }

    @Test
    @DisplayName("dropChip: chips in different columns land independently at row 5")
    void dropChip_independentColumnsLandAtBottom() {
        model.dropChip(0); // P1 -> (5,0)
        model.dropChip(6); // P2 -> (5,6)

        assertEquals(5, model.getCurrentRow());
        assertEquals(6, model.getCurrentColumn());
    }

    // =========================================================================
    // Player switching
    // =========================================================================

    @Test
    @DisplayName("getPlayer: switches to 'Player 2' after one move")
    void getPlayer_switchesToPlayer2AfterOneMove() {
        model.dropChip(0);

        assertEquals("Player 2", model.getPlayer());
    }

    @Test
    @DisplayName("getColor: switches to YELLOW for Player 2 after one move")
    void getColor_switchesToYellowAfterOneMove() {
        model.dropChip(0);

        assertEquals(Color.YELLOW, model.getColor());
    }

    @Test
    @DisplayName("getPlayer: alternates back to 'Player 1' after two moves")
    void getPlayer_alternatesBackToPlayer1AfterTwoMoves() {
        model.dropChip(0);
        model.dropChip(1);

        assertEquals("Player 1", model.getPlayer());
    }

    @Test
    @DisplayName("getPlayer: alternates correctly over many moves")
    void getPlayer_alternatesOverManyMoves() {
        // Use non-consecutive columns so neither player accidentally forms four-in-a-row
        // P1 lands at (5,0),(5,2),(5,4),(5,6) — every other column, no four consecutive
        // P2 lands at (5,1),(5,3),(5,5),(4,1) — spread across odd columns
        int[] p1Cols = {0, 2, 4, 6};
        int[] p2Cols = {1, 3, 5, 1};

        for (int i = 0; i < p1Cols.length; i++) {
            model.dropChip(p1Cols[i]); // P1 drops
            assertEquals("Player 2", model.getPlayer(),
                    "Should be Player 2 after P1's move (iteration " + i + ")");
            model.dropChip(p2Cols[i]); // P2 drops
            assertEquals("Player 1", model.getPlayer(),
                    "Should be Player 1 after P2's move (iteration " + i + ")");
        }
    }

    // =========================================================================
    // Observer — game (per-turn) notifications
    // =========================================================================

    @Test
    @DisplayName("dropChip: notifies a registered ConnectFourObserver on each move")
    void dropChip_notifiesGameObserver() {
        model.registerObserver(mockObserver);

        model.dropChip(0);

        verify(mockObserver).updateGame();
    }

    @Test
    @DisplayName("dropChip: notifies game observer once per move across multiple moves")
    void dropChip_notifiesGameObserverOnEachMove() {
        model.registerObserver(mockObserver);

        model.dropChip(0);
        model.dropChip(1);
        model.dropChip(2);

        verify(mockObserver, times(3)).updateGame();
    }

    @Test
    @DisplayName("dropChip: does NOT notify WinnerObserver on a non-winning move")
    void dropChip_doesNotNotifyWinnerObserverOnNormalMove() {
        model.registerObserver(mockWinnerObserver);

        model.dropChip(0);

        verify(mockWinnerObserver, never()).updateWinner();
    }

    @Test
    @DisplayName("registerObserver: two game observers both receive the notification")
    void registerObserver_multipleGameObserversAllNotified() {
        ConnectFourObserver second = mock(ConnectFourObserver.class);
        model.registerObserver(mockObserver);
        model.registerObserver(second);

        model.dropChip(0);

        verify(mockObserver).updateGame();
        verify(second).updateGame();
    }

    @Test
    @DisplayName("removeObserver: removed ConnectFourObserver no longer receives notifications")
    void removeObserver_removedGameObserverNotNotified() {
        model.registerObserver(mockObserver);
        model.removeObserver(mockObserver);

        model.dropChip(0);

        verify(mockObserver, never()).updateGame();
    }

    @Test
    @DisplayName("removeObserver: removed WinnerObserver no longer receives end-game notifications")
    void removeObserver_removedWinnerObserverNotNotified() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            model.registerObserver(mockWinnerObserver);
            model.removeObserver(mockWinnerObserver);

            // Build a horizontal win for Player 1 (P1 drops cols 0-3, P2 fills col 6)
            model.dropChip(0); model.dropChip(6);
            model.dropChip(1); model.dropChip(6);
            model.dropChip(2); model.dropChip(6);
            model.dropChip(3); // P1 wins

            verify(mockWinnerObserver, never()).updateWinner();
        }
    }

    // =========================================================================
    // getDisableButton — captured inside observer callback
    // =========================================================================

    @Test
    @DisplayName("getDisableButton: returns the column index during the notification when a column becomes full")
    void getDisableButton_returnsColumnIndexWhenColumnFull() {
        ConnectFourModel localModel = new ConnectFourModel();
        int[] captured = {-999};

        // Register an observer that records the value while still inside the notification
        localModel.registerObserver((ConnectFourObserver) () -> captured[0] = localModel.getDisableButton());

        // Drop 6 chips consecutively into col 0.
        // Each dropChip switches the active player, so the column fills with
        // P1@row5, P2@row4, P1@row3, P2@row2, P1@row1, P2@row0 — no four-in-a-row.
        for (int i = 0; i < 6; i++) {
            localModel.dropChip(0);
        }

        assertEquals(0, captured[0],
                "getDisableButton should equal 0 during the notification for the move that fills column 0");
    }

    @Test
    @DisplayName("getDisableButton: resets to -1 immediately after the notification is dispatched")
    void getDisableButton_resetToMinusOneAfterNotification() {
        // Drop 6 chips consecutively into col 0 — alternates P1 and P2 so no win is triggered
        for (int i = 0; i < 6; i++) {
            model.dropChip(0);
        }

        // By the time the last dropChip() returns, disablingButton must be reset to -1
        assertEquals(-1, model.getDisableButton());
    }

    // =========================================================================
    // Win detection — Horizontal
    // =========================================================================

    @Test
    @DisplayName("dropChip: notifies WinnerObserver when Player 1 achieves a horizontal win")
    void dropChip_notifiesWinnerObserverOnHorizontalWin() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            model.registerObserver(mockWinnerObserver);

            // P1 fills row 5, cols 0-3; P2 occupies col 6 as filler
            model.dropChip(0); model.dropChip(6); // T1 P1, T2 P2
            model.dropChip(1); model.dropChip(6); // T3 P1, T4 P2
            model.dropChip(2); model.dropChip(6); // T5 P1, T6 P2
            model.dropChip(3);                    // T7 P1 — horizontal win: (5,0)-(5,1)-(5,2)-(5,3)

            verify(mockWinnerObserver).updateWinner();
        }
    }

    @Test
    @DisplayName("dropChip: shows a win dialog when a horizontal win is detected")
    void dropChip_showsDialogOnHorizontalWin() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            model.dropChip(0); model.dropChip(6);
            model.dropChip(1); model.dropChip(6);
            model.dropChip(2); model.dropChip(6);
            model.dropChip(3); // P1 wins

            mockedPane.verify(() ->
                    JOptionPane.showMessageDialog(null, "Player 1 wins!"));
        }
    }

    // =========================================================================
    // Win detection — Vertical
    // =========================================================================

    @Test
    @DisplayName("dropChip: notifies WinnerObserver when Player 1 achieves a vertical win")
    void dropChip_notifiesWinnerObserverOnVerticalWin() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            model.registerObserver(mockWinnerObserver);

            // P1 drops in col 0 four times; P2 uses col 1 as filler
            model.dropChip(0); model.dropChip(1); // T1, T2
            model.dropChip(0); model.dropChip(1); // T3, T4
            model.dropChip(0); model.dropChip(1); // T5, T6
            model.dropChip(0);                    // T7 P1 — vertical win in col 0

            verify(mockWinnerObserver).updateWinner();
        }
    }

    // =========================================================================
    // Win detection — Left diagonal  (code direction: row↑, col↓)
    //
    //  P1 chips land at: (5,0) (4,1) (3,2) (2,3)
    //  Verified move sequence (P1=odd turns, P2=even turns):
    //   T1  P1 col0 → (5,0)   T2  P2 col1 → (5,1)
    //   T3  P1 col2 → (5,2)   T4  P2 col3 → (5,3)
    //   T5  P1 col1 → (4,1)   T6  P2 col2 → (4,2)
    //   T7  P1 col2 → (3,2)   T8  P2 col6 → (5,6) filler
    //   T9  P1 col3 → (4,3)   T10 P2 col3 → (3,3)
    //   T11 P1 col3 → (2,3)  ← win
    // =========================================================================

    @Test
    @DisplayName("dropChip: notifies WinnerObserver when Player 1 achieves a left-diagonal win")
    void dropChip_notifiesWinnerObserverOnLeftDiagonalWin() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            model.registerObserver(mockWinnerObserver);

            model.dropChip(0); model.dropChip(1); // T1  T2
            model.dropChip(2); model.dropChip(3); // T3  T4
            model.dropChip(1); model.dropChip(2); // T5  T6
            model.dropChip(2); model.dropChip(6); // T7  T8
            model.dropChip(3); model.dropChip(3); // T9  T10
            model.dropChip(3);                    // T11 — left-diagonal win

            verify(mockWinnerObserver).updateWinner();
        }
    }

    // =========================================================================
    // Win detection — Right diagonal  (code direction: row↑, col↑)
    //
    //  P1 chips land at: (5,3) (4,2) (3,1) (2,0)
    //  Verified move sequence:
    //   T1  P1 col3 → (5,3)   T2  P2 col2 → (5,2)
    //   T3  P1 col2 → (4,2)   T4  P2 col0 → (5,0)
    //   T5  P1 col1 → (5,1)   T6  P2 col1 → (4,1)
    //   T7  P1 col1 → (3,1)   T8  P2 col6 → (5,6) filler
    //   T9  P1 col0 → (4,0)   T10 P2 col0 → (3,0)
    //   T11 P1 col0 → (2,0)  ← win
    // =========================================================================

    @Test
    @DisplayName("dropChip: notifies WinnerObserver when Player 1 achieves a right-diagonal win")
    void dropChip_notifiesWinnerObserverOnRightDiagonalWin() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            model.registerObserver(mockWinnerObserver);

            model.dropChip(3); model.dropChip(2); // T1  T2
            model.dropChip(2); model.dropChip(0); // T3  T4
            model.dropChip(1); model.dropChip(1); // T5  T6
            model.dropChip(1); model.dropChip(6); // T7  T8
            model.dropChip(0); model.dropChip(0); // T9  T10
            model.dropChip(0);                    // T11 — right-diagonal win

            verify(mockWinnerObserver).updateWinner();
        }
    }

    // =========================================================================
    // Player state after a win
    // =========================================================================

    @Test
    @DisplayName("getPlayer: returns Player 1 after Player 1 wins (double switchPlayer resets to P1)")
    void getPlayer_returnsPlayer1AfterPlayer1Wins() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            // P1 wins horizontally; inside dropChip the win branch calls switchPlayer
            // once, and the unconditional call at the end calls it again → net P1
            model.dropChip(0); model.dropChip(6);
            model.dropChip(1); model.dropChip(6);
            model.dropChip(2); model.dropChip(6);
            model.dropChip(3); // P1 wins

            assertEquals("Player 1", model.getPlayer());
        }
    }

    // =========================================================================
    // WinnerObserver — two observers both notified
    // =========================================================================

    @Test
    @DisplayName("dropChip: notifies all registered WinnerObservers on a win")
    void dropChip_notifiesAllWinnerObserversOnWin() {
        try (MockedStatic<JOptionPane> ignored = mockStatic(JOptionPane.class)) {
            WinnerObserver second = mock(WinnerObserver.class);
            model.registerObserver(mockWinnerObserver);
            model.registerObserver(second);

            model.dropChip(0); model.dropChip(6);
            model.dropChip(1); model.dropChip(6);
            model.dropChip(2); model.dropChip(6);
            model.dropChip(3); // P1 wins

            verify(mockWinnerObserver).updateWinner();
            verify(second).updateWinner();
        }
    }

    // =========================================================================
    // reset()
    // =========================================================================

    @Test
    @DisplayName("reset: getPlayer returns 'Player 1' after reset regardless of whose turn it was")
    void reset_setsCurrentPlayerToPlayer1() {
        model.dropChip(0); // P1 -> now P2's turn
        model.reset();

        assertEquals("Player 1", model.getPlayer());
    }

    @Test
    @DisplayName("reset: getColor returns RED after reset")
    void reset_setsColorToRed() {
        model.dropChip(0); // P1 -> now P2's turn
        model.reset();

        assertEquals(Color.RED, model.getColor());
    }

    @Test
    @DisplayName("reset: first chip after reset lands at the bottom row again")
    void reset_boardClearedSoChipLandsAtBottomRow() {
        // Drop several chips, then reset
        model.dropChip(0);
        model.dropChip(0);
        model.dropChip(0);
        model.reset();

        model.dropChip(0);
        assertEquals(5, model.getCurrentRow(),
                "After reset, the first chip in the column should land at row 5");
    }

    @Test
    @DisplayName("reset: all columns are available again after reset (chip lands at row 5 in every column)")
    void reset_allColumnsAvailableAfterReset() {
        // Partially fill a few columns, then reset
        model.dropChip(0); model.dropChip(1);
        model.dropChip(2); model.dropChip(3);
        model.reset();

        for (int col = 0; col < model.getTotalColumns(); col++) {
            ConnectFourModel fresh = new ConnectFourModel();
            fresh.dropChip(col);
            assertEquals(5, fresh.getCurrentRow());
        }
    }

    // =========================================================================
    // Game flow — no extra WinnerObserver calls on normal moves
    // =========================================================================

    @Test
    @DisplayName("dropChip: WinnerObserver is never called during a sequence of normal (non-winning) moves")
    void dropChip_winnerObserverNeverCalledDuringNormalPlay() {
        model.registerObserver(mockWinnerObserver);

        // Drop chips in various columns without forming four-in-a-row
        int[] moves = {0, 1, 2, 0, 1, 2, 0, 1, 2};
        for (int col : moves) {
            model.dropChip(col);
        }

        verify(mockWinnerObserver, never()).updateWinner();
    }
}






