package yuki.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuki.account.domain.AccountUser;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
}
