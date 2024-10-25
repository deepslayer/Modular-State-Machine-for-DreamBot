package Framework.Interface;

/**
 * ValidatableState is an extension of State.
 * It adds a method to check whether the state is valid for execution.
 */
public interface ValidatableState extends State {
    boolean isValid();  // Determines if the state should be executed based on conditions
}
