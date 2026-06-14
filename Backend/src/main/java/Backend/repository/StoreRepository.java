package Backend.repository;

import Backend.entity.Store;
import Backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByOwner(User owner);

    List<Store> findAllByOwner(User owner);

    Optional<Store> findByOwnerId(Long ownerId);

    boolean existsByEmail(String email);

    @Query("""
            select s from Store s
            where (:search is null or :search = ''
                or lower(s.name)                        like lower(concat('%', :search, '%'))
                or lower(coalesce(s.email, ''))         like lower(concat('%', :search, '%'))
                or lower(coalesce(s.address, ''))       like lower(concat('%', :search, '%')))
            """)
    Page<Store> searchStores(String search, Pageable pageable);

    @Query("select avg(r.rating) from Rating r where r.store = :store")
    Double findAverageRatingByStore(Store store);

    @Query("select count(r) from Rating r where r.store.id = :storeId")
    long countRatingsByStoreId(Long storeId);
}
