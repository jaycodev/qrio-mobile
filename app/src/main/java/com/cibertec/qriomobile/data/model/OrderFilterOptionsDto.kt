package com.cibertec.qriomobile.data.model

data class OrderFilterOptionsDto(
    val tables: List<OptionDto>,
    val customers: List<OptionDto>
)

data class OptionDto(
    val value: Long,
    val label: String
)
