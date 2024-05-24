package dev.surly.ai.collab.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine(name = "simpleStateMachine1")
public class SimpleStateMachine extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Bean
    public Action<States, Events> initAction() {
        return ctx -> System.out.println(ctx.getTarget().getId());
    }

    @Bean
    public Action<States, Events> executeAction() {
        return ctx -> System.out.println("Do " + ctx.getTarget().getId());
    }

    @Bean
    public Action<States, Events> completedAction() {
        return ctx -> System.out.println("Completed " + ctx.getTarget().getId());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
                .withStates()
                .initial(States.INITIAL)
                .state(States.IN_PROGRESS, executeAction())
                .end(States.COMPLETED)
                .state(States.COMPLETED, completedAction());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal().source(States.INITIAL).target(States.IN_PROGRESS).event(Events.START).action(initAction())
                .and()
                .withExternal().source(States.IN_PROGRESS).target(States.COMPLETED).event(Events.FINISH);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config
                .withConfiguration()
                .machineId("simpleStateMachine")
                .autoStartup(true);
    }
}
