public class AutomationRule {

    // ENCAPSULATION: private fields
    private String trigger;       // e.g., "AWAY_MODE", "NIGHT_MODE"
    private String targetDevice;  // e.g., "Living Room Light"
    private String action;        // e.g., "TURN_OFF", "TURN_ON"
    private boolean isActive;     // rule is active or not

    // Constructor with validation
    public AutomationRule(String trigger, String targetDevice, String action) {
        // EXCEPTION HANDLING: validate all fields
        if (trigger == null || trigger.trim().isEmpty()) {
            throw new IllegalArgumentException("Trigger condition cannot be null or empty.");
        }
        if (targetDevice == null || targetDevice.trim().isEmpty()) {
            throw new IllegalArgumentException("Target device cannot be null or empty.");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be null or empty.");
        }

        this.trigger = trigger.toUpperCase();
        this.targetDevice = targetDevice;
        this.action = action.toUpperCase();
        this.isActive = true; // rules are active by default
    }

    /**
     * Evaluate if this rule should fire based on current condition
     * EXCEPTION HANDLING: null condition check
     * POLYMORPHISM setup: can be overridden in subclasses for complex rules
     */
    public void evaluate(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition to evaluate cannot be null or empty.");
        }

        if (!isActive) {
            System.out.println(" Rule for trigger '" + trigger + "' is currently inactive.");
            return;
        }

        if (condition.toUpperCase().equals(trigger)) {
            System.out.println("[RULE TRIGGERED] Condition '" + condition + "' matched trigger '" + trigger + "'.");
            System.out.println("  -> Executing: " + action + " on device: " + targetDevice);
        } else {
            System.out.println("[RULE CHECK] Condition '" + condition + "' did NOT match trigger '" + trigger + "'.");
        }
    }

    /**
     * Activate this rule
     * EXCEPTION HANDLING: throw if already active
     */
    public void activate() {
        if (isActive) {
            throw new IllegalStateException(
                "Rule for trigger '" + trigger + "' is already active."
            );
        }
        isActive = true;
        System.out.println("[RULE ACTIVATED] Rule '" + trigger + "' is now active.");
    }

    /**
     * Deactivate this rule
     * EXCEPTION HANDLING: throw if already inactive
     */
    public void deactivate() {
        if (!isActive) {
            throw new IllegalStateException(
                "Rule for trigger '" + trigger + "' is already inactive."
            );
        }
        isActive = false;
        System.out.println("[RULE DEACTIVATED] Rule '" + trigger + "' has been deactivated.");
    }

    /**
     * Get rule details as a formatted string
     */
    public String getRule() {
        return "AutomationRule{trigger='" + trigger +
               "', targetDevice='" + targetDevice +
               "', action='" + action +
               "', isActive=" + isActive + "}";
    }

    // Getters
    public String getTrigger()      { return trigger; }
    public String getTargetDevice() { return targetDevice; }
    public String getAction()       { return action; }
    public boolean isActive()       { return isActive; }

    @Override
    public String toString() {
        return getRule();
    }
}
