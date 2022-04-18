package ru.szhuvertsev.itmo.sd.stock.service

import org.springframework.stereotype.Service
import ru.szhuvertsev.itmo.sd.stock.entity.Company
import ru.szhuvertsev.itmo.sd.stock.entity.Shares
import ru.szhuvertsev.itmo.sd.stock.exception.NoSuchCompanyException
import ru.szhuvertsev.itmo.sd.stock.exception.ServiceException
import ru.szhuvertsev.itmo.sd.stock.repository.CompanyRepo
import ru.szhuvertsev.itmo.sd.stock.repository.SharesRepo
import java.math.BigDecimal

@Service
class StockService(
    private val companyRepo: CompanyRepo,
    private val sharesRepo: SharesRepo
) {
    fun findCompanyByName(name: String) = companyRepo.findCompanyByName(name)

    fun createNewCompany(name: String, sharesValue: BigDecimal) = companyRepo.save(Company(name = name, sharesPrice = sharesValue))

    fun issueShares(companyId: String, n: Int) {
        sharesRepo.saveAll(Array(n) { _ -> Shares(companyId = companyId) }.asIterable())
    }

    fun getCompanySharesPrice(companyId: String): BigDecimal =
        companyRepo.findById(companyId)
            .map {share -> share.sharesPrice }
            .orElseThrow { NoSuchCompanyException(companyId) }

    fun countCompanySharesPrice(companyId: String): Long =
        sharesRepo.countByCompanyId(companyId)

    fun buyCompanyShares(buyerId: String, companyId: String, n: Int) {
        val enableShares = sharesRepo.findFreeSharesByCompanyId(companyId)
        if (n > enableShares.size)
            throw ServiceException("Unable buy shares. Company with id:$companyId, have not $n shares")

        for (i in 0 until n) {
            if (enableShares[i].ownerId != null)
                error("Only one user enabled")

            enableShares[i].ownerId = buyerId
        }
        sharesRepo.saveAll(enableShares)
    }

    fun sellCompanyShares(buyerId: String, companyId: String, n: Int) {
        val sharesWantToBuy = sharesRepo.findByOwnerIdAndCompanyId(buyerId, companyId)
        if (n > sharesWantToBuy.size)
            throw ServiceException("Unable to sell $n shares.")

        for (i in 0 until n) {
            sharesWantToBuy[i].ownerId = null
        }

        sharesRepo.saveAll(sharesWantToBuy)
    }

    fun updateCompanySharesPrice(companyId: String, percent: Double) {
        companyRepo.findById(companyId).ifPresentOrElse(
            { company ->
                val currentPrice = company.sharesPrice
                val extraShares = currentPrice.multiply(
                    BigDecimal
                        .valueOf(percent))
                        .divide(BigDecimal.valueOf(100))

                companyRepo.save(company.apply { sharesPrice = currentPrice.plus(extraShares) })
            }, {
                throw NoSuchCompanyException(companyId)
            }
        )
    }


}