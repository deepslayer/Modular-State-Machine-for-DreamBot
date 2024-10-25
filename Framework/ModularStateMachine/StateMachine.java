package Framework.ModularStateMachine;

import Framework.Interface.State;
import Framework.Interface.ValidatableState;

import java.util.ArrayList;
import java.util.List;

/**
 * The StateMachine class manages a list of states and ensures that
 * the correct state is executed based on validity and completeness.
 *
 * It also ensures control flows correctly based on parent-child relationships.
 */
public class StateMachine {
    private final List<State> states = new ArrayList<>(); // List of states managed by the state machine
    private State currentState; // The current active state

    /**
     * Adds a new state to the state machine.
     *
     * @param state The state to be added.
     */
    public void addState(State state) {
        states.add(state);
    }

    /**
     * Starts the state machine by finding and entering the first valid state.
     */
    public void start() {
        transitionToNextValidState();  // Use transition logic to start the first valid state
    }

    /**
     * Updates the current state of the state machine. If the current state is
     * complete, it transitions to the next valid state.
     */
    public void update() {
        if (currentState != null && !currentState.isComplete()) {
            currentState.execute();  // Keep executing the current state
        } else {
            transitionToNextValidState();  // Move to the next valid state
        }
    }

    /**
     * Transitions from the current state to the next valid state.
     * If no valid states are found, the state machine continues to cycle and checks again
     * in the next loop cycle.
     */
    private void transitionToNextValidState() {
        if (currentState != null) {
            currentState.exit();  // Exit the current state if transitioning
        }

        // Find the next valid state
        for (State state : states) {
            if (state instanceof ValidatableState && ((ValidatableState) state).isValid()) {
                currentState = state;
                currentState.enter();  // Enter the valid state
                return;
            }

            if (state instanceof ActionState) {
                currentState = state;
                currentState.enter();  // Enter the ActionState directly
                return;
            }
        }

        // No valid state was found, but the machine keeps cycling
        currentState = null;  // Set currentState to null, but keep running in next loop
    }

    /**
     * Checks if the state machine is currently running.
     * It is considered running if there are states, even if none are currently valid.
     *
     * @return true if the state machine has states to process, false otherwise.
     */
    public boolean isRunning() {
        return !states.isEmpty();  // Machine is considered running as long as states exist
    }
}