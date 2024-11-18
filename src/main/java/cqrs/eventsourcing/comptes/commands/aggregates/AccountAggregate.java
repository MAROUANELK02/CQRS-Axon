package cqrs.eventsourcing.comptes.commands.aggregates;

import cqrs.eventsourcing.comptes.commonapi.commands.CreateAccountCommand;
import cqrs.eventsourcing.comptes.commonapi.commands.CreditAccountCommand;
import cqrs.eventsourcing.comptes.commonapi.commands.DebitAccountCommand;
import cqrs.eventsourcing.comptes.commonapi.enums.AccountStatus;
import cqrs.eventsourcing.comptes.commonapi.events.AccountActivatedEvent;
import cqrs.eventsourcing.comptes.commonapi.events.AccountCreatedEvent;
import cqrs.eventsourcing.comptes.commonapi.events.AccountCreditedEvent;
import cqrs.eventsourcing.comptes.commonapi.events.AccountDebitedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if(createAccountCommand.getInitialBalance() < 0)
            throw new RuntimeException("Impossible ...");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency()
        ));
    }

    @CommandHandler
    public void handle(CreditAccountCommand creditAccountCommand) {
        if(creditAccountCommand.getAmount() < 0)
            throw new RuntimeException("Impossible ...");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                creditAccountCommand.getId(),
                creditAccountCommand.getAmount(),
                creditAccountCommand.getCurrency()
        ));
    }

    @CommandHandler
    public void handle(DebitAccountCommand debitAccountCommand) {
        if(debitAccountCommand.getAmount() < 0 || this.balance < debitAccountCommand.getAmount())
            throw new RuntimeException("Impossible ...");
        AggregateLifecycle.apply(new AccountDebitedEvent(
                debitAccountCommand.getId(),
                debitAccountCommand.getAmount(),
                debitAccountCommand.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        this.accountId = accountCreatedEvent.getId();
        this.balance = accountCreatedEvent.getInitialBalance();
        this.currency = accountCreatedEvent.getCurrency();
        this.status = AccountStatus.CREATED;
        AggregateLifecycle.apply(new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent accountActivatedEvent) {
        this.status = accountActivatedEvent.getStatus();
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent accountCreditedEvent) {
        this.balance += accountCreditedEvent.getAmount();
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent accountDebitedEvent) {
        this.balance -= accountDebitedEvent.getAmount();
    }

}
