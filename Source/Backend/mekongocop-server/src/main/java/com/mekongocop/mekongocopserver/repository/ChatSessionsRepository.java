package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.ChatSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionsRepository extends JpaRepository<ChatSessions, Integer> {
    @Query("SELECT cs FROM ChatSessions cs WHERE cs.user.user_id = :userId")
    List<ChatSessions> findByUser_UserId(@Param("userId") int userId);
    @Query("SELECT cs FROM ChatSessions cs WHERE cs.store.store_id = :storeId")
    List<ChatSessions> findByStore_StoreId(@Param("storeId") int storeId);
    @Query("SELECT cs FROM ChatSessions cs WHERE cs.user.user_id = :userId AND cs.store.store_id = :storeId")
    Optional<ChatSessions> findByUserIdAndStoreId(@Param("userId") int userId, @Param("storeId") int storeId);
    @Query("SELECT cs FROM ChatSessions cs WHERE cs.user.user_id = :userId")
    List<ChatSessions> findByUser_Id(@Param("userId") int userId);

    @Query("SELECT cs FROM ChatSessions cs WHERE cs.store.store_id = :storeId")
    List<ChatSessions> findByStore_Id(@Param("storeId") int storeId);

}
