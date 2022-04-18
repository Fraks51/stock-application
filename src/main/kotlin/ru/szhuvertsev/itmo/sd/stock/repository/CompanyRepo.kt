package ru.szhuvertsev.itmo.sd.stock.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.szhuvertsev.itmo.sd.stock.entity.Company

@Repository
interface CompanyRepo: JpaRepository<Company, String> {
    fun findCompanyByName(name: String): Company?
}