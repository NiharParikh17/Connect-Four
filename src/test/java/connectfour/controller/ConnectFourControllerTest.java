package connectfour.controller;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import connectfour.model.ConnectFourModelInterface;
import connectfour.view.ConnectFourView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
  * Unit tests for {@link ConnectFourController}.
  *
  * <p>The test suite uses Mockito to mock both the {@link ConnectFourModelInterface}
  * and the {@link ConnectFourView}, allowing full isolation of controller logic
  * without creating a live Swing window.</p>
  */
@ExtendWith(MockitoExtension.class)
class ConnectFourControllerTest {

    @Mock
    private ConnectFourModelInterface mockModel;

    @Mock
    private ConnectFourView mockView;

    private ConnectFourController controller;

    @BeforeEach
    void setUp() {
        // Use the package-private constructor to inject mocks, bypassing Swing creation.
        controller = new ConnectFourController(mockModel, mockView);
    }

    // -------------------------------------------------------------------------
    // dropChip
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("dropChip: delegates to model.dropChip with the correct column")
    void dropChip_delegatesToModel() {
        controller.dropChip(3);

        verify(mockModel).dropChip(3);
    }

    @Test
    @DisplayName("dropChip: calls view.switchTurns after delegating to model")
    void dropChip_callsSwitchTurnsOnView() {
        controller.dropChip(0);

        verify(mockView).switchTurns();
    }

    @Test
    @DisplayName("dropChip: model.dropChip is called before view.switchTurns (order)")
    void dropChip_modelCalledBeforeView() {
        var orderVerifier = inOrder(mockModel, mockView);

        controller.dropChip(5);

        orderVerifier.verify(mockModel).dropChip(5);
        orderVerifier.verify(mockView).switchTurns();
    }

    @ParameterizedTest(name = "dropChip delegates for column {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    @DisplayName("dropChip: delegates for every valid column index (0–6)")
    void dropChip_allValidColumns(int column) {
        controller.dropChip(column);

        verify(mockModel).dropChip(column);
        verify(mockView).switchTurns();
    }

    @Test
    @DisplayName("dropChip: no other model or view interactions occur")
    void dropChip_noExtraInteractions() {
        controller.dropChip(2);

        verify(mockModel).dropChip(2);
        verify(mockView).switchTurns();
        verifyNoMoreInteractions(mockModel, mockView);
    }

    // -------------------------------------------------------------------------
    // exit
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("exit: delegates to model.exit")
    void exit_delegatesToModel() {
        controller.exit();

        verify(mockModel).exit();
    }

    @Test
    @DisplayName("exit: no view interactions occur")
    void exit_noViewInteractions() {
        controller.exit();

        verifyNoInteractions(mockView);
    }

    @Test
    @DisplayName("exit: no other model interactions occur")
    void exit_noExtraModelInteractions() {
        controller.exit();

        verify(mockModel).exit();
        verifyNoMoreInteractions(mockModel);
    }

    // -------------------------------------------------------------------------
    // reset
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("reset: delegates to model.reset")
    void reset_delegatesToModel() {
        controller.reset();

        verify(mockModel).reset();
    }

    @Test
    @DisplayName("reset: calls view.resetView after delegating to model")
    void reset_callsResetViewOnView() {
        controller.reset();

        verify(mockView).resetView();
    }

    @Test
    @DisplayName("reset: model.reset is called before view.resetView (order)")
    void reset_modelCalledBeforeView() {
        var orderVerifier = inOrder(mockModel, mockView);

        controller.reset();

        orderVerifier.verify(mockModel).reset();
        orderVerifier.verify(mockView).resetView();
    }

    @Test
    @DisplayName("reset: no other model or view interactions occur")
    void reset_noExtraInteractions() {
        controller.reset();

        verify(mockModel).reset();
        verify(mockView).resetView();
        verifyNoMoreInteractions(mockModel, mockView);
    }

    // -------------------------------------------------------------------------
    // Multiple interactions
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("sequence: multiple dropChip calls each delegate to model and view")
    void multipleDropChips_eachDelegatesIndependently() {
        controller.dropChip(0);
        controller.dropChip(3);
        controller.dropChip(6);

        verify(mockModel).dropChip(0);
        verify(mockModel).dropChip(3);
        verify(mockModel).dropChip(6);
        verify(mockView, times(3)).switchTurns();
    }

    @Test
    @DisplayName("sequence: dropChip followed by reset delegates correctly to both collaborators")
    void dropChipThenReset_delegatesCorrectly() {
        controller.dropChip(2);
        controller.reset();

        verify(mockModel).dropChip(2);
        verify(mockView).switchTurns();
        verify(mockModel).reset();
        verify(mockView).resetView();
    }
}

