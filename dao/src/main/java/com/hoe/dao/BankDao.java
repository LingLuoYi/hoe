package com.hoe.dao;


import com.hoe.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDao extends JpaRepository<Bank,Integer> {

    Bank findAllByPayType(String type);
}
