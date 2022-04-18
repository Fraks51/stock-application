package ru.szhuvertsev.itmo.sd.stock.entity

typealias Json = Map<String, String?>

data class UserBody constructor(
    val login: String? = null,
    val userId: String? = null,
    var companyName: String? = null,
    var sharesPrice: String? = null,
    val amount: Int? = null,
    val companyId: String? = null,
    val valueToAdd: String? = null,
    val initialBalance: String? = null
)
