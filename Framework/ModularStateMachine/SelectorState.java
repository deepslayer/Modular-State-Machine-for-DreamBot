package Framework.ModularStateMachine;

import Framework.Interface.State;
import java.util.ArrayList;
import java.util.List;
import static org.dreambot.api.utilities.Logger.log;

/**
 * SelectorState evaluates a set of substates (SequenceStates) and executes the first valid one.
 * It can only be used directly within a SequenceState, and its children can only be SequenceState instances.
 * SelectorState will return control to its parent once a valid substate completes.
 */
public abstract class SelectorState extends ActionState {
    private final List<SequenceState> selectorSubstates = new ArrayList<>();  // List of potential SequenceState substates
    private SequenceState currentSubstate;  // Currently active substate
    private State parentState;  // Tracks the parent state, could be SequenceState or DecisionState

    /**
     * Constructor for SelectorState.
     *
     * @param machine The state machine that this SelectorState is part of.
     */
    public SelectorState(StateMachine machine) {
        super(machine);
    }

    /**
     * Adds a substate to SelectorState.
     * Only SequenceState types are allowed as children.
     *
     * @param state The substate to add, must be a SequenceState.
     */
    public void addSubState(SequenceState state) {
        selectorSubstates.add(state);
        state.setParent(this);  // Set this SelectorState as the parent of the added substate
    }

    /**
     * Sets the parent state of this SelectorState.
     *
     * @param parent The parent state, either a SequenceState, SelectorState, or DecisionState.
     */
    protected void setParent(State parent) {
        if (!(parent instanceof SequenceState || parent instanceof SelectorState || parent instanceof DecisionState || parent == null)) {
            throw new IllegalArgumentException("Invalid parent type for SelectorState: " + parent.getClass().getSimpleName());
        }
        this.parentState = parent;
    }

    /**
     * Final enter method that calls the subclass-specific onEnter() logic.
     */
    @Override
    public final void enter() {
        log("Enter " + this.getClass().getSimpleName());
        resetCompletion();
        onEnter();
        findNextValidSubstate();
    }

    /**
     * Final exit method that calls the subclass-specific onExit() logic.
     */
    @Override
    public final void exit() {
        if (currentSubstate != null) {
            currentSubstate.exit();
        }
        log("Exit " + this.getClass().getSimpleName());
        onExit();
    }

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
     * Executes the current valid substate if found. If no valid substate is found,
     * it marks itself as complete and returns control to the parent state.
     */
    @Override
    public void execute() {
        if (currentSubstate != null) {
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();  // Execute the current valid substate
            } else {
                currentSubstate.exit();  // Exit the completed substate
                markComplete();
                returnToParent();  // Return control to the parent
            }
        } else {
            markComplete();  // Mark SelectorState as complete if no valid substate
            returnToParent();
        }
    }

    /**
     * Finds the next valid substate in the selectorSubstates list.
     */
    protected void findNextValidSubstate() {
        currentSubstate = null;  // Reset current substate

        for (SequenceState substate : selectorSubstates) {
            if (substate.isValid()) {  // Check validity of each substate
                currentSubstate = substate;
                currentSubstate.enter();  // Enter the first valid substate found
                return;
            }
        }

        markComplete();  // No valid substates found, mark as complete
        returnToParent();
    }

    /**
     * Returns control to the parent state after SelectorState completes.
     */
    protected void returnToParent() {
        if (parentState instanceof SequenceState || parentState instanceof SelectorState) {
            parentState.execute();  // Return control to parent SequenceState or SelectorState
        } else if (machine != null) {
            machine.update();  // Return control to the root StateMachine if no valid parent state
        }
    }
}
