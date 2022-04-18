package ru.szhuvertsev.itmo.sd.stock.service

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.szhuvertsev.itmo.sd.stock.entity.Shares
import java.math.BigDecimal


data class SharesPrice @JsonCreator constructor(
    @JsonProperty("id") var id: String,
    @JsonProperty("companyId") var companyId: String,
    @JsonProperty("ownerId") var ownerId: String? = null,
    @JsonProperty("price") var price: BigDecimal
) {
    constructor(shares: Shares, price: BigDecimal): this(shares.id, shares.companyId!!, shares.ownerId, price)
}