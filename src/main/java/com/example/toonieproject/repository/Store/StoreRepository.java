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
    List<Store> findByUser_Id(Long userId);
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


    @Query(value = """
        SELECT s.store_id, s.name, s.image, a.address, a.lat, a.lng,
               (6371 * acos(cos(radians(:lat)) * cos(radians(a.lat)) 
               * cos(radians(a.lng) - radians(:lon)) 
               + sin(radians(:lat)) * sin(radians(a.lat))))
               AS distance 
        FROM store s
        INNER JOIN address_store a ON s.store_id = a.store_id
        INNER JOIN series_of_store sos ON s.store_id = sos.store_id
        WHERE sos.series_id = :seriesId
        ORDER BY distance ASC
        """, nativeQuery = true)
    List<Object[]> findStoresBySeriesIdWithDistance(@Param("seriesId") Long seriesId,
                                                    @Param("lat") double lat,
                                                    @Param("lon") double lon);

}