package Framework.ModularStateMachine;

import Framework.Interface.State;
import Framework.Interface.ValidatableState;

import java.util.ArrayList;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

/**
 * DecisionState makes decisions about which substate to execute based on validity.
 * It can contain other DecisionStates or SequenceStates.
 *
 * Once a DecisionState finishes executing, it returns control directly to the root StateMachine.
 */
public abstract class DecisionState extends ActionState implements ValidatableState {
    private final List<State> decisionSubstates = new ArrayList<>();
    private State currentSubstate;

    /**
     * Constructor for DecisionState.
     *
     * @param machine The state machine this DecisionState is part of.
     */
    public DecisionState(StateMachine machine) {
        super(machine);
    }

    /**
     * Adds a substate to the DecisionState. Only SequenceStates or other DecisionStates are allowed.
     *
     * @param state The substate to add.
     */
    public void addSubState(State state) {
        if (state instanceof SequenceState || state instanceof DecisionState) {
            decisionSubstates.add(state);
        } else {
            throw new IllegalArgumentException("ActionState cannot be directly added under DecisionState. Use SequenceState instead.");
        }
    }

    /**
     * Called when entering the DecisionState. It will search for the first valid substate to run.
     */
    @Override
    public void enter() {
        log("Entering DecisionState: " + this.getClass().getSimpleName());
        findNextValidSubstate();
    }

    /**
     * Executes the current valid substate if found. If no valid substate is found,
     * it marks itself as complete and returns control to the root.
     */
    @Override
    public void execute() {
        if (currentSubstate != null) {
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();  // Execute the valid substate
                return;
            } else {
                currentSubstate.exit();
            }
        }
        markComplete();  // Mark DecisionState complete after one cycle
        returnToRoot();  // Return control to root when done
    }

    /**
     * Called when exiting the DecisionState. It will also exit the current substate
     * if one is still running.
     */
    @Override
    public void exit() {
        log("Exiting DecisionState: " + this.getClass().getSimpleName());
        if (currentSubstate != null) {
            currentSubstate.exit();
        }
    }

    /**
     * Finds the next valid substate in the decisionSubstates list.
     */
    private void findNextValidSubstate() {
        currentSubstate = null;  // Reset current substate

        // Iterate over each substate and check if it's valid
        for (State substate : decisionSubstates) {
            if (substate instanceof ValidatableState && ((ValidatableState) substate).isValid()) {
                currentSubstate = substate;
                currentSubstate.enter();  // Enter the valid substate
                return;
            }
        }

        // No valid substate found, mark this DecisionState as complete
        log("No valid substates found for DecisionState: " + this.getClass().getSimpleName());
        markComplete();
        returnToRoot();
    }

    /**
     * Returns control to the root state machine after the decision process completes.
     * This ensures that after completing, the root regains control instead of the parent DecisionState.
     */
    private void returnToRoot() {
        machine.update();  // Calls the root machine update cycle
    }

    /**
     * Abstract method to determine if the DecisionState itself is valid.
     *
     * @return true if the DecisionState is valid, otherwise false.
     */
    @Override
    public abstract boolean isValid();
}
