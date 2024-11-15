package com.infinan.common.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.infinan.common.entity.StockList;

public interface StockListRepository extends JpaRepository<StockList, String>{
	List<StockList> findAll();
	List<StockList> getAllByIndices(String indices);
	
	@Query("select s.name from StockList s where s.indices =:indices")
	List<String> getAllNameByIndices(@Param("indices") String indices);
}
