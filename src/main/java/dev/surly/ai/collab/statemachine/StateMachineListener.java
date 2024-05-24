package dev.surly.ai.collab.statemachine;

import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

@Component
public class StateMachineListener extends StateMachineListenerAdapter<States, Events> {
    @Override
    public void stateChanged(State from, State to) {
        System.out.printf("Transitioned from %s to %s%n", from == null ?
                "none" : from.getId(), to.getId());
    }
}
