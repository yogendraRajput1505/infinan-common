package com.infinan.common.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infinan.common.entity.KiteLoginCredentials;

public interface KiteLoginCredentialsRepository extends JpaRepository<KiteLoginCredentials,String>{

}
