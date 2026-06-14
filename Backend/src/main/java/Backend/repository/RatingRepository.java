package Backend.repository;

import Backend.entity.Rating;
import Backend.entity.Store;
import Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByStore(Store store);

    List<Rating> findByStoreOrderByUpdatedAtDesc(Store store);

    Optional<Rating> findByUserAndStore(User user, Store store);

    List<Rating> findByStoreOwner(User owner);

    @Query("select avg(r.rating) from Rating r where r.store = :store")
    Double findAverageRatingByStore(Store store);

    long countByStore(Store store);

    Optional<Rating> findByStoreAndUser(Store store, User user);
}
