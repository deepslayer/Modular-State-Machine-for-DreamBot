package Framework.ModularStateMachine;

import Framework.Interface.State;

/**
 * ActionState is an abstract class that represents an individual action.
 * It implements the core methods for managing state execution and completion.
 */
public abstract class ActionState implements State {
    protected StateMachine machine;
    private boolean complete = false;

    /**
     * Constructor for ActionState.
     *
     * @param machine The state machine that this ActionState is part of.
     */
    public ActionState(StateMachine machine) {
        this.machine = machine;
    }

    /**
     * Checks if the ActionState is complete.
     *
     * @return true if the state is complete, false otherwise.
     */
    @Override
    public boolean isComplete() {
        return complete;
    }

    /**
     * Called when the ActionState is entered. Resets the completion status.
     */
    @Override
    public void enter() {
        resetCompletion();
    }

    /**
     * Abstract method that must be implemented to define
     * the specific behavior when the state executes.
     */
    @Override
    public abstract void execute();

    /**
     * Abstract method that must be implemented to define
     * cleanup behavior when the state exits.
     */
    @Override
    public abstract void exit();

    /**
     * Marks the ActionState as complete, allowing the state machine to transition.
     */
    protected void markComplete() {
        this.complete = true;
    }

    /**
     * Resets the completion flag.
     */
    protected void resetCompletion() {
        this.complete = false;
    }
}
