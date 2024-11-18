package cqrs.eventsourcing.comptes.query.repositories;

import cqrs.eventsourcing.comptes.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
