package ru.szhuvertsev.itmo.sd.stock.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.szhuvertsev.itmo.sd.stock.entity.User
import java.math.BigDecimal

@Repository
interface UserRepo: JpaRepository<User, String> {

    fun findUserByLogin(login: String): User?

    @Modifying
    @Query("UPDATE User SET balance = balance + :value WHERE id = :userId")
    fun addValueToUser(userId: String, value: BigDecimal)

}