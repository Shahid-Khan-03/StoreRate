package Backend.repository;

import Backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("""
            select u from User u
            where (:search is null or :search = ''
                or lower(u.name)                          like lower(concat('%', :search, '%'))
                or lower(u.email)                         like lower(concat('%', :search, '%'))
                or lower(coalesce(u.address, ''))         like lower(concat('%', :search, '%'))
                or lower(cast(u.role as string))          like lower(concat('%', :search, '%')))
            """)
    Page<User> searchUsers(String search, Pageable pageable);
}
