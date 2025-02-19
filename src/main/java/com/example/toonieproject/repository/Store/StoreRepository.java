package com.example.toonieproject.repository.Store;



import com.example.toonieproject.entity.Store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwner_Id(Long ownerId);
    Optional<Store> findById(Long id);


    @Query(value = """
        SELECT s.* FROM store s
        JOIN address_store a ON s.store_id = a.store_id
        ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(a.lat)) * 
                              cos(radians(a.lng) - radians(:lng)) + 
                              sin(radians(:lat)) * sin(radians(a.lat))))
        ASC LIMIT :pageSize OFFSET :offset
    """, nativeQuery = true)
    List<Store> findNearbyStores(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );

    @Query("SELECT s FROM Store s JOIN AddressOfStore a ON s.id = a.store.id " +
            "WHERE a.lat BETWEEN :minLat AND :maxLat " +
            "AND a.lng BETWEEN :minLng AND :maxLng")
    List<Store> findStoresInBounds(@Param("minLat") BigDecimal minLat,
                                   @Param("maxLat") BigDecimal maxLat,
                                   @Param("minLng") BigDecimal minLng,
                                   @Param("maxLng") BigDecimal maxLng);

}