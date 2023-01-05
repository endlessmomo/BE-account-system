package yuki.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuki.account.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long> {
}
