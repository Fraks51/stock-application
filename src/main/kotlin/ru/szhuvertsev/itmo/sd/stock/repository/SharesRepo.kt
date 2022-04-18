package ru.szhuvertsev.itmo.sd.stock.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.szhuvertsev.itmo.sd.stock.entity.Shares

@Repository
interface SharesRepo: JpaRepository<Shares, String> {

    @Query("SELECT new ru.szhuvertsev.itmo.sd.stock.entity.Shares(s.id, s.companyId, s.ownerId) FROM Shares s WHERE s.ownerId IS NULL AND s.companyId = :companyId ")
    fun findFreeSharesByCompanyId(companyId: String): List<Shares>

    fun countByCompanyId(companyId: String): Long

    fun findByOwnerId(userId: String): List<Shares>

    fun findByOwnerIdAndCompanyId(ownerId: String, companyId: String): List<Shares>
}