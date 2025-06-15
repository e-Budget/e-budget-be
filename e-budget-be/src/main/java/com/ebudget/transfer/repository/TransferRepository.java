package com.ebudget.transfer.repository;

import com.ebudget.transfer.model.Transfer;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TransferRepository implements PanacheRepositoryBase<Transfer, UUID> {
}
