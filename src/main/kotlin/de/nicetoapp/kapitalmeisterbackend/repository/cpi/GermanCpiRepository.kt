package de.nicetoapp.kapitalmeisterbackend.repository.cpi

import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.*
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GermanCpiCategoryRepository : JpaRepository<GermanCpiCategory, String>

@Repository
interface GermanMonthlyCategorizedCpiRepository :
    JpaRepository<GermanMonthlyCategorizedCpi, GermanMonthlyCategorizedCpi.CategorizedMonthlyCpiId> {
    fun findByYearBetween(startYear: Int, endYear: Int, sort: Sort): List<GermanMonthlyCategorizedCpi>

    @Query("SELECT MAX(c.year) FROM GermanMonthlyCategorizedCpi c")
    fun findMaxYear(): Int

    @Query("SELECT MAX(c.month) FROM GermanMonthlyCategorizedCpi c WHERE c.year = :year AND c.value IS NOT NULL")
    fun findMaxMonthByYearWithValue(@Param("year") year: Int): Int

    @Query("SELECT c FROM GermanMonthlyCategorizedCpi c WHERE c.year = :year AND c.month = :month AND c.germanCpiCategory.parentCategory.id = :parentId")
    fun findChildCategories(@Param("year") year: Int, @Param("month") month: Int, @Param("parentId") parentId: String): List<GermanMonthlyCategorizedCpi>

    @Query("SELECT c FROM GermanMonthlyCategorizedCpi c WHERE c.year = :year AND c.month = :month AND c.germanCpiCategory.parentCategory IS NULL")
    fun findByYearAndMonthAndCategoryWithNoParent(@Param("year") year: Int, @Param("month") month: Int, sort: Sort): List<GermanMonthlyCategorizedCpi>
}

@Repository
interface GermanMonthlyCpiRepository : JpaRepository<GermanMonthlyCpi, GermanMonthlyCpi.MonthlyCpiId> {
    fun findByYearBetween(startYear: Int, endYear: Int, sort: Sort): List<GermanMonthlyCpi>
}

@Repository
interface GermanYearlyCpiRepository : JpaRepository<GermanYearlyCpi, Int>

