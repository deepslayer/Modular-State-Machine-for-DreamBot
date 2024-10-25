### Example: Rapid Attack Sequence with Modular State Machine vs. TreeScript

#### Scenario: PvP Combat Bot’s Complex Attack Sequence

In a high-stakes PvP environment, the bot needs to execute a **rapid sequence of attack actions** in a precise order:
1. Select appropriate prayers.
2. Switch to melee gear.
3. Equip the correct weapon.
4. Initiate the attack.

Each of these actions must be executed **quickly and in sequence** for the strategy to be effective.

### Why Modular State Machine (MSM) Handles This Better Than TreeScript

1. **Sequential Flow Without Re-Evaluation**:
   - **In MSM**: Using a `SequenceState`, this entire attack sequence is defined as a set of `ActionState`s. Each action (like selecting prayers or switching gear) is simply executed in order, without needing to return to      the root or re-evaluate conditions. This makes the flow smooth and lightning-fast.
   - **In TreeScript**: Each action in the sequence would need to be a `Leaf` within a `Branch` (such as `AttackWithMeleeBranch`). After each action, TreeScript passes control back to the root, requiring it to re-evaluate      conditions at every level to return to the correct `Leaf`. This frequent re-evaluation slows down the process, requiring exact conditions and flags to keep track of progress.

2. **Simplified, Flagless Execution**:
   - **In MSM**: MSM’s `SequenceState` uses `currentSubstateIndex` to track progress in the sequence, so there’s no need for additional flags or complex logic. After each action completes, MSM automatically advances to         the next `ActionState` without any extra condition checks. This keeps the code clean and avoids redundant checks.
   - **In TreeScript**: To manage this sequence, TreeScript would require flags or additional checks in each `Leaf` to determine if it should continue or reset. After each step, TreeScript evaluates these flags to decide       the next action, adding complexity to the structure.

3. **High-Speed Execution for Rapid PvP Actions**:
   - **In MSM**: Each action in the `SequenceState` is executed back-to-back, with control passing to `onLoop` between each action for real-time game updates. However, MSM only re-evaluates conditions within the                `SequenceState` itself, making it efficient while still staying responsive. This is critical in PvP scenarios where each millisecond counts, as actions can execute sequentially without re-checking conditions outside       the sequence.
   - **In TreeScript**: TreeScript’s constant returns to the root and condition re-evaluations introduce delays, making it less suitable for fast, sequential actions. Each `Leaf` execution involves a series of re-checks        across branches and the root, creating additional overhead between steps.

4. **Customizable Sequence Flexibility**:
   - **In MSM**: `SequenceState` allows for flexible completion by marking each `ActionState` as complete using `markComplete` or `isComplete`. This enables each action in the sequence to either complete fully or skip steps based on in-game conditions (e.g., skipping prayer selection if it’s already active).
   - **In TreeScript**: Customizing sequences mid-flow is more challenging. You’d need conditions and flags on multiple `Leaf` nodes to handle each adjustment, increasing complexity and the likelihood of errors.

### Additional Advantage: Modular Design and Single Responsibility

By using MSM’s `SequenceState` with modular `ActionState`s, each action has a **single responsibility**—keeping the code clean, focused, and following good object-oriented practices:

- **Single Responsibility**: Each action (`SelectPrayersAction`, `SwitchGearAction`, etc.) is responsible for one specific task, making the code easier to maintain, understand, and modify.
- **Reusability**: Any `ActionState` (such as `SwitchWeaponAction`) can be reused in other sequences without needing additional changes.
- **Readability and Maintainability**: Having each action in a separate class makes the code easy to follow, with each action clearly documented by its class name and logic. Combining multiple actions into one large `ActionState` or `Leaf` would create a monolithic, harder-to-debug structure.
  
In contrast, **TreeScript** would encourage combining these actions into a single `Leaf` or branch structure to avoid re-evaluation complexity. This approach leads to **complex, multi-responsibility code** that is harder to reuse, debug, and adapt.

### Summary of MSM’s Power in This Scenario

Using **MSM** for this PvP attack sequence provides:
- **Sequential, high-speed execution** with real-time responsiveness, returning control to `onLoop` between actions without needing to re-evaluate conditions outside the sequence.
- **Flagless, straightforward progress tracking** with `currentSubstateIndex` already integrated in the framework.
- **Easy customization** of sequence actions using `markComplete` or `isComplete`, which allows conditions to control whether an action completes fully or skips.

Meanwhile, **TreeScript** would require:
- **Complex flags and condition checks** to track progress and sequence order.
- **Frequent re-evaluation** from the root, adding overhead and slowing down each step.
- **Monolithic branches** to handle multiple actions within a single `Leaf`, violating single-responsibility principles.

This example highlights MSM’s power in scenarios where **speed, flexibility, and modularity** are crucial, making it ideal for real-time PvP sequences where precision and adaptability are essential.
