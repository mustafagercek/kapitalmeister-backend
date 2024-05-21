package de.nicetoapp.kapitalmeisterbackend.repository.cpi

import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanCpiCategory
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCategorizedCpi
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCpi
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanYearlyCpi
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GermanCpiCategoryRepository : JpaRepository<GermanCpiCategory, String>

@Repository
interface GermanMonthlyCategorizedCpiRepository :
    JpaRepository<GermanMonthlyCategorizedCpi, GermanMonthlyCategorizedCpi.CategorizedMonthlyCpiId>,
    JpaSpecificationExecutor<GermanMonthlyCategorizedCpi>

@Repository
interface GermanMonthlyCpiRepository : JpaRepository<GermanMonthlyCpi, GermanMonthlyCpi.MonthlyCpiId> {
    @Query(
        """
        SELECT c FROM GermanMonthlyCpi c 
        WHERE c.year = (SELECT MAX(c1.year) FROM GermanMonthlyCpi c1 WHERE c1.value IS NOT NULL)
        AND c.month = (SELECT MAX(c2.month) FROM GermanMonthlyCpi c2 WHERE c2.year = c.year AND c2.value IS NOT NULL)
    """
    )
    fun findLatestMonthWithValue(): GermanMonthlyCpi?

}

@Repository
interface GermanYearlyCpiRepository : JpaRepository<GermanYearlyCpi, Int>

