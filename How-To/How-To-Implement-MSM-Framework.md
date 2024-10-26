# Building a Basic State Machine with DreamBot

This tutorial will guide you through creating a DreamBot script that uses a state machine. You'll set up the script manifest, initialize and run the state machine from `onLoop`, and define basic states for testing.

### Table of Contents
- [Prerequisites](#prerequisites)
- [Step 1: Set Up the Script Manifest and Main Class](#step-1-set-up-the-script-manifest-and-main-class)
- [Step 2: Define States](#step-2-define-states)
  - [Create Action States](#create-action-states)
  - [Create a Sequence State](#create-a-sequence-state)
  - [Create a Decision State](#create-a-decision-state)
- [Step 3: Run the State Machine in onLoop](#step-3-run-the-state-machine-in-onloop)

---

### Prerequisites

Make sure that you have DreamBot set up, along with the `Framework` package setup under your 'src' folder.

---

## Step 1: Set Up the Script Manifest and Main Class

Start by creating a `Main` class in your project. This class is where DreamBot will run your script. It includes `onStart`, `onLoop`, and `onExit` methods, which control the script’s lifecycle.

Here’s a basic setup:

```java
package Main;

import Framework.ModularStateMachine.StateMachine;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(
    author = "Deep Slayer",
    name = "State Machine Example Bot",
    version = 1.0,
    description = "A bot example using a basic state machine",
    category = Category.MISC
)
public class Main extends AbstractScript {
    private StateMachine stateMachine;

    @Override
    public void onStart() {
        log("Script Started...");

        // Initialize the state machine and add initial states
        stateMachine = new StateMachine();
        stateMachine.addState(new InitialDecisionState(stateMachine));  // Add initial decision state

        // Start the state machine
        stateMachine.start();
        log("State machine initialized and started.");
    }

    @Override
    public int onLoop() {
        if (stateMachine.isRunning()) {  
            stateMachine.update();  // Update the state machine in each loop
        } else {
            log("State machine has no states left to run. It will continue checking for valid states.");
        }
        return 20;  // Delay of 20ms between each loop cycle (adjust to the responsiveness needed)
    }


    @Override
    public void onExit() {
        log("Stopping Bot");
    }
}
```

This code initializes the `StateMachine` in `onStart`, updates it in `onLoop`, and stops the script if the state machine finishes running.

---

## Step 2: Define States

With the main structure set up, let’s add basic states. Each type of state serves a different purpose and will demonstrate how to add functionality within the state machine.

### Create Action States

`ActionState` represents individual tasks. Here’s a simple logging action:

```java
package States.Action;

import Framework.ModularStateMachine.ActionState;
import Framework.ModularStateMachine.StateMachine;
import static org.dreambot.api.utilities.Logger.log;

public class LogAction extends ActionState {
    public LogAction(StateMachine machine) {
        super(machine);
    }

    @Override
    public void execute() {
        log("Executing LogAction...");
        markComplete();  // Mark the action as complete after execution
    }

    @Override
    protected void onEnter() {
        log("Entering LogAction...");
    }

    @Override
    protected void onExit() {
        log("Exiting LogAction...");
    }
}
```

### Create a Sequence State

A `SequenceState` organizes a series of actions in a specific order. Here’s a `SetupSequence` that performs the `LogAction` as a step:

```java
package States.Sequence;

import Framework.ModularStateMachine.SequenceState;
import Framework.ModularStateMachine.StateMachine;
import States.Action.LogAction;
import static org.dreambot.api.utilities.Logger.log;

public class SetupSequence extends SequenceState {
    public SetupSequence(StateMachine machine) {
        super(machine);
        addSubState(new LogAction(machine));  // Add LogAction as part of the sequence
    }

    @Override
    protected void onEnter() {
        log("Entering SetupSequence...");
    }

    @Override
    protected void onExit() {
        log("Exiting SetupSequence...");
    }

    @Override
    public boolean isValid() {
        return true;  // Makes the sequence valid for execution
    }
}
```

### Create a Decision State

`DecisionState` allows conditional branching. Here’s an example that decides whether to run `SetupSequence`:

```java
package States.Decision;

import Framework.ModularStateMachine.DecisionState;
import Framework.ModularStateMachine.StateMachine;
import States.Sequence.SetupSequence;
import static org.dreambot.api.utilities.Logger.log;

public class InitialDecisionState extends DecisionState {
    public InitialDecisionState(StateMachine machine) {
        super(machine);
        addSubState(new SetupSequence(machine));  // Adds SetupSequence as a potential substate
    }

    @Override
    protected void onEnter() {
        log("Entering InitialDecisionState...");
    }

    @Override
    protected void onExit() {
        log("Exiting InitialDecisionState...");
    }

    @Override
    public boolean isValid() {
        return true;  // Ensures this decision state is valid for execution
    }
}
```

---

## Step 3: Run the State Machine in onLoop

The `onLoop` method of the `Main` class is responsible for continuously updating the state machine. Since each state runs until complete, the state machine will process states based on the framework’s conditions.

This setup allows you to add more actions, sequences, or decisions without modifying the main loop, making it adaptable for complex tasks.

---

### Summary

This tutorial demonstrates how to:
- Set up a DreamBot script using the `StateMachine` framework.
- Define various types of states (`ActionState`, `SequenceState`, `DecisionState`).
- Run the state machine in `onLoop` for continuous execution.

This structure is expandable, allowing you to add more complex behaviors and conditions by creating new states and incorporating them into the state machine.
