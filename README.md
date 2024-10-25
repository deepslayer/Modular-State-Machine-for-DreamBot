## Modular State Machine for DreamBot

### Overview
The **Modular State Machine (MSM)** is an adaptable and efficient state machine framework designed specifically for DreamBot scripting, enabling streamlined, responsive bot behavior. MSM uses modular state management, leveraging DreamBot’s `onLoop` cycle to allow background processing between bot actions. This approach allows for efficient and highly responsive scripts, prioritizing a structured, linear flow ideal for building scalable, complex bot logic.

### Components and Features

**1. ActionState**  
   - **Core Task Management**: `ActionState` represents discrete tasks within the bot’s logic and includes lifecycle methods (`enter()`, `execute()`, `exit()`) for controlled entry, task execution, and exit.
   - **Loop Integration**: Executes during DreamBot’s `onLoop` cycle, allowing for consistent background processing between bot actions. This design keeps the bot responsive to real-time game updates and events.
   - **No `isValid()` Requirement**: Since `ActionState`s are typically part of a `SequenceState`, they do not require individual `isValid()` methods. The `SequenceState` itself contains the `isValid()` check, determining whether the entire sequence should run. This            simplifies the `ActionState` logic, as each `ActionState` executes when reached in the sequence without additional validity checks.
   - **Flexible Task Completion**: Each `ActionState` can mark itself as complete using `markComplete()` or by overriding `isComplete()`. This means that while actions within a sequence don’t need an `isValid()` check, they don't necessarily need to execute fully or in         a strict order either; they can skip over specific steps if conditions make them unnecessary. This feature provides adaptability within a predefined sequence, allowing `ActionState`s to complete or exit early based on dynamic conditions.
   - **Partial Task Completion**: Supports early returns, allowing for incremental task progress that resumes exactly where it left off on the next loop. This is ideal for complex, multi-step tasks within an ActionState that may require multiple cycles to complete fully.

**2. DecisionState**  
   - **Adaptive Decision-Making**: `DecisionState` enables dynamic control over nested substates, selecting the next state to execute based on conditions.
   - **Controlled, Linear Flow**: Rather than looping through nested substates, `DecisionState` finds and activates a valid state in a single pass, then returns control to the main `StateMachine`.

**3. SequenceState**  
   - **Sequential Task Handling with Early Exits**: `SequenceState` manages a chain of subtasks, executing each subtask in a fixed sequential order. Importantly, each substate within the sequence can still exit early and move onto the next part of the sequence based on         custom conditions defined in the `isComplete()` method. This allows for a flexible, condition-based progression through the sequence, ideal for routines that may need to adapt mid-sequence.
   - **Modular Hierarchy Support**: `SequenceState` can be nested within `DecisionState`, 'SequenceState' or used standalone, providing a flexible structure for linear task sequences without requiring re-evaluation each loop cycle.
   - **Efficient Control Return**: Once a `SequenceState` completes all of its substates, control automatically returns to its parent, either a `DecisionState`, 'SequenceState' or the root `StateMachine`.

**4. StateMachine**  
   - **Centralized Control**: The root `StateMachine` manages all states, transitioning between them based on validity and completeness.
   - **Loop-Driven Execution**: All state transitions and updates happen within DreamBot’s `onLoop` function, allowing individual states to run without blocking background processes. 

---

### How Modular State Machine Differs from TreeScript

Both **Modular State Machine** and DreamBot’s `TreeScript` provide robust frameworks for state management, though they differ significantly in structure:

1. **Execution Flow and Background Responsiveness**
   - **Modular State Machine (MSM)** leverages DreamBot’s `onLoop` cycle to efficiently manage decision-making and action execution. MSM processes conditions only from the root when making decisions, determining which path or state to activate. Once a state is selected,      MSM executes actions directly, without re-evaluating conditions from the root, allowing for efficient sequential execution. Importantly, MSM passes control back to `onLoop` between each cycle, enabling the game to process updates (such as inventory changes, NPC          changes, or health checks) before the next action is taken. This approach maintains both performance and responsiveness in dynamic, sequential tasks.
   - **TreeScript** also returns to the root after each `onLoop` pass, re-evaluating the tree to locate the next valid `Leaf` node. This structure supports organized decision-making but requires condition checks from the root each cycle, which can add overhead in complex scripts with many branches. MSM’s direct, linear action execution minimizes repeated evaluations, enhancing efficiency while still supporting game updates between cycles.

2. **Modularity and State Reusability**
   - **Modular State Machine (MSM)** is designed for flexible modularity, allowing states to be organized both sequentially and conditionally through `SequenceState` and `DecisionState`. This structure enables MSM to handle sequential tasks without complex tracking mechanisms. Actions can be arranged in a predefined order within a `SequenceState`, which automatically manages progression. Each action within the sequence can independently determine completion status using `markComplete` and `isComplete` methods, enabling customized flows where specific actions can skip or finish early based on conditions. This adaptive setup reduces the need for additional flags and makes MSM’s modular sequences reusable across different workflows.
   - **TreeScript**, by contrast, is structured as a branching hierarchy of `Branch` and `Leaf` nodes, optimized for conditional decision-making rather than linear task sequences. To perform a series of sequential actions in TreeScript, developers typically rely on multiple boolean flags to track progress between `Leaf` nodes, as the framework lacks a native way to enforce order within a single branch. While effective for certain hierarchical flows, this structure becomes cumbersome when handling sequential tasks, making it less suited for workflows that require ordered actions without extensive flagging.

3. **Hierarchical and Controlled Flow**  
   - **Modular State Machine** uses indexing and state tracking within each `SequenceState` and `DecisionState`. This allows each state to execute its substates in sequence without deep recursive calls. `MSM` also returns control to the root `StateMachine` between each       loop, and it only re-evaluates conditions from the root when making new decisions. This design keeps execution layers shallow and efficient, improving performance for complex scripts.
   - **TreeScript** maintains a structured tree hierarchy with `Branch` and `Leaf` nodes. Each `Branch` sequentially evaluates its children, with control returning to the root on each loop. While not recursive in execution, `TreeScript`’s structure requires repeated          condition checks from the root to `Leaf` nodes in every cycle, which can increase CPU usage as script complexity grows. In contrast, MSM executes actions directly in sequence without reevaluating conditions from the root each cycle, optimizing for performance in          sequential tasks.

4. **Incremental Task Completion and Early Returns**
   - **Modular State Machine** allows partial task completion within states. Each `ActionState` can perform a partial task and return to the main `onLoop` cycle, resuming exactly where it left off on the next loop. This fine control is ideal for tasks needing multiple        cycles to complete.
   - **TreeScript** executes `Leaf` nodes based on conditional validity. It doesn’t inherently support partial task completion within branches, re-evaluating the entire each branch each loop to locate the next valid action node.
  

### Diagrams



### Conclusion

The **Modular State Machine** framework offers a flexible, loop-driven alternative to traditional state management, focusing on modularity, real-time processing, and adaptive state transitions. By allowing incremental task completion, layered control flow, and efficient execution, MSM provides a powerful tool for building responsive, adaptive bot scripts in DreamBot.
