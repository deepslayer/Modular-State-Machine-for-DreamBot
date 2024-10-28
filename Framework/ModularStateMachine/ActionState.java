package Framework.ModularStateMachine;

import Framework.Interface.State;

import static org.dreambot.api.utilities.Logger.log;

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
     * Final enter method that enforces default behavior and then calls the subclass-specific onEnter().
     */
    @Override
    public void enter() {
        log("Enter " + this.getClass().getSimpleName());
        resetCompletion();  // Default behavior in ActionState
        onEnter();          // Call subclass-specific behavior
    }

    /**
     * Final exit method that enforces any default exit behavior, then calls subclass-specific onExit().
     */
    @Override
    public void exit() {
        log("Exit " + this.getClass().getSimpleName());
        onExit();  // Call subclass-specific exit behavior
    }

    /**
     * Abstract method for subclass-specific behavior on enter.
     * Subclasses are required to override this method.
     */
    protected abstract void onEnter();

    /**
     * Abstract method that must be implemented to define
     * the specific behavior when the state executes.
     */
    @Override
    public abstract void execute();

    /**
     * Abstract method for subclass-specific behavior on exit.
     * Subclasses are required to override this method.
     */
    protected abstract void onExit();


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