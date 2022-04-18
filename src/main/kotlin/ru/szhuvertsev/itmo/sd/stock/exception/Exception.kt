package ru.szhuvertsev.itmo.sd.stock.exception

open class ServiceException(reason: String): RuntimeException(reason)

class NoSuchCompanyException(id: String): ServiceException(
    "Unable to find company with id:$id"
)