package com.qianwen.Booknetworkproject.entities.activationCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByActivationCode(String activationCode);

}
