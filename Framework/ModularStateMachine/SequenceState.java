package Framework.ModularStateMachine;

import Framework.Interface.State;
import Framework.Interface.ValidatableState;
import java.util.ArrayList;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

/**
 * SequenceState manages a sequence of substates that are executed sequentially.
 * It does not check for validity (isValid()); it simply executes each substate in order.
 *
 * When a SequenceState completes:
 * - If the parent is a SequenceState or SelectorState, control returns to that parent.
 * - If the parent is a DecisionState, control returns to the root StateMachine.
 */
public abstract class SequenceState extends ActionState implements ValidatableState {
    private final List<State> substates = new ArrayList<>();  // List of substates (can be ActionState or SequenceState)
    private State currentSubstate;  // The currently active substate being executed
    private int currentSubstateIndex = 0;  // Keeps track of the current substate index
    private State parentState;  // Tracks the parent state (could be a SequenceState, SelectorState, or DecisionState)

    /**
     * Constructor for SequenceState.
     *
     * @param machine The state machine that this SequenceState is part of.
     */
    public SequenceState(StateMachine machine) {
        super(machine);
    }

    /**
     * Adds a substate to the SequenceState. These substates can be ActionStates or SequenceStates.
     * Automatically sets the parent based on the state type.
     *
     * @param substate The substate to add.
     */
    public void addSubState(State substate) {
        substates.add(substate);
        if (substate instanceof SequenceState) {
            ((SequenceState) substate).setParent(this);  // Set the parent to this SequenceState
        } else if (substate instanceof SelectorState) {
            ((SelectorState) substate).setParent(this);  // Set parent to SelectorState if applicable
        }
    }



    /**
     * Sets the parent state of this SequenceState.
     *
     * @param parent The parent state, either a SequenceState, SelectorState, or a DecisionState.
     */
    protected void setParent(State parent) {
        if (!(parent instanceof SequenceState || parent instanceof SelectorState || parent instanceof DecisionState || parent == null)) {
            throw new IllegalArgumentException("Invalid parent type for SequenceState: " + parent.getClass().getSimpleName());
        }
        this.parentState = parent;
    }

    /**
     * Final enter method that calls the subclass-specific onEnter() logic.
     */
    @Override
    public final void enter() {
        if (substates.isEmpty()) {
            log("No substates in SequenceState: " + this.getClass().getSimpleName() + ". Marking as complete.");
            markComplete();
            returnControl();  // Immediately return control if empty
        } else {
            log("Enter " + this.getClass().getSimpleName());
            onEnter();
            currentSubstateIndex = 0;  // Start at the first substate
            currentSubstate = substates.get(currentSubstateIndex);
            currentSubstate.enter();  // Enter the first substate
            resetCompletion();  // Reset the complete flag when entering the sequence
        }
    }

    /**
     * Final exit method that calls the subclass-specific onExit() logic.
     */
    @Override
    public final void exit() {
        if (currentSubstate != null && !currentSubstate.isComplete()) {
            currentSubstate.exit();  // Exit the current substate if it hasn't finished yet
        }
        log("Exit " + this.getClass().getSimpleName());
        onExit();
    }

    /**
     * Determines if the SequenceState should be executed based on its own validity conditions.
     * This method should be overridden by subclasses to provide specific validity logic.
     *
     * @return true if this SequenceState should be executed, otherwise false.
     */
    @Override
    public abstract boolean isValid();

    /**
     * Abstract method for subclass-specific behavior on enter.
     * Subclasses are required to override this method.
     */
    @Override
    protected abstract void onEnter();

    /**
     * Abstract method for subclass-specific behavior on exit.
     * Subclasses are required to override this method.
     */
    @Override
    protected abstract void onExit();

    /**
     * Executes the current substate (either an ActionState or another SequenceState).
     * If the current substate completes, it moves to the next one until all substates have been executed.
     */
    @Override
    public void execute() {
        if (currentSubstate != null) {
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();  // Execute the current substate
            } else {
                currentSubstate.exit();  // Exit the current substate after completion
                currentSubstateIndex++;  // Move to the next substate

                // Check if there are more substates to execute
                if (currentSubstateIndex < substates.size()) {
                    currentSubstate = substates.get(currentSubstateIndex);
                    currentSubstate.enter();  // Enter the next substate
                } else {
                    markComplete();  // Mark the sequence as complete when all substates are done
                    returnControl();  // Return control to parent or root
                }
            }
        }
    }

    /**
     * Checks if the SequenceState is complete. It is marked complete when all substates have been executed.
     *
     * @return true if all substates have been completed, false otherwise.
     */
    @Override
    public boolean isComplete() {
        return super.isComplete();  // Use inherited isComplete() from ActionState
    }

    /**
     * Returns control based on the type of parent state:
     * - If the parent is a SequenceState or SelectorState, return control to that parent.
     * - If the parent is a DecisionState, return control to the root StateMachine.
     * - If the parent is null, return control to the root StateMachine.
     */
    protected void returnControl() {
        if (parentState instanceof SequenceState || parentState instanceof SelectorState) {
            parentState.execute();  // Return control to the parent SequenceState or SelectorState
        } else if (parentState instanceof DecisionState || parentState == null) {
            machine.update();  // Return control to the root StateMachine if DecisionState or no parent
        }
    }
}
