package org.billing.dbclasses;

import java.util.ArrayList;
import java.util.List;

public class FxswapValidationResult {
    private boolean isValid;
    private List<String> conflictMessages;

    public FxswapValidationResult() {
        this.isValid = true;
        this.conflictMessages = new ArrayList<>();
    }

    public void addConflict(String message) {
        this.isValid = false;
        this.conflictMessages.add(message);
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getConflictMessages() {
        return conflictMessages;
    }
}
