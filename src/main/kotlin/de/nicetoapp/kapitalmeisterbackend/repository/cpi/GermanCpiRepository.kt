package de.nicetoapp.kapitalmeisterbackend.repository.cpi

import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.*
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GermanCpiCategoryRepository : JpaRepository<GermanCpiCategory, String>

@Repository
interface GermanMonthlyCategorizedCpiRepository :
    JpaRepository<GermanMonthlyCategorizedCpi, GermanMonthlyCategorizedCpi.CategorizedMonthlyCpiId> {
    fun findByYearBetween(startYear: Int, endYear: Int, sort: Sort): List<GermanMonthlyCategorizedCpi>
}

@Repository
interface GermanMonthlyCpiRepository : JpaRepository<GermanMonthlyCpi, GermanMonthlyCpi.MonthlyCpiId> {
    fun findByYearBetween(startYear: Int, endYear: Int, sort: Sort): List<GermanMonthlyCpi>
}

@Repository
interface GermanYearlyCpiRepository : JpaRepository<GermanYearlyCpi, Int>

