package Framework.ModularStateMachine;


import java.util.ArrayList;
import java.util.List;

import static org.dreambot.api.utilities.Logger.log;

/**
 * SelectorState evaluates a set of substates and executes the first valid one.
 * It can only be used directly within a SequenceState, and its children can only be SequenceState.
 */
public abstract class SelectorState extends ActionState {
    private final List<SequenceState> selectorSubstates = new ArrayList<>();  // List of potential SequenceState substates
    private SequenceState currentSubstate;

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
    }

    /**
     * Final enter method that calls the subclass-specific onEnter() logic.
     */
    @Override
    public final void enter() {
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
     * it marks itself as complete and returns control to the parent SequenceState.
     */
    @Override
    public void execute() {
        if (currentSubstate != null) {
            if (!currentSubstate.isComplete()) {
                currentSubstate.execute();
            } else {
                currentSubstate.exit();
                markComplete();
                returnToParent();
            }
        } else {
            markComplete();
            returnToParent();
        }
    }

    /**
     * Finds the next valid substate in the selectorSubstates list.
     */
    protected void findNextValidSubstate() {
        currentSubstate = null;

        for (SequenceState substate : selectorSubstates) {
            if (substate.isValid()) {  // Only checks validity of substates
                currentSubstate = substate;
                currentSubstate.enter();
                return;
            }
        }

        markComplete();  // No valid substates found, mark as complete
        returnToParent();
    }

    /**
     * Returns control to the parent SequenceState after the selector process completes.
     */
    protected void returnToParent() {
        if (machine != null && !isComplete()) {
            markComplete();
            machine.update();
        }
    }
}
