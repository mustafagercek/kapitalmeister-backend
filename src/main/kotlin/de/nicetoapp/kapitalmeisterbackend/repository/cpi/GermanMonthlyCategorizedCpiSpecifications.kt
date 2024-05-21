package de.nicetoapp.kapitalmeisterbackend.repository.cpi

import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanCpiCategory
import de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german.GermanMonthlyCategorizedCpi
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object GermanMonthlyCategorizedCpiSpecifications {
    fun byYearMonthAndCategory(
        year: Int?,
        month: Int?,
        catId: String?,
        includedChildrenDepth: Int = 0
    ): Specification<GermanMonthlyCategorizedCpi> {
        return Specification { root: Root<GermanMonthlyCategorizedCpi>, _: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            year?.let { predicates.add(criteriaBuilder.equal(root.get<Int>("year"), it)) }
            month?.let { predicates.add(criteriaBuilder.equal(root.get<Int>("month"), it)) }
            val categoryJoin = root.join<GermanMonthlyCategorizedCpi, GermanCpiCategory>("germanCpiCategory")

            catId?.let {
                val likePattern = if (includedChildrenDepth > 0) {
                    catId + "_".repeat(includedChildrenDepth)
                } else {
                    "$catId"
                }
                predicates.add(criteriaBuilder.like(categoryJoin.get("id"), likePattern))
            } ?: run {
                val lengthCheck = criteriaBuilder.equal(
                    criteriaBuilder.length(categoryJoin.get("id")),
                    7 + includedChildrenDepth
                )
                predicates.add(lengthCheck)

            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}