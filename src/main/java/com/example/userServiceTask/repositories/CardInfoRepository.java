package com.example.userServiceTask.repositories;

import com.example.userServiceTask.model.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    CardInfo findById(long id);

    @Query(value = "SELECT c from CardInfo c where c.id IN :ids")
    List<CardInfo> findCardInfoByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "UPDATE CardInfo c SET c = :cardInfo where c.id = :id")
    CardInfo updateCardInfoById(@Param("id") long id,@Param("cardInfo") CardInfo cardInfo);


    @Modifying
    @Query(value = "DELETE CardInfo c where c.id = :id")
    CardInfo deleteCardInfoById(@Param("id") long id);

}
