package ru.szhuvertsev.itmo.sd.stock.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.szhuvertsev.itmo.sd.stock.entity.Shares
import ru.szhuvertsev.itmo.sd.stock.entity.User
import ru.szhuvertsev.itmo.sd.stock.exception.NoSuchCompanyException
import ru.szhuvertsev.itmo.sd.stock.exception.ServiceException
import ru.szhuvertsev.itmo.sd.stock.repository.CompanyRepo
import ru.szhuvertsev.itmo.sd.stock.repository.SharesRepo
import ru.szhuvertsev.itmo.sd.stock.repository.UserRepo
import java.math.BigDecimal

@Service
class UserService(
    private val userRepo: UserRepo,
    private val sharesRepo: SharesRepo,
    private val companyRepo: CompanyRepo
) {

    fun getUserBalance(id: String) =
        userRepo.findByIdOrNull(id)
            ?.balance
            ?: throw ServiceException("User id:$id was not found")

    fun registerUser(login: String, balance: BigDecimal = BigDecimal(0)): User {
        userRepo.findUserByLogin(login)?.let {
            throw ServiceException("User login:$login with exists")
        }
        return userRepo.save(User(login = login, balance = balance))
    }

    fun increaseUserBalance(userId: String, valueToAdd: BigDecimal) {
        userRepo.addValueToUser(userId, valueToAdd)
    }

    fun getUserShares(userId: String): List<SharesPrice> {
        val currentShares: Map<String, List<Shares>> =
            sharesRepo.findByOwnerId(userId).groupBy { share -> share.companyId!! }

        val userShares = ArrayList<SharesPrice>()

        currentShares.forEach { (companyId, sharesList) ->
            val company = companyRepo.findById(companyId)
                .orElseThrow { throw NoSuchCompanyException(companyId) }

            sharesList.forEach { shares ->
                userShares.add(SharesPrice(shares, company.sharesPrice))
            }
        }

        return userShares
    }

    fun decreaseUserBalance(userId: String, valueToDecrease: BigDecimal) {
        val extraValues = valueToDecrease.multiply(BigDecimal("-1"))
        val curUserBalance = userRepo.findByIdOrNull(userId)?.balance
            ?: throw ServiceException("User id:$userId not found")
        if (curUserBalance < extraValues) {
            throw ServiceException("User id:$userId has no this money")
        }
        userRepo.addValueToUser(userId, extraValues)
    }
}