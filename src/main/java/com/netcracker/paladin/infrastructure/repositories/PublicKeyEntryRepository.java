package com.netcracker.paladin.infrastructure.repositories;

import com.netcracker.paladin.domain.PublicKeyEntry;

import java.util.List;

/**
 * Created on 27.10.14.
 */
public interface PublicKeyEntryRepository {
    public void insert(PublicKeyEntry PublicKeyEntry);

    public PublicKeyEntry findByEmail(String email);

    void deleteByEmail(String email);

    List<PublicKeyEntry> findAll();
}
