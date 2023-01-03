package yuki.account.repository;

import yuki.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuki.account.domain.AccountUser;

import java.util.List;
import java.util.Optional;

/**
 * findFirstByOrderByIdDesc : 맨 마지막으로 등록된 Id를 가져온다.
 *
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional <Account> findFirstByOrderByIdDesc();
    Integer countByAccountUser(AccountUser accountUser);
}
