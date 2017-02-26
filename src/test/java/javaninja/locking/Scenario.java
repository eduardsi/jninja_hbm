package javaninja.locking;

import com.google.common.base.Throwables;

@FunctionalInterface
interface Scenario {

    void execute() throws Exception;

    default void executeUnchecked() {
        try {
            execute();
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }


}