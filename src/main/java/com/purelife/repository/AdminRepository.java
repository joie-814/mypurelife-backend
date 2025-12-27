package com.purelife.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.purelife.entity.Admin;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Integer> {
    
    Optional<Admin> findByAccount(String account);
    
    boolean existsByAccount(String account);
}