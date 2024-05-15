package de.nicetoapp.kapitalmeisterbackend.model.common

import de.nicetoapp.kapitalmeisterbackend.model.dto.GenesisTableResponse

data class SeedFileHeader(val valueType: ValueType, val structureType: StructureType) {


    enum class StructureType {
        ROW, COLUMN, HEADER;
    }

    enum class ValueType {
        YEAR, MONTH, PRICE_INDEX, BASKET_ITEM, IGNORED;

        companion object {
            fun mapGenesisValue(valueCode: GenesisTableResponse.ValueCode?): ValueType {
                return when (valueCode) {
                    GenesisTableResponse.ValueCode.YEAR -> YEAR
                    GenesisTableResponse.ValueCode.MONTH -> MONTH
                    GenesisTableResponse.ValueCode.INDEX -> PRICE_INDEX
                    GenesisTableResponse.ValueCode.CC13A5 -> BASKET_ITEM
                    else -> {
                        IGNORED
                    }
                }
            }
        }
    }
}

