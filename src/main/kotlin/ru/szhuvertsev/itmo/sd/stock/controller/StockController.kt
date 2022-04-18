package ru.szhuvertsev.itmo.sd.stock.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.szhuvertsev.itmo.sd.stock.exception.ServiceException
import ru.szhuvertsev.itmo.sd.stock.service.StockService
import ru.szhuvertsev.itmo.sd.stock.entity.UserBody
import ru.szhuvertsev.itmo.sd.stock.entity.Json
import java.math.BigDecimal

@RestController
class StockController(private val stockService: StockService) {

    private val mapper = ObjectMapper()

    @PostMapping("/createCompany", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createCompany(@RequestBody body: String): ResponseEntity<Json> {
        val bodyMapper = mapper.readValue(body, UserBody::class.java)
        val companyName = bodyMapper.companyName!!
        if (stockService.findCompanyByName(companyName) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    mapOf("error" to "Company with name:$companyName already exists")
                )

        return ResponseEntity.ok(mapOf("company"
                to
                mapper.writeValueAsString(
                    stockService.createNewCompany(
                        companyName,
                        BigDecimal(bodyMapper.sharesPrice!!)
                    )
                )
        ))
    }

    @PostMapping("/issueShares", consumes = ["application/json"], produces = ["application/json"])
    fun issueShares(@RequestBody body: String): ResponseEntity<Json> {
        val bodyObj = mapper.readValue(body, UserBody::class.java)
        if (bodyObj.amount!! < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Incorrent amount=$bodyObj.amount")
            )
        }
        return try {
            stockService.issueShares(bodyObj.companyId!!, bodyObj.amount)
            ResponseEntity.ok().build()
        } catch (e: ServiceException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/getCompanySharesPrice", produces = ["application/json"])
    fun getCompanySharesPrice(@RequestParam("companyId") companyId: String): ResponseEntity<Json> =
        try {
            ResponseEntity.ok(
                mapOf(
                    "price"
                    to
                    stockService.getCompanySharesPrice(companyId).toPlainString()
                )
            )
        } catch (e: ServiceException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }


    @GetMapping("/getCompanySharesAmount", produces = ["application/json"])
    fun getCompanySharesAmount(@RequestParam("companyId") companyId: String): ResponseEntity<Json> =
        try {
            ResponseEntity.ok(mapOf(
                "amount"
                to
                stockService.countCompanySharesPrice(companyId).toString())
            )
        } catch (e: ServiceException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }


    @GetMapping("/buyCompanyShares", produces = ["application/json"])
    fun buyCompanyShares(
        @RequestParam("buyerId") buyerId: String,
        @RequestParam("companyId") companyId: String,
        @RequestParam("amount") amount: Int
    ): ResponseEntity<Json> = try {
        stockService.buyCompanyShares(buyerId, companyId, amount)
        ResponseEntity.ok(mapOf())
    } catch (e: ServiceException) {
        ResponseEntity.badRequest().body(mapOf("error" to e.message))
    }

    @GetMapping("/updateCompanySharesPrice", produces = ["application/json"])
    fun updateCompanySharesPrice(
        @RequestParam("companyId") companyId: String,
        @RequestParam("percent") percent: Double
    ): ResponseEntity<Json> =
        try {
            stockService.updateCompanySharesPrice(companyId, percent)
            ResponseEntity.ok(mapOf())
        } catch (e: ServiceException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
}